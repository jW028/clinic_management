package boundary;

import control.PatientMaintenance;
import entity.*;
import utility.IDGenerator;
import utility.InputHandler;
import adt.*;
import utility.DateTimeFormatterUtil;

/**
 * PatientUI class handles the user interface for patient management operations.
 * It provides methods to register, update, delete patients, manage queues and waitlists,
 * and view patient records and visit history.
 */
public class PatientUI {
    private final PatientMaintenance patientMaintenance;

    public PatientUI(PatientMaintenance patientMaintenance) {
        this.patientMaintenance = patientMaintenance;
    }

    /**
     * Main menu
     */
    public void displayMenu() {
        int choice;
        do {
            printMenu();
            choice = InputHandler.getInt("Select an option", 0, 5);

            switch(choice) {
                case 1:
                    patientRegistrationMenu();
                    break;
                case 2:
                    queueManagementMenu();
                    break;
                case 3:
                    waitlistManagementMenu();
                    break;
                case 4:
                    recordManagementMenu();
                    break;
                case 5:
                    displaySystemStatistics();
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (choice != 0) {
                InputHandler.pauseForUser();
            }

        } while (choice != 0);
    }

    /**
     * Display menu options
     */
    public void printMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("   PATIENT MANAGEMENT SYSTEM");
        System.out.println("=".repeat(40));
        System.out.println("1. Patient Registration");
        System.out.println("2. Queue Management");
        System.out.println("3. Waitlist Management");
        System.out.println("4. View Records");
        System.out.println("5. Display System Statistics");
        System.out.println("0. Back to Main Menu");
        System.out.println("=".repeat(40));
    }

    /**
     * Patient Registration submenu
     */
    public void patientRegistrationMenu() {
        int choice;
        do {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("    PATIENT REGISTRATION MENU");
            System.out.println("=".repeat(40));
            System.out.println("1. Register New Patient"); // Patient
            System.out.println("2. Update Patient Details"); // Patient
            System.out.println("3. Delete Patient"); // Admin
            System.out.println("4. View Patient List"); // Admin
            System.out.println("5. Sort Patients");
            System.out.println("0. Back to Patient Menu");
            System.out.println("=".repeat(40));

            choice = InputHandler.getInt("Select an option", 0, 5);

            switch(choice) {
                case 1:
                    registerPatient();
                    break;
                case 2:
                    updatePatient();
                    break;
                case 3:
                    deletePatient();
                    break;
                case 4:
                    viewPatientList();
                    break;
                case 5:
                    sortPatients();
                    break;
                case 0:
                    System.out.println("Returning to patient menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (choice != 0) {
                InputHandler.pauseForUser();
            }

        } while (choice != 0);
    }

    /**
     * Queue Management submenu
     */
    public void queueManagementMenu() {
        int choice;
        do {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("     QUEUE MANAGEMENT MENU");
            System.out.println("=".repeat(40));
            System.out.println("1. Add Patient to Queue");
            System.out.println("2. Serve Next Patient");
            System.out.println("3. View Next Patient");
            System.out.println("4. View Queue Status");
            System.out.println("5. Display Queue Patients");
            System.out.println("6. Clear All Queues");
            System.out.println("0. Back to Patient Menu");
            System.out.println("=".repeat(40));

            choice = InputHandler.getInt("Select an option", 0, 6);

            switch(choice) {
                case 1:
                    addToQueue();
                    break;
                case 2:
                    servePatient();
                    break;
                case 3:
                    viewNextPatient();
                    break;
                case 4:
                    viewQueueStatus();
                    break;
                case 5:
                    viewQueue();
                    break;
                case 6:
                    clearAllQueues();
                    break;
                case 0:
                    System.out.println("Returning to patient menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (choice != 0) {
                InputHandler.pauseForUser();
            }

        } while (choice != 0);
    }

    /**
     * Waitlist Management submenu
     */
    public void waitlistManagementMenu() {
        int choice;
        do {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("    WAITLIST MANAGEMENT MENU");
            System.out.println("=".repeat(40));
            System.out.println("1. View Waitlist");
            System.out.println("2. Add Patient to Waitlist");
            System.out.println("3. Remove from Waitlist");
            System.out.println("4. Promote from Waitlist to Queue");
            System.out.println("0. Back to Patient Menu");
            System.out.println("=".repeat(40));

            choice = InputHandler.getInt("Select an option", 0, 4);

            switch(choice) {
                case 1:
                    viewWaitlist();
                    break;
                case 2:
                    addToWaitlist();
                    break;
                case 3:
                    removeFromWaitlist();
                    break;
                case 4:
                    promoteFromWaitlist();
                    break;
                case 0:
                    System.out.println("Returning to patient menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (choice != 0) {
                InputHandler.pauseForUser();
            }

        } while (choice != 0);
    }

    /**
     * Record Management submenu
     */
    public void recordManagementMenu() {
        int choice;
        do {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("     RECORD MANAGEMENT MENU");
            System.out.println("=".repeat(40));
            System.out.println("1. Search Patient Records");
            System.out.println("2. View Patient Records");
            System.out.println("0. Back to Patient Menu");
            System.out.println("=".repeat(40));

            choice = InputHandler.getInt("Select an option", 0, 2);

            switch(choice) {
                case 1:
                    searchPatientRecords();
                    break;
                case 2:
                    viewPatientRecordsWithHistory();
                    break;
                case 0:
                    System.out.println("Returning to patient menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (choice != 0) {
                InputHandler.pauseForUser();
            }

        } while (choice != 0);
    }

    /**
     * Register new patient workflow
     */
    public void registerPatient() {
        System.out.println("\n=== REGISTER NEW PATIENT ===");

        String id = IDGenerator.generatePatientID();
        System.out.println("Generated Patient ID: " + id);

        String name = InputHandler.getString("Enter Name");
        int age = InputHandler.getInt("Enter Age", 1, 120);
        String gender = InputHandler.getGender("Select Gender");
        String contactNumber = InputHandler.getPhoneNumber("Enter Contact Number");
        String address = InputHandler.getString("Enter Address");
        boolean isEmergency = InputHandler.getYesNo("Is this an emergency case?");

        if (patientMaintenance.registerPatient(id, name, age, gender, contactNumber, address, isEmergency)) {
            System.out.println("✅ Patient registered successfully!");
            System.out.println("Patient ID: " + id);
        } else {
            System.out.println("❌ Registration failed - ID already exists");
        }
    }

    /**
     * Update patient workflow
     */
    public void updatePatient() {
        System.out.println("\n=== UPDATE PATIENT DETAILS ===");

        String id = InputHandler.getString("Enter Patient ID to update");
        Patient patient = patientMaintenance.getPatientById(id.toUpperCase());

        if (patient == null) {
            System.out.println("❌ Patient not found!");
            return;
        }

        System.out.println("Current patient information:");
        displayPatientDetails(patient);
        System.out.println("\n--- ENTER NEW DETAILS ---");

        String name = InputHandler.getString("Enter New Name");
        int age = InputHandler.getInt("Enter New Age", 1, 120);
        String gender = InputHandler.getGender("Select New Gender");
        String contactNumber = InputHandler.getPhoneNumber("Enter New Contact Number");
        String address = InputHandler.getString("Enter New Address");
        boolean isEmergency = InputHandler.getYesNo("Is this an emergency case?");

        if (patientMaintenance.updatePatient(id, name, age, gender, contactNumber, address, isEmergency)) {
            System.out.println("✅ Patient details updated successfully!");
        } else {
            System.out.println("❌ Update failed - Patient not found");
        }
    }

    /**
     * Delete patient workflow
     */
    public void deletePatient() {
        System.out.println("\n=== DELETE PATIENT ===");

        String id = InputHandler.getString("Enter Patient ID to delete");

        // Use CustomADT's containsKey for efficient existence check
        if (!patientMaintenance.getAllPatients().containsKey(id)) {
            System.out.println("✗ Patient not found with ID: " + id);
            return;
        }

        Patient patient = patientMaintenance.getPatientById(id.toUpperCase());
        System.out.println("Patient to delete:");
        displayPatientDetails(patient);

        boolean confirm = InputHandler.getYesNo("Are you sure you want to delete this patient?");
        if (!confirm) {
            System.out.println("Operation cancelled.");
            return;
        }

        if (patientMaintenance.deletePatient(id)) {
            System.out.println("✅ Patient deleted successfully!");
        } else {
            System.out.println("❌ Patient not found or could not be deleted.");
        }
    }

    /**
     * Display all patients in a formatted list
     */
    public void viewPatientList() {
        System.out.println("\n=== PATIENT LIST ===");

        CustomADT<String, Patient> patients = patientMaintenance.getAllPatients();
        if (patients.isEmpty()) {
            System.out.println("No patients registered.");
            return;
        }

        displayPatientList(patients, "All Registered Patients");

        System.out.println("\nTotal registered patients: " + patientMaintenance.getRegisteredPatientCount());

        // Option to view details
        System.out.println("\nOptions:");
        System.out.println("1. View Patient Details");
        System.out.println("0. Back to Registration Menu");

        int actionChoice = InputHandler.getInt("Select action", 0, 1);

        if (actionChoice == 1) {
            viewSelectedPatientDetails(patients);
        }
    }

    /**
     * Add patient to queue workflow
     */
    public void addToQueue() {
        System.out.println("\n=== ADD PATIENT TO QUEUE ===");

        String id = InputHandler.getString("Enter Patient ID");
        Patient patient = patientMaintenance.getPatientById(id.toUpperCase());

        if (patient == null) {
            System.out.println("❌ Patient not found!");
            return;
        }

        if (patientMaintenance.isPatientInQueue(id)) {
            System.out.println("❌ Patient is already in queue!");
            return;
        }

        if (patientMaintenance.isPatientInWaitlist(id)) {
            System.out.println("❌ Patient is currently in waitlist. Use 'Promote from Waitlist' option instead.");
            return;
        }

        if (patientMaintenance.isQueueFull()) {
            System.out.println("⚠️ Queue is full! Patient will be added to waitlist instead.");
            if (patientMaintenance.addToWaitlist(id)) {
                System.out.println("✅ Patient " + patient.getName() + " has been added to the waitlist.");
                System.out.println("Waitlist position: " + patientMaintenance.getWaitlistSize());
            } else {
                System.out.println("❌ Cannot add to waitlist - it may be full or patient is already listed.");
            }
        } else {
            patientMaintenance.enqueuePatient(id);
            System.out.println("✅ Patient " + patient.getName() + " has been added to the " +
                    (patient.isEmergency() ? "emergency" : "normal") + " queue.");
        }

        displayCurrentStatus();
    }

    /**
     * Serve next patient workflow
     */
    public void servePatient() {
        System.out.println("\n=== SERVE NEXT PATIENT ===");

        Patient next = patientMaintenance.serveNextPatient();
        if (next != null) {
            System.out.println("🏥 Now serving:");
            displayPatientDetails(next);
        } else {
            System.out.println("❌ No patients in queue");
        }
    }

    /*
    * View next patient using CustomADT's peek method
     */
    private void viewNextPatient() {
        System.out.println("\n=== NEXT PATIENT IN QUEUE ===");

        Patient next = patientMaintenance.peekNextPatient();
        if (next != null) {
            System.out.println("Next patient to be served:");
            displayPatientDetails(next);
        } else {
            System.out.println("❌ No patients in queue");
        }
    }

    /**
     * View queue status
     */
    public void viewQueueStatus() {
        System.out.println("\n=== QUEUE STATUS ===");
        displayCurrentStatus();
    }

    /**
     * Clear all queues using CustomADT operations
     */
    private void clearAllQueues() {
        System.out.println("\n=== CLEAR ALL QUEUES ===");
        if (!InputHandler.getYesNo("Confirm clear all queues")) {
            System.out.println("Operation cancelled.");
            return;
        }
        patientMaintenance.clearAllQueues();
        System.out.println("✅ All queues cleared.");
        displayCurrentStatus();
    }

    /**
     * View waitlist
     */
    public void viewWaitlist() {
        System.out.println("\n=== WAITLIST STATUS ===");
        System.out.println("Waitlist size: " + patientMaintenance.getWaitlistSize() + "/30");

        CustomADT<String, Patient> waitlist = patientMaintenance.getWaitlist();
        if (waitlist.isEmpty()) {
            System.out.println("No patients in waitlist");
        } else {
            displayWaitlistPatients(waitlist);
        }
    }

    /**
     * Add patient to waitlist
     */
    public void addToWaitlist() {
        System.out.println("\n=== ADD PATIENT TO WAITLIST ===");

        String id = InputHandler.getString("Enter Patient ID to add to waitlist");
        Patient patient = patientMaintenance.getPatientById(id.toUpperCase());

        if (patient == null) {
            System.out.println("❌ Patient not found!");
            return;
        }

        if (patientMaintenance.isPatientInQueue(id)) {
            System.out.println("❌ Patient is already in queue!");
            return;
        }

        if (patientMaintenance.addToWaitlist(id)) {
            System.out.println("✅ Patient " + patient.getName() + " added to waitlist successfully!");
            System.out.println("Waitlist position: " + patientMaintenance.getWaitlistSize());
        } else {
            System.out.println("❌ Cannot add to waitlist. It may be full or patient is already in waitlist.");
        }
    }

    /**
     * Remove patient from waitlist
     */
    public void removeFromWaitlist() {
        System.out.println("\n=== REMOVE FROM WAITLIST ===");

        if (patientMaintenance.getWaitlistSize() == 0) {
            System.out.println("❌ Waitlist is empty!");
            return;
        }

        String id = InputHandler.getString("Enter Patient ID to remove from waitlist");

        if (patientMaintenance.removeFromWaitlist(id)) {
            System.out.println("✅ Patient removed from waitlist successfully!");
        } else {
            System.out.println("❌ Patient not found in waitlist!");
        }
    }

    /**
     * Promote patient from waitlist to queue
     */
    public void promoteFromWaitlist() {
        System.out.println("\n=== PROMOTE FROM WAITLIST ===");

        if (patientMaintenance.getWaitlistSize() == 0) {
            System.out.println("❌ Waitlist is empty!");
            return;
        }

        if (patientMaintenance.isQueueFull()) {
            System.out.println("❌ Queue is full! Cannot promote from waitlist.");
            return;
        }

        String id = InputHandler.getString("Enter Patient ID to promote from waitlist to queue");
        Patient patient = patientMaintenance.getPatientById(id.toUpperCase());

        if (patient == null) {
            System.out.println("❌ Patient not found!");
            return;
        }

        if (patientMaintenance.promoteFromWaitlist(id)) {
            System.out.println("✅ Patient " + patient.getName() + " promoted from waitlist to " +
                    (patient.isEmergency() ? "emergency" : "normal") + " queue successfully!");
            displayCurrentStatus();
        } else {
            System.out.println("❌ Cannot promote patient. They may not be in waitlist or queue may be full.");
        }
    }

    /**
     * View patient records
     */
    public void viewPatientRecordsWithHistory() {
        System.out.println("\n=== VIEW PATIENT RECORDS & HISTORY ===");

        String id = InputHandler.getString("Enter Patient ID");
        Patient patient = patientMaintenance.getPatientById(id.toUpperCase());

        if (patient == null) {
            System.out.println("❌ Patient not found!");
            return;
        }

        // Display patient details
        displayPatientDetails(patient);

        // Show current status
        if (patientMaintenance.isPatientInQueue(id)) {
            System.out.println("Current Status: IN QUEUE (" +
                    (patient.isEmergency() ? "Emergency" : "Normal") + ")");
        } else if (patientMaintenance.isPatientInWaitlist(id)) {
            System.out.println("Current Status: IN WAITLIST");
        } else {
            System.out.println("Current Status: REGISTERED (Not in queue or waitlist)");
        }

        CustomADT<String, VisitHistory> visitHistories = patientMaintenance.getPatientVisitHistory(id);
        displayPatientVisitHistory(id);
        displayConsultationsForPatient(id);
        displayTreatmentsForPatient(id);
        // Visit history actions menu
        String[] options = {"Add", "Update", "Remove", "Exit"};
        int action = InputHandler.displayMenu("Visit History Actions", options);

        switch (action) {
            case 0: // Add
                addVisitHistory(id);
                break;
            case 1: // Update
                if (visitHistories.isEmpty()) {
                    System.out.println("No visit histories to update.");
                    break;
                }
                int updateChoice = InputHandler.getInt("Select visit to update (0 to cancel)", 0, visitHistories.size());
                if (updateChoice == 0) return;
                VisitHistory selectedUpdate = visitHistories.get(updateChoice - 1);
                updateVisitHistory(id, selectedUpdate.getVisitId());
                break;
            case 2: // Remove
                if (visitHistories.isEmpty()) {
                    System.out.println("No visit histories to remove.");
                    break;
                }
                int removeChoice = InputHandler.getInt("Select visit to remove (0 to cancel)", 0, visitHistories.size());
                if (removeChoice == 0) return;
                VisitHistory selectedRemove = visitHistories.get(removeChoice - 1);
                removeVisitHistory(id, selectedRemove.getVisitId());
                break;
            case 3: // Exit
                System.out.println("Returning...");
                break;
        }
    }

    private void displayConsultationsForPatient(String patientId) {
        CustomADT<String, Consultation> consults = patientMaintenance.getConsultationsByPatient(patientId);
        System.out.println("\n=== CONSULTATIONS ===");
        if (consults.isEmpty()) {
            System.out.println("No consultations found.");
            return;
        }
        for (int i = 0; i < consults.size(); i++) {
            Consultation c = consults.get(i);
            System.out.printf("%d. %s | Doctor: %s | Time: %s | Diagnosis: %s%n",
                    i + 1,
                    c.getConsultationId(),
                    c.getDoctor() != null ? c.getDoctor().getName() : "-",
                    c.getConsultationTime(),
                    c.getDiagnosis() != null ? c.getDiagnosis().getName() : "-");
        }
    }

    private void displayTreatmentsForPatient(String patientId) {
        CustomADT<String, Treatment> treatments = patientMaintenance.getTreatmentsForPatient(patientId);
        System.out.println("\n=== TREATMENTS ===");
        if (treatments.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }
        for (int i = 0; i < treatments.size(); i++) {
            Treatment t = treatments.get(i);
            System.out.printf("%d. %s | Status: %s | Type: %s | Critical: %s | Total Procedure Cost: RM%.2f%n",
                    i + 1,
                    t.getTreatmentID(),
                    t.getStatus(),
                    t.getType(),
                    t.isCritical() ? "Yes" : "No",
                    t.getTotalProcedureCost());
        }
    }

    /**
     * Search patient records
     */
    public void searchPatientRecords() {
        System.out.println("\n=== SEARCH PATIENT RECORDS ===");

        String searchTerm = InputHandler.getString("Enter patient name or ID to search");
        CustomADT<String, Patient> searchResults = new CustomADT<>();

        CustomADT<String, Patient> patients = patientMaintenance.getAllPatients();
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            if (patient.getPatientId().contains(searchTerm) ||
                    patient.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                searchResults.put(patient.getPatientId(), patient);
            }
        }

        if (searchResults.isEmpty()) {
            System.out.println("❌ No patients found matching the search term.");
            return;
        }

        displayPatientList(searchResults, "Search Results for: " + searchTerm);

        // Option to view details
        System.out.println("\nOptions:");
        System.out.println("1. View Patient Details");
        System.out.println("0. Back to Records Menu");

        int actionChoice = InputHandler.getInt("Select action", 0, 1);

        if (actionChoice == 1) {
            viewSelectedPatientDetails(searchResults);
        }
    }

    /**
     * Display detailed information for a single patient
     */
    public void displayPatientDetails(Patient patient) {
        if (patient == null) {
            System.out.println("No patient data available.");
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("                PATIENT DETAILS");
        System.out.println("=".repeat(50));

        System.out.printf("Patient ID    : %s\n", patient.getPatientId());
        System.out.printf("Name          : %s\n", patient.getName());
        System.out.printf("Age           : %d\n", patient.getAge());
        System.out.printf("Gender        : %s\n", patient.getGender());
        System.out.printf("Contact       : %s\n", patient.getContactNumber());
        System.out.printf("Address       : %s\n", patient.getAddress());
        System.out.printf("Emergency     : %s\n", patient.isEmergency() ? "YES" : "No");

        System.out.println("=".repeat(50));
    }

    /**
     * Display a list of patients with essential information
     */
    public void displayPatientList(CustomADT<String, Patient> patients, String title) {
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("  " + title.toUpperCase());
        System.out.println("=".repeat(80));
        System.out.printf("%-4s %-12s %-20s %-4s %-8s %-15s %-10s\n",
                "#", "ID", "Name", "Age", "Gender", "Contact", "Emergency");
        System.out.println("-".repeat(80));

        Patient[] patientsOrder = patientMaintenance.getAllPatientsArray();

        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            if (patient != null) {
                String name = patient.getName();
                String contact = patient.getContactNumber();

                // Truncate long names for table formatting
                if (name.length() > 19) name = name.substring(0, 16) + "...";
                if (contact.length() > 14) contact = contact.substring(0, 11) + "...";

                int patOrder = findRegistrationOrder(patientsOrder, patient.getPatientId());

                System.out.printf("%-4d %-12s %-20s %-4d %-8s %-15s %-10s\n",
                        i + 1,
                        patient.getPatientId(),
                        name,
                        patient.getAge(),
                        patient.getGender(),
                        contact,
                        patient.isEmergency() ? "YES" : "No"
                );
            }
        }
        System.out.println("=".repeat(80));
    }

    private int findRegistrationOrder(Patient[] patientsOrder, String patientId) {
        for (int i = 0; i < patientsOrder.length; i++) {
            if (patientsOrder[i] != null && patientsOrder[i].getPatientId().equals(patientId)) {
                return i + 1;
            }
        }
        return -1; // Not found
    }

    /**
     * Display waitlist patients
     */
    public void displayWaitlistPatients(CustomADT<String, Patient> waitlist) {
        System.out.println("\nPatients in waitlist (in order):");
        System.out.println("-".repeat(50));
        for (int i = 0; i < waitlist.size(); i++) {
            Patient patient = waitlist.get(i);
            System.out.printf("%d. %s (%s) - %s\n",
                    (i + 1),
                    patient.getName(),
                    patient.getPatientId(),
                    patient.isEmergency() ? "EMERGENCY" : "NORMAL");
        }
        System.out.println("-".repeat(50));
    }

    /**
     * View Specific Queue Patients
     * Allows user to view patients in a specific queue (emergency or normal)
     * and perform actions like viewing next patient or displaying all patients in that queue.
     */
    private void viewQueue() {
        System.out.println("\n=== VIEW QUEUE PATIENTS ===");
        String queueType = InputHandler.getString("Enter queue type (emergency/normal)").toLowerCase().trim();
        if (!queueType.equals("emergency") && !queueType.equals("normal")) {
            System.out.println("❌ Invalid queue type. Please enter 'emergency' or 'normal'.");
            return;
        }

        displayQueuePatients(queueType);
        System.out.println("\nOptions:");
        System.out.println("1. View Next Patient");
        System.out.println("2. Display Queue Patients");
        System.out.println("0. Back to Queue Menu");
        int actionChoice = InputHandler.getInt("Select action", 0, 2);
        switch (actionChoice) {
            case 1:
                viewNextPatient();
                break;
            case 2:
                displayQueuePatients(queueType);
                break;
            case 0:
                System.out.println("Returning to queue menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    /*
       * Display Queue contents
     */
    public void displayQueuePatients(String queueType) {
        System.out.println("\nPatients in " + queueType + " queue (in order):");
        System.out.println("-".repeat(50));
        CustomADT<String, Patient> queue = queueType.equals("emergency")
                ? patientMaintenance.getEmergencyQueue()
                : patientMaintenance.getNormalQueue();

        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }

        System.out.println("Queue size: " + queue.size());
        System.out.println("-".repeat(80));

        int position = 1;
        // Use CustomADT's iterator for efficient queue traversal
        for (Patient patient : queue) {
            if (patient != null) {
                System.out.printf("%d. %s\n", position++, patient.toString());
            }
        }
        System.out.println("-".repeat(80));
    }

    /**
     * Display current system status
     */
    public void displayCurrentStatus() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("         CURRENT STATUS");
        System.out.println("=".repeat(40));
        System.out.println("Emergency queue: " + patientMaintenance.getEmergencyQueueSize() + " patients");
        System.out.println("Normal queue: " + patientMaintenance.getNormalQueueSize() + " patients");
        System.out.println("Total queue size: " + patientMaintenance.getTotalQueueSize() + "/20");
        System.out.println("Waitlist size: " + patientMaintenance.getWaitlistSize() + "/30");
        System.out.println("=".repeat(40));
    }

    /**
     * Helper method to view details of a selected patient from a list
     */
    public void viewSelectedPatientDetails(CustomADT<String, Patient> patients) {
        if (patients.isEmpty()) {
            System.out.println("No patients available.");
            return;
        }

        int choice = InputHandler.getInt("Select patient to view", 1, patients.size());
        Patient selectedPatient = patients.get(choice - 1);

        if (selectedPatient != null) {
            displayPatientDetails(selectedPatient);

            // Show status
            String id = selectedPatient.getPatientId();
            if (patientMaintenance.isPatientInQueue(id)) {
                System.out.println("Status: IN QUEUE");
            } else if (patientMaintenance.isPatientInWaitlist(id)) {
                System.out.println("Status: IN WAITLIST");
            } else {
                System.out.println("Status: REGISTERED");
            }
        } else {
            System.out.println("Patient not found.");
        }
    }

    /**
     * Display patient visit history
     */
    public void displayPatientVisitHistory(String patientId) {
        CustomADT<String, VisitHistory> visitHistories = patientMaintenance.getPatientVisitHistory(patientId);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("                    VISIT HISTORY");
        System.out.println("=".repeat(80));

        if (visitHistories.isEmpty()) {
            System.out.println("No visit history found for this patient.");
            System.out.println("=".repeat(80));
            return;
        }

        System.out.printf("%-4s %-12s %-20s %-15s %-8s\n",
                "#", "Visit ID", "Visit Date", "Reason", "Status");
        System.out.println("-".repeat(80));

        for (int i = 0; i < visitHistories.size(); i++) {
            VisitHistory visit = visitHistories.get(i);
            String reason = visit.getVisitReason();
            if (reason.length() > 19) reason = reason.substring(0, 16) + "...";

            System.out.printf("%-4d %-12s %-20s %-15s %-8s\n",
                    i + 1,
                    visit.getVisitId(),
                    DateTimeFormatterUtil.formatForDisplay(visit.getVisitDate()),
                    reason,
                    visit.getStatus()
            );
        }
        System.out.println("=".repeat(80));
        System.out.println("Total visits: " + visitHistories.size());
    }

    /**
     * Add visit history
     */
    public void addVisitHistory(String patientId) {
        System.out.println("\n=== ADD VISIT HISTORY ===");
        Patient patient = patientMaintenance.getPatientById(patientId.toUpperCase());
        if (patient == null) {
            System.out.println("❌ Patient not found!");
            return;
        }
        String visitReason = InputHandler.getString("Enter visit reason");
        System.out.println("New status (SCHEDULED / IN_PROGRESS / COMPLETED / CANCELLED)");
        String[] statusOptions = {"SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"};
        int statusChoice = InputHandler.getInt("Select status", 1, statusOptions.length);
        String status = statusOptions[statusChoice - 1];
        if (patientMaintenance.addVisitHistory(patientId, visitReason, status)) {
            System.out.println("✅ Visit history added successfully!");
        } else {
            System.out.println("❌ Failed to add visit history.");
        }
    }

    /**
     * Update visit history
     */
    public void updateVisitHistory(String patientId, String visitId) {
        VisitHistory visit = patientMaintenance.getVisitHistory(visitId);
        if (visit == null || !visit.getPatient().getPatientId().equalsIgnoreCase(patientId)) {
            System.out.println("❌ Visit history not found for this patient.");
            return;
        }

        System.out.println("\nCurrent details:");
        displayVisitDetails(visit);

        System.out.println("\n--- ENTER NEW VALUES (leave blank to keep) ---");
        String newReason = InputHandler.getOptionalString("New visit reason");
        System.out.println("New status (SCHEDULED / IN_PROGRESS / COMPLETED / CANCELLED)");
        String[] statusOptions = {"SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"};
        int statusChoice = InputHandler.getInt("Select status", 1, statusOptions.length);
        String status = statusOptions[statusChoice - 1];

        if ((newReason == null || newReason.isBlank()) &&
                (status == null || status.isBlank())) {
            System.out.println("No changes entered. Aborting.");
            return;
        }

        boolean ok = patientMaintenance.updateVisitHistory(
                patientId,
                visitId,
                newReason,
                status
        );

        System.out.println(ok ? "✅ Visit history updated." : "❌ Update failed.");
    }

    /**
     * Remove visit history
     */
    public void removeVisitHistory(String patientId, String visitId) {
        VisitHistory visitHistory = patientMaintenance.getVisitHistory(visitId);
        if (visitHistory == null || !visitHistory.getPatient().getPatientId().equalsIgnoreCase(patientId)) {
            System.out.println("❌ Visit history not found for this patient!");
            return;
        }
        displayVisitDetails(visitHistory);
        boolean confirm = InputHandler.getYesNo("Are you sure you want to remove this visit history?");
        if (!confirm) {
            System.out.println("Operation cancelled.");
            return;
        }
        if (patientMaintenance.removeVisitHistory(visitId)) {
            System.out.println("✅ Visit history removed successfully!");
        } else {
            System.out.println("❌ Failed to remove visit history.");
        }
    }

    /**
     * Display detailed visit information
     */
    public void displayVisitDetails(VisitHistory visit) {
        if (visit == null) {
            System.out.println("No visit data available.");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("                 VISIT DETAILS");
        System.out.println("=".repeat(60));

        System.out.printf("Visit ID        : %s\n", visit.getVisitId());
        System.out.printf("Patient         : %s (%s)\n",
                visit.getPatient().getName(), visit.getPatient().getPatientId());
        System.out.printf("Visit Date      : %s\n",
                DateTimeFormatterUtil.formatForDisplay(visit.getVisitDate()));
        System.out.printf("Visit Reason    : %s\n", visit.getVisitReason());
        System.out.printf("Status          : %s\n", visit.getStatus());

        System.out.println("=".repeat(60));
    }

    /*
    * Patient Sorting and Verification
     */
    private void sortPatients() {
        int choice;
        do {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("       PATIENT SORTING MENU");
            System.out.println("=".repeat(40));
            System.out.println("1. Sort Patients by ID");
            System.out.println("2. Sort Patients by Registration Order");
            System.out.println("0. Back to Patient Menu");
            System.out.println("=".repeat(40));

            choice = InputHandler.getInt("Select an option", 0, 2);

            switch(choice) {
                case 1:
                    //sortPatientsById();
                    break;
                case 2:
                    //sortPatientsByRegistration();
                    break;
                case 0:
                    System.out.println("Returning to patient menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (choice != 0) {
                InputHandler.pauseForUser();
            }

        } while (choice != 0);
    }

    /**
     * Helper method to view details of selected visit
     */
    public void viewSelectedVisitDetails(CustomADT<String, VisitHistory> visits) {
        if (visits.isEmpty()) {
            System.out.println("No visit histories available.");
            return;
        }

        int choice = InputHandler.getInt("Select visit to view", 1, visits.size());
        VisitHistory selectedVisit = visits.get(choice - 1);

        if (selectedVisit != null) {
            displayVisitDetails(selectedVisit);
        } else {
            System.out.println("Visit history not found.");
        }
    }

    /**
     * Display comprehensive system statistics
     */
    private void displaySystemStatistics() {
        int choice;
        do {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("           REPORTS MENU");
            System.out.println("=".repeat(40));
            System.out.println("1. Patient Registration Summary Report");
            System.out.println("2. Patient Visit Activity Report");
            System.out.println("0. Back to Main Menu");
            System.out.println("=".repeat(40));

            choice = InputHandler.getInt("Select report type", 0, 2);

            switch (choice) {
                case 1:
                    displayPatientRegistrationReport();
                    break;
                case 2:
                    displayPatientVisitSummaryReport();
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (choice != 0) {
                InputHandler.pauseForUser();
            }

        } while (choice != 0);
    }

    /**
     * Display patient registration summary report
     */
    public void displayPatientRegistrationReport() {
        CustomADT<String, Object> reportData = patientMaintenance.generatePatientRegistrationReport();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("            PATIENT REGISTRATION SUMMARY REPORT");
        System.out.println("=".repeat(60));

        // Overall statistics
        System.out.println("📊 OVERALL PATIENT STATISTICS:");
        System.out.println("   Total Registered Patients: " + reportData.get("totalPatients"));

        // Queue status
        System.out.println("\n🏥 CURRENT QUEUE STATUS:");
        System.out.println("   Patients in Queue: " + reportData.get("patientsInQueue") + "/20");
        System.out.println("   Emergency Queue: " + reportData.get("emergencyQueueSize"));
        System.out.println("   Normal Queue: " + reportData.get("normalQueueSize"));
        System.out.println("   Waitlist: " + reportData.get("waitlistSize") + "/30");

        // Gender breakdown
        System.out.println("\n👥 GENDER DISTRIBUTION:");
        CustomADT<String, Integer> genderStats = (CustomADT<String, Integer>) reportData.get("genderBreakdown");
        for (String gender : new String[]{"Male", "Female"}) {
            if (genderStats.containsKey(gender)) {
                Integer count = genderStats.get(gender);
                double percentage = (count.doubleValue() / (Integer) reportData.get("totalPatients")) * 100;
                System.out.printf("   %-10s: %d patients (%.1f%%)\n", gender, count, percentage);
            }
        }

        // Age group breakdown
        System.out.println("\n📈 AGE GROUP DISTRIBUTION:");
        CustomADT<String, Integer> ageStats = (CustomADT<String, Integer>) reportData.get("ageGroupBreakdown");
        String[] ageOrder = {"0-18", "19-35", "36-50", "51-65", "65+"};
        for (String ageGroup : ageOrder) {
            Integer count = ageStats.get(ageGroup);
            double percentage = (count.doubleValue() / (Integer) reportData.get("totalPatients")) * 100;
            System.out.printf("   %-8s: %d patients (%.1f%%)\n", ageGroup, count, percentage);
        }

        System.out.println("=".repeat(60));
    }

    public void displayPatientVisitSummaryReport() {
        CustomADT<String, Object> report = patientMaintenance.generatePatientVisitSummaryReport();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("         PATIENT VISIT SUMMARY REPORT");
        System.out.println("=".repeat(60));
        System.out.println("Total Visits: " + report.get("totalVisits"));
        System.out.println("Average Visits per Patient: " + String.format("%.2f", report.get("averageVisitsPerPatient")));

        System.out.println("\nVisits by Status:");
        CustomADT<String, Integer> statusCounts = (CustomADT<String, Integer>) report.get("statusCounts");
        for (String status : new String[]{"SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"}) {
            System.out.printf("  %-12s: %d\n", status, statusCounts.get(status) != null ? statusCounts.get(status) : 0);
        }

        System.out.println("\nTop 3 Patients by Visit Count:");
        CustomADT<String, Integer> topPatients = (CustomADT<String, Integer>) report.get("topPatients");
        // Iterate over all possible patient IDs in topPatients
        for (int i = 0, printed = 0; i < topPatients.size() && printed < topPatients.size(); i++) {
            // Find the key at index i
            for (Patient p : patientMaintenance.getAllPatientsArray()) {
                if (topPatients.containsKey(p.getPatientId())) {
                    Integer count = topPatients.get(p.getPatientId());
                    System.out.printf("  %s: %d visits\n", p.getPatientId(), count);
                    printed++;
                    if (printed >= topPatients.size()) break;
                }
            }
        }

        System.out.println("\nVisits per Month:");
        CustomADT<String, Integer> visitsPerMonth = (CustomADT<String, Integer>) report.get("visitsPerMonth");
        // Collect all possible month keys
        for (int i = 0, printed = 0; i < visitsPerMonth.size() && printed < visitsPerMonth.size(); i++) {
            // Find the key at index i
            for (int j = 0; j < visitsPerMonth.size(); j++) {
                String[] months = new String[visitsPerMonth.size()];
                int idx = 0;
                // Collect keys by iterating over all VisitHistory entries
                for (VisitHistory vh : patientMaintenance.getAllVisitHistories()) {
                    String month = vh.getVisitDate().getYear() + "-" + String.format("%02d", vh.getVisitDate().getMonthValue());
                    if (visitsPerMonth.containsKey(month)) {
                        boolean alreadyPrinted = false;
                        for (int k = 0; k < idx; k++) {
                            if (months[k].equals(month)) {
                                alreadyPrinted = true;
                                break;
                            }
                        }
                        if (!alreadyPrinted) {
                            months[idx++] = month;
                            Integer count = visitsPerMonth.get(month);
                            System.out.printf("  %s: %d visits\n", month, count);
                            printed++;
                            if (printed >= visitsPerMonth.size()) break;
                        }
                    }
                }
                break;
            }
        }
        System.out.println("=".repeat(60));
    }

    public static void main(String[] args) {
        new PatientUI(new PatientMaintenance()).displayMenu();
    }
}