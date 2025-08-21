package boundary;

import control.PatientMaintenance;
import entity.*;
import utility.IDGenerator;
import utility.InputHandler;
import adt.CustomADT;

public class PatientUI {
    private final PatientMaintenance patientMaintenance;

    public PatientUI(PatientMaintenance patientMaintenance) {
        this.patientMaintenance = patientMaintenance;
    }

    public void displayMenu() {
        while (true) {
            String[] options = {
                    "Patient Registration",
                    "Queue Management",
                    "Waitlist Management",
                    "View Records",
                    "Back to Main Menu"
            };

            int choice = InputHandler.displayMenu("Patient Management System", options);

            switch (choice) {
                case 0 -> patientRegistrationMenu();
                case 1 -> queueManagementMenu();
                case 2 -> waitlistManagementMenu();
                case 3 -> recordManagementMenu();
                case 4 -> { return; }
            }
        }
    }

    private void patientRegistrationMenu() {
        String[] options = {
                "Register New Patient",
                "Update Patient Details",
                "Delete Patient",
                "View Patient List",
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

    private void waitlistManagementMenu() {
        String[] options = {
                "View Waitlist",
                "Add Patient to Waitlist",
                "Remove from Waitlist",
                "Promote from Waitlist to Queue",
                "Back to Previous Menu"
        };

        while (true) {
            int choice = InputHandler.displayMenu("Waitlist Management Menu", options);

            switch (choice) {
                case 0 -> viewWaitlist();
                case 1 -> addToWaitlist();
                case 2 -> removeFromWaitlist();
                case 3 -> promoteFromWaitlist();
                case 4 -> { return; }
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
        boolean isEmergency = InputHandler.getYesNo("Is this an emergency case?");

        if (patientMaintenance.registerPatient(id, name, age, gender, contactNumber, address, isEmergency)) {
            System.out.println("Patient registered successfully!");
            System.out.println("Patient ID: " + id);
            System.out.println("Priority: " + (isEmergency ? "EMERGENCY" : "NORMAL"));
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
        boolean isEmergency = InputHandler.getYesNo("Is this an emergency case?");

        if (patientMaintenance.updatePatient(id, name, age, gender, contactNumber, address, isEmergency)) {
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
        Patient patient = patientMaintenance.getPatientById(id);

        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        if (patientMaintenance.isPatientInQueue(id)) {
            System.out.println("Patient is already in queue!");
            return;
        }

        if (patientMaintenance.isPatientInWaitlist(id)) {
            System.out.println("Patient is currently in waitlist. Use 'Promote from Waitlist' option instead.");
            return;
        }

        // Check if queue is full
        if (patientMaintenance.isQueueFull()) {
            System.out.println("Queue is full! Patient will be added to waitlist instead.");
            if (patientMaintenance.addToWaitlist(id)) {
                System.out.println("Patient " + patient.getName() + " has been added to the waitlist.");
                System.out.println("Waitlist position: " + patientMaintenance.getWaitlistSize());
            } else {
                System.out.println("Cannot add to waitlist - it may be full or patient is already listed.");
            }
        } else {
            patientMaintenance.enqueuePatient(id);
            System.out.println("Patient " + patient.getName() + " has been added to the " +
                    (patient.isEmergency() ? "emergency" : "normal") + " queue.");
        }

        displayCurrentStatus();
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
        displayCurrentStatus();
        InputHandler.pauseForUser();
    }

    // Waitlist methods
    private void viewWaitlist() {
        System.out.println("\n=== WAITLIST STATUS ===");
        System.out.println("Waitlist size: " + patientMaintenance.getWaitlistSize() + "/" + 30);

        CustomADT<String, Patient> waitlist = patientMaintenance.getWaitlist();
        if (waitlist.isEmpty()) {
            System.out.println("No patients in waitlist");
        } else {
            System.out.println("\nPatients in waitlist (in order):");
            for (int i = 0; i < waitlist.size(); i++) {
                Patient patient = waitlist.get(i);
                System.out.printf("%d. %s (%s) - %s\n",
                        (i + 1),
                        patient.getName(),
                        patient.getPatientId(),
                        patient.isEmergency() ? "EMERGENCY" : "NORMAL");
            }
        }
        InputHandler.pauseForUser();
    }

    private void addToWaitlist() {
        String id = InputHandler.getString("Enter Patient ID to add to waitlist");
        Patient patient = patientMaintenance.getPatientById(id);

        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        if (patientMaintenance.isPatientInQueue(id)) {
            System.out.println("Patient is already in queue!");
            return;
        }

        if (patientMaintenance.addToWaitlist(id)) {
            System.out.println("Patient " + patient.getName() + " added to waitlist successfully!");
            System.out.println("Waitlist position: " + patientMaintenance.getWaitlistSize());
        } else {
            System.out.println("Cannot add to waitlist. It may be full or patient is already in waitlist.");
        }

        InputHandler.pauseForUser();
    }

    private void removeFromWaitlist() {
        if (patientMaintenance.getWaitlistSize() == 0) {
            System.out.println("Waitlist is empty!");
            return;
        }

        String id = InputHandler.getString("Enter Patient ID to remove from waitlist");

        if (patientMaintenance.removeFromWaitlist(id)) {
            System.out.println("Patient removed from waitlist successfully!");
        } else {
            System.out.println("Patient not found in waitlist!");
        }

        InputHandler.pauseForUser();
    }

    private void promoteFromWaitlist() {
        if (patientMaintenance.getWaitlistSize() == 0) {
            System.out.println("Waitlist is empty!");
            return;
        }

        if (patientMaintenance.isQueueFull()) {
            System.out.println("Queue is full! Cannot promote from waitlist.");
            return;
        }

        String id = InputHandler.getString("Enter Patient ID to promote from waitlist to queue");
        Patient patient = patientMaintenance.getPatientById(id);

        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        if (patientMaintenance.promoteFromWaitlist(id)) {
            System.out.println("Patient " + patient.getName() + " promoted from waitlist to " +
                    (patient.isEmergency() ? "emergency" : "normal") + " queue successfully!");
            displayCurrentStatus();
        } else {
            System.out.println("Cannot promote patient. They may not be in waitlist or queue may be full.");
        }

        InputHandler.pauseForUser();
    }

    private void displayCurrentStatus() {
        System.out.println("\n=== CURRENT STATUS ===");
        System.out.println("Emergency queue: " + patientMaintenance.getEmergencyQueueSize() + " patients");
        System.out.println("Normal queue: " + patientMaintenance.getNormalQueueSize() + " patients");
        System.out.println("Total queue size: " + patientMaintenance.getTotalQueueSize() + "/20");
        System.out.println("Waitlist size: " + patientMaintenance.getWaitlistSize() + "/30");
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

        // Show current status
        if (patientMaintenance.isPatientInQueue(id)) {
            System.out.println("Current Status: IN QUEUE (" +
                    (patient.isEmergency() ? "Emergency" : "Normal") + ")");
        } else if (patientMaintenance.isPatientInWaitlist(id)) {
            System.out.println("Current Status: IN WAITLIST");
        } else {
            System.out.println("Current Status: REGISTERED (Not in queue or waitlist)");
        }

        InputHandler.pauseForUser();
    }

    private void searchPatientRecords() {
        String searchTerm = InputHandler.getString("Enter patient name or ID to search");

        System.out.println("\nSearch Results:");
        CustomADT<String, Patient> patients = patientMaintenance.getAllPatients();
        boolean found = false;

        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            if (patient.getPatientId().contains(searchTerm) ||
                    patient.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                System.out.println(patient.toString());

                // Show status
                if (patientMaintenance.isPatientInQueue(patient.getPatientId())) {
                    System.out.println("Status: IN QUEUE");
                } else if (patientMaintenance.isPatientInWaitlist(patient.getPatientId())) {
                    System.out.println("Status: IN WAITLIST");
                } else {
                    System.out.println("Status: REGISTERED");
                }
                System.out.println();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No patients found matching the search term.");
        }

        InputHandler.pauseForUser();
    }

    public static void main(String[] args) {
        new PatientUI(new PatientMaintenance()).displayMenu();
    }
}