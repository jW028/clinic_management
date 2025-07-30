package control;

import adt.CustomADTInterface;
import boundary.PaymentMaintenanceUI;
import dao.PaymentDAO;
import entity.Payment;
import utility.MessageUI;

public class PaymentMaintenance {
    private final CustomADTInterface<String, Payment> paymentMap;
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final PaymentMaintenanceUI PaymentMaintenanceUI = new PaymentMaintenanceUI();

    public PaymentMaintenance() {
        this.paymentMap = paymentDAO.retrieveFromFile();
    }

    public void runPaymentMaintenance() {
        int choice;
        do {
            choice = PaymentMaintenanceUI.getMenuChoice();
            switch (choice) {
                case 0 -> MessageUI.displayExitMessage();
                case 1 -> {
                    addNewPayment();
                    displayAllPayments();
                }
                case 2 -> displayAllPayments();
                default -> MessageUI.displayInvalidChoiceMessage();
            }
        } while (choice != 0);
    }

    public void addNewPayment() {
        Payment payment = PaymentMaintenanceUI.inputPaymentDetails();
        paymentMap.put(payment.getPaymentId(), payment);
        paymentDAO.saveToFile(paymentMap);
    }

    public String getAllPayments() {
        StringBuilder sb = new StringBuilder();
        for (Payment p : paymentMap.toArray()) {
            sb.append(p).append("\n");
        }
        return sb.toString();
    }

    public void displayAllPayments() {
        PaymentMaintenanceUI.listAllPayments(getAllPayments());
    }

    public static void main(String[] args) {
        PaymentMaintenance paymentMaintenance = new PaymentMaintenance();
        paymentMaintenance.runPaymentMaintenance();
    }
}
