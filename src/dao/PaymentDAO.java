package dao;

import adt.CustomADT;
import adt.CustomADTInterface;
import entity.Payment;

import java.io.*;

public class PaymentDAO {
    private final String fileName = "payments.dat";

    public void saveToFile(CustomADTInterface<String, Payment> payments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(payments);
        } catch (IOException e) {
            System.out.println("Error saving payments to file: " + e.getMessage());
        }
    }

    public CustomADTInterface<String, Payment> retrieveFromFile() {
        CustomADTInterface<String, Payment> payments = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            payments = (CustomADTInterface<String, Payment>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing payment file found or error reading it. ");
        }
        return payments;
    }
}