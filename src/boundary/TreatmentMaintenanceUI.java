package boundary;

import adt.CustomADT;
import control.*;
import entity.*;
import utility.*;


public class TreatmentMaintenanceUI {
    private final TreatmentMaintenance treatmentController;
    private final PatientMaintenance patientController;
    private final ProcedureUI procedureUI;

    public TreatmentMaintenanceUI() {
        this.treatmentController = new TreatmentMaintenance();
        this.patientController = new PatientMaintenance();
        this.procedureUI = new ProcedureUI();
    }

    /**
     * Main menu
     */
    public void displayMenu() {
        int choice;
        do {
            printMenu();
            choice = InputHandler.getInt("Select an option", 0, 6);
            
            switch(choice) {
                case 1: 
                    createTreatment();
                    break;
                case 2:
                    viewTreatmentsMenu();
                    break;            
                case 3:
                    updateTreatment();
                    break;
                case 4:
                    removeTreatment();
                    break;
                case 5:
                    processTreatmentsMenu();
                    break;
                case 6:
                    procedureUI.displayMainMenu();
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

    /*
     * Display menu options
     */
    public void printMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("   TREATMENT MAINTENANCE MENU");
        System.out.println("=".repeat(40));
        System.out.println("1. Create Treatment");
        System.out.println("2. View Treatments");
        System.out.println("3. Update Treatment");
        System.out.println("4. Remove Treatment");
        System.out.println("5. Process Treatments");
        System.out.println("6. Manage Procedures");
        System.out.println("0. Back to Main Menu");
        System.out.println("=".repeat(40));
    }

    /**
     * View Treatments submenu
     */
    public void viewTreatmentsMenu() {
        int choice;
        do {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("       VIEW TREATMENTS MENU");
            System.out.println("=".repeat(40));
            System.out.println("1. View All Treatments");
            System.out.println("2. View Treatments by Patient");
            System.out.println("3. View Treatments by Status");
            System.out.println("0. Back to Treatment Menu");
            System.out.println("=".repeat(40));
            
            choice = InputHandler.getInt("Select an option", 0, 3);
            
            switch(choice) {
                case 1:
                    displayAllTreatments();
                    break;
                case 2:
                    viewTreatmentsByPatient();
                    break;
                case 3:
                    viewTreatmentsByStatus();
                    break;
                case 0:
                    System.out.println("Returning to treatment menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            
            if (choice != 0) {
                InputHandler.pauseForUser();
            }
            
        } while (choice != 0);
    }

    /*
     * Workflow to create new treatment from existing consultation
     */
    public void createTreatment() {
        System.out.println("\n=== CREATE NEW TREATMENT ===");

        // Get available consultations
        CustomADT<String, Consultation> availableConsultations = treatmentController.getConsultationsWithoutTreatment();

        if (availableConsultations.isEmpty()) {
            System.out.println("No consultations available for treatment creation.");
            return;
        }

        // Display consultation list
        displayAvailableConsultations(availableConsultations);

        // Get user choice
        System.out.println("0. Cancel");
        int consultationChoice = InputHandler.getInt(0, availableConsultations.size() - 1);
        if (consultationChoice == 0) {
            System.out.println("Operation cancelled.");
            return;
        }

        String consultationId = availableConsultations.get(consultationChoice - 1).getConsultationId();

        String treatmentType = getTreatmentType();
        if (treatmentType == null) {
            System.out.println("Operation cancelled.");
            return;
        }

        boolean isCritical = InputHandler.getYesNo("Is this treatment critical?");
        String notes = InputHandler.getOptionalString("Enter treatment notes");

        // Create the treatment (delegate to business logic)
        Treatment treatment = treatmentController.createTreatment(
            consultationId,
            treatmentType,
            isCritical,
            notes
        );

        if (treatment != null) {
            System.out.println("✅ Treatment created successfully!");
            System.out.println("Treatment ID: " + treatment.getTreatmentID());
        } else {
            System.out.println("❌ Failed to create treatment.");
        }

    }

    /**
     * Display available consultations
     */
    public void displayAvailableConsultations(CustomADT<String, Consultation> consultations) {
        System.out.println("\n=== AVAILABLE CONSULTATIONS ===");

        for (int i = 0; i < consultations.size(); i++) {
            Consultation consultation = consultations.get(i);

            System.out.printf("%d. ID: %s | Patient: %s | Doctor: %s | Date: %s | Diagnosis: %s%n",
                i + 1,
                consultation.getConsultationId(),
                consultation.getPatient() != null ? consultation.getPatient().getName() : "N/A",
                consultation.getDoctor() != null ? consultation.getDoctor().getName() : "N/A",
                DateTimeFormatterUtil.formatForDisplay(consultation.getConsultationTime()),
                consultation.getDiagnosis() != null ? consultation.getDiagnosis().getName() : "N/A"
            );
        }
        System.out.println();
    }

    public String getTreatmentType() {
        String[] types = {"OUTPATIENT", "INPATIENT", "EMERGENCY", "FOLLOW_UP"};
        System.out.println("Select treatment type:");
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i]);
        }

        System.out.println("0. Cancel");
        int choice = InputHandler.getInt(0, types.length);
        if (choice == 0) {
            return null; // Cancelled
        }
        return types[choice - 1];
    }

    /** 
     * Display all treatments with interactive options
     */
    public void displayAllTreatments() {
        CustomADT<String, Treatment> treatments = treatmentController.getAllTreatments();
        if (treatments.isEmpty()) {
            System.out.println("No treatments available.");
            return;
        }

        // Use our reusable display function
        displayTreatmentList(treatments, "All Treatments");
        
        // Option to view details or update
        System.out.println("\nOptions:");
        System.out.println("1. View Treatment Details");
        System.out.println("2. Update Treatment");
        System.out.println("0. Back to View Menu");
        
        int actionChoice = InputHandler.getInt("Select action", 0, 2);
        
        switch (actionChoice) {
            case 1:
                viewSelectedTreatmentDetails(treatments);
                break;
            case 2:
                updateSelectedTreatment(treatments);
                break;
            case 0:
                return; // Back to menu
        }
    }

    /**
     * Update treatment functionality
     */
    public void updateTreatment() {
        System.out.println("\n=== UPDATE TREATMENT ===");
        
        CustomADT<String, Treatment> treatments = treatmentController.getAllTreatments();
        if (treatments.isEmpty()) {
            System.out.println("No treatments available to update.");
            return;
        }
        
        displayTreatmentList(treatments, "Select Treatment to Update");

        String treatmentID = InputHandler.getString("Enter Treatment ID to update");
        Treatment treatment = treatmentController.getTreatmentByID(treatmentID);        
        if (treatment == null) {
            System.out.println("Treatment not found.");
            return;
        }
        
        updateSpecificTreatment(treatment);
    }

     /*
      * Remove treatment functionality
      */
      public void removeTreatment() {
        System.out.println("\n=== REMOVE TREATMENT ===");
        
        CustomADT<String, Treatment> treatments = treatmentController.getAllTreatments();
        if (treatments.isEmpty()) {
            System.out.println("No treatments available to remove.");
            return;
        }
        
        displayTreatmentList(treatments, "Select Treatment to Remove");
        
        String treatmentID = InputHandler.getString("Enter Treatment ID to remove");
        boolean success = treatmentController.removeTreatment(treatmentID);

        if (success) {
            System.out.println("✅ Treatment removed successfully.");
        } else {
            System.out.println("❌ Failed to remove treatment. It may not exist.");
        }
      }


      /*
       * View treatments by patient functionality
       */
      public void viewTreatmentsByPatient() {
        System.out.println("\n=== VIEW TREATMENTS BY PATIENT ===");
        CustomADT<String, Patient> patients = treatmentController.getPatientsWithTreatments();
        if (patients.isEmpty()) {
            System.out.println("No patients with treatments found.");
            return;
        }
        
        displayPatientsWithTreatments(patients);
        int choice = InputHandler.getInt("Select a patient", 1, patients.size());
        String patientId = patients.get(choice - 1).getPatientId();

        // Get treatments for selected patient
        CustomADT<String, Treatment> patientTreatments = treatmentController.getTreatmentsByPatient(patientId);
        
        if (patientTreatments.isEmpty()) {
            System.out.println("No treatments found for selected patient.");
            return;
        }
        
        // Display treatments using our reusable function
        Patient selectedPatient = patients.get(choice - 1);
        displayTreatmentList(patientTreatments, "Treatments for Patient: " + selectedPatient.getName());
        
        // Option to view details or update
        System.out.println("\nOptions:");
        System.out.println("1. View Treatment Details");
        System.out.println("2. Update Treatment");
        System.out.println("0. Back to Treatment Menu");
        
        int actionChoice = InputHandler.getInt("Select action", 0, 2);
        
        switch (actionChoice) {
            case 1:
                viewSelectedTreatmentDetails(patientTreatments);
                break;
            case 2:
                updateSelectedTreatment(patientTreatments);
                break;
            case 0:
                return; // Back to menu
        }
      }

      /**
         * View treatments by status functionality
         */
        public void viewTreatmentsByStatus() {
            System.out.println("\n=== VIEW TREATMENTS BY STATUS ===");
            
            // Status options
            String[] statusOptions = {
                "SCHEDULED", 
                "IN_PROGRESS", 
                "COMPLETED"
            };
            
            System.out.println("Select status to view:");
            for (int i = 0; i < statusOptions.length; i++) {
                System.out.println((i + 1) + ". " + statusOptions[i]);
            }
            System.out.println("0. Back to Treatment Menu");
            
            int choice = InputHandler.getInt("Select status", 0, statusOptions.length);
            if (choice == 0) {
                return; // Back to menu
            }
            
            String selectedStatus = statusOptions[choice - 1];
            
            // Get treatments by status from controller
            CustomADT<String, Treatment> statusTreatments = treatmentController.getTreatmentsByStatus(selectedStatus);
            
            if (statusTreatments.isEmpty()) {
                System.out.println("No treatments found with status: " + selectedStatus);
                return;
            }
            
            // Display the treatments using our reusable function
            displayTreatmentList(statusTreatments, "Treatments with Status: " + selectedStatus);
            
            // Option to view details or update
            System.out.println("\nOptions:");
            System.out.println("1. View Treatment Details");
            System.out.println("2. Update Treatment");
            System.out.println("0. Back to Treatment Menu");
            
            int actionChoice = InputHandler.getInt("Select action", 0, 2);
            
            switch (actionChoice) {
                case 1:
                    viewSelectedTreatmentDetails(statusTreatments);
                    break;
                case 2:
                    updateSelectedTreatment(statusTreatments);
                    break;
                case 0:
                    return; // Back to menu
            }
        }

      /**
       * Display patients with existing treatments
       */
      public void displayPatientsWithTreatments(CustomADT<String, Patient> patients) {
        if (patients.isEmpty()) {
            System.out.println("No patients with treatments found.");
            return;
        }
        System.out.println("\n=== PATIENTS WITH TREATMENTS ===");
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            System.out.printf("%d. ID: %s | Name: %s | Age: %d\n",
                    i + 1, patient.getPatientId(), patient.getName(), patient.getAge());
        }
    }

    /**
     * Display detailed information for a single treatment
     * @param treatment The treatment to display
     */
    public void displayTreatmentDetails(Treatment treatment) {
        if (treatment == null) {
            System.out.println("No treatment data available.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                 TREATMENT DETAILS");
        System.out.println("=".repeat(60));
        
        // Basic Information
        System.out.printf("Treatment ID    : %s\n", treatment.getTreatmentID());
        System.out.printf("Consultation ID : %s\n", treatment.getConsultationID());
        System.out.printf("Type            : %s\n", treatment.getType());
        System.out.printf("Status          : %s\n", treatment.getStatus());
        System.out.printf("Critical        : %s\n", treatment.isCritical() ? "YES" : "No");
        System.out.printf("Date/Time       : %s\n", DateTimeFormatterUtil.formatForDisplay(treatment.getTreatmentDate()));
        
        // Patient Information
        System.out.println("\n--- PATIENT INFORMATION ---");
        if (treatment.getPatient() != null) {
            Patient patient = treatment.getPatient();
            System.out.printf("Patient ID      : %s\n", patient.getPatientId());
            System.out.printf("Name            : %s\n", patient.getName());
            System.out.printf("Age             : %d\n", patient.getAge());
            System.out.printf("Gender          : %s\n", patient.getGender());
            System.out.printf("Contact         : %s\n", patient.getContactNumber());
        } else {
            System.out.println("Patient information not available.");
        }
        
        // Doctor Information
        System.out.println("\n--- DOCTOR INFORMATION ---");
        if (treatment.getDoctor() != null) {
            Doctor doctor = treatment.getDoctor();
            System.out.printf("Doctor ID       : %s\n", doctor.getDoctorID());
            System.out.printf("Name            : %s\n", doctor.getName());
            System.out.printf("Specialty       : %s\n", doctor.getSpecialty());
            System.out.printf("Contact         : %s\n", doctor.getPhone());
        } else {
            System.out.println("Doctor information not available.");
        }
        
        // Diagnosis Information
        System.out.println("\n--- DIAGNOSIS ---");
        if (treatment.getDiagnosis() != null) {
            Diagnosis diagnosis = treatment.getDiagnosis();
            System.out.printf("Diagnosis ID    : %s\n", diagnosis.getId());
            System.out.printf("Name            : %s\n", diagnosis.getName());
            System.out.printf("Description     : %s\n", diagnosis.getDescription());
            System.out.printf("Severity        : %s\n", diagnosis.getSeverity());
        } else {
            System.out.println("Diagnosis information not available.");
        }
        
        // Procedures
        System.out.println("\n--- PROCEDURES ---");
        if (treatment.hasProcedures()) {
            System.out.printf("Number of procedures: %d\n", treatment.getProcedures().size());
            System.out.printf("Total procedure cost: RM %.2f\n", treatment.getTotalProcedureCost());
        } else {
            System.out.println("No procedures recorded for this treatment.");
        }
        
        // Prescriptions
        System.out.println("\n--- PRESCRIPTIONS ---");
        if (treatment.hasPrescriptions()) {
            System.out.println("Prescriptions available (details would require Prescription implementation)");
        } else {
            System.out.println("No prescriptions for this treatment.");
        }
        
        // Notes
        System.out.println("\n--- TREATMENT NOTES ---");
        if (treatment.getNotes() != null && !treatment.getNotes().trim().isEmpty()) {
            System.out.println(treatment.getNotes());
        } else {
            System.out.println("No additional notes.");
        }
        
        System.out.println("=".repeat(60));
    }

    /**
     * Display a list of treatments with essential information
     * @param treatments CustomADT of treatments to display
     * @param title Title for the list
     */
    public void displayTreatmentList(CustomADT<String, Treatment> treatments, String title) {
        if (treatments.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("  " + title.toUpperCase());
        System.out.println("=".repeat(80));
        System.out.printf("%-4s %-12s %-15s %-15s %-20s %-12s %-8s\n", 
                         "#", "ID", "Patient", "Doctor", "Date", "Status", "Critical");
        System.out.println("-".repeat(80));
        
        for (int i = 0; i < treatments.size(); i++) {
            Treatment treatment = treatments.get(i);
            if (treatment != null) {
                String patientName = treatment.getPatient() != null ? 
                    treatment.getPatient().getName() : "N/A";
                String doctorName = treatment.getDoctor() != null ? 
                    treatment.getDoctor().getName() : "N/A";
                
                // Truncate long names for table formatting
                if (patientName.length() > 14) patientName = patientName.substring(0, 11) + "...";
                if (doctorName.length() > 14) doctorName = doctorName.substring(0, 11) + "...";
                
                System.out.printf("%-4d %-12s %-15s %-15s %-20s %-12s %-8s\n",
                    i + 1,
                    treatment.getTreatmentID(),
                    patientName,
                    doctorName,
                    DateTimeFormatterUtil.formatForDisplay(treatment.getTreatmentDate()),
                    treatment.getStatus(),
                    treatment.isCritical() ? "YES" : "No"
                );
            }
        }
        System.out.println("=".repeat(80));
    }

    /**
     * Helper method to view details of a selected treatment from a list
     */
    public void viewSelectedTreatmentDetails(CustomADT<String, Treatment> treatments) {
        if (treatments.isEmpty()) {
            System.out.println("No treatments available.");
            return;
        }
        
        int choice = InputHandler.getInt("Select treatment to view", 1, treatments.size());
        Treatment selectedTreatment = treatments.get(choice - 1);
        
        if (selectedTreatment != null) {
            displayTreatmentDetails(selectedTreatment);
        } else {
            System.out.println("Treatment not found.");
        }
    }
    
    /**
     * Helper method to update a selected treatment from a list
     */
    public void updateSelectedTreatment(CustomADT<String, Treatment> treatments) {
        if (treatments.isEmpty()) {
            System.out.println("No treatments available.");
            return;
        }
        
        int choice = InputHandler.getInt("Select treatment to update", 1, treatments.size());
        Treatment selectedTreatment = treatments.get(choice - 1);
        
        if (selectedTreatment != null) {
            updateSpecificTreatment(selectedTreatment);
        } else {
            System.out.println("Treatment not found.");
        }
    }
    
    /**
     * Helper function to update a specific treatment
     */
    public void updateSpecificTreatment(Treatment treatment) {
        System.out.println("\n=== UPDATE TREATMENT ===");
        System.out.println("Selected Treatment: " + treatment.getTreatmentID());
        
        // Show a list of fields to update
        System.out.println("Select field to update:");
        System.out.println("1. Notes");
        System.out.println("2. Critical Status");
        System.out.println("3. Type");
        System.out.println("0. Cancel");
        
        int choice = InputHandler.getInt("Select field", 0, 3);
        
        switch (choice) {
            case 1:
                String notes = InputHandler.getOptionalString("Enter new notes");
                treatment.setNotes(notes);
                System.out.println("✅ Notes updated successfully.");
                break;
            case 2:
                boolean isCritical = InputHandler.getYesNo("Is this treatment critical?");
                treatment.setCritical(isCritical);
                System.out.println("✅ Critical status updated successfully.");
                break;
            case 3:
                String type = getTreatmentType();
                if (type != null) {
                    treatment.setType(type);
                    System.out.println("✅ Treatment type updated successfully.");
                } else {
                    System.out.println("Operation cancelled.");
                }
                break;
            case 0:
                System.out.println("Update cancelled.");
                break;
        }
    }

    /**
     * Treatment processing menu
     */
    public void processTreatmentsMenu() {
        int choice;
        do { 
            printProcessMenu();
            choice = InputHandler.getInt("Select an option", 0, 4);

            switch(choice) {
                case 1:
                    processNextEmergency();
                    break;
                case 2:
                    processNextRegular();
                    break;
                case 3:
                    viewRecentActivities();
                    break;
                case 4:
                    displayProcessingStatistics();
                    break;
                case 0:
                    System.out.println("Returning to treatment menu...");
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
     * Display processing menu options
     */
    public void printProcessMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("   TREATMENT PROCESSING MENU");
        System.out.println("=".repeat(40));
        System.out.println("1. Process Next Emergency Treatment");
        System.out.println("2. Process Next Regular Treatment");
        System.out.println("3. View Recent Processing Activities");
        System.out.println("4. Processing Statistics");
        System.out.println("0. Back to Treatment Menu");
        System.out.println("=".repeat(40));
    }

    /**
     * Process next emergency treatment
     */
    public void processNextEmergency() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         PROCESS NEXT EMERGENCY");
        System.out.println("=".repeat(50));

        Treatment treatment = treatmentController.processNextEmergency();

        if (treatment != null) {
            System.out.println("🚨 Emergency treatment processed successfully!");
            System.out.println("⏰ Processing Time: " + DateTimeFormatterUtil.getCurrentTimestamp());
            
            // Display brief treatment info
            System.out.println("\n--- PROCESSED TREATMENT ---");
            System.out.println("Treatment ID: " + treatment.getTreatmentID());
            System.out.println("Patient: " + (treatment.getPatient() != null ? 
                                            treatment.getPatient().getName() : "N/A"));
            System.out.println("Type: " + treatment.getType());
            System.out.println("Status: " + treatment.getStatus());

            // Ask if user wants to view full details
            boolean viewDetails = InputHandler.getYesNo("View full treatment details?");
            if (viewDetails) {
                displayTreatmentDetails(treatment);
            }

            System.out.println("\n✅ Emergency treatment processed successfully.");
        } else {
            System.out.println("❌ No emergency treatments available to process.");
            System.out.println("All emergency treatments are either completed or none are pending.");
        }
    }

    /**
     * Process next regular treatment
     */
    public void processNextRegular() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         PROCESS NEXT REGULAR");
        System.out.println("=".repeat(50));

        Treatment treatment = treatmentController.processNextRegular();

        if (treatment != null) {
            System.out.println("📋 Regular treatment processed successfully!");
            System.out.println("⏰ Processing Time: " + DateTimeFormatterUtil.getCurrentTimestamp());
            
            // Display brief treatment info
            System.out.println("\n--- PROCESSED TREATMENT ---");
            System.out.println("Treatment ID: " + treatment.getTreatmentID());
            System.out.println("Patient: " + (treatment.getPatient() != null ? 
                                            treatment.getPatient().getName() : "N/A"));
            System.out.println("Type: " + treatment.getType());
            System.out.println("Status: " + treatment.getStatus());

            // Ask if user wants to view full details
            boolean viewDetails = InputHandler.getYesNo("View full treatment details?");
            if (viewDetails) {
                displayTreatmentDetails(treatment);
            }

            System.out.println("\n✅ Regular treatment processed successfully.");
        } else {
            System.out.println("❌ No regular treatments available to process.");
            System.out.println("All regular treatments are either completed or none are pending.");
        }
    }

    /**
     * View recent treatment activities
     */
    public void viewRecentActivities() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         RECENT ACTIVITIES");
        System.out.println("=".repeat(50));

        CustomADT<String, String> recents = treatmentController.getRecentTreatments();

        if (recents.isEmpty()) {
            System.out.println("No recent treatment activities found.");
            System.out.println("Recently processed treatments will appear here.");
            return;
        }

        System.out.println("📚 Recent treatment activities (most recent first):");
        System.out.println("-".repeat(50));

        int count = 1;
        for (String activity : recents) {
            System.out.printf("%2d . %s\n", count, activity);
            count++;
        }

        System.out.println("-".repeat(50));
        System.out.printf("Total activities recorded: %d\n", recents.size());

        // Option to clear recent activities
        if (recents.size() > 0) {
            boolean clearHistory = InputHandler.getYesNo("Clear recent treatment history?");
            if (clearHistory) {
                if (treatmentController.clearRecentTreatments()) {
                    System.out.println("🗑️ Recent treatment history cleared successfully.");
                } else {
                    System.out.println("❌ Failed to clear recent treatment history.");
                }
            }
        }
    }

    /**
     * Display processing statistics
     */
    public void displayProcessingStatistics() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              PROCESSING STATISTICS");
        System.out.println("=".repeat(60));
        
        // Get all treatments for analysis
        CustomADT<String, Treatment> allTreatments = treatmentController.getAllTreatments();
        
        if (allTreatments.isEmpty()) {
            System.out.println("ℹ️  No treatment data available for statistics.");
            return;
        }
        
        // Count treatments by status
        int scheduledCount = 0;
        int inProgressCount = 0;
        int completedCount = 0;
        int emergencyCount = 0;
        int regularCount = 0;
        int criticalCount = 0;
        
        for (Treatment treatment : allTreatments) {
            // Count by status
            switch (treatment.getStatus().toUpperCase()) {
                case "SCHEDULED":
                    scheduledCount++;
                    break;
                case "IN_PROGRESS":
                    inProgressCount++;
                    break;
                case "COMPLETED":
                    completedCount++;
                    break;
            }
            
            // Count by type
            if ("EMERGENCY".equals(treatment.getType())) {
                emergencyCount++;
            } else {
                regularCount++;
            }
            
            // Count critical treatments
            if (treatment.isCritical()) {
                criticalCount++;
            }
        }
        
        // Display statistics
        System.out.println("📊 TREATMENT OVERVIEW:");
        System.out.println("-".repeat(30));
        System.out.printf("Total Treatments      : %d\n", allTreatments.size());
        System.out.println();
        
        System.out.println("📋 BY STATUS:");
        System.out.println("-".repeat(30));
        System.out.printf("Scheduled            : %d\n", scheduledCount);
        System.out.printf("In Progress          : %d\n", inProgressCount);
        System.out.printf("Completed            : %d\n", completedCount);
        System.out.println();
        
        System.out.println("🏥 BY TYPE:");
        System.out.println("-".repeat(30));
        System.out.printf("Emergency            : %d\n", emergencyCount);
        System.out.printf("Regular              : %d\n", regularCount);
        System.out.println();
        
        System.out.println("⚠️  PRIORITY:");
        System.out.println("-".repeat(30));
        System.out.printf("Critical Treatments  : %d\n", criticalCount);
        System.out.printf("Non-Critical         : %d\n", allTreatments.size() - criticalCount);
        System.out.println();
        
        // Processing progress
        double completionRate = allTreatments.size() > 0 ? 
            (double) completedCount / allTreatments.size() * 100 : 0;
        
        System.out.println("📈 PROCESSING PROGRESS:");
        System.out.println("-".repeat(30));
        System.out.printf("Completion Rate      : %.1f%%\n", completionRate);
        System.out.printf("Pending Treatments   : %d\n", scheduledCount + inProgressCount);
        
        // Get recent activities count
        CustomADT<String, String> recentActivities = treatmentController.getRecentTreatments();
        System.out.printf("Recent Activities    : %d\n", recentActivities.size());
        
        System.out.println("=".repeat(60));
        
        // Quick action suggestions
        if (emergencyCount > 0 && scheduledCount > 0) {
            System.out.println("💡 QUICK ACTIONS:");
            System.out.println("   • Process emergency treatments first");
            System.out.println("   • Check critical treatment status");
        }
        
        if (criticalCount > 0) {
            System.out.println("⚠️  ATTENTION: " + criticalCount + " critical treatments require immediate attention!");
        }
    }











    // TODO: UI, and control logic for adding Prescription


    //TODO: UI, and control logic for adding Procedures
}