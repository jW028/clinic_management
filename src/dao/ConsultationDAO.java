package dao;

import adt.CustomADT;
import entity.Consultation;

import java.io.*;

public class ConsultationDAO {
    private final String fileName = "consultation.dat";

    public void saveToFile(CustomADT<String, Consultation> consultations) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(consultations);
        } catch (IOException e) {
            System.out.println("Error saving consultations to file. ");
        }
    }

    public CustomADT<String, Consultation> retrieveFromFile() {
        CustomADT<String, Consultation> consultations = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            consultations = (CustomADT<String, Consultation>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing consultaiton file found or error reading it. ");
        }
        return consultations;
    }
}
