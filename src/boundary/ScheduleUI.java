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
        this.scheduleMaintenance = new ScheduleMaintenance();
        scanner = new Scanner(System.in);
    }

    public void runScheduleMenu() {
        int choice;
        do {
            printMenu();
            choice = InputHandler.getInt("Enter your choice", 0, 5);

            switch (choice) {
                case 1 -> listAllSchedulesFlow();
                case 2 -> searchFlow();
                case 3 -> assignFlow();
                case 4 -> removeFlow();
                case 5 -> markLeaveFlow();
                case 0 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }

    // Updated menu UI
    public void printMenu() {
        System.out.println("\n┌" + "─".repeat(58) + "┐");
        System.out.println("│              DOCTOR SCHEDULE MANAGEMENT MENU             │");
        System.out.println("├" + "─".repeat(58) + "┤");
        System.out.println("│ 1. List Schedule Calendar                                │");
        System.out.println("│ 2. Search Schedule                                       │");
        System.out.println("│ 3. Assign New Schedule(s)                                │");
        System.out.println("│ 4. Remove Schedule(s)                                    │");
        System.out.println("│ 5. Mark Leave (Update Schedule Status)                   │");
        System.out.println("│ 0. Back to Main Menu                                     │");
        System.out.println("└" + "─".repeat(58) + "┘");
    }

    private void listAllSchedulesFlow() {
        CustomADT<Integer, Schedule> schedules = scheduleMaintenance.getAllSchedules();
        showScheduleTable(schedules);
        sortScheduleTableFlow(schedules);
    }

    // 2. Search (new flow based on requirements)
    private void searchFlow() {
        System.out.println("\n┌" + "─".repeat(20) + "┐");
        System.out.println("│ Search by:         │");
        System.out.println("├" + "─".repeat(20) + "┤");
        System.out.println("│ 1. Date            │");
        System.out.println("│ 2. Doctor          │");
        System.out.println("│ 3. Status          │");
        System.out.println("│ 0. Back            │");
        System.out.println("└" + "─".repeat(20) + "┘");
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

        System.out.println(" 1. Show all schedules for this date");
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
        LocalDate currentDate = LocalDate.now();
        int year = InputHandler.getInt("Enter year (e.g. 2025)", 2000, 2100);
        int month = InputHandler.getInt("Enter month", 1, 12);

        int monthLength = YearMonth.of(year, month).lengthOfMonth();
        int day = InputHandler.getInt("Pick a day", 1, monthLength);
        LocalDate selectedDate = LocalDate.of(year, month, day);

        // Prevent assignment for past or current date
        if (!selectedDate.isAfter(currentDate)) {
            System.out.println("You can only assign schedule(s) for dates after today (" + currentDate + ").");
            return;
        }

        String date = selectedDate.toString();
        String doctorID = InputHandler.getString("Enter Doctor ID: ").trim();

        // Show existing schedules for this doctor on that day
        CustomADT<Integer, Schedule> existingSchedules = scheduleMaintenance.getSchedulesByDateAndDoctor(date, doctorID);
        if (existingSchedules.size() > 0) {
            System.out.println("\nExisting schedules for Doctor " + doctorID + " on " + date + ":");
            showScheduleTable(existingSchedules);
        } else {
            System.out.println("\nNo existing schedules for this doctor on this date.");
        }

        assignSchedule(doctorID, date);
    }

    // 4. Remove
    private void removeFlow() {
        CustomADT<Integer, Schedule> schedules = scheduleMaintenance.getAllSchedules();
        CustomADT<Integer, Schedule> removableSchedules = new CustomADT<>();
        int idx = 0;
        for (int i = 0; i < schedules.size(); i++) {
            Schedule s = schedules.get(i);
            if (s.getStatus()) { // Only show schedules with status == true
                removableSchedules.put(idx++, s);
            }
        }

        if (removableSchedules.size() == 0) {
            System.out.println("No available schedules to remove.");
            return;
        }

        showScheduleTable(removableSchedules);

        int slotNum = InputHandler.getInt("Select a schedule number to remove", 1, removableSchedules.size());
        Schedule selectedSchedule = removableSchedules.get(slotNum - 1);

        if (scheduleMaintenance.removeSchedule(selectedSchedule.getDoctorID(), selectedSchedule.getDate(), selectedSchedule.getTimeslot())) {
            System.out.println("Schedule removed for " + selectedSchedule.getTimeslot());
        } else {
            System.out.println("Failed to remove the schedule.");
        }
    }

    // 5. Mark Leave / Update status to unavailable
    private void markLeaveFlow() {
        LocalDate currentDate = LocalDate.now();
        int year = InputHandler.getInt("Enter year (e.g. 2025)", currentDate.getYear(), 2100);
        int month = InputHandler.getInt("Enter month", 1, 12);

        scheduleMaintenance.displayUnifiedCalendar(year, month);

        int monthLength = YearMonth.of(year, month).lengthOfMonth();

        int minDay = (year == currentDate.getYear() && month == currentDate.getMonthValue()) ? currentDate.getDayOfMonth() + 1 : 1;
        if (minDay > monthLength) {
            System.out.println("No days left in this month after today.");
            return;
        }
        int day = InputHandler.getInt("Pick a day after today (" + minDay + "-" + monthLength + ", 0 to return)", minDay, monthLength);
        if (day == 0) return;

        String date = String.format("%04d-%02d-%02d", year, month, day);

        CustomADT<Integer, Schedule> schedules = scheduleMaintenance.getSchedulesByDate(date);
        CustomADT<Integer, Schedule> availableSchedules = new CustomADT<>();
        int idx = 0;
        for (int i = 0; i < schedules.size(); i++) {
            Schedule s = schedules.get(i);
            if (s.getStatus()) {
                availableSchedules.put(idx++, s);
            }
        }

        if (availableSchedules.size() == 0) {
            System.out.println("No available schedules to mark as leave for " + date + ".");
            return;
        }

        showScheduleTable(availableSchedules);

        int slotNum = InputHandler.getInt("Select a schedule number to mark as leave", 1, availableSchedules.size());
        Schedule selectedSchedule = availableSchedules.get(slotNum - 1);

        if (scheduleMaintenance.markScheduleAsLeave(
                selectedSchedule.getDoctorID(),
                selectedSchedule.getDate(),
                selectedSchedule.getTimeslot())) {
            System.out.println("Schedule marked as leave for " + selectedSchedule.getTimeslot());
        } else {
            System.out.println("Schedule not found or not available.");
        }
    }

    // Universally styled table display
    private void showScheduleTable(CustomADT<Integer, Schedule> schedules) {
        if (schedules == null || schedules.size() == 0) {
            System.out.println("No schedules to display.");
            return;
        }
        System.out.println("+-----+--------------+---------------+---------------+---------------+---------------+");
        System.out.printf("| %-3s | %-12s | %-13s | %-13s | %-13s | %-13s |\n",
                "No.", "Date", "Doctor ID", "Time Slot", "Available", "Schedule ID");
        System.out.println("+-----+--------------+---------------+---------------+---------------+---------------+");
        for (int i = 0; i < schedules.size(); i++) {
            Schedule s = schedules.get(i);
            System.out.printf("| %-3d | %-12s | %-13s | %-13s | %-13s | %-13s |\n",
                    i + 1,
                    s.getDate(),
                    s.getDoctorID(),
                    s.getTimeslot(),
                    s.getStatus() ? "Yes" : "No",
                    s.getScheduleID());
        }
        System.out.println("+-----+--------------+---------------+---------------+---------------+---------------+");
    }

    // Sort menu for any shown table
    private void sortScheduleTableFlow(CustomADT<Integer, Schedule> schedules) {
        if (schedules == null || schedules.size() == 0) return;
        System.out.println("\n┌" + "─".repeat(40) + "┐");
        System.out.println("│ Sort by:                               │");
        System.out.println("├" + "─".repeat(40) + "┤");
        System.out.println("│ 1. Date                                │");
        System.out.println("│ 2. Doctor                              │");
        System.out.println("│ 3. Time Slot                           │");
        System.out.println("│ 4. Status                              │");
        System.out.println("│ 0. No Sorting                          │");
        System.out.println("└" + "─".repeat(40) + "┘");
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

}