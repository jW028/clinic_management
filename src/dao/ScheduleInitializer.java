package dao;

import adt.CustomADT;
import entity.Schedule;

public class ScheduleInitializer {
    public static void main(String[] args) {
        CustomADT<String, Schedule> scheduleMap = new CustomADT<>();

        scheduleMap.put("DC001_2025-08-15_09:00–10:00",
                new Schedule("S001", "DC001", "2025-08-15", "09:00–10:00", true));
        scheduleMap.put("DC001_2025-08-15_10:00–11:00",
                new Schedule("S002", "DC001", "2025-08-15", "10:00–11:00", false));
        scheduleMap.put("DC002_2025-08-16_09:00–10:00",
                new Schedule("S003", "DC002", "2025-08-16", "09:00–10:00", true));

        // For testing purpose
        //scheduleMap.put("DC003_2025-08-15_10:00–11:00",
        //        new Schedule("S002", "DC003", "2025-08-15", "10:00–11:00", true));
        //scheduleMap.put("DC004_2025-08-16_09:00–10:00",
        //        new Schedule("S003", "DC004", "2025-08-16", "09:00–10:00", true));

        ScheduleDAO dao = new ScheduleDAO();
        dao.saveToFile(scheduleMap);
        System.out.println("Schedules initialized and saved.");
    }
}
