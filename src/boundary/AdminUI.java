package boundary;

import control.PatientMaintenance;
import entity.Patient;

import java.util.Scanner;

public class AdminUI {

    final private PatientMaintenance patientMaintenace;

    public AdminUI (PatientMaintenance patientMaintenance) {
        this.patientMaintenace = patientMaintenance;
    }

    
    public void displayMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== ADMIN MAINTENANCE MENU ===");
            System.out.println("1. Patient Maintenance");
            System.out.println("2. Doctor Maintenance");
            System.out.println("3. Consultation Maintenance");
            System.out.println("4. Treatment Maintenance");
            System.out.println("5. Prescription Maintenance");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    PatientUI patientUI = new PatientUI(patientMaintenace);
                    patientUI.displayMenu();
                    break;
                case 2:
                    DoctorUI doctorUI = new DoctorUI();
                    doctorUI.runDoctorMenu();
                    break;
                case 3:
                    ConsultationMaintenanceUI consultationUI = new ConsultationMaintenanceUI(patientMaintenace);
                    consultationUI.run();
                    break;
                case 4:
                    TreatmentMaintenanceUI treatmentUI = new TreatmentMaintenanceUI();
                    treatmentUI.displayMenu();
                    break;
                case 5:
                    PharmacyUI pharmacyUI = new PharmacyUI();
                    pharmacyUI.start();
                    break;
                case 0:
                    System.out.println("Exiting AdminUI.");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }


    public static void main(String[] args) {
        PatientMaintenance patientController = new PatientMaintenance();
        AdminUI adminUI = new AdminUI(patientController);
        adminUI.displayMenu();

//        AdminUI adminUI = new AdminUI();
//        adminUI.displayMenu();
    }
}