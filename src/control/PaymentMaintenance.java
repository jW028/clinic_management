package control;

import adt.CustomADT;
import dao.PaymentDAO;
import entity.Payment;

public class PaymentMaintenance {
    private final CustomADT<String, Payment> paymentMap;
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public PaymentMaintenance() {
        this.paymentMap = paymentDAO.retrieveFromFile();
    }

    public void addPayment(Payment payment) {
        paymentMap.put(payment.getPaymentId(), payment);
        paymentDAO.saveToFile(paymentMap);
    }

    public Payment getPayment(String paymentId) {
        return paymentMap.get(paymentId);
    }

    public boolean removePayment(String paymentId) {
        Payment removed = paymentMap.remove(paymentId);
        paymentDAO.saveToFile(paymentMap);
        return removed != null;
    }

    public Payment[] getAllPayments() {
        return paymentMap.toArray(new Payment[0]);
    }

    public int countPayments() {
        return paymentMap.size();
    }

    public double getTotalPayments() {
        double total = 0.0;
        for (Payment p : paymentMap.toArray(new Payment[0])) {
            total += p.getTotalAmount();
        }
        return total;
    }
}