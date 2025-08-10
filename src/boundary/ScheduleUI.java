package boundary;

import control.ScheduleMaintenance;
import entity.Schedule;
import utility.InputHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ScheduleUI {
    private final ScheduleMaintenance scheduleMaintenance;
    private final Scanner scanner;

    public ScheduleUI() {
        scheduleMaintenance = new ScheduleMaintenance();
        scanner = new Scanner(System.in);
    }

//    public void runScheduleMenu() {
//        int choice;
//        do {
//            System.out.println("\n=== Doctor Schedule Management Menu ===");
//            System.out.println("1. Manage Schedule for Doctor");
//            System.out.println("0. Back to Main Menu");
//            choice = InputHandler.getInt("Enter your choice", 0, 1);
//
//            switch (choice) {
//                case 1 -> manageDoctorSchedule();
//                case 0 -> System.out.println("Returning to main menu...");
//                default -> System.out.println("Invalid choice, try again.");
//            }
//        } while (choice != 0);
//    }

    public void runScheduleMenu() {
        String doctorID = InputHandler.getString("Enter Doctor ID");

        while (true) {
            scheduleMaintenance.displayCalendar(doctorID);
            int day = InputHandler.getInt("Enter a date (1–31) or 0 to exit", 0, 31);
            if (day == 0) break;

            LocalDate date = LocalDate.now().withDayOfMonth(day);
            manageDateSchedule(doctorID, date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }

    void manageDateSchedule(String doctorID, String date) {
        while (true) {
            System.out.println("\n--- Time Slots for " + date + " ---");
            scheduleMaintenance.listTimeSlotsForDate(doctorID, date);

            System.out.println("\n[1] Assign New Schedule");
            System.out.println("[2] Update Availability");
            System.out.println("[3] Remove Schedule");
            System.out.println("[0] Back to Calendar");

            int choice = InputHandler.getInt("Enter choice", 0, 3);
            switch (choice) {
                case 1 -> assignSchedule(doctorID, date);
                case 2 -> updateAvailability(doctorID, date);
                case 3 -> removeSchedule(doctorID, date);
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void assignSchedule(String doctorID, String date) {
        String scheduleID = InputHandler.getString("Enter Schedule ID");
        String timeslot = InputHandler.getString("Enter Time Slot (e.g. 09:00–10:00)");
        boolean status = InputHandler.getBoolean("Is doctor available? (true/false)");

        // Updated Schedule without patientID
        Schedule schedule = new Schedule(scheduleID, doctorID, date, timeslot, status);
        if (scheduleMaintenance.assignSchedule(schedule)) {
            System.out.println("✅ Schedule assigned successfully.");
        } else {
            System.out.println("❌ Schedule already exists.");
        }
    }

    private void updateAvailability(String doctorID, String date) {
        String timeSlot = InputHandler.getString("Enter Time Slot to update");
        boolean status = InputHandler.getBoolean("Set availability (true/false)");

        if (scheduleMaintenance.updateAvailability(doctorID, date, timeSlot, status)) {
            System.out.println("✅ Availability updated.");
        } else {
            System.out.println("❌ Schedule not found.");
        }
    }

    private void removeSchedule(String doctorID, String date) {
        String timeSlot = InputHandler.getString("Enter Time Slot to remove");

        if (scheduleMaintenance.removeSchedule(doctorID, date, timeSlot)) {
            System.out.println("🗑️ Schedule removed.");
        } else {
            System.out.println("❌ Schedule not found.");
        }
    }
}
