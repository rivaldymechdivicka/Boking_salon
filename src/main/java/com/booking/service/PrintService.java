package com.booking.service;

import java.util.List;
import java.util.stream.Collectors;
import com.booking.models.Customer;
import com.booking.models.Employee;
import com.booking.models.Person;
import com.booking.models.Reservation;
import com.booking.models.Service;

public class PrintService {
    public static void printMenu(String title, String[] menuArr) {
        int num = 1;
        System.out.println(title);
        for (int i = 0; i < menuArr.length; i++) {
            if (i == (menuArr.length - 1)) {
                num = 0;
            }
            System.out.println(num + ". " + menuArr[i]);
            num++;
        }
    }

    public String printServices(List<Service> serviceList) {
        String result = "";
        // Bisa disesuaikan kembali
        for (Service service : serviceList) {
            result += service.getServiceName() + ", ";
        }
        return result;
    }

    // Function yang dibuat hanya sebgai contoh bisa disesuaikan kembali

    /**
 * Menampilkan daftar reservasi yang sedang dalam proses.
 * @param reservationList Daftar reservasi yang akan ditampilkan.
 */
    public void showRecentReservation(List<Reservation> reservationList) {
        int num = 1;
        List<Reservation> inProcessReservations = reservationList.stream()
                .filter(reservation -> "Dalam Proses".equalsIgnoreCase(reservation.getWorkstage()))
                .collect(Collectors.toList());
        System.out.printf("| %-4s | %-10s | %-11s | %-20s | %-15s | %-15s | %-10s |\n",
                "No.", "ID", "Nama Customer", "Service", "Biaya Service", "Pegawai", "Workstage");
        System.out.println(
                "+===========================================================================================================+");
        for (Reservation reservation : inProcessReservations) {
            int counter = 0;
            for (Service service : reservation.getServices()) {
                if (counter == 0)
                    System.out.printf("| %-4s | %-10s | %-13s | %-20s | %-15s | %-15s | %-10s |\n",
                            num, reservation.getReservationId(), reservation.getCustomer().getName(),
                            service.getServiceName(), reservation.getReservationPrice(),
                            reservation.getEmployee().getName(), reservation.getWorkstage());
                else
                    System.out.printf("| %-4s | %-10s | %-13s | %-20s | %-15s | %-15s | %-10s |\n",
                            "", "", "", service.getServiceName(), "", "", "");
                counter++;
            }
            num++;
        }
        System.out.println(
                "+===========================================================================================================+");
    }
/**
 * Menampilkan daftar seluruh pelanggan.
 * @param personList Daftar semua orang yang akan ditampilkan.
 */
    public void showAllCustomer(List<Person> personList) {
        System.out.println("Daftar Semua Pelanggan:");
        System.out.printf("| %-4s | %-11s | %-15s | %-15s | %-15s |\n", "No.", "ID", "Nama Customer", "Member Status",
                "Wallet");
        System.out.println("+==========================================================================+");
        int num = 1;
        for (Person person : personList) {
            if (person instanceof Customer) {
                Customer customer = (Customer) person;
                System.out.printf("| %-4s | %-11s | %-15s | %-15s | %-15s |\n",
                        num, customer.getId(), customer.getName(), customer.getMember().getMembershipName(),
                        customer.getWallet());
                num++;
            }
        }
        System.out.println("+==========================================================================+");
    }
    /**
 * Menampilkan daftar seluruh karyawan.
 * @param personList Daftar semua orang yang akan ditampilkan.
 */

    public void showAllEmployee(List<Person> personList) {
        System.out.println("Daftar Seluruh Karyawan:");
        System.out.printf("| %-4s | %-11s | %-15s | %-15s |\n", "No.", "ID", "Nama Employee", "Experience");
        System.out.println("+========================================================+");
        int num = 1;
        for (Person person : personList) {
            if (person instanceof Employee) {
                Employee employee = (Employee) person;
                System.out.printf("| %-4s | %-11s | %-15s | %-15s |\n",
                        num, employee.getId(), employee.getName(), employee.getExperience());
                num++;
            }
        }
        System.out.println("+========================================================+");
    }
/**
 * Menampilkan riwayat reservasi beserta total keuntungan.
 * @param reservationList Daftar reservasi yang akan ditampilkan.
 */
    public void showHistoryReservation(List<Reservation> reservationList) {
        double totalProfit = 0;
        System.out.println("Daftar Riwayat Reservasi:");
        System.out.printf("| %-4s | %-11s | %-15s | %-20s | %-15s | %-15s | %-10s |\n",
                "No.", "ID", "Customer Name", "Service", "Service's Price", "Pegawai", "Workstage");
        System.out
                .println(
                        "+==============================================================================================================+");
        int num = 1;
        for (Reservation reservation : reservationList) {
            int counter = 0;
            for (Service service : reservation.getServices()) {
                if (counter == 0)
                    System.out.printf("| %-4s | %-11s | %-15s | %-20s | %-15s | %-15s | %-10s |\n",
                            num, reservation.getReservationId(), reservation.getCustomer().getName(),
                            service.getServiceName(), reservation.getReservationPrice(),
                            reservation.getEmployee().getName(), reservation.getWorkstage());
                else
                    System.out.printf("| %-4s | %-11s | %-15s | %-20s | %-15s | %-15s | %-10s |\n",
                            "", "", "", service.getServiceName(), "", "", "");
                counter++;
            }

            if (reservation.getWorkstage().equals("Selsai"))
                totalProfit += reservation.getReservationPrice();
            num++;
        }
        System.out
                .println(
                        "+==============================================================================================================+");
        System.out.println("Total Keuntungan dari Reservasi: Rp. " + totalProfit);
        System.out
                .println(
                        "+==============================================================================================================+");
    }

/**
 * Menampilkan daftar layanan yang tersedia.
 * @param serviceList Daftar layanan yang akan ditampilkan.
 */
    public void showAvailableService(List<Service> serviceList) {
        System.out.println("Daftar Layanan:");
        System.out.printf("| %-4s | %-11s | %-20s | %-15s |\n",
                "No.", "ID", "Service Name", "Price");
        System.out
                .println("+=============================================================+");
        int num = 1;
        for (Service service : serviceList) {
            System.out.printf("| %-4s | %-11s | %-20s | %-15s |\n",
                    num, service.getServiceId(), service.getServiceName(), service.getPrice());
            num++;
        }
        System.out
                .println("+=============================================================+");
    }
}
