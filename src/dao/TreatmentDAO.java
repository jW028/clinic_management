package dao;

import adt.CustomADT;
import entity.Medicine;
import entity.Treatment;
import java.io.*;

public class TreatmentDAO {
    private final String fileName = "treatments.dat";

    public void saveToFile(CustomADT<String, Treatment> treatments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(treatments);
        } catch (IOException e) {
            System.out.println("Error saving treatments to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public CustomADT<String, Treatment> retrieveFromFile() {
        CustomADT<String, Treatment> treatments = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            treatments = (CustomADT<String, Treatment>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error retrieving treatments from file: " + e.getMessage());
            e.printStackTrace();
        }
        return treatments;
    }

}


