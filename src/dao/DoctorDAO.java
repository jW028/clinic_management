package dao;

import adt.OrderedMap;
import entity.Doctor;
import java.io.*;

public class DoctorDAO {
    private static final String FILE_NAME = "src/data/doctors.dat";

    public static void saveDoctors(OrderedMap<String, Doctor> doctors) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(doctors);
        } catch (IOException e) {
            System.out.println("Error saving doctors to file: " + e.getMessage());
        }
    }

    public static OrderedMap<String, Doctor> loadDoctors() {
        OrderedMap<String, Doctor> doctors = new OrderedMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            doctors = (OrderedMap<String, Doctor>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing doctor file found or error reading it.");
        }
        return doctors;
    }

}