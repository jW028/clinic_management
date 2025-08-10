package dao;

import adt.CustomADT;
import entity.Appointment;

import java.time.LocalDateTime;

public class AppointmentInitializer {
    public static void main(String[] args) {
        CustomADT<String, Appointment> appointmentMap = new CustomADT<>();

        appointmentMap.put("A001", new Appointment("A001", "P001", "DC001", LocalDateTime.of(2025, 8, 1, 9, 0), "Scheduled"));
        appointmentMap.put("A002", new Appointment("A002", "P002", "DC002", LocalDateTime.of(2025, 8, 1, 9, 30), "Completed"));
        appointmentMap.put("A003", new Appointment("A003", "P003", "DC001", LocalDateTime.of(2025, 8, 1, 10, 0), "Completed"));
        appointmentMap.put("A004", new Appointment("A004", "P004", "DC003", LocalDateTime.of(2025, 8, 1, 10, 30), "Canceled"));
        appointmentMap.put("A005", new Appointment("A005", "P005", "DC002", LocalDateTime.of(2025, 8, 1, 11, 0), "Scheduled"));
        appointmentMap.put("A006", new Appointment("A006", "P006", "DC003", LocalDateTime.of(2025, 8, 1, 11, 30), "Scheduled"));
        appointmentMap.put("A007", new Appointment("A007", "P007", "DC001", LocalDateTime.of(2025, 8, 1, 12, 0), "Completed"));
        appointmentMap.put("A008", new Appointment("A008", "P008", "DC002", LocalDateTime.of(2025, 8, 1, 12, 30), "Completed"));
        appointmentMap.put("A009", new Appointment("A009", "P009", "DC003", LocalDateTime.of(2025, 8, 1, 13, 0), "Canceled"));
        appointmentMap.put("A010", new Appointment("A010", "P010", "DC001", LocalDateTime.of(2025, 8, 1, 13, 30), "Scheduled"));

        AppointmentDAO dao = new AppointmentDAO();
        dao.saveToFile(appointmentMap);
        System.out.println("Appointments initialized and saved.");
    }
}
