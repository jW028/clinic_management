package dao;

import adt.CustomADT;
import entity.Transaction;
import java.io.*;

public class TransactionDAO {
    private final String fileName = "transactions.dat";

    public void saveToFile(CustomADT<String, Transaction> transactions) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))){
            oos.writeObject(transactions);
        } catch (IOException e){
            System.out.println("Error saving transactions to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public CustomADT<String, Transaction> retrieveFromFile() {
        CustomADT<String, Transaction> transactions = new CustomADT<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            transactions = (CustomADT<String, Transaction>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error retrieving transactions from file: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }
}


