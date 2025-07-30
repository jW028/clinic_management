package boundary;

import adt.CustomADT;
import entity.Payment;
import java.util.Scanner;

public class PaymentMaintenanceUI {
    private final Scanner scanner = new Scanner(System.in);

    public int getMenuChoice() {
        System.out.println("\n--- Payment Maintenance Menu ---");
        System.out.println("1. Add New Payment");
        System.out.println("2. View All Payments");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
        return scanner.nextInt();
    }

    public Payment inputPaymentDetails() {
        scanner.nextLine();
        System.out.print("Enter Payment ID: ");
        String paymentId = scanner.nextLine();

        System.out.print("Enter Consultation ID: ");
        String consultationId = scanner.nextLine();

        System.out.print("Enter Total Amount: ");
        double totalAmount = scanner.nextDouble();

        System.out.print("Enter Paid Amount: ");
        double paidAmount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter Payment Status (Paid/Unpaid): ");
        String paymentStatus = scanner.nextLine();

        // Build breakdown (e.g., separate service charges)
        CustomADT<String, Double> breakdown = new CustomADT<>();
        String more;
        do {
            System.out.print("Enter breakdown description (e.g., Consultation Fee): ");
            String desc = scanner.nextLine();
            System.out.print("Enter amount for " + desc + ": ");
            double amt = scanner.nextDouble();
            scanner.nextLine();
            breakdown.put(desc, amt);

            System.out.print("Add another breakdown? (y/n): ");
            more = scanner.nextLine();
        } while (more.equalsIgnoreCase("y"));

        return new Payment(paymentId, consultationId, totalAmount, paidAmount, paymentStatus, breakdown);
    }

    public void listAllPayments(String paymentsStr) {
        System.out.println("\n--- All Payments ---");
        System.out.println(paymentsStr);
    }
}
