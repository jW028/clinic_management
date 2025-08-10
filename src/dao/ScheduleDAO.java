package dao;

import adt.CustomADT;
import entity.Schedule;

import java.io.*;

public class ScheduleDAO {
    private static final String FILE_NAME = "schedules.txt";

    /**
     * Save all schedules to a text file.
     * Format: scheduleID;doctorID;date;timeslot;status
     */
    public void saveSchedules(CustomADT<String, Schedule> schedules) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            schedules.forEach(schedule -> {
                writer.println(schedule.getScheduleID() + ";" +
                        schedule.getDoctorID() + ";" +
                        schedule.getDate() + ";" +
                        schedule.getTimeslot() + ";" +
                        schedule.getStatus());
            });
        } catch (IOException e) {
            System.out.println("❌ Error saving schedules: " + e.getMessage());
        }
    }

    /**
     * Load schedules from the text file.
     * Returns empty CustomADT if file not found or error occurs.
     */
    public CustomADT<String, Schedule> loadSchedules() {
        CustomADT<String, Schedule> schedules = new CustomADT<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) return schedules;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    String scheduleID = parts[0];
                    String doctorID = parts[1];
                    String date = parts[2];
                    String timeslot = parts[3];
                    boolean status = Boolean.parseBoolean(parts[4]);

                    Schedule schedule = new Schedule(scheduleID, doctorID, date, timeslot, status);

                    // Use your key convention: doctorID_date_timeslot
                    String key = doctorID + "_" + date + "_" + timeslot;
                    schedules.put(key, schedule);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error loading schedules: " + e.getMessage());
        }

        return schedules;
    }
}
