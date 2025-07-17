package entity;

import adt.CustomADT;

public class Payment {
    private String paymentId;
    private String consultationId;
    private double totalAmount;
    private double paidAmount;
    private String paymentStatus;
    private CustomADT<String, Double> paymentBreakdown;

    public Payment(String paymentId, String consultationId, double totalAmount, double paidAmount,
                   String paymentStatus, CustomADT<String, Double> paymentBreakdown) {
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

    public CustomADT<String, Double> getPaymentBreakdown() {
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

    public void setPaymentBreakdown(CustomADT<String, Double> paymentBreakdown) {
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
