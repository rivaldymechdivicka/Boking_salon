package com.booking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import com.booking.models.Customer;
import com.booking.models.Employee;
import com.booking.models.Person;
import com.booking.models.Reservation;
import com.booking.models.Service;
import com.booking.repositories.ServiceRepository;

public class ReservationService {

    private static List<Service> serviceList = ServiceRepository.getAllService();
    private static Scanner input = new Scanner(System.in);

    private static PrintService printService = new PrintService();
// LOGIKA MEMBUAT RESERVATION
public static void createReservation(List<Reservation> reservationList, List<Person> personList) {
    Customer customer = getValidCustomerInput(personList);
    Employee employee = getValidEmployeeInput(personList);
    List<Service> selectedServices = selectServices();
    String workstage = "Dalam Proses";
    String reservationId = "Reservasi" + UUID.randomUUID().toString().substring(0, 6);
    double reservationPrice = calculateReservationPrice(selectedServices, customer);
    Reservation reservation = new Reservation(reservationId, customer, employee, selectedServices, workstage);
    reservation.setReservationPrice(reservationPrice);
    reservationList.add(reservation);
    System.out.println("Reservasi berhasil dibuat!");
}
// Code ini memastikan bahwa reservasi hanya dibuat jika pelanggan dan karyawan yang valid telah dipilih, dan layanan telah dipilih oleh pengguna.
private static Customer getValidCustomerInput(List<Person> personList) {
    Customer customer = null;
    
    do {
        printService.showAllCustomer(personList);
        System.out.println("Masukkan ID pelanggan untuk reservasi:");
        String customerId = input.nextLine();
        customer = findCustomerById(customerId, personList);
        if (customer == null) {
            System.out.println("Pelanggan tidak tersedia. Silakan coba lagi.");
        }
    } while (customer == null);
    return customer;
}
/**
 * Meminta input ID karyawan dari pengguna untuk reservasi, memvalidasi ID tersebut,
 * dan mengembalikan objek Employee yang sesuai.
 * Jika ID yang dimasukkan tidak valid (tidak ditemukan dalam daftar karyawan),
 * pengguna diminta untuk memasukkan ID karyawan yang ada.
 * @param personList Daftar semua orang (termasuk karyawan) dalam sistem.
 * @return Objek Employee yang valid untuk digunakan dalam pembuatan reservasi.
 */
private static Employee getValidEmployeeInput(List<Person> personList) {
    Employee employee = null;

    do {
        printService.showAllEmployee(personList);
        System.out.println("Masukkan ID karyawan untuk reservasi: ");
        String employeeId = input.nextLine();
        employee = findEmployeeById(employeeId, personList);

        if (employee == null) {
            System.out.println("Karyawan tidak tersedia!");
            System.out.println("Masukkan ID karyawan yang ada untuk reservasi: ");
        }
    } while (employee == null);

    return employee;
}
/**
 * Menampilkan reservasi terbaru dan memungkinkan pengguna untuk mengedit tahap kerja reservasi tertentu.
 * Pengguna diminta untuk memasukkan ID reservasi yang ingin diedit.
 * Jika ID adalah "0", kembali ke menu utama.
 * Jika ID reservasi tidak ditemukan, atau reservasi sudah selesai, berikan pesan sesuai.
 * Jika ID reservasi valid dan reservasi masih dalam proses, meminta pengguna untuk memilih tahap kerja baru
 * menggunakan metode selectedWorkStage. Jika pengguna memilih "Selesai", melakukan perhitungan pembayaran
 * menggunakan metode calculateCustomerWallet.
 * Setelah pembaruan berhasil, tahap kerja reservasi diubah dan pesan sukses ditampilkan.
 * Jika pembayaran gagal atau tahap kerja tidak dapat diperbarui, pesan kesalahan ditampilkan.
 * @param reservationList Daftar semua reservasi dalam sistem.
 * @param personList Daftar semua orang (termasuk pelanggan) dalam sistem.
 */
public static void editReservationWorkstage(List<Reservation> reservationList, List<Person> personList) {
    printService.showRecentReservation(reservationList);
    System.out.println("Masukkan ID reservasi untuk mengedit tahap kerja: ");
    String reservationId = input.nextLine();

    Reservation reservation;

    do {
        if (reservationId.equals("0")) {
            System.out.println("Kembali ke menu.");
            return;
        }

        reservation = findReservationById(reservationId, reservationList);

        if (reservation == null) {
            System.out.println("Reservasi tidak tersedia!");
        } else if (!reservation.getWorkstage().equals("Dalam Proses")) {
            System.out.println("Reservasi ini sudah selesai!");
        } else {
            String newWorkstage = selectedWorkStage();
            double oldWallet = reservation.getCustomer().getWallet();

            if (newWorkstage.equals("Selesai")) {
                calculateCustomerWallet(reservation, personList);
            }
            if (reservation.getCustomer().getWallet() != oldWallet) {
                reservation.setWorkstage(newWorkstage);
                System.out.println("Tahap kerja berhasil diperbarui!");
            } else {
                System.out.println("Pembaruan tahap kerja gagal!");
            }

            return;
        }

        System.out.println("Masukkan ID reservasi yang ada untuk mengedit tahap kerja: (0 Kembali ke Menu)");
        reservationId = input.nextLine();

    } while (true);
}
// Metode untuk mencari Customer berdasarkan ID dalam daftar orang.
private static Customer findCustomerById(String customerId, List<Person> personList) {
    for (Person person : personList) {
        if (person instanceof Customer && person.getId().equals(customerId)) {
            return (Customer) person;
        }
    }
    return null;
}
// Metode untuk mencari Employee berdasarkan ID dalam daftar orang.
private static Employee findEmployeeById(String employeeId, List<Person> personList) {
    for (Person person : personList) {
        if (person instanceof Employee && person.getId().equals(employeeId)) {
            return (Employee) person;
        }
    }
    return null;
}
/**
 * Memungkinkan pengguna untuk memilih layanan untuk reservasi.
 * Menampilkan daftar layanan yang tersedia menggunakan printService.showAvailableService.
 * Pengguna diminta untuk memasukkan ID layanan yang ingin dipilih.
 * Jika ID yang dimasukkan adalah "0", keluar dari loop dan kembali ke menu utama.
 * Jika ID layanan tidak ditemukan, berikan pesan kesalahan.
 * Jika ID layanan sudah dipilih sebelumnya, berikan pesan bahwa layanan tersebut sudah dipilih.
 * Jika ID layanan valid dan belum dipilih sebelumnya, tambahkan layanan tersebut ke dalam daftar yang dipilih.
 * Teruskan proses ini sampai pengguna memasukkan "0".
 * @return List Service yang berisi layanan-layanan yang dipilih untuk reservasi.
 */
private static List<Service> selectServices() {
    printService.showAvailableService(serviceList);
    List<Service> selectedServices = new ArrayList<>();

    while (true) {
        System.out.println("Masukkan ID layanan untuk reservasi: (0 Kembali Ke Menu)");
        String serviceIdsInput = input.nextLine();

        if (serviceIdsInput.equals("0")) {
            break;
        }

        Service service = findServiceById(serviceIdsInput, serviceList);

        if (service == null) {
            System.out.println("Layanan tidak tersedia!");
        } else if (!selectedServices.contains(service)) {
            selectedServices.add(service);
        } else {
            System.out.println("Layanan sudah dipilih!");
        }
    }

    return selectedServices;
}
/**
 * Menghitung total harga reservasi berdasarkan layanan yang dipilih dan potensi diskon pelanggan.
 * Jumlah total dihitung dari harga layanan yang dipilih.
 * Jika pelanggan memiliki keanggotaan (membership), diterapkan diskon berdasarkan jenis keanggotaan.
 * @param selectedServices List layanan yang dipilih untuk reservasi.
 * @param customer Objek pelanggan yang membuat reservasi.
 * @return Total harga reservasi setelah diterapkan diskon jika ada.
 */
private static double calculateReservationPrice(List<Service> selectedServices, Customer customer) {
    double totalPrice = selectedServices.stream()
            .mapToDouble(Service::getPrice)
            .sum();

    if (customer != null && customer.getMember() != null) {
        String membershipName = customer.getMember().getMembershipName().toLowerCase();
        double discountRate = getDiscountRateByMembership(membershipName);
        totalPrice *= (1 - discountRate);
    }

    return totalPrice;
}
/**
 * Mendapatkan tingkat diskon berdasarkan jenis keanggotaan pelanggan.
 * @param membershipName Jenis keanggotaan pelanggan.
 * @return Tingkat diskon yang diterapkan sebagai pecahan (misal: 0.05 untuk diskon 5%).
 */
private static double getDiscountRateByMembership(String membershipName) {
    switch (membershipName) {
        case "silver":
            return 0.05; // Diskon 5%
        case "gold":
            return 0.10; // Diskon 10%
        default:
            return 0.0; // Tidak ada diskon
    }
}
/**
 * Menghitung pembayaran dari dompet pelanggan setelah reservasi selesai.
 * Memeriksa apakah saldo dompet mencukupi untuk membayar total harga reservasi.
 * Jika mencukupi, mengurangkan total harga dari saldo dompet dan memberikan pemberitahuan pembayaran berhasil.
 * Jika saldo tidak mencukupi, memberikan pemberitahuan bahwa dompet tidak mencukupi.
 * @param reservation Objek reservasi yang akan dibayar.
 * @param personList Daftar semua orang, termasuk pelanggan.
 */
private static void calculateCustomerWallet(Reservation reservation, List<Person> personList) {
    Customer reservationCustomer = reservation.getCustomer();
    double servicePrice = reservation.getReservationPrice();

    for (Person person : personList) {
        if (person instanceof Customer) {
            Customer customer = (Customer) person;
            if (customer.getId().equals(reservationCustomer.getId())) {
                double wallet = customer.getWallet() - servicePrice;
                if (wallet < 0) {
                    System.out.println("Dompet tidak mencukupi");
                } else {
                    customer.setWallet(wallet);
                    System.out.println("Pembayaran berhasil");
                }
                break;
            }
        }
    }
}
/**
 * Mencari reservasi berdasarkan ID reservasi.
 * @param reservationId ID reservasi yang dicari.
 * @param reservationList Daftar semua reservasi.
 * @return Objek reservasi jika ditemukan, atau null jika tidak ditemukan.
 */

private static Reservation findReservationById(String reservationId, List<Reservation> reservationList) {
    for (Reservation reservation : reservationList) {
        if (reservation.getReservationId().equals(reservationId)) {
            return reservation;
        }
    }
    return null;
}
/**
 * Mencari layanan berdasarkan ID layanan.
 * @param serviceId ID layanan yang dicari.
 * @param serviceList Daftar semua layanan.
 * @return Objek layanan jika ditemukan, atau null jika tidak ditemukan.
 */
private static Service findServiceById(String serviceId, List<Service> serviceList) {
    for (Service service : serviceList) {
        if (service.getServiceId().equals(serviceId)) {
            return service;
        }
    }
    return null;
}
/**
 * Meminta pengguna memilih tahap kerja baru.
 * Menampilkan opsi dan memvalidasi pilihan pengguna.
 * Mengembalikan string yang merepresentasikan tahap kerja yang dipilih.
 * @return Tahap kerja yang dipilih oleh pengguna.
 */
public static String selectedWorkStage() {
    int choice;
    String workstage = "";
    
    do {
        System.out.printf("Masuk ke tahap kerja baru:%n1. %s%n2. %s%n3. %s%nPilihan Anda: ", "Dalam Proses", "Selesai",
                "Dibatalkan");
        choice = Integer.valueOf(input.nextLine());
        
        if (choice > 3 || choice < 1) {
            System.out.println("WorkStage tidak valid! Silakan Coba Lagi:");
        }
    } while (choice > 3 || choice < 1);

    switch (choice) {
        case 1:
            workstage = "Dalam Proses";
            break;
        case 2:
            workstage = "Selesai";
            break;
        case 3:
            workstage = "Dibatalkan";
            break;
    }

    return workstage;
}
    // Silahkan tambahkan function lain, dan ubah function diatas sesuai kebutuhan
}
