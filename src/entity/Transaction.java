package entity;

import java.io.Serializable;
import adt.OrderedMap;
import utility.*;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private String transactionID;
    private String patientID;
    private OrderedMap<String, PrescribedMedicine> medicines;
    private LocalDateTime date;
    private double totalPrice;

    public Transaction() {
        this.transactionID = "";
        this.patientID = "";
        this.medicines = new OrderedMap<>();
        this.date = LocalDateTime.now();
        this.totalPrice = 0.0;
    }

    public Transaction(String patientID) {
        this.transactionID = IDGenerator.generateTransactionID();
        this.patientID = patientID;
        this.medicines = new OrderedMap<>();
        this.totalPrice = 0.0;
        this.date = LocalDateTime.now();
    }

    public void addMedicine(PrescribedMedicine prescribedMedicine) {
        if (prescribedMedicine != null && prescribedMedicine.getMedicine() != null) {
            medicines.put(prescribedMedicine.getMedicine().getId(), prescribedMedicine);
            totalPrice += prescribedMedicine.calculateSubtotal();
        }
    }

    public OrderedMap<String, PrescribedMedicine> getMedicines() {
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

    public String getPatientId() {
        return patientID;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Transaction ID: %s | Patient: %s | Total: RM%.2f | Date: %s\n",
                transactionID, patientID, totalPrice, DateTimeFormatterUtil.formatForDisplay(date)));
        sb.append("Medicines:\n");
        for (int i = 0; i < medicines.size(); i++) {
            PrescribedMedicine pm = medicines.get(i);
            sb.append("  - ").append(pm.toString()).append("\n");
        }
        return sb.toString();
    }
}