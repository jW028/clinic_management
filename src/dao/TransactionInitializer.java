package dao;

import adt.OrderedMap;
import entity.Transaction;

public class TransactionInitializer {
    public static void main(String[] args){
        OrderedMap<String, Transaction> transactionMap = new OrderedMap<>();

        TransactionDAO dao = new TransactionDAO();
        dao.saveToFile(transactionMap);
        System.out.println("Transaction data initialized and saved to file.");
    }
}
