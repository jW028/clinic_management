package dao;

import adt.OrderedMap;
import entity.Consultation;
import java.io.*;

public class ConsultationDAO {
    private final String fileName = "src/data/consultation.dat";

    public void saveToFile(OrderedMap<String, Consultation> consultations) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(consultations);
        } catch (IOException e) {
            System.out.println("Error saving consultations to file. ");
        }
    }

    public OrderedMap<String, Consultation> retrieveFromFile() {
        OrderedMap<String, Consultation> consultations = new OrderedMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            consultations = (OrderedMap<String, Consultation>) ois.readObject();
            System.out.println("Consultations loaded from file. ");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing consultaiton file found or error reading it. ");
        }
        return consultations;
    }
}
