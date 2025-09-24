package boundary;

import control.PatientMaintenance;
import entity.*;
import utility.IDGenerator;
import utility.InputHandler;
import adt.*;
import utility.DateTimeFormatterUtil;

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
            choice = InputHandler.getInt("Select an option", 0, 4);

            switch(choice) {
                case 1:
                    patientRegistrationMenu();
                    break;
                case 2:
                    queueManagementMenu();
                    break;
                case 3:
                    recordManagementMenu();
                    break;
                case 4:
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
        System.out.println("3. View Records");
        System.out.println("4. Display System Statistics");
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
            System.out.println("5. Undo Last Patient Action");
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
                    showRecentPatientActionsWithUndo();
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


    private void showRecentPatientActionsWithUndo() {
        System.out.println("\n--- Undo Last Patient Action ---");
        // If you want to show a list of recent actions, you can implement that here.
        System.out.println("Would you like to undo the last action?");
        System.out.println("1. Undo Last Action");
        System.out.println("0. Cancel");
        int undoChoice = InputHandler.getInt("Choose option", 0, 1);
        if (undoChoice == 1) {
            String result = patientMaintenance.undoLastAction();
            System.out.println(result);
        } else {
            System.out.println("Undo cancelled.");
        }
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
            System.out.println("1. View Next Patient");
            System.out.println("2. View Queue Status");
            System.out.println("3. Display Queue Patients");
            System.out.println("4. Clear All Queues");
            System.out.println("0. Back to Patient Menu");
            System.out.println("=".repeat(40));

            choice = InputHandler.getInt("Select an option", 0, 4);

            switch(choice) {
                case 1:
                    viewNextPatient();
                    break;
                case 2:
                    viewQueueStatus();
                    break;
                case 3:
                    viewQueue();
                    break;
                case 4:
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

        String name;
        while (true) {
            name = InputHandler.getString("Enter Name");
            if (name.matches("[A-Za-z ]+")) break;
            System.out.println("❌ Name must contain only letters and spaces. Please try again.");
        }

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

        // Use OrderedMap's containsKey for efficient existence check
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

        OrderedMap<String, Patient> patients = patientMaintenance.getAllPatients();
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

    /*
    * View next patient using OrderedMap's peek method
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
     * Clear all queues using OrderedMap operations
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
        displayPatientDetails(patient);
        if (patientMaintenance.isPatientInQueue(id)) {
            System.out.println("Current Status: IN QUEUE (" +
                    (patient.isEmergency() ? "Emergency" : "Normal") + ")");
        }  else {
            System.out.println("Current Status: REGISTERED (Not in queue)");
        }

        OrderedMap<String, VisitHistory> visitHistories = patientMaintenance.getPatientVisitHistory(id);
        displayPatientVisitHistory(id);
        displayConsultationsForPatient(id);
        displayTreatmentsForPatient(id);
        String[] options = {"View", "Add", "Update", "Remove", "Exit"};
        int action = InputHandler.displayMenu("Visit History Actions", options);

        switch (action) {
            case 0:
                viewSelectedVisitDetails(visitHistories);
                break;
            case 1:
                addVisitHistory(id);
                break;
            case 2:
                if (visitHistories.isEmpty()) {
                    System.out.println("No visit histories to update.");
                    break;
                }
                int updateChoice = InputHandler.getInt("Select visit to update (0 to cancel)", 0, visitHistories.size());
                if (updateChoice == 0) return;
                VisitHistory selectedUpdate = visitHistories.get(updateChoice - 1);
                updateVisitHistory(id, selectedUpdate.getVisitId());
                break;
            case 3:
                if (visitHistories.isEmpty()) {
                    System.out.println("No visit histories to remove.");
                    break;
                }
                int removeChoice = InputHandler.getInt("Select visit to remove (0 to cancel)", 0, visitHistories.size());
                if (removeChoice == 0) return;
                VisitHistory selectedRemove = visitHistories.get(removeChoice - 1);
                removeVisitHistory(id, selectedRemove.getVisitId());
                break;
            case 4: // Exit
                System.out.println("Returning...");
                break;
        }
    }

    private void displayConsultationsForPatient(String patientId) {
        OrderedMap<String, Consultation> consults = patientMaintenance.getConsultationsByPatient(patientId);
        System.out.println("\n=== CONSULTATIONS ===");
        if (consults.isEmpty()) {
            System.out.println("No consultations found.");
            return;
        }
        System.out.println("┌────┬───────────────┬───────────────┬─────────────────────┬───────────────┐");
        System.out.println("│ No │ Consultation  │ Doctor        │ Time                │ Diagnosis     │");
        System.out.println("├────┼───────────────┼───────────────┼─────────────────────┼───────────────┤");
        for (int i = 0; i < consults.size(); i++) {
            Consultation c = consults.get(i);
            String doctor = c.getDoctor() != null ? c.getDoctor().getName() : "-";
            String diagnosis = c.getDiagnosis() != null ? c.getDiagnosis().getName() : "-";
            String time = c.getConsultationTime() != null ? c.getConsultationTime().toString() : "-";
            System.out.printf("│ %-2d │ %-13s │ %-13s │ %-19s │ %-13s │%n",
                    i + 1,
                    c.getConsultationId(),
                    doctor.length() > 13 ? doctor.substring(0, 10) + "..." : doctor,
                    time.length() > 19 ? time.substring(0, 16) + "..." : time,
                    diagnosis.length() > 13 ? diagnosis.substring(0, 10) + "..." : diagnosis
            );
        }
        System.out.println("└────┴───────────────┴───────────────┴─────────────────────┴───────────────┘");
    }

    private void displayTreatmentsForPatient(String patientId) {
        OrderedMap<String, Treatment> treatments = patientMaintenance.getTreatmentsForPatient(patientId);
        System.out.println("\n=== TREATMENTS ===");
        if (treatments.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }
        System.out.println("┌────┬───────────────┬─────────┬─────────────┬───────────┬───────────────┐");
        System.out.println("│ No │ Treatment ID  │ Status  │ Type        │ Critical  │ Total Cost    │");
        System.out.println("├────┼───────────────┼─────────┼─────────────┼───────────┼───────────────┤");
        for (int i = 0; i < treatments.size(); i++) {
            Treatment t = treatments.get(i);
            System.out.printf("│ %-2d │ %-13s │ %-7s │ %-11s │ %-9s │ RM%10.2f │%n",
                    i + 1,
                    t.getTreatmentID(),
                    t.getStatus(),
                    t.getType().length() > 11 ? t.getType().substring(0, 8) + "..." : t.getType(),
                    t.isCritical() ? "Yes" : "No",
                    t.getTotalProcedureCost()
            );
        }
        System.out.println("└────┴───────────────┴─────────┴─────────────┴───────────┴───────────────┘");
    }

    /**
     * Search patient records
     */
    public void searchPatientRecords() {
        System.out.println("\n=== SEARCH PATIENT RECORDS ===");

        String searchTerm = InputHandler.getString("Enter patient name or ID to search");
        OrderedMap<String, Patient> searchResults = patientMaintenance.searchPatients(searchTerm);

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
    public void displayPatientList(OrderedMap<String, Patient> patients, String title) {
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }
        System.out.println("\n┌────┬───────────────┬────────────────────┬─────┬─────────┬───────────────┬────────────┐");
        System.out.printf("│ #  │ Patient ID    │ Name               │ Age │ Gender  │ Contact       │ Emergency  │%n");
        System.out.println("├────┼───────────────┼────────────────────┼─────┼─────────┼───────────────┼────────────┤");
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            String name = p.getName().length() > 18 ? p.getName().substring(0, 15) + "..." : p.getName();
            String contact = p.getContactNumber().length() > 13 ? p.getContactNumber().substring(0, 10) + "..." : p.getContactNumber();
            System.out.printf("│ %-2d │ %-13s │ %-18s │ %-3d │ %-7s │ %-13s │ %-10s │%n",
                    i + 1,
                    p.getPatientId(),
                    name,
                    p.getAge(),
                    p.getGender(),
                    contact,
                    p.isEmergency() ? "YES" : "No"
            );
        }
        System.out.println("└────┴───────────────┴────────────────────┴─────┴─────────┴───────────────┴────────────┘");
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
        System.out.println("\n┌────┬───────────────┬────────────────────┬─────┬─────────┬───────────────┬────────────┐");
        System.out.printf("│ #  │ Patient ID    │ Name               │ Age │ Gender  │ Contact       │ Emergency  │%n");
        System.out.println("├────┼───────────────┼────────────────────┼─────┼─────────┼───────────────┼────────────┤");
        OrderedMap<String, Patient> queue = queueType.equals("emergency")
                ? patientMaintenance.getEmergencyQueue()
                : patientMaintenance.getNormalQueue();
        int position = 1;
        for (Patient p : queue) {
            String name = p.getName().length() > 18 ? p.getName().substring(0, 15) + "..." : p.getName();
            String contact = p.getContactNumber().length() > 13 ? p.getContactNumber().substring(0, 10) + "..." : p.getContactNumber();
            System.out.printf("│ %-2d │ %-13s │ %-18s │ %-3d │ %-7s │ %-13s │ %-10s │%n",
                    position++,
                    p.getPatientId(),
                    name,
                    p.getAge(),
                    p.getGender(),
                    contact,
                    p.isEmergency() ? "YES" : "No"
            );
        }
        System.out.println("└────┴───────────────┴────────────────────┴─────┴─────────┴───────────────┴────────────┘");
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
        System.out.println("=".repeat(40));
    }

    /**
     * Helper method to view details of a selected patient from a list
     */
    public void viewSelectedPatientDetails(OrderedMap<String, Patient> patients) {
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
        OrderedMap<String, VisitHistory> visitHistories = patientMaintenance.getPatientVisitHistory(patientId);
        System.out.println("\n┌────┬───────────────┬────────────────────┬────────────────────┬────────────┐");
        System.out.printf("│ #  │ Visit ID      │ Visit Date         │ Reason             │ Status     │%n");
        System.out.println("├────┼───────────────┼────────────────────┼────────────────────┼────────────┤");
        for (int i = 0; i < visitHistories.size(); i++) {
            VisitHistory v = visitHistories.get(i);
            String reason = v.getVisitReason().length() > 18 ? v.getVisitReason().substring(0, 15) + "..." : v.getVisitReason();
            String date = DateTimeFormatterUtil.formatForDisplay(v.getVisitDate());
            System.out.printf("│ %-2d │ %-13s │ %-18s │ %-18s │ %-10s │%n",
                    i + 1,
                    v.getVisitId(),
                    date,
                    reason,
                    v.getStatus()
            );
        }
        System.out.println("└────┴───────────────┴────────────────────┴────────────────────┴────────────┘");
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

    /**
     * Helper method to view details of selected visit
     */
    public void viewSelectedVisitDetails(OrderedMap<String, VisitHistory> visits) {
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
        OrderedMap<String, Object> data = patientMaintenance.generatePatientRegistrationReport();
        String now = java.time.ZonedDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a"));

        line();
        System.out.println(center("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY"));
        System.out.println(center("PATIENT MANAGEMENT SUBSYSTEM"));
        System.out.println(center("PATIENT REGISTRATION ANALYSIS REPORT"));
        line();
        System.out.println(rightInDash("generated at: " + now));
        dash(); blank();

        int total = (Integer) data.get("totalPatients");
        int qTot  = (Integer) data.get("patientsInQueue");
        int qEm   = (Integer) data.get("emergencyQueueSize");
        int qNm   = (Integer) data.get("normalQueueSize");

        OrderedMap<String,Integer> gender = (OrderedMap<String,Integer>) data.get("genderBreakdown");
        OrderedMap<String,Integer> ages = (OrderedMap<String,Integer>) data.get("ageGroupBreakdown");
        String[] ageOrder = {"0-18","19-35","36-50","51-65","65+"};

        // Prepare rows for each section
        String[] overview = {
                String.format("Total registered: %d", total),
                String.format("In queues: %d / 20", qTot),
                String.format("└ Emergency: %d", qEm),
                String.format("└ Normal: %d", qNm),
        };
        String[] genderRows = {
                "Gender   Count   %",
                String.format("Male     %3d   %5s", n(gender.get("Male")), pct(n(gender.get("Male")), total)),
                String.format("Female   %3d   %5s", n(gender.get("Female")), pct(n(gender.get("Female")), total))
        };
        String[] ageRows = new String[ageOrder.length + 1];
        ageRows[0] = "Age Group  Count   %";
        for (int i = 0; i < ageOrder.length; i++) {
            int c = n(ages.get(ageOrder[i]));
            ageRows[i+1] = String.format("%-9s %3d   %5s", ageOrder[i], c, pct(c, total));
        }
        System.out.println(center("PATIENT SUMMARY", W));
        dash();

        int col1 = 26, col2 = 20, col3 = 20;

        // header row
        System.out.printf("  %-"+col1+"s │ %-"+col2+"s │ %-"+col3+"s%n",
                "Overview", "Gender", "Age Group");
        System.out.printf("  %-"+col1+"s │ %-"+col2+"s │ %-"+col3+"s%n",
                "", "Count  %", "Count  %");
        dash();

        int maxRows = Math.max(overview.length, Math.max(genderRows.length-1, ageRows.length-1));
        for (int i = 0; i < maxRows; i++) {
            String o = i < overview.length ? overview[i] : "";
            String g = (i+1 < genderRows.length) ? genderRows[i+1] : ""; // skip header
            String a = (i+1 < ageRows.length)   ? ageRows[i+1]   : "";   // skip header
            System.out.printf("  %-"+col1+"s │ %-"+col2+"s │ %-"+col3+"s%n", o, g, a);
        }
        dash(); blank();

        System.out.println(center("AGE GROUP DISTRIBUTION HISTOGRAM", W));
        dash();
        int maxCount = 0;
        for (String age : ageOrder) maxCount = Math.max(maxCount, n(ages.get(age)));
        for (String age : ageOrder) {
            int count = n(ages.get(age));
            int barLen = maxCount == 0 ? 0 : (int) Math.round((count * 40.0) / maxCount);
            String bar = "█".repeat(barLen);
            System.out.printf("  %-9s | %-3d | %s%n", age, count, bar);
        }
        dash();
        System.out.println(center("END OF THE REPORT", W));
        line();
    }

    public void displayPatientVisitSummaryReport() {
        OrderedMap<String,Object> rpt = patientMaintenance.generatePatientVisitSummaryReport();
        String now = java.time.ZonedDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a"));

        line();
        System.out.println(center("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY"));
        System.out.println(center("PATIENT MANAGEMENT SUBSYSTEM"));
        System.out.println(center("PATIENT VISIT SUMMARY REPORT"));
        line();
        System.out.println(rightInDash("generated at: " + now));
        dash(); blank();

        int totalVisits = (Integer) rpt.get("totalVisits");
        double avg = ((Double) rpt.get("averageVisitsPerPatient")).doubleValue();

        OrderedMap<String,Integer> status = (OrderedMap<String,Integer>) rpt.get("statusCounts");
        String[] sts = {"SCHEDULED","IN_PROGRESS","COMPLETED","CANCELLED"};

        OrderedMap<String,Integer> vpm = (OrderedMap<String,Integer>) rpt.get("visitsPerMonth");
        OrderedMap<String,Integer> top = (OrderedMap<String,Integer>) rpt.get("topPatients");

        String[] months = new String[vpm.size()];
        int mCount = 0;
        for (VisitHistory vh : patientMaintenance.getAllVisitHistories()){
            String m = vh.getVisitDate().getYear() + "-" + String.format("%02d", vh.getVisitDate().getMonthValue());
            if (vpm.containsKey(m)) {
                boolean seen = false; for (int j=0;j<mCount;j++) if (months[j].equals(m)) { seen = true; break; }
                if (!seen) months[mCount++] = m;
            }
        }

        String[] topRows = new String[Math.max(1, top.size())];
        int tCount = 0;
        Patient[] all = patientMaintenance.getAllPatientsArray();
        for (int i=0;i<all.length && tCount<top.size();i++){
            Patient p = all[i];
            if (p!=null && top.containsKey(p.getPatientId())){
                topRows[tCount++] = String.format("%-8s %3d", p.getPatientId(), n(top.get(p.getPatientId())));
            }
        }
        if (tCount == 0) topRows[0] = String.format("%-8s %3d", "-", 0);

        String[] overview = {
                String.format("Total visits: %d", totalVisits),
                String.format("Avg/Patient: %.2f", avg)
        };

        String[] statusRows = new String[sts.length];
        for (int i=0;i<sts.length;i++) {
            int c = n(status.get(sts[i]));
            statusRows[i] = String.format("%-11s %3d %5s", sts[i], c, pct(c, totalVisits));
        }

        String[] monthRows = new String[Math.max(1,mCount)];
        if (mCount > 0) {
            for (int i=0;i<mCount;i++) {
                monthRows[i] = String.format("%-7s %3d", months[i], n(vpm.get(months[i])));
            }
        } else {
            monthRows[0] = String.format("%-7s %3d", "-", 0);
        }

        int col1 = 26, col2 = 22, col3 = 18, col4 = 20;

        System.out.println(center("VISIT SUMMARY", W));
        dash();
        System.out.printf("  %-"+col1+"s │ %-"+col2+"s │ %-"+col3+"s │ %-"+col4+"s%n",
                "Overview", "Status", "Visits per Month", "Top Patients");
        dash();

        int maxRows = Math.max(overview.length,
                Math.max(statusRows.length,
                        Math.max(monthRows.length, topRows.length)));

        for (int i=0;i<maxRows;i++) {
            String o = (i < overview.length)   ? overview[i]   : "";
            String s = (i < statusRows.length)? statusRows[i] : "";
            String m = (i < monthRows.length) ? monthRows[i]  : "";
            String t = (i < topRows.length)   ? topRows[i]    : "";
            System.out.printf("  %-"+col1+"s │ %-"+col2+"s │ %-"+col3+"s │ %-"+col4+"s%n", o, s, m, t);
        }
        dash();
        System.out.println(center("VISITS PER MONTH HISTOGRAM", W));
        dash();
        int maxMonthCount = 0;
        for (String month : months) maxMonthCount = Math.max(maxMonthCount, n(vpm.get(month)));
        for (String month : months) {
            int count = n(vpm.get(month));
            int barLen = maxMonthCount == 0 ? 0 : (int) Math.round((count * 40.0) / maxMonthCount);
            String bar = "█".repeat(barLen);
            System.out.printf("  %-7s | %-3d | %s%n", month, count, bar);
        }
        dash();
        System.out.println(center("END OF THE REPORT", W));
        line();
    }


    private static String center(String s, int w) {
        int pad = Math.max(0, (w - s.length()) / 2);
        return " ".repeat(pad) + s;
    }

    private static final int W = 108;

    private static void line() { System.out.println("=".repeat(W)); }
    private static void dash() { System.out.println("-".repeat(W)); }
    private static void blank() { System.out.println(); }

    private static String center(String s) {
        if (s.length() >= W) return s;
        int pad = (W - s.length()) / 2;
        return " ".repeat(pad) + s;
    }
    private static String rightInDash(String s) {
        String left = "-".repeat(Math.max(0, W - s.length()));
        return left.substring(0, Math.max(0, left.length() - 1)) + " " + s;
    }
    private static int n(Integer v){ return v==null?0:v; }
    private static String pct(int part, int total){
        double p = (total==0?0.0: (part*100.0/total));
        return String.format("%.1f%%", p);
    }

    public static void main(String[] args) {
        new PatientUI(new PatientMaintenance()).displayMenu();
    }
}