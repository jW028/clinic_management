package dao;

import java.io.*;
import adt.CustomADT;
import entity.Diagnosis;

public class DiagnosisDAO {
    private static final String DATA_DIR = "src/data/";
    private static final String FILE_NAME = DATA_DIR + "diagnosis.dat";

    public void saveDiagnosis(CustomADT<String, Diagnosis> diagnoses) {
        try {
            // Ensure directory exists
            File directory = new File(DATA_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                oos.writeObject(diagnoses);
                System.out.println("✅ Diagnosis data saved successfully to " + FILE_NAME);
            }
        } catch (IOException e) {
            System.err.println("❌ Error saving diagnoses to file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public CustomADT<String, Diagnosis> retrieveFromFile() {
        CustomADT<String, Diagnosis> diagnoses = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            diagnoses = (CustomADT<String, Diagnosis>) ois.readObject();
            System.out.println("✅ Diagnosis data loaded successfully from " + FILE_NAME);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("ℹ️ No existing diagnosis file found or error reading it. Starting with empty data.");
        }
        return diagnoses;
    }
}
