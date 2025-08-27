package dao;

import adt.CustomADT;
import entity.ConsultationService;
import java.io.*;

public class ConsultationServiceDAO {
    private final String fileName = "src/data/service.dat";

    public void saveToFile(CustomADT<String, ConsultationService> services) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(services);
        } catch (IOException e) {
            // System.out.println("Error saving services to file. ");
            System.out.println("Error saving services to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public CustomADT<String, ConsultationService> retrieveFromFile() {
        CustomADT<String, ConsultationService> services = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            services = (CustomADT<String, ConsultationService>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing service file found or error reading it. ");
        }
        return services;
    }
}
