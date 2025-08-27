package dao;

import adt.CustomADT;
import entity.Payment;
import java.io.*;

public class PaymentDAO {
    private final String fileName = "src/data/payments.dat";

    public void saveToFile(CustomADT<String, Payment> payments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(payments);
        } catch (IOException e) {
            System.out.println("Error saving payments to file: " + e.getMessage());
        }
    }

    public CustomADT<String, Payment> retrieveFromFile() {
        CustomADT<String, Payment> payments = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            payments = (CustomADT<String, Payment>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing payment file found or error reading it. ");
        }
        return payments;
    }
}