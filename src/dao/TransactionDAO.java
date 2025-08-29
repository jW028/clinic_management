package dao;

import adt.OrderedMap;
import entity.Transaction;
import java.io.*;

public class TransactionDAO {
    private final String fileName = "src/data/transactions.dat";

    public void saveToFile(OrderedMap<String, Transaction> transactions) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))){
            oos.writeObject(transactions);
        } catch (IOException e){
            System.out.println("Error saving transactions to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public OrderedMap<String, Transaction> retrieveFromFile() {
        OrderedMap<String, Transaction> transactions = new OrderedMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            transactions = (OrderedMap<String, Transaction>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error retrieving transactions from file: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }
}


