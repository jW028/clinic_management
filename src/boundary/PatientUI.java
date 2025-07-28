package boundary;

import control.PatientMaintenance;
import entity.*;
import java.util.Scanner;
import adt.CustomADT;
import java.time.LocalDateTime;

public class PatientUI {
    private final PatientMaintenance patientMaintenance;
    private final Scanner scanner;

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
            System.out.println("4. Create Medical Record");
            System.out.println("5. View Medical Records");
            System.out.println("6. View Queue Status");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1 -> registerPatient();
                case 2 -> addToQueue();
                case 3 -> servePatient();
                case 4 -> createMedicalRecord();
                case 5 -> viewMedicalRecords();
                case 6 -> viewQueueStatus();
                case 0 -> {
                    patientMaintenance.saveChanges();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void registerPatient() {
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Age: ");
        int age = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter Gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();


        if (patientMaintenance.registerPatient(id, name, age, gender, contactNumber, address)) {
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

    private void createMedicalRecord() {
        System.out.println("\n=== Create Medical Record ===");
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();

        Patient patient = patientMaintenance.getPatientById(patientId);
        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        // Create record ID
        String recordId = "REC" + System.currentTimeMillis();

        // Get treatment details
        System.out.println("\nEnter Treatment Details:");
        System.out.print("Enter Diagnosis Name: ");
        String diagnosisName = scanner.nextLine();
        System.out.print("Enter Severity (Low/Medium/High): ");
        String severity = scanner.nextLine();

        // Create diagnosis
        Diagnosis diagnosis = new Diagnosis(recordId + "D", diagnosisName, severity);
        diagnosis.setDescription("Diagnosis based on consultation on " + LocalDateTime.now().toLocalDate());

        Doctor placeholderDoctor = new Doctor("D-N/A", "Unassigned", "General", "", "", "", "", "");        // Create treatment
        Treatment treatment = new Treatment(
                recordId + "T",
                recordId + "C",
                patient,
                placeholderDoctor, // Use the placeholder object, not null
                diagnosis,
                LocalDateTime.now(),
                "Initial consultation",
                "High".equalsIgnoreCase(severity)
        );

        // Create medical record
        MedicalRecord record = new MedicalRecord(recordId, patient, treatment);

        // Store the record as a string representation
        patientMaintenance.saveChanges();
        System.out.println("Medical Record created successfully!");
    }

    private void viewMedicalRecords() {
        System.out.println("\n=== View Medical Records ===");
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();

        CustomADT<String, MedicalRecord> records = patientMaintenance.getAllMedicalRecords(patientId);
        if (records == null || records.isEmpty()) {
            System.out.println("No medical records found for this patient");
            return;
        }

        System.out.println("\nMedical Records for " + patientMaintenance.getPatientById(patientId).getName() + ":");        for (int i = 0; i < records.size(); i++) {
            System.out.printf("%d. %s%n", (i + 1), records.get(i));
        }

        System.out.print("\nEnter record number to view details (0 to go back): ");
        int choice = Integer.parseInt(scanner.nextLine());

        if (choice > 0 && choice <= records.size()) {
            MedicalRecord selectedRecord = records.get(choice - 1);
            System.out.println("\n--- Medical Record Details ---");
            System.out.println(selectedRecord);
        }
    }

    private void viewQueueStatus() {
        System.out.println("\n--- Consultation Queue Status ---");
        System.out.println("Patients currently waiting in queue: " + patientMaintenance.getQueueSize());
        System.out.println("Total registered patients in system: " + patientMaintenance.getRegisteredPatientCount());
    }
}