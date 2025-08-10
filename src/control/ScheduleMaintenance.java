package control;

import adt.CustomADT;
import dao.ScheduleDAO;
import entity.Schedule;

import java.time.LocalDate;

public class ScheduleMaintenance {
    private CustomADT<String, Schedule> scheduleList;
    private final ScheduleDAO scheduleDAO;

    public ScheduleMaintenance() {
        scheduleDAO = new ScheduleDAO();
        scheduleList = scheduleDAO.loadSchedules(); // Load from file at start
    }

    public boolean assignSchedule(Schedule schedule) {
        String key = generateKey(schedule.getDoctorID(), schedule.getDate(), schedule.getTimeslot());
        if (scheduleList.containsKey(key)) {
            return false;
        }
        scheduleList.put(key, schedule);
        scheduleDAO.saveSchedules(scheduleList); // Save after adding
        return true;
    }

    public Schedule getSchedule(String doctorID, String date, String timeSlot) {
        String key = generateKey(doctorID, date, timeSlot);
        return scheduleList.get(key);
    }

    public boolean updateAvailability(String doctorID, String date, String timeSlot, boolean available) {
        Schedule schedule = getSchedule(doctorID, date, timeSlot);
        if (schedule != null) {
            schedule.setStatus(available);
            scheduleDAO.saveSchedules(scheduleList); // Save after update
            return true;
        }
        return false;
    }

    public boolean removeSchedule(String doctorID, String date, String timeSlot) {
        String key = generateKey(doctorID, date, timeSlot);
        boolean removed = scheduleList.remove(key) != null;
        if (removed) {
            scheduleDAO.saveSchedules(scheduleList); // Save after removal
        }
        return removed;
    }

    public void listAllSchedules() {
        printScheduleHeader();
        scheduleList.forEach(this::printScheduleRow);
    }

    /** Show schedules only for one doctor **/
    public void printScheduleByDoctorID(String doctorID) {
        final boolean[] found = {false};
        printScheduleHeader();
        scheduleList.forEach(s -> {
            if (s.getDoctorID().equalsIgnoreCase(doctorID)) {
                printScheduleRow(s);
                found[0] = true;
            }
        });
        if (!found[0]) {
            System.out.println("No schedules found for this doctor.");
        }
    }

    private void printScheduleHeader() {
        System.out.printf("%-12s %-12s %-15s %-10s%n",
                "ScheduleID", "Date", "TimeSlot", "Available");
        System.out.println("-----------------------------------------------");
    }

    private void printScheduleRow(Schedule s) {
        System.out.printf("%-12s %-12s %-15s %-10s%n",
                s.getScheduleID(),
                s.getDate(),
                s.getTimeslot(),
                s.getStatus() ? "Yes" : "No");
    }

    private String generateKey(String doctorID, String date, String timeSlot) {
        return doctorID + "_" + date + "_" + timeSlot;
    }

    public int getScheduleCount() {
        return scheduleList.size();
    }

    public void displayCalendar(String doctorID) {
        System.out.println("\n=== Schedule Calendar for Doctor: " + doctorID + " ===");
        LocalDate today = LocalDate.now();
        int monthLength = today.lengthOfMonth();
        int dayOfWeek = today.withDayOfMonth(1).getDayOfWeek().getValue(); // 1=Mon

        // Print padding for first week
        for (int i = 1; i < dayOfWeek; i++) {
            System.out.print("    ");
        }

        for (int day = 1; day <= monthLength; day++) {
            String date = today.withDayOfMonth(day).toString();
            boolean available = isDoctorAvailableOnDate(doctorID, date);
            System.out.printf("%2d%s ", day, available ? "*" : " ");

            if ((day + dayOfWeek - 1) % 7 == 0) {
                System.out.println();
            }
        }
        System.out.println("\n* = Has at least one available slot");
    }

    private boolean isDoctorAvailableOnDate(String doctorID, String date) {
        final boolean[] available = {false};
        scheduleList.forEach(s -> {
            if (s.getDoctorID().equalsIgnoreCase(doctorID) &&
                    s.getDate().equals(date) && s.getStatus()) {
                available[0] = true;
            }
        });
        return available[0];
    }

    public void listTimeSlotsForDate(String doctorID, String date) {
        final boolean[] found = {false};
        scheduleList.forEach(s -> {
            if (s.getDoctorID().equalsIgnoreCase(doctorID) &&
                    s.getDate().equals(date)) {
                System.out.printf("%-15s %-10s%n",
                        s.getTimeslot(), s.getStatus() ? "Yes" : "No");
                found[0] = true;
            }
        });
        if (!found[0]) {
            System.out.println("No schedules found for this date.");
        }
    }
}
