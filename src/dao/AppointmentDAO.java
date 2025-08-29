package dao;

import adt.OrderedMap;
import entity.Appointment;
import java.io.*;

public class AppointmentDAO {
    private final String fileName = "src/data/appointment.dat";

    public void saveToFile(OrderedMap<String, Appointment> appointments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(appointments);
        } catch (IOException e) {
            System.out.println("Error saving appointments to file. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public OrderedMap<String, Appointment> retrieveFromFile() {
        OrderedMap<String, Appointment> appointments = new OrderedMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            appointments = (OrderedMap<String, Appointment>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing appointment file found or error reading it. ");
        }
        return appointments;
    }
}
