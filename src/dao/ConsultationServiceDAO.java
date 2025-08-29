package dao;

import adt.OrderedMap;
import entity.ConsultationService;
import java.io.*;

public class ConsultationServiceDAO {
    private final String fileName = "src/data/service.dat";

    public void saveToFile(OrderedMap<String, ConsultationService> services) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(services);
        } catch (IOException e) {
            // System.out.println("Error saving services to file. ");
            System.out.println("Error saving services to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public OrderedMap<String, ConsultationService> retrieveFromFile() {
        OrderedMap<String, ConsultationService> services = new OrderedMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            services = (OrderedMap<String, ConsultationService>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing service file found or error reading it. ");
        }
        return services;
    }
}
