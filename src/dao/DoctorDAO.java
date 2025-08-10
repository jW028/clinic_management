package dao;

import adt.CustomADT;
import entity.Doctor;

import java.io.*;

public class DoctorDAO {
    private static final String FILE_NAME = "doctors.dat";

    public static void saveDoctors(CustomADT<String, Doctor> doctors) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(doctors);
        } catch (IOException e) {
            System.out.println("Error saving doctors to file: " + e.getMessage());
        }
    }

    public static CustomADT<String, Doctor> loadDoctors() {
        CustomADT<String, Doctor> doctors = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            doctors = (CustomADT<String, Doctor>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing doctor file found or error reading it.");
        }
        return doctors;
    }
}