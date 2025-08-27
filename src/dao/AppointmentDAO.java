package dao;

import adt.CustomADT;
import entity.Appointment;
import java.io.*;

public class AppointmentDAO {
    private final String fileName = "src/data/appointment.dat";

    public void saveToFile(CustomADT<String, Appointment> appointments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(appointments);
        } catch (IOException e) {
            System.out.println("Error saving appointments to file. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public CustomADT<String, Appointment> retrieveFromFile() {
        CustomADT<String, Appointment> appointments = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            appointments = (CustomADT<String, Appointment>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing appointment file found or error reading it. ");
        }
        return appointments;
    }
}
