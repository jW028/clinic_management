package dao;

import adt.CustomADT;
import entity.Schedule;

import java.io.*;

public class ScheduleDAO {
    private final String fileName = "schedules.dat";

    public void saveToFile(CustomADT<String, Schedule> schedules) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(schedules);
        } catch (IOException e) {
            System.out.println("Error saving schedules to file. ");
        }
    }

    public CustomADT<String, Schedule> retrieveFromFile() {
        CustomADT<String, Schedule> schedules = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            schedules = (CustomADT<String, Schedule>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing schedule file found or error reading it. ");
        }
        return schedules;
    }
}