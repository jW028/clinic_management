package dao;

import adt.OrderedMap;
import entity.Appointment;
import java.time.LocalDateTime;

public class AppointmentInitializer {
    public static void main(String[] args) {
        OrderedMap<String, Appointment> appointmentMap = new OrderedMap<>();

        appointmentMap.put("A001", new Appointment("A001", "P001", "DC001", LocalDateTime.of(2025, 8, 1, 9, 0), "Scheduled", "walk-in"));
        appointmentMap.put("A002", new Appointment("A002", "P002", "DC002", LocalDateTime.of(2025, 8, 1, 9, 30), "Completed", "appointment"));
        appointmentMap.put("A003", new Appointment("A003", "P003", "DC001", LocalDateTime.of(2025, 8, 1, 10, 0), "Completed", "appointment"));
        appointmentMap.put("A004", new Appointment("A004", "P004", "DC003", LocalDateTime.of(2025, 8, 1, 10, 30), "Cancelled", "appointment"));
        appointmentMap.put("A005", new Appointment("A005", "P005", "DC002", LocalDateTime.of(2025, 8, 1, 11, 0), "Scheduled", "walk-in"));
        appointmentMap.put("A006", new Appointment("A006", "P006", "DC003", LocalDateTime.of(2025, 8, 1, 11, 30), "Scheduled", "walk-in"));
        appointmentMap.put("A007", new Appointment("A007", "P007", "DC001", LocalDateTime.of(2025, 8, 1, 12, 0), "Completed", "appointment"));
        appointmentMap.put("A008", new Appointment("A008", "P008", "DC002", LocalDateTime.of(2025, 8, 1, 12, 30), "Completed", "walk-in"));
        appointmentMap.put("A009", new Appointment("A009", "P009", "DC003", LocalDateTime.of(2025, 8, 1, 13, 0), "Cancelled", "appointment"));
        appointmentMap.put("A010", new Appointment("A010", "P010", "DC001", LocalDateTime.of(2025, 8, 1, 13, 30), "Scheduled", "walk-in"));

        AppointmentDAO dao = new AppointmentDAO();
        dao.saveToFile(appointmentMap);
        System.out.println("Appointments initialized and saved.");
    }
}
