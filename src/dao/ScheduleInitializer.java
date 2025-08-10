package dao;

import adt.CustomADT;
import entity.Schedule;

public class ScheduleInitializer {
    public static void main(String[] args) {
        CustomADT<String, Schedule> scheduleMap = new CustomADT<>();

        scheduleMap.put("DC001_2025-08-15_09:00–10:00",
                new Schedule("S001", "D001", "2025-08-15", "09:00–10:00", true));
        scheduleMap.put("DC001_2025-08-15_10:00–11:00",
                new Schedule("S002", "D001", "2025-08-15", "10:00–11:00", false));
        scheduleMap.put("DC002_2025-08-16_09:00–10:00",
                new Schedule("S003", "D002", "2025-08-16", "09:00–10:00", true));

        ScheduleDAO dao = new ScheduleDAO();
        dao.saveToFile(scheduleMap);
        System.out.println("Schedules initialized and saved.");
    }
}
