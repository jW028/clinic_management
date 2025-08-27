package dao;

import adt.*;
import entity.Patient;
import java.io.*;

public class PatientDAO {
    private final String fileName = "src/data/patients.dat";
    public void saveToFile(CustomADT<String, Patient> patientList) { // 2. Update method signature
        File file = new File(fileName);
        // Use try-with-resources for automatic stream closing
        try (ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file))) {
            ooStream.writeObject(patientList);
        } catch (FileNotFoundException ex) {
            System.out.println("\nFile not found: " + fileName);
        } catch (IOException ex) {
            System.out.println("\nError: Cannot save to file. " + ex.getMessage());
        }
    }

    public CustomADT<String, Patient> retrieveFromFile() { // 3. Update return type
        File file = new File(fileName);
        if (!file.exists()) {
            // Return a new, empty list if the file doesn't exist yet.
            return new CustomADT<>();
        }

        // Use try-with-resources for automatic stream closing
        try (ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file))) {
            // 4. Cast to the correct interface
            return (CustomADT<String, Patient>) oiStream.readObject();
        } catch (FileNotFoundException ex) {
            // This case is handled by the file.exists() check above, but good to have.
            System.out.println("\nFile not found during retrieval: " + fileName);
        } catch (IOException ex) {
            System.out.println("\nError: Cannot read from file. The file might be corrupted. " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println("\nError: The class structure has changed. " + ex.getMessage());
        }
        // Return an empty list if any error occurred.
        return new CustomADT<>();
    }
}

// Should implement CustomADT instead of CustomADT