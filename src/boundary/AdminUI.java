package boundary;

import control.PatientMaintenance;
import control.TreatmentMaintenance;
import control.PaymentMaintenance;
import control.ConsultationMaintenance;
import java.util.Scanner;

public class AdminUI {
    private final PatientMaintenance patientMaintenance;
    private final TreatmentMaintenance treatmentMaintenance;
    private final PaymentMaintenance paymentMaintenance;
    private final ConsultationMaintenance consultationMaintenance;

    public AdminUI() {
        this.patientMaintenance = new PatientMaintenance();
        this.treatmentMaintenance = new TreatmentMaintenance();
        this.paymentMaintenance = PaymentMaintenance.getInstance();
        this.consultationMaintenance = new ConsultationMaintenance();
    }

    public PatientMaintenance getPatientMaintenance() {
        return patientMaintenance;
    }

    public TreatmentMaintenance getTreatmentMaintenance() {
        return treatmentMaintenance;
    }

    public PaymentMaintenance getPaymentMaintenance() {
        return paymentMaintenance;
    }

    public ConsultationMaintenance getConsultationMaintenance() {
        return consultationMaintenance;
    }

    public void displayMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== ADMIN MAINTENANCE MENU ===");
            System.out.println("1. Patient Maintenance");
            System.out.println("2. Treatment Maintenance");
            System.out.println("3. Payment Maintenance");
            System.out.println("4. Consultation Maintenance");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    openPatientMaintenance();
                    break;
                case 2:
                    openTreatmentMaintenance();
                    break;
                case 3:
                    openPaymentMaintenance();
                    break;
                case 4:
                    openConsultationMaintenance();
                    break;
                case 0:
                    System.out.println("Exiting AdminUI.");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void openPatientMaintenance() {
        System.out.println("Patient Maintenance selected.");
        PatientUI patientUI = new PatientUI(patientMaintenance);
        patientUI.displayMenu();
        // Add actual PatientMaintenance UI logic here
    }

    private void openTreatmentMaintenance() {
        System.out.println("Treatment Maintenance selected.");
        // Add actual TreatmentMaintenance UI logic here
    }

    private void openPaymentMaintenance() {
        System.out.println("Payment Maintenance selected.");
        // Add actual PaymentMaintenance UI logic here
    }

    private void openConsultationMaintenance() {
        System.out.println("Consultation Maintenance selected.");
        // Add actual ConsultationMaintenance UI logic here
    }

    public static void main(String[] args) {
        AdminUI adminUI = new AdminUI();
        adminUI.displayMenu();
    }
}