package dao;

import adt.CustomADT;
import entity.Schedule;

public class ScheduleInitializer {
    public static void main(String[] args) {
        CustomADT<String, Schedule> scheduleMap = new CustomADT<>();

        // Add more comprehensive schedule data for all doctors
        // Today and future dates with available slots
        String today = java.time.LocalDate.now().toString(); // 2025-08-26
        String tomorrow = java.time.LocalDate.now().plusDays(1).toString(); // 2025-08-27
        String dayAfter = java.time.LocalDate.now().plusDays(2).toString(); // 2025-08-28

        // Dr. Alice Tan (DC001) - Cardiology
        scheduleMap.put("DC001_" + today + "_09:00–10:00",
                new Schedule("S001", "DC001", today, "09:00–10:00", true));
        scheduleMap.put("DC001_" + today + "_10:00–11:00",
                new Schedule("S002", "DC001", today, "10:00–11:00", true));
        scheduleMap.put("DC001_" + today + "_14:00–15:00",
                new Schedule("S003", "DC001", today, "14:00–15:00", true));
        scheduleMap.put("DC001_" + tomorrow + "_09:00–10:00",
                new Schedule("S004", "DC001", tomorrow, "09:00–10:00", true));
        scheduleMap.put("DC001_" + tomorrow + "_10:00–11:00",
                new Schedule("S005", "DC001", tomorrow, "10:00–11:00", true));

        // Dr. John Lim (DC002) - Neurology
        scheduleMap.put("DC002_" + today + "_09:00–10:00",
                new Schedule("S006", "DC002", today, "09:00–10:00", true));
        scheduleMap.put("DC002_" + today + "_11:00–12:00",
                new Schedule("S007", "DC002", today, "11:00–12:00", true));
        scheduleMap.put("DC002_" + tomorrow + "_14:00–15:00",
                new Schedule("S008", "DC002", tomorrow, "14:00–15:00", true));
        scheduleMap.put("DC002_" + dayAfter + "_09:00–10:00",
                new Schedule("S009", "DC002", dayAfter, "09:00–10:00", true));

        // Dr. Sarah Lee (DC003) - Pediatrics
        scheduleMap.put("DC003_" + today + "_08:00–09:00",
                new Schedule("S010", "DC003", today, "08:00–09:00", true));
        scheduleMap.put("DC003_" + today + "_15:00–16:00",
                new Schedule("S011", "DC003", today, "15:00–16:00", true));
        scheduleMap.put("DC003_" + tomorrow + "_08:00–09:00",
                new Schedule("S012", "DC003", tomorrow, "08:00–09:00", true));

        // Dr. Ahmad Farid (DC004) - Orthopedics
        scheduleMap.put("DC004_" + today + "_13:00–14:00",
                new Schedule("S013", "DC004", today, "13:00–14:00", true));
        scheduleMap.put("DC004_" + tomorrow + "_13:00–14:00",
                new Schedule("S014", "DC004", tomorrow, "13:00–14:00", true));
        scheduleMap.put("DC004_" + dayAfter + "_10:00–11:00",
                new Schedule("S015", "DC004", dayAfter, "10:00–11:00", true));

        // Dr. Emily Wong (DC005) - Dermatology
        scheduleMap.put("DC005_" + today + "_16:00–17:00",
                new Schedule("S016", "DC005", today, "16:00–17:00", true));
        scheduleMap.put("DC005_" + tomorrow + "_16:00–17:00",
                new Schedule("S017", "DC005", tomorrow, "16:00–17:00", true));

        // Legacy schedules (can remove these)
        // scheduleMap.put("DC001_2025-08-15_09:00–10:00",
        //         new Schedule("S001", "DC001", "2025-08-15", "09:00–10:00", true));
        // scheduleMap.put("DC001_2025-08-15_10:00–11:00",
        //         new Schedule("S002", "DC001", "2025-08-15", "10:00–11:00", false));
        // scheduleMap.put("DC002_2025-08-16_09:00–10:00",
        //         new Schedule("S003", "DC002", "2025-08-16", "09:00–10:00", true));

        ScheduleDAO dao = new ScheduleDAO();
        dao.saveToFile(scheduleMap);
        System.out.println("Schedules initialized and saved.");
    }
}
