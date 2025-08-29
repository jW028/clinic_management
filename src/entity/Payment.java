package entity;

import adt.OrderedMap;

import java.io.Serializable;

public class Payment implements Serializable {
    private String paymentId;
    private String consultationId;
    private double totalAmount;
    private double paidAmount;
    private String paymentStatus;
    private OrderedMap<String, Double> paymentBreakdown;

    public Payment(String paymentId, String consultationId, double totalAmount, double paidAmount,
                   String paymentStatus, OrderedMap<String, Double> paymentBreakdown) {
        this.paymentId = paymentId;
        this.consultationId = consultationId;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.paymentStatus = paymentStatus;
        this.paymentBreakdown = paymentBreakdown;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public OrderedMap<String, Double> getPaymentBreakdown() {
        return paymentBreakdown;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentBreakdown(OrderedMap<String, Double> paymentBreakdown) {
        this.paymentBreakdown = paymentBreakdown;
    }

    public String toString() {
        return "Payment ID: " + paymentId +
                "\nConsultation ID: " + consultationId +
                "\nTotal Amount: " + totalAmount +
                "\nPaid Amount: " + paidAmount +
                "\nPayment Status: " + paymentStatus +
                "\nPayment Breakdown: " + paymentBreakdown;
    }
}
