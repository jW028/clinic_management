package boundary;

import control.ScheduleMaintenance;
import adt.CustomADT;
import utility.IDGenerator;
import utility.InputHandler;
import entity.Schedule;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Scanner;

public class ScheduleUI {
    private final ScheduleMaintenance scheduleMaintenance;
    private final Scanner scanner;

    public ScheduleUI() {
        scheduleMaintenance = new ScheduleMaintenance();
        scanner = new Scanner(System.in);
    }

    public void runScheduleMenu() {
        int choice;
        do {
            System.out.println("\n=== Doctor Schedule Management Menu ===");
            System.out.println("1. List Schedule Calendar");
            System.out.println("2. Search Schedule");
            System.out.println("3. Assign New Schedule(s)");
            System.out.println("4. Remove Schedule(s)");
            System.out.println("0. Back to Main Menu");
            choice = InputHandler.getInt("Enter your choice", 0, 4);

            switch (choice) {
                case 1 -> listAllSchedulesFlow();
                case 2 -> searchFlow();
                case 3 -> assignFlow();
                case 4 -> removeFlow();
                case 0 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }

    // 1. List schedule calendar with month/year selection and * for scheduled days
//    private void listCalendarFlow() {
//        int year = InputHandler.getInt("Enter year (e.g. 2025)", 2000, 2100);
//        int month = InputHandler.getInt("Enter month ", 1, 12);
//
//        scheduleMaintenance.displayUnifiedCalendar(year, month);
//
//        int monthLength = YearMonth.of(year, month).lengthOfMonth();
//        int day = InputHandler.getInt("Pick a day (1-" + monthLength + ", 0 to return) ", 0, monthLength);
//
//        if (day == 0) return;
//
//        String selectedDate = String.format("%04d-%02d-%02d", year, month, day);
//        if (!scheduleMaintenance.isScheduledDate(selectedDate)) {
//            System.out.println("No schedules for this date.");
//            return;
//        }
//
//        CustomADT<Integer, Schedule> schedules = scheduleMaintenance.getSchedulesByDate(selectedDate);
//        showScheduleTable(schedules);
//        sortScheduleTableFlow(schedules);
//    }
    private void listAllSchedulesFlow() {
        CustomADT<Integer, Schedule> schedules = scheduleMaintenance.getAllSchedules();
        showScheduleTable(schedules);
        sortScheduleTableFlow(schedules);
    }

    // 2. Search (new flow based on requirements)
    private void searchFlow() {
        System.out.println("\nSearch by: [1] Date [2] Doctor [3] Status [0] Back");
        int option = InputHandler.getInt("Choose search option", 0, 3);
        switch (option) {
            case 1 -> searchByDateFlow();
            case 2 -> searchByDoctorFlow();
            case 3 -> searchByStatusFlow();
            case 0 -> { return; }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void searchByDateFlow() {
        int year = InputHandler.getInt("Enter year (e.g. 2025) ", 2000, 2100);
        int month = InputHandler.getInt("Enter month ", 1, 12);

        scheduleMaintenance.displayUnifiedCalendar(year, month);

        int monthLength = YearMonth.of(year, month).lengthOfMonth();
        int day = InputHandler.getInt("Pick a day (1-" + monthLength + ", 0 to return)", 0, monthLength);
        if (day == 0) return;

        String date = String.format("%04d-%02d-%02d", year, month, day);

        System.out.println("1. Show all schedules for this date");
        System.out.println("2. Show schedules for this date by doctor");
        int choice = InputHandler.getInt("Choose option", 1, 2);
        if (choice == 1) {
            CustomADT<Integer, Schedule> schedules = scheduleMaintenance.getSchedulesByDate(date);
            showScheduleTable(schedules);
            sortScheduleTableFlow(schedules);
        } else {
            CustomADT<Integer, String> doctorIDs = scheduleMaintenance.getDoctorIDsByDate(date);
            if (doctorIDs.size() == 0) {
                System.out.println("No doctors have schedules on this date.");
                return;
            }
            System.out.println("\nDoctors with schedules for " + date + ":");
            for (int i = 0; i < doctorIDs.size(); i++) {
                System.out.println((i+1) + ". " + doctorIDs.get(i));
            }
            int doctorChoice = InputHandler.getInt("Select doctor number to view slots", 1, doctorIDs.size());
            String doctorID = doctorIDs.get(doctorChoice - 1);
            CustomADT<Integer, Schedule> schedules = scheduleMaintenance.getSchedulesByDateAndDoctor(date, doctorID);
            showScheduleTable(schedules);
            sortScheduleTableFlow(schedules);
        }
    }

    // Search by Doctor
    private void searchByDoctorFlow() {
        String doctorID = InputHandler.getString("Enter Doctor ID: ").trim();
        CustomADT<Integer, Schedule> schedules = scheduleMaintenance.getSchedulesByDoctor(doctorID);

        if (schedules.size() == 0) {
            System.out.println("No schedules found for this doctor.");
            return;
        }

        // Show calendar for doctor
        scheduleMaintenance.displayDoctorCalendar(doctorID);
        showScheduleTable(schedules);
        sortScheduleTableFlow(schedules);
    }

    // Search by Status
    private void searchByStatusFlow() {
        String statusStr = InputHandler.getString("Enter status (true/false): ").trim();
        CustomADT<Integer, Schedule> schedules = scheduleMaintenance.getSchedulesByStatus(statusStr);

        if (schedules.size() == 0) {
            System.out.println("No schedules found for this status.");
            return;
        }

        scheduleMaintenance.displayStatusCalendar(statusStr);
        showScheduleTable(schedules);
        sortScheduleTableFlow(schedules);
    }

    // 3. Assign
    private void assignFlow() {
        int year = InputHandler.getInt("Enter year (e.g. 2025)", 2000, 2100);
        int month = InputHandler.getInt("Enter month", 1, 12);

        int monthLength = YearMonth.of(year, month).lengthOfMonth();
        int day = InputHandler.getInt("Pick a day", 1, monthLength);
        String date = String.format("%04d-%02d-%02d", year, month, day);

        String doctorID = InputHandler.getString("Enter Doctor ID: ").trim();
        assignSchedule(doctorID, date);
    }

    // 4. Remove
    private void removeFlow() {
        int year = InputHandler.getInt("Enter year (e.g. 2025)", 2000, 2100);
        int month = InputHandler.getInt("Enter month ", 1, 12);

        int monthLength = YearMonth.of(year, month).lengthOfMonth();
        int day = InputHandler.getInt("Pick a day", 1, monthLength);
        String date = String.format("%04d-%02d-%02d", year, month, day);

        String doctorID = InputHandler.getString("Enter Doctor ID: ").trim();
        removeSchedule(doctorID, date);
    }

    // Table display for schedules
    private void showScheduleTable(CustomADT<Integer, Schedule> schedules) {
        if (schedules == null || schedules.size() == 0) {
            System.out.println("No schedules to display.");
            return;
        }
        System.out.printf("%-5s %-12s %-15s %-15s %-10s %-12s\n", "No.", "Date", "Doctor ID", "Time Slot", "Available", "Schedule ID");
        System.out.println("-------------------------------------------------------------------------------");
        for (int i = 0; i < schedules.size(); i++) {
            Schedule s = schedules.get(i);
            System.out.printf("%-5d %-12s %-15s %-15s %-10s %-12s\n",
                    i + 1, s.getDate(), s.getDoctorID(), s.getTimeslot(), s.getStatus() ? "Yes" : "No", s.getScheduleID());
        }
    }

    // Sort menu for any shown table
    private void sortScheduleTableFlow(CustomADT<Integer, Schedule> schedules) {
        if (schedules == null || schedules.size() == 0) return;
        System.out.println("\nSort by: [1] Date [2] Doctor [3] Time Slot [4] Status [0] No Sorting");
        int sortOption = InputHandler.getInt("Choose sort option", 0, 4);
        if (sortOption != 0) {
            CustomADT<Integer, Schedule> sorted = scheduleMaintenance.sortSchedules(schedules, sortOption);
            System.out.println("\nSorted Schedule Table:");
            showScheduleTable(sorted);
        }
    }

    // Assign schedule
    private void assignSchedule(String doctorID, String date) {
        String scheduleID = IDGenerator.generateScheduleID();
        System.out.println("Generated Schedule ID: " + scheduleID);
        String timeSlotInput;
        CustomADT<Integer, String> validSlots;
        while (true) {
            timeSlotInput = InputHandler.getString("Enter Time Slot (e.g. 09:00–10:00)").trim();
            validSlots = scheduleMaintenance.parseAndValidateTimeSlots(timeSlotInput);
            if (validSlots != null && validSlots.size() != 0) break;
            System.out.println("Invalid format or slot length. Please enter a valid one-hour slot or a range that splits into one-hour slots.");
        }
        boolean status = InputHandler.getBoolean("Is doctor available?");

        boolean allAssigned = scheduleMaintenance.assignSchedule(doctorID, date, timeSlotInput, status);
        if (allAssigned) {
            System.out.println("Schedule(s) assigned successfully.");
            for (int i = 0; i < validSlots.size(); i++) {
                System.out.println("Assigned slot: " + validSlots.get(i));
            }
        } else {
            System.out.println("Some slots may already exist. Please check.");
        }
    }

    private void removeSchedule(String doctorID, String date) {
        CustomADT<Integer, String> slots = scheduleMaintenance.getTimeSlotsForDate(doctorID, date);
        if (slots == null || slots.size() == 0) {
            System.out.println("No schedules found for this date.");
            return;
        }

        System.out.printf("%-5s %-15s %-10s %-12s\n", "No.", "Time Slot", "Available", "Schedule ID");
        System.out.println("---------------------------------------------------");
        for (int i = 0; i < slots.size(); i++) {
            String slot = slots.get(i);
            var schedule = scheduleMaintenance.getSchedule(doctorID, date, slot);
            String status = (schedule != null && schedule.getStatus()) ? "Yes" : "No";
            String scheduleID = (schedule != null) ? schedule.getScheduleID() : "-";
            System.out.printf("%-5d %-15s %-10s %-12s\n", i + 1, slot, status, scheduleID);
        }

        int slotNum = InputHandler.getInt("Select a slot number to remove", 1, slots.size());
        String selectedSlot = slots.get(slotNum - 1);

        if (scheduleMaintenance.removeSchedule(doctorID, date, selectedSlot)) {
            System.out.println("Schedule removed for " + selectedSlot);
        } else {
            System.out.println("Schedule not found.");
        }
    }
}