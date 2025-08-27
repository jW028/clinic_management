package dao;

import adt.CustomADT;
import entity.Treatment;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class TreatmentDAO {
    private static final String DATA_DIRECTORY = "src/data/";
    private static final String TREATMENTS_FILE = DATA_DIRECTORY + "treatments.dat";
    private static final String RECENT_TREATMENTS_FILE = DATA_DIRECTORY + "recent_treatments.dat";

    private static final Logger logger = Logger.getLogger(TreatmentDAO.class.getName());

    public TreatmentDAO() {
        createDataDirectories();
    }

    private void createDataDirectories() {
        try {
            Files.createDirectories(Paths.get(DATA_DIRECTORY));
        } catch (IOException e) {
            System.out.println("Error creating data directories: " + e.getMessage());
        }
    }

    public boolean saveToFile(CustomADT<String, Treatment> treatments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TREATMENTS_FILE))) {
            oos.writeObject(treatments);
            System.out.println("Successfully saved treatments to file.");
            return true;
        } catch (IOException e) {
            System.out.println("Error saving treatments to file: " + e.getMessage());
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public CustomADT<String, Treatment> retrieveFromFile() {
        CustomADT<String, Treatment> treatments = new CustomADT<>();
        File file = new File (TREATMENTS_FILE); 

        if (!file.exists()) {
            System.out.println("Treatments file not found, returning empty collection");
            return treatments;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(TREATMENTS_FILE))) {
            treatments = (CustomADT<String, Treatment>) ois.readObject();
            System.out.println("Treatments loaded from " + TREATMENTS_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error retrieving treatments from file: " + e.getMessage());
        }
        return treatments;
    }

    public boolean saveRecentActivities(CustomADT<String, String> recentActivities) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(RECENT_TREATMENTS_FILE))) {
            oos.writeObject(recentActivities);
            System.out.println("Recent activities saved to " + RECENT_TREATMENTS_FILE);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving recent activities to file: " + e.getMessage());
            return false;
        }
    }

     @SuppressWarnings("unchecked")
    public CustomADT<String, String> retrieveRecentActivities() {
        CustomADT<String, String> recentActivities = new CustomADT<>();
        File file = new File(RECENT_TREATMENTS_FILE);
        
        if (!file.exists()) {
            System.out.println("Recent activities file not found, returning empty collection");
            return recentActivities;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(RECENT_TREATMENTS_FILE))) {
            recentActivities = (CustomADT<String, String>) ois.readObject();
            System.out.println("Recent activities loaded from " + RECENT_TREATMENTS_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error retrieving recent activities from file: " + e.getMessage());
        }
        return recentActivities;
    }

}


