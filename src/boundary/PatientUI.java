package boundary;

import control.PatientMaintenance;
import entity.*;
import java.time.format.DateTimeFormatter;
import utility.IDGenerator;
import utility.InputHandler;
import adt.CustomADT;

public class PatientUI {
    private final PatientMaintenance patientMaintenance;
    private final DateTimeFormatter dateFormatter;

    public PatientUI(PatientMaintenance patientMaintenance) {
        this.patientMaintenance = patientMaintenance;
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    public void displayMenu() {
        while (true) {
            String[] options = {
                    "Patient Registration",
                    "Queue Management",
                    "View Patient List",
                    "Back to Main Menu"
            };

            int choice = InputHandler.displayMenu("Patient Management System", options);

            switch (choice) {
                case 0 -> patientRegistrationMenu();
                case 1 -> queueManagementMenu();
                case 2 -> recordManagementMenu();
                case 3 -> { return; }
            }
        }
    }

    private void patientRegistrationMenu() {
        String[] options = {
                "Register New Patient",
                "Update Patient Details",
                "Delete Patient",
                "Back to Previous Menu"
        };

        while (true) {
            int choice = InputHandler.displayMenu("Patient Registration Menu", options);

            switch (choice) {
                case 0 -> registerPatient();
                case 1 -> updatePatient();
                case 2 -> deletePatient();
                case 3 -> viewPatientList();
                case 4 -> { return; }
            }
        }
    }

    private void queueManagementMenu() {
        String[] options = {
                "Add Patient to Queue",
                "Serve Next Patient",
                "View Queue Status",
                "Back to Previous Menu"
        };

        while (true) {
            int choice = InputHandler.displayMenu("Queue Management Menu", options);

            switch (choice) {
                case 0 -> addToQueue();
                case 1 -> servePatient();
                case 2 -> viewQueueStatus();
                case 3 -> { return; }
            }
        }
    }

    private void recordManagementMenu() {
        String[] options = {
                "View Patient Records",
                "Search Patient Records",
                "Return to Main Menu"
        };

        while (true) {
            int choice = InputHandler.displayMenu("Record Management Menu", options);

            switch (choice) {
                case 0 -> viewPatientRecords();
                case 1 -> searchPatientRecords();
                case 2 -> { return; }
            }
        }
    }

    private void registerPatient() {
        String id = IDGenerator.generatePatientID();
        System.out.println("Registering new patient with ID: " + id);

        String name = InputHandler.getString("Enter Name");
        int age = InputHandler.getInt("Enter Age", 1, 150);
        String gender = InputHandler.getGender("Select Gender");
        String contactNumber = InputHandler.getPhoneNumber("Enter Contact Number");
        String address = InputHandler.getString("Enter Address");

        if (patientMaintenance.registerPatient(id, name, age, gender, contactNumber, address)) {
            System.out.println("Patient registered successfully!");
            System.out.println("Patient ID: " + id);
        } else {
            System.out.println("Registration failed - ID already exists");
        }
    }

    private void updatePatient() {
        String id = InputHandler.getString("Enter Patient ID to update");
        Patient patient = patientMaintenance.getPatientById(id);

        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        System.out.println("\nCurrent Details:");
        System.out.println(patient.toString());
        System.out.println("\nEnter new details:");

        String name = InputHandler.getString("Enter New Name");
        int age = InputHandler.getInt("Enter New Age", 1, 150);
        String gender = InputHandler.getGender("Select New Gender");
        String contactNumber = InputHandler.getPhoneNumber("Enter New Contact Number");
        String address = InputHandler.getString("Enter New Address");

        if (patientMaintenance.updatePatient(id, name, age, gender, contactNumber, address)) {
            System.out.println("Patient details updated successfully!");
        } else {
            System.out.println("Update failed - Patient not found");
        }
        InputHandler.pauseForUser();
    }

    private void deletePatient() {
        String id = InputHandler.getString("Enter Patient ID");

        if (patientMaintenance.deletePatient(id)) {
            System.out.println("Patient deleted successfully!");
        } else {
            System.out.println("Patient not found or could not be deleted.");
        }
        InputHandler.pauseForUser();
    }

    private void viewPatientList() {
        System.out.println("\nPatient List:");
        System.out.println("Total registered patients: " + patientMaintenance.getRegisteredPatientCount());

        CustomADT<String, Patient> patients = patientMaintenance.getAllPatients();
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            System.out.printf("ID: %s | Name: %s | Age: %d | Gender: %s | Contact: %s%n",
                    patient.getPatientId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getGender(),
                    patient.getContactNumber());
        }
        InputHandler.pauseForUser();
    }
    private void addToQueue() {
        String id = InputHandler.getString("Enter Patient ID");
        patientMaintenance.enqueuePatient(id);
        System.out.println("Patient added to queue.");
        InputHandler.pauseForUser();
    }

    private void servePatient() {
        Patient next = patientMaintenance.serveNextPatient();
        if (next != null) {
            System.out.println("Now serving: " + next.toString());
        } else {
            System.out.println("No patients in queue");
        }
        InputHandler.pauseForUser();
    }

    private void viewQueueStatus() {
        System.out.println("\nCurrent Queue Status:");
        System.out.println("Total patients in queue: " + patientMaintenance.getQueueSize());
        InputHandler.pauseForUser();
    }

    private void viewPatientRecords() {
        String id = InputHandler.getString("Enter Patient ID");
        Patient patient = patientMaintenance.getPatientById(id);

        if (patient == null) {
            System.out.println("Patient not found!");
            InputHandler.pauseForUser();
            return;
        }

        System.out.println(patient.getDetailedView());
        InputHandler.pauseForUser();
    }

    private void searchPatientRecords() {
        String searchTerm = InputHandler.getString("Enter patient name or ID to search");

        System.out.println("\nSearch Results:");
        CustomADT<String, Patient> patients = patientMaintenance.getAllPatients();
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            if (patient.getPatientId().contains(searchTerm) ||
                    patient.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                System.out.println(patient.toString());
            }
        }

        InputHandler.pauseForUser();
    }
}