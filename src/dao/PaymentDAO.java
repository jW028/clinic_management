package dao;

import adt.OrderedMap;
import entity.Payment;
import java.io.*;

public class PaymentDAO {
    private final String fileName = "src/data/payments.dat";

    public void saveToFile(OrderedMap<String, Payment> payments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(payments);
        } catch (IOException e) {
            System.out.println("Error saving payments to file: " + e.getMessage());
        }
    }

    public OrderedMap<String, Payment> retrieveFromFile() {
        OrderedMap<String, Payment> payments = new OrderedMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            payments = (OrderedMap<String, Payment>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing payment file found or error reading it. ");
        }
        return payments;
    }
}