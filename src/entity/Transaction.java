package entity;

import java.io.Serializable;
import adt.CustomADT;
import utility.*;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private String transactionID;
    private String customerID;
    private CustomADT<String, PrescribedMedicine> medicines;
    private LocalDateTime date;
    private double totalPrice;

    public Transaction(String customerID) {
        this.transactionID = IDGenerator.generateTransactionID();
        this.customerID = customerID;
        this.medicines = new CustomADT<>();
        this.totalPrice = 0.0;
        this.date = LocalDateTime.now();
    }

    public void addMedicine(PrescribedMedicine prescribedMedicine) {
        if (prescribedMedicine != null && prescribedMedicine.getMedicine() != null) {
            medicines.put(prescribedMedicine.getMedicine().getId(), prescribedMedicine);
            totalPrice += prescribedMedicine.calculateSubtotal();
        }
    }

    public CustomADT<String, PrescribedMedicine> getMedicines() {
        return medicines;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getCustomerID() {
        return customerID;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Transaction ID: %s | Patient: %s | Total: RM%.2f | Date: %s\n",
                transactionID, customerID, totalPrice, DateTimeFormatterUtil.formatForDisplay(date)));
        sb.append("Medicines:\n");
        for (int i = 0; i < medicines.size(); i++) {
            PrescribedMedicine pm = medicines.get(i);
            sb.append("  - ").append(pm.toString()).append("\n");
        }
        return sb.toString();
    }
}