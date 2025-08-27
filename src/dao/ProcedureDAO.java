package dao;

import adt.CustomADT;
import entity.Procedure;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProcedureDAO {
    private static final String DATA_DIRECTORY = "src/data/";
    private static final String PROCEDURES_FILE = DATA_DIRECTORY + "procedures.dat";

    public ProcedureDAO() {
        createDataDirectories();
    }

    private void createDataDirectories() {
        try {
            Files.createDirectories(Paths.get(DATA_DIRECTORY));
        } catch (IOException e) {
            System.out.println("Error creating data directories: " + e.getMessage());
        }
    }

    /**
     * Save procedures to file
     * @param procedures CustomADT of procedures to save
     * @return true if successful, false otherwise
     */
    public boolean saveToFile(CustomADT<String, Procedure> procedures) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PROCEDURES_FILE))) {
            oos.writeObject(procedures);
            System.out.println("Successfully saved " + procedures.size() + " procedures to " + PROCEDURES_FILE);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving procedures to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieve procedures from file
     * @return CustomADT of procedures, empty if file doesn't exist or error occurs
     */
    @SuppressWarnings("unchecked")
    public CustomADT<String, Procedure> retrieveFromFile() {
        CustomADT<String, Procedure> procedures = new CustomADT<>();
        File file = new File(PROCEDURES_FILE);

        if (!file.exists()) {
            System.out.println("Procedures file not found, returning empty collection");
            return procedures;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PROCEDURES_FILE))) {
            procedures = (CustomADT<String, Procedure>) ois.readObject();
            System.out.println("Successfully loaded " + procedures.size() + " procedures from " + PROCEDURES_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error retrieving procedures from file: " + e.getMessage());
        }
        return procedures;
    }

    /**
     * Check if procedures file exists and has data
     * @return true if file exists and is not empty
     */
    public boolean proceduresFileExists() {
        File file = new File(PROCEDURES_FILE);
        return file.exists() && file.length() > 0;
    }

    /**
     * Get the file path for procedures
     * @return the file path as a String
     */
    public String getFilePath() {
        return PROCEDURES_FILE;
    }
}
