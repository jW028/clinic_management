package boundary;

import control.PatientMaintenance;
import entity.*;
import java.util.Scanner;
import adt.CustomADT;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PatientUI {
    private final PatientMaintenance patientMaintenance;
    private final Scanner scanner;

    public PatientUI(PatientMaintenance patientMaintenance) {
        this.patientMaintenance = patientMaintenance;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n=== Patient Management System ===");
            System.out.println("1. Patient Registration");
            System.out.println("2. Queue Management");
            System.out.println("3. Record Management");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1 -> patientRegistrationMenu();
                case 2 -> queueManagementMenu();
                case 3 -> recordManagementMenu();
                case 0 -> {
                    patientMaintenance.saveChanges();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void patientRegistrationMenu() {
        while (true) {
            System.out.println("\n=== Patient Registration Menu ===");
            System.out.println("1. Register New Patient");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> registerPatient();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void queueManagementMenu() {
        while (true) {
            System.out.println("\n=== Queue Management Menu ===");
            System.out.println("1. Add Patient to Queue");
            System.out.println("2. Serve Next Patient");
            System.out.println("3. View Queue Status");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addToQueue();
                case 2 -> servePatient();
                case 3 -> viewQueueStatus();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void recordManagementMenu() {
        while (true) {
            System.out.println("\n=== Record Management Menu ===");
            System.out.println("1. Create Medical Record");
            System.out.println("2. View Patient Records");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createMedicalRecord();
                case 2 -> recordMaintenance();
                case 0 -> { return; }
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

    private void recordMaintenance() {
        System.out.println("\n=== Patient Records Management ===");

        // Display list of all patients
        System.out.println("\nRegistered Patients:");
        System.out.println("-------------------");
        final int[] counter = {1};

        patientMaintenance.listAllPatients(patient ->
                System.out.printf("%d. %-10s | %-20s | Age: %-3d | Gender: %s%n",
                        counter[0]++,
                        patient.getPatientId(),
                        patient.getName(),
                        patient.getAge(),
                        patient.getGender())
        );

        if (counter[0] == 1) {
            System.out.println("No patients registered in the system.");
            return;
        }

        // Get user selection
        System.out.print("\nEnter patient number to view records (0 to go back): ");
        int selection = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (selection == 0 || selection >= counter[0]) {
            return;
        }

        // Get and display selected patient's details
        Patient selectedPatient = patientMaintenance.getPatientByIndex(selection - 1);
        if (selectedPatient == null) {
            System.out.println("Error: Patient not found!");
            return;
        }

        // Display detailed patient information
        System.out.println(selectedPatient.getDetailedView());

        // Display medical records
        CustomADT<String, MedicalRecord> records = selectedPatient.getMedicalRecords();
        if (records.isEmpty()) {
            System.out.println("\nNo medical records found for this patient.");
            return;
        }

        System.out.println("\nMedical Records (sorted by date):");
        System.out.println("--------------------------------");

        for (int i = 0; i < records.size(); i++) {
            MedicalRecord record = records.get(i);
            System.out.printf("\n%d. Date: %s%n   Record ID: %s%n   Diagnosis: %s%n",
                    i + 1,
                    record.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    record.getRecordId(),
                    record.getTreatment().getDiagnosis().getName());
        }

        System.out.print("\nEnter record number to view full details (0 to go back): ");
        int recordChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (recordChoice > 0 && recordChoice <= records.size()) {
            MedicalRecord selectedRecord = records.get(recordChoice - 1);
            System.out.println("\n=== Detailed Medical Record ===");
            System.out.println(selectedRecord.getDetailedView());
            System.out.println("\nTreatment Information:");
            System.out.println(selectedRecord.getTreatment().toString());
            System.out.println("\nDiagnosis Details:");
            System.out.println(selectedRecord.getTreatment().getDiagnosis().toString());

            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
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