package control;

import adt.CustomADT;
import dao.ScheduleDAO;
import entity.Schedule;
import utility.IDGenerator;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ScheduleMaintenance {
    private CustomADT<String, Schedule> scheduleList;
    private final ScheduleDAO scheduleDAO;

    public ScheduleMaintenance() {
        IDGenerator.loadCounter("counter.dat");

        this.scheduleDAO = new ScheduleDAO();
        this.scheduleList = scheduleDAO.retrieveFromFile();

        String highestID = "S000";
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule schedule = scheduleList.get(i);
            String id = schedule.getScheduleID();
            if (id != null && id.compareTo(highestID) > 0) {
                highestID = id;
            }
        }
        IDGenerator.updateScheduleCounterFromHighestID(highestID);
        IDGenerator.saveCounters("counter.dat");
    }

    // Show calendar for selected year and month, marking * for scheduled days
    public void displayUnifiedCalendar(int year, int month) {
        System.out.printf("\n=== Unified Schedule Calendar for %d-%02d ===\n", year, month);
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int monthLength = YearMonth.of(year, month).lengthOfMonth();
        int dayOfWeek = firstDay.getDayOfWeek().getValue(); // 1=Mon
        for (int i = 1; i < dayOfWeek; i++) {
            System.out.print("    ");
        }
        for (int day = 1; day <= monthLength; day++) {
            String date = String.format("%04d-%02d-%02d", year, month, day);
            boolean hasSchedule = isScheduledDate(date);
            System.out.printf("%2d%s ", day, hasSchedule ? "*" : " ");
            if ((day + dayOfWeek - 1) % 7 == 0) {
                System.out.println();
            }
        }
        System.out.println("\n* = At least one schedule record exists");
    }

    public boolean isScheduledDate(String date) {
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule s = scheduleList.get(i);
            if (s.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    // List all schedules
    public CustomADT<Integer, Schedule> getAllSchedules() {
        CustomADT<Integer, Schedule> result = new CustomADT<>();
        for (int i = 0; i < scheduleList.size(); i++) {
            result.put(i, scheduleList.get(i));
        }
        return result;
    }

    // By date
    public CustomADT<Integer, Schedule> getSchedulesByDate(String date) {
        CustomADT<Integer, Schedule> result = new CustomADT<>();
        int idx = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule s = scheduleList.get(i);
            if (s.getDate().equals(date)) {
                result.put(idx++, s);
            }
        }
        return result;
    }

    // By doctor
    public CustomADT<Integer, Schedule> getSchedulesByDoctor(String doctorID) {
        CustomADT<Integer, Schedule> result = new CustomADT<>();
        int idx = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule s = scheduleList.get(i);
            if (s.getDoctorID().equalsIgnoreCase(doctorID)) {
                result.put(idx++, s);
            }
        }
        return result;
    }
    public CustomADT<Integer, String> getDoctorIDsByDate(String date) {
        CustomADT<Integer, String> doctorIDs = new CustomADT<>();
        int idx = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule s = scheduleList.get(i);
            if (s.getDate().equals(date) && !containsIgnoreCase(doctorIDs, s.getDoctorID())) {
                doctorIDs.put(idx++, s.getDoctorID());
            }
        }
        return doctorIDs;
    }

    private boolean containsIgnoreCase(CustomADT<Integer, String> adt, String value) {
        for (int i = 0; i < adt.size(); i++) {
            if (adt.get(i).equalsIgnoreCase(value)) return true;
        }
        return false;
    }
    // By date and doctor
    public CustomADT<Integer, Schedule> getSchedulesByDateAndDoctor(String date, String doctorID) {
        CustomADT<Integer, Schedule> result = new CustomADT<>();
        int idx = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule s = scheduleList.get(i);
            if (s.getDate().equals(date) && s.getDoctorID().equalsIgnoreCase(doctorID)) {
                result.put(idx++, s);
            }
        }
        return result;
    }

    // By status
    public CustomADT<Integer, Schedule> getSchedulesByStatus(String statusStr) {
        boolean status = statusStr.equalsIgnoreCase("true");
        CustomADT<Integer, Schedule> result = new CustomADT<>();
        int idx = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule s = scheduleList.get(i);
            if (s.getStatus() == status) {
                result.put(idx++, s);
            }
        }
        return result;
    }

    // By status and date
    public CustomADT<Integer, Schedule> getSchedulesByStatusAndDate(String statusStr, String date) {
        boolean status = statusStr.equalsIgnoreCase("true");
        CustomADT<Integer, Schedule> result = new CustomADT<>();
        int idx = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule s = scheduleList.get(i);
            if (s.getStatus() == status && s.getDate().equals(date)) {
                result.put(idx++, s);
            }
        }
        return result;
    }

    // Sorting: 1-Date, 2-Doctor, 3-TimeSlot, 4-Status
    public CustomADT<Integer, Schedule> sortSchedules(CustomADT<Integer, Schedule> schedules, int sortOption) {
        if (sortOption == 0 || schedules.size() <= 1) return schedules;
        return mergeSort(schedules, sortOption);
    }

    private CustomADT<Integer, Schedule> mergeSort(CustomADT<Integer, Schedule> schedules, int sortOption) {
        int n = schedules.size();
        if (n <= 1) return schedules;
        int mid = n / 2;
        CustomADT<Integer, Schedule> left = new CustomADT<>();
        CustomADT<Integer, Schedule> right = new CustomADT<>();
        for (int i = 0; i < mid; i++) left.put(i, schedules.get(i));
        for (int i = mid; i < n; i++) right.put(i - mid, schedules.get(i));
        left = mergeSort(left, sortOption);
        right = mergeSort(right, sortOption);
        return merge(left, right, sortOption);
    }

    private CustomADT<Integer, Schedule> merge(CustomADT<Integer, Schedule> left, CustomADT<Integer, Schedule> right, int sortOption) {
        CustomADT<Integer, Schedule> result = new CustomADT<>();
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()) {
            Schedule s1 = left.get(i);
            Schedule s2 = right.get(j);
            boolean chooseLeft = false;
            switch (sortOption) {
                case 1: // Date
                    chooseLeft = s1.getDate().compareTo(s2.getDate()) <= 0;
                    break;
                case 2: // Doctor
                    chooseLeft = s1.getDoctorID().compareTo(s2.getDoctorID()) <= 0;
                    break;
                case 3: // Time Slot
                    chooseLeft = s1.getTimeslot().compareTo(s2.getTimeslot()) <= 0;
                    break;
                case 4: // Status
                    chooseLeft = (s1.getStatus() && !s2.getStatus()) || (s1.getStatus() == s2.getStatus());
                    break;
                default:
                    chooseLeft = true;
            }
            result.put(k++, chooseLeft ? left.get(i++) : right.get(j++));
        }
        while (i < left.size()) result.put(k++, left.get(i++));
        while (j < right.size()) result.put(k++, right.get(j++));
        return result;
    }
// In ScheduleMaintenance.java

    // Show calendar for the schedules of a specific doctor (with * marking scheduled days)
    public void displayDoctorCalendar(String doctorID) {
        System.out.println("\n=== Doctor " + doctorID + " Schedule Calendar ===");
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int monthLength = firstDay.lengthOfMonth();
        int dayOfWeek = firstDay.getDayOfWeek().getValue();
        for (int i = 1; i < dayOfWeek; i++) System.out.print("    ");
        for (int day = 1; day <= monthLength; day++) {
            String date = String.format("%04d-%02d-%02d", year, month, day);
            boolean hasSchedule = false;
            for (int i = 0; i < scheduleList.size(); i++) {
                Schedule s = scheduleList.get(i);
                if (s.getDoctorID().equalsIgnoreCase(doctorID) && s.getDate().equals(date)) {
                    hasSchedule = true;
                    break;
                }
            }
            System.out.printf("%2d%s ", day, hasSchedule ? "*" : " ");
            if ((day + dayOfWeek - 1) % 7 == 0) System.out.println();
        }
        System.out.println("\n* = schedule exists for this doctor");
    }

    // Show calendar for the schedules of a specific status (with * marking scheduled days)
    public void displayStatusCalendar(String statusStr) {
        boolean status = statusStr.equalsIgnoreCase("true");
        System.out.println("\n=== Status " + statusStr + " Schedule Calendar ===");
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int monthLength = firstDay.lengthOfMonth();
        int dayOfWeek = firstDay.getDayOfWeek().getValue();
        for (int i = 1; i < dayOfWeek; i++) System.out.print("    ");
        for (int day = 1; day <= monthLength; day++) {
            String date = String.format("%04d-%02d-%02d", year, month, day);
            boolean hasSchedule = false;
            for (int i = 0; i < scheduleList.size(); i++) {
                Schedule s = scheduleList.get(i);
                if (s.getStatus() == status && s.getDate().equals(date)) {
                    hasSchedule = true;
                    break;
                }
            }
            System.out.printf("%2d%s ", day, hasSchedule ? "*" : " ");
            if ((day + dayOfWeek - 1) % 7 == 0) System.out.println();
        }
        System.out.println("\n* = schedule with status " + statusStr + " exists");
    }

    // Assign schedule(s)
    public boolean assignSchedule(String doctorID, String date, String timeSlotInput, boolean available) {
        CustomADT<Integer, String> validSlots = parseAndValidateTimeSlots(timeSlotInput);
        if (validSlots == null || validSlots.size() == 0)
            return false;
        boolean allAssigned = true;
        for (int i = 0; i < validSlots.size(); i++) {
            String timeslot = validSlots.get(i);
            Schedule schedule = new Schedule(null, doctorID, date, timeslot, available);
            if (!assignSingleSchedule(schedule)) {
                allAssigned = false;
            }
        }
        return allAssigned;
    }

    private boolean assignSingleSchedule(Schedule schedule) {
        String scheduleID = IDGenerator.generateScheduleID();
        schedule.setScheduleID(scheduleID);
        String key = generateKey(schedule.getDoctorID(), schedule.getDate(), schedule.getTimeslot());
        if (scheduleList.containsKey(key)) {
            return false;
        }
        scheduleList.put(key, schedule);
        scheduleDAO.saveToFile(scheduleList);
        IDGenerator.saveCounters("counter.dat");
        return true;
    }

    public Schedule getSchedule(String doctorID, String date, String timeSlot) {
        String key = generateKey(doctorID, date, timeSlot);
        return scheduleList.get(key);
    }

    public CustomADT<Integer, String> getTimeSlotsForDate(String doctorID, String date) {
        CustomADT<Integer, String> slots = new CustomADT<>();
        int idx = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule s = scheduleList.get(i);
            if (s.getDoctorID().equalsIgnoreCase(doctorID) && s.getDate().equals(date)) {
                slots.put(idx++, s.getTimeslot());
            }
        }
        return slots;
    }

    // Update availability (for leave): only if available
    public boolean markScheduleAsLeave(String doctorID, String date, String timeSlot) {
        Schedule schedule = getSchedule(doctorID, date, timeSlot);
        if (schedule != null && schedule.getStatus()) { // only if available
            schedule.setStatus(false); // Mark as leave/unavailable
            scheduleDAO.saveToFile(scheduleList);
            IDGenerator.saveCounters("counter.dat");
            return true;
        }
        System.out.println("Cannot mark as leave: Slot is already unavailable or does not exist.");
        return false;
    }

    // Remove slot: only if available
    public boolean removeSchedule(String doctorID, String date, String timeSlot) {
        String key = generateKey(doctorID, date, timeSlot);
        Schedule schedule = scheduleList.get(key);
        if (schedule != null && schedule.getStatus()) { // only if available
            scheduleList.remove(key);
            scheduleDAO.saveToFile(scheduleList);
            IDGenerator.saveCounters("counter.dat");
            return true;
        }
        System.out.println("Cannot remove: Slot is not available or does not exist.");
        return false;
    }

    // Slot parsing and validation
    public CustomADT<Integer, String> parseAndValidateTimeSlots(String input) {
        CustomADT<Integer, String> slots = new CustomADT<>();
        input = input.trim();

        // Format 1: HH:mm–HH:mm (en dash)
        String pattern1 = "^\\d{2}:\\d{2}–\\d{2}:\\d{2}$";
        if (input.matches(pattern1)) {
            String[] parts = input.split("–");
            LocalTime start = parseLocalTime(parts[0]);
            LocalTime end = parseLocalTime(parts[1]);
            if (start != null && end != null && isOneHour(start, end)) {
                slots.put(0, formatSlot(start, end));
                return slots;
            } else {
                return null;
            }
        }

        // Format 2: HH-HH (e.g. 10-12)
        String pattern2 = "^\\d{2}-\\d{2}$";
        if (input.matches(pattern2)) {
            int startHour = Integer.parseInt(input.substring(0,2));
            int endHour = Integer.parseInt(input.substring(3,5));
            if (startHour < endHour) {
                int idx = 0;
                for (int h = startHour; h < endHour; h++) {
                    LocalTime start = LocalTime.of(h, 0);
                    LocalTime end = LocalTime.of(h+1, 0);
                    slots.put(idx++, formatSlot(start, end));
                }
                return slots;
            }
            return null;
        }

        // Format 3: HHmm-HHmm (e.g. 1230-1330)
        String pattern3 = "^\\d{4}-\\d{4}$";
        if (input.matches(pattern3)) {
            LocalTime start = parseLocalTime(input.substring(0,4));
            LocalTime end = parseLocalTime(input.substring(5,9));
            if (start != null && end != null) {
                int minutes = (int) java.time.Duration.between(start, end).toMinutes();
                if (minutes % 60 == 0 && minutes > 0) {
                    int slotsCount = minutes / 60;
                    int idx = 0;
                    for (int i = 0; i < slotsCount; i++) {
                        LocalTime s = start.plusHours(i);
                        LocalTime e = s.plusHours(1);
                        if (e.isAfter(end)) break;
                        slots.put(idx++, formatSlot(s, e));
                    }
                    return slots;
                }
            }
            return null;
        }

        // If none matched, try to split with a space or an en dash
        String[] tokens = input.split("–|-| ");
        if (tokens.length == 2) {
            LocalTime start = parseLocalTime(tokens[0]);
            LocalTime end = parseLocalTime(tokens[1]);
            if (start != null && end != null) {
                int minutes = (int) java.time.Duration.between(start, end).toMinutes();
                if (minutes % 60 == 0 && minutes > 0) {
                    int slotsCount = minutes / 60;
                    int idx = 0;
                    for (int i = 0; i < slotsCount; i++) {
                        LocalTime s = start.plusHours(i);
                        LocalTime e = s.plusHours(1);
                        if (e.isAfter(end)) break;
                        slots.put(idx++, formatSlot(s, e));
                    }
                    return slots;
                }
            }
        }
        return null;
    }

    private LocalTime parseLocalTime(String t) {
        try {
            if (t.length() == 5 && t.contains(":")) {
                return LocalTime.parse(t, DateTimeFormatter.ofPattern("HH:mm"));
            } else if (t.length() == 4) {
                return LocalTime.of(Integer.parseInt(t.substring(0,2)), Integer.parseInt(t.substring(2,4)));
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    private boolean isOneHour(LocalTime start, LocalTime end) {
        return java.time.Duration.between(start, end).toMinutes() == 60;
    }

    private String formatSlot(LocalTime start, LocalTime end) {
        return start.format(DateTimeFormatter.ofPattern("HH:mm")) + "–" +
                end.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String generateKey(String doctorID, String date, String timeSlot) {
        return doctorID + "_" + date + "_" + timeSlot;
    }
    public CustomADT<String, Integer> getScheduleCountPerDoctor() {
        CustomADT<Integer, Schedule> allSchedules = getAllSchedules();
        CustomADT<String, Integer> countPerDoctor = new CustomADT<>();
        for (int i = 0; i < allSchedules.size(); i++) {
            Schedule s = allSchedules.get(i);
            String docId = s.getDoctorID();
            Integer count = countPerDoctor.get(docId);
            countPerDoctor.put(docId, count == null ? 1 : count + 1);
        }
        return countPerDoctor;
    }

    public CustomADT<String, Integer> getScheduleCountByStatus() {
        CustomADT<Integer, Schedule> allSchedules = getAllSchedules();
        int available = 0, leave = 0;
        for (int i = 0; i < allSchedules.size(); i++) {
            Schedule s = allSchedules.get(i);
            if (s.getStatus()) available++;
            else leave++;
        }
        CustomADT<String, Integer> statusMap = new CustomADT<>();
        statusMap.put("Available", available);
        statusMap.put("Leave", leave);
        return statusMap;
    }
}