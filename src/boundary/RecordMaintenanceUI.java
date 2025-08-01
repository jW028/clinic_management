package boundary;

import adt.CustomADT;
import control.PatientMaintenance;
import entity.Patient;
import entity.MedicalRecord;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class RecordMaintenanceUI {
    private final PatientMaintenance patientMaintenance;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;

    public RecordMaintenanceUI(PatientMaintenance patientMaintenance) {
        this.patientMaintenance = patientMaintenance;
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n=== Medical Records Maintenance ===");
            System.out.println("1. View Patient Records");
            System.out.println("2. Search Patient Records");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewPatientRecords();
                case 2 -> searchPatientRecords();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewPatientRecords() {
        // Display list of patients with numbering
        System.out.println("\n=== Patient List ===");
        final int[] counter = {1};

        patientMaintenance.listAllPatients(patient ->
                System.out.printf("%d. %s - %s%n",
                        counter[0]++,
                        patient.getPatientId(),
                        patient.getName())
        );

        // Get user selection
        System.out.print("\nEnter patient number to view records (0 to cancel): ");
        int selection = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (selection == 0 || selection > patientMaintenance.getRegisteredPatientCount()) {
            return;
        }

        // Get selected patient
        Patient patient = patientMaintenance.getPatientByIndex(selection - 1);
        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        displayPatientDetails(patient);
    }

    private void searchPatientRecords() {
        System.out.print("Enter Patient ID or Name to search: ");
        String searchTerm = scanner.nextLine();

        System.out.println("\n=== Search Results ===");
        final int[] counter = {1};
        final boolean[] found = {false};

        patientMaintenance.listAllPatients(patient -> {
            if (patient.getPatientId().contains(searchTerm) ||
                    patient.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                System.out.printf("%d. %s - %s%n",
                        counter[0]++,
                        patient.getPatientId(),
                        patient.getName());
                found[0] = true;
            }
        });

        if (!found[0]) {
            System.out.println("No patients found matching your search.");
            return;
        }

        System.out.print("\nEnter patient number to view records (0 to cancel): ");
        int selection = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (selection == 0 || selection >= counter[0]) {
            return;
        }

        // Find the selected patient
        Patient selectedPatient = null;
        counter[0] = 1;
        for (int i = 0; i < patientMaintenance.getRegisteredPatientCount(); i++) {
            Patient patient = patientMaintenance.getPatientByIndex(i);
            if (patient.getPatientId().contains(searchTerm) ||
                    patient.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                if (counter[0] == selection) {
                    selectedPatient = patient;
                    break;
                }
                counter[0]++;
            }
        }

        if (selectedPatient != null) {
            displayPatientDetails(selectedPatient);
        }
    }

    private void displayPatientDetails(Patient patient) {
        System.out.println("\n=== Patient Details ===");
        System.out.println(patient.getDetailedView());

        CustomADT<String, MedicalRecord> records = patient.getMedicalRecords();
        if (records.isEmpty()) {
            System.out.println("\nNo medical records found for this patient.");
            return;
        }

        System.out.println("\n=== Medical Records ===");
        System.out.println("(Sorted by date, most recent first)\n");

        // Create a sorted list of records
        for (int i = 0; i < records.size(); i++) {
            MedicalRecord record = records.get(i);
            System.out.printf("%d. Date: %s%n   Record ID: %s%n   %s%n%n",
                    i + 1,
                    record.getCreatedDate().format(dateFormatter),
                    record.getRecordId(),
                    record.getTreatment().getDiagnosis().getName()
            );
        }

        System.out.print("Enter record number to view details (0 to go back): ");
        int recordChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (recordChoice > 0 && recordChoice <= records.size()) {
            MedicalRecord selectedRecord = records.get(recordChoice - 1);
            System.out.println(selectedRecord.getDetailedView());

            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
}