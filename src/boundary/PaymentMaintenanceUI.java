package boundary;

import control.PaymentMaintenance;
import entity.Payment;
import adt.OrderedMap;

import java.util.Scanner;

public class PaymentMaintenanceUI {
    private final PaymentMaintenance maintenance;
    private final Scanner scanner;

    public PaymentMaintenanceUI() {
        maintenance = new PaymentMaintenance();
        scanner = new Scanner(System.in);
    }

    public void run() {
        int choice;
        do {
            System.out.println("\n=== Payment Maintenance Menu ===");
            System.out.println("1. Add Payment");
            System.out.println("2. View All Payments");
            System.out.println("3. Search Payment by ID");
            System.out.println("4. Delete Payment");
            System.out.println("5. Count Total Payments");
            System.out.println("6. View Total Payment Amount");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> addPayment();
                case 2 -> viewAllPayments();
                case 3 -> searchPayment();
                case 4 -> deletePayment();
                case 5 -> countPayments();
                case 6 -> viewTotalPaymentAmount();
                case 0 -> System.out.println("Exiting Payment Maintenance.");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private void addPayment() {
        System.out.println("\n-- Add Payment --");
        System.out.print("Payment ID: ");
        String paymentId = scanner.nextLine();

        System.out.print("Consultation ID (can be empty if not linked): ");
        String consultationId = scanner.nextLine();

        OrderedMap<String, Double> breakdown = new OrderedMap<>();
        double totalAmount = 0.0;
        String more;
        do {
            System.out.print("Enter breakdown description (e.g., Consultation Fee, Treatment Fee, Medical Fee): ");
            String desc = scanner.nextLine();
            System.out.print("Enter amount for " + desc + ": ");
            double amt = Double.parseDouble(scanner.nextLine());
            breakdown.put(desc, amt);
            totalAmount += amt;

            System.out.print("Add another breakdown? (y/n): ");
            more = scanner.nextLine();
        } while (more.equalsIgnoreCase("y"));

        System.out.print("Enter Paid Amount: ");
        double paidAmount = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter Payment Status (Paid/Unpaid): ");
        String paymentStatus = scanner.nextLine();

        Payment payment = new Payment(paymentId, consultationId, totalAmount, paidAmount, paymentStatus, breakdown);
        maintenance.addPayment(payment);
        System.out.println("Payment added.");
    }

    private void viewAllPayments() {
        System.out.println("\n-- All Payments --");
        Payment[] payments = maintenance.getAllPayments();
        for (Payment p : payments) {
            System.out.println(p);
            System.out.println("----");
        }
    }

    private void searchPayment() {
        System.out.print("Enter Payment ID: ");
        String id = scanner.nextLine();
        Payment payment = maintenance.getPayment(id);
        if (payment != null) {
            System.out.println(payment);
        } else {
            System.out.println("Payment not found.");
        }
    }

    private void deletePayment() {
        System.out.print("Enter Payment ID to delete: ");
        String id = scanner.nextLine();
        boolean success = maintenance.removePayment(id);
        if (success) {
            System.out.println("Payment deleted.");
        } else {
            System.out.println("Payment not found.");
        }
    }

    private void countPayments() {
        System.out.println("Total payments stored: " + maintenance.countPayments());
    }

    private void viewTotalPaymentAmount() {
        System.out.println("Total payment amount (sum of all payments): RM " + maintenance.getTotalPayments());
    }

    public static void main(String[] args) {
        new PaymentMaintenanceUI().run();
    }
}