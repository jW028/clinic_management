package dao;

import adt.CustomADT;
import entity.VisitHistory;
import java.io.*;

public class VisitHistoryDAO {
    private static final String FILE_NAME = "src/data/visithistory.dat";

    public void saveToFile(CustomADT<String, VisitHistory> visitHistoryMap) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(visitHistoryMap);
        } catch (IOException e) {
            System.err.println("Error saving visit history data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public CustomADT<String, VisitHistory> retrieveFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (CustomADT<String, VisitHistory>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Visit history file not found. Starting with empty data.");
            return new CustomADT<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading visit history data: " + e.getMessage());
            return new CustomADT<>();
        }
    }
}