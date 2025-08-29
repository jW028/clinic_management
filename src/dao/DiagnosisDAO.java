package dao;

import java.io.*;
import adt.OrderedMap;
import entity.Diagnosis;

public class DiagnosisDAO {
    private static final String DATA_DIR = "src/data/";
    private static final String FILE_NAME = DATA_DIR + "diagnosis.dat";

    public void saveDiagnosis(OrderedMap<String, Diagnosis> diagnoses) {
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
    public OrderedMap<String, Diagnosis> retrieveFromFile() {
        OrderedMap<String, Diagnosis> diagnoses = new OrderedMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            diagnoses = (OrderedMap<String, Diagnosis>) ois.readObject();
            System.out.println("✅ Diagnosis data loaded successfully from " + FILE_NAME);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("ℹ️ No existing diagnosis file found or error reading it. Starting with empty data.");
        }
        return diagnoses;
    }
}
