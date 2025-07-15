package boundary;

import control.PatientMaintenance;
import adt.CustomADT;
import java.util.Scanner;

public class PatientUI {
    private PatientMaintenance patientMaintenance;
    private Scanner scanner;

    public PatientUI(PatientMaintenance patientMaintenance) {
        this.patientMaintenance = patientMaintenance;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\nPatient Management System");
            System.out.println("1. Register Patient");
            System.out.println("2. Add to Queue");
            System.out.println("3. Serve Next Patient");
            System.out.println("4. Add Medical Record");
            System.out.println("5. View Medical Records");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1 -> registerPatient();
                case 2 -> addToQueue();
                case 3 -> servePatient();
                case 4 -> addMedicalRecord();
                case 5 -> viewMedicalRecords();
                case 0 -> System.exit(0);
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void registerPatient() {
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        if (patientMaintenance.registerPatient(id, name)) {
            System.out.println("Patient registered successfully!");
        } else {
            System.out.println("Registration failed - ID already exists");
        }
    }

    private void addToQueue() {
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine();
        patientMaintenance.enqueuePatient(id);
        System.out.println("Patient added to queue");
    }

    private void servePatient() {
        Patient next = patientMaintenance.serveNextPatient();
        if (next != null) {
            System.out.println("Now serving: " + next.getName());
        } else {
            System.out.println("No patients in queue");
        }
    }

    private void addMedicalRecord() {
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Medical Record: ");
        String record = scanner.nextLine();
        patientMaintenance.addMedicalRecord(id, record);
        System.out.println("Record added");
    }

    private void viewMedicalRecords() {
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine();
        CustomADT records = patientMaintenance.getMedicalRecords(id);
        if (records != null) {
            System.out.println("\nMedical Records:");
            for (int i = 0; i < records.size(); i++) {
                System.out.println((i+1) + ". " + records.get(i));
            }
        } else {
            System.out.println("Patient not found");
        }
    }
}