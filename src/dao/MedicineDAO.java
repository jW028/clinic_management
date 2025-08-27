package dao;

import adt.CustomADT;
import entity.Medicine;
import java.io.*;

public class MedicineDAO {
    private final String fileName = "src/data/medicine.dat";

    public void saveToFile(CustomADT<String, Medicine> medicines) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))){
            oos.writeObject(medicines);
        } catch (IOException e){
            System.out.println("Error saving medicines to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public CustomADT<String, Medicine> retrieveFromFile() {
        CustomADT<String, Medicine> medicines = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            medicines = (CustomADT<String, Medicine>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error retrieving medicines from file: " + e.getMessage());
            e.printStackTrace();
        }
        return medicines;
    }
}
