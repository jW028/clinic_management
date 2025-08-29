package dao;

import adt.OrderedMap;
import entity.Schedule;
import java.io.*;

public class ScheduleDAO {
    private final String fileName;

    public ScheduleDAO() {
        this.fileName = "src/data/schedules.dat";
    }

    public void saveToFile(OrderedMap<String, Schedule> schedules) {
        try {
            // Ensure the directory exists
            File file = new File(fileName);
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
                oos.writeObject(schedules);
                System.out.println("Schedules saved successfully to: " + fileName);
            }
        } catch (IOException e) {
            System.out.println("Error saving schedules to file: " + e.getMessage());
        }
    }

    public OrderedMap<String, Schedule> retrieveFromFile() {
        OrderedMap<String, Schedule> schedules = new OrderedMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            @SuppressWarnings("unchecked")
            OrderedMap<String, Schedule> loadedSchedules = (OrderedMap<String, Schedule>) ois.readObject();
            schedules = loadedSchedules;
            System.out.println("Schedules loaded successfully from: " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing schedule file found at: " + fileName);
            System.out.println("Error details: " + e.getMessage());
        }
        return schedules;
    }
}