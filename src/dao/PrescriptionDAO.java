package dao;

import adt.CustomADT;
import entity.Prescription;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PrescriptionDAO {
    private String fileName;
    private final String fileName1 = "src/data/pending_prescriptions.dat";
    private final String fileName2 = "src/data/processed_prescriptions.dat";

    public PrescriptionDAO(int type) {
        if (type == 1) {
            this.fileName = fileName1;
        } else if (type == 2) {
            this.fileName = fileName2;
        } else {
            throw new IllegalArgumentException("Invalid prescription type.");
        }
    }

    public void saveToFile(CustomADT<String, Prescription> prescriptions) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))){
            oos.writeObject(prescriptions);
        } catch (IOException e){
            System.out.println("Error saving prescriptions to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public CustomADT<String, Prescription> retrieveFromFile() {
        CustomADT<String, Prescription> prescriptions = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            prescriptions = (CustomADT<String, Prescription>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error retrieving prescriptions from file: " + e.getMessage());
            e.printStackTrace();
        }
        return prescriptions;
    }
}
