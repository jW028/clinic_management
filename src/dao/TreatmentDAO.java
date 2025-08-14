package dao;

import entity.Treatment;
import adt.CustomADT;

public class TreatmentDAO {
    private static final String FILE_NAME = "treatments.dat";

    public static void saveTreatments(CustomADT<String, Treatment> treatments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(treatments);
        } catch (IOException e) {
            System.out.println("Error saving treatments to file: " + e.getMessage());
        }
    }

    public static CustomADT<String, Treatment> loadTreatments() {
        CustomADT<String, Treatment> treatments = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            treatments = (CustomADT<String, Treatment>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing treatment file found or error reading it.");
        }
        return treatments;
    }
}