package dao;

import adt.CustomADT;
import entity.Transaction;

public class TransactionInitializer {
    public static void main(String[] args){
        CustomADT<String, Transaction> transactionMap = new CustomADT<>();

        TransactionDAO dao = new TransactionDAO();
        dao.saveToFile(transactionMap);
        System.out.println("Transaction data initialized and saved to file.");
    }
}
