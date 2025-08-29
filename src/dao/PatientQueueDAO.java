package dao;

import adt.CustomADT;
import entity.Patient;

import java.io.*;

public class PatientQueueDAO {
    private String fileName;
    private final String fileName1 = "src/data/patientEmergencyQueue.dat";
    private final String fileName2 = "src/data/patientNormalQueue.dat";

    public PatientQueueDAO(int type) {
        if (type == 1) {
            this.fileName = fileName1;
        } else if (type == 2) {
            this.fileName = fileName2;
        } else {
            throw new IllegalArgumentException("Invalid prescription type.");
        }
    }

    public void saveToFile(CustomADT<String, Patient> patients) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))){
            oos.writeObject(patients);
        } catch (IOException e){
            System.out.println("Error saving patient queue to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public CustomADT<String, Patient> retrieveFromFile() {
        CustomADT<String, Patient> patients = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            patients = (CustomADT<String, Patient>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error retrieving patient queue from file: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }
}
