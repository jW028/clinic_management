/**
 *@author Tan Jin Wei
 */


package boundary;

import adt.OrderedMap;
import control.*;
import entity.*;
import java.time.LocalDateTime;
import utility.*;


public class TreatmentMaintenanceUI {
    private final TreatmentMaintenance treatmentController;
    private final PharmacyMaintenance pharmacyController;
    private final ProcedureUI procedureUI;

    public TreatmentMaintenanceUI() {
        this.treatmentController = new TreatmentMaintenance();
        this.pharmacyController = new PharmacyMaintenance();
        this.procedureUI = new ProcedureUI();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nSaving data before exit...");
            treatmentController.saveAllData();
        }));
    }

    /**
     * Main menu
     */
    public void displayMenu() {
        int choice;
        do {
            printMenu();
            choice = InputHandler.getInt("Select an option", 0, 9);

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
                    searchTreatmentsMenu();
                    break;
                case 7:
                    procedureUI.displayMainMenu();
                    break;
                case 8:
                    reportsMenu();
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
        System.out.println("\n┌" + "─".repeat(40) + "┐");
        System.out.println("│       TREATMENT MAINTENANCE MENU       │");
        System.out.println("├" + "─".repeat(40) + "┤");
        System.out.println("│ 1. Create Treatment                    │");
        System.out.println("│ 2. View Treatments                     │");
        System.out.println("│ 3. Update Treatment                    │");
        System.out.println("│ 4. Remove Treatment                    │");
        System.out.println("│ 5. Process Treatments                  │");
        System.out.println("│ 6. Search Treatments                   │");
        System.out.println("│ 7. Manage Procedures                   │");
        System.out.println("│ 8. Reports and Statistics              │");
        System.out.println("│ 0. Back to Main Menu                   │");
        System.out.println("└" + "─".repeat(40) + "┘");
    }

    /**
     * View Treatments submenu
     */
    public void viewTreatmentsMenu() {
        int choice;
        do {
            System.out.println("\n┌" + "─".repeat(40) + "┐");
            System.out.println("│          VIEW TREATMENTS MENU          │");
            System.out.println("├" + "─".repeat(40) + "┤");
            System.out.println("│ 1. View All Treatments                 │");
            System.out.println("│ 2. View Treatments by Patient          │");
            System.out.println("│ 3. View Treatments by Status           │");
            System.out.println("│ 4. Sort Treatments                     │");
            System.out.println("│ 0. Back to Treatment Menu              │");
            System.out.println("└" + "─".repeat(40) + "┘");

            choice = InputHandler.getInt("Select an option", 0, 4);

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
                case 4:
                    sortTreatmentsMenu();
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
     * Complete treatment creation workflow with integrated prescription and procedure addition
     */
    public void createTreatment() {
        System.out.println("\n┌" + "─".repeat(52) + "┐");
        printCenteredTableHeader("CREATE NEW TREATMENT", 52);
        System.out.println("└" + "─".repeat(52) + "┘");

        try {
            // Step 1: Get available consultations
            OrderedMap<String, Consultation> availableConsultations = treatmentController.getConsultationsWithoutTreatment();

            if (availableConsultations.isEmpty()) {
                System.out.println("\n┌" + "─".repeat(50) + "┐");
                printCenteredTableHeader("NO CONSULTATIONS AVAILABLE", 50);
                System.out.println("├" + "─".repeat(50) + "┤");
                System.out.println("│ No consultations available for treatment        │");
                System.out.println("│ creation. Please create consultations first.    │");
                System.out.println("└" + "─".repeat(50) + "┘");
                return;
            }

            // Step 2: Display consultation list and get selection
            displayAvailableConsultations(availableConsultations);

            System.out.println("\n┌" + "─".repeat(40) + "┐");
            printCenteredTableHeader("SELECT CONSULTATION", 40);
            System.out.println("├" + "─".repeat(40) + "┤");
            System.out.println("│ 0. Cancel                              │");
            System.out.println("└" + "─".repeat(40) + "┘");

            int consultationChoice = InputHandler.getInt("Select consultation", 0, availableConsultations.size());

            if (consultationChoice == 0) {
                System.out.println("\n┌" + "─".repeat(35) + "┐");
                printCenteredTableHeader("CANCELLED", 35);
                System.out.println("├" + "─".repeat(35) + "┤");
                System.out.println("│ Operation cancelled by user.      │");
                System.out.println("└" + "─".repeat(35) + "┘");
                return;
            }

            String consultationId = availableConsultations.get(consultationChoice - 1).getConsultationId();

            // Step 3: Get treatment details
            String treatmentType = getTreatmentType();
            if (treatmentType == null) {
                System.out.println("\n┌" + "─".repeat(35) + "┐");
                printCenteredTableHeader("CANCELLED", 35);
                System.out.println("├" + "─".repeat(35) + "┤");
                System.out.println("│ Operation cancelled by user.      │");
                System.out.println("└" + "─".repeat(35) + "┘");
                return;
            }

            boolean isCritical = InputHandler.getYesNo("Is this treatment critical?");
            String notes = InputHandler.getOptionalString("Enter treatment notes");

            // Step 4: Create the treatment
            Treatment treatment = treatmentController.createTreatment(
                    consultationId,
                    treatmentType,
                    isCritical,
                    notes
            );

            if (treatment == null) {
                System.out.println("\n┌" + "─".repeat(45) + "┐");
                printCenteredTableHeader("CREATION FAILED", 45);
                System.out.println("├" + "─".repeat(45) + "┤");
                System.out.println("│ ❌ Failed to create treatment.           │");
                System.out.println("└" + "─".repeat(45) + "┘");
                return;
            }

            // Step 5: Display created treatment summary
            displayTreatmentCreationSummary(treatment);

            // Step 6: Add Procedures (Optional)
            System.out.println("\n┌" + "─".repeat(50) + "┐");
            printCenteredTableHeader("PROCEDURE ADDITION", 50);
            System.out.println("└" + "─".repeat(50) + "┘");

            boolean addProcedures = InputHandler.getYesNo("Would you like to add procedures to this treatment?");

            if (addProcedures) {
                addProceduresToNewTreatment(treatment);
            }

            // Step 7: Add Prescription (Optional)
            System.out.println("\n┌" + "─".repeat(50) + "┐");
            printCenteredTableHeader("PRESCRIPTION ADDITION", 50);
            System.out.println("└" + "─".repeat(50) + "┘");

            boolean addPrescription = InputHandler.getYesNo("Would you like to add a prescription to this treatment?");

            if (addPrescription) {
                addPrescriptionToNewTreatment(treatment);
            }

            // Step 8: Final Summary
            displayFinalTreatmentSummary(treatment);

            System.out.println("\n┌" + "─".repeat(65) + "┐");
            printCenteredTableHeader("TREATMENT CREATION COMPLETED", 65);
            System.out.println("├" + "─".repeat(65) + "┤");
            System.out.println("│ Treatment ID: " + centerText(treatment.getTreatmentID() + " is ready for processing.", 49) + " │");
            System.out.println("└" + "─".repeat(65) + "┘");

        } catch (Exception e) {
            System.out.println("ERROR");
            System.out.println("❌ Error creating treatment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Display available consultations
     */
    public void displayAvailableConsultations(OrderedMap<String, Consultation> consultations) {
        System.out.println("\n┌" + "─".repeat(TableWidths.FULL_WIDTH) + "┐");
        printCenteredTableHeader("AVAILABLE CONSULTATIONS", TableWidths.FULL_WIDTH);
        System.out.println("├" + "─".repeat(4) + "┬" + "─".repeat(14) + "┬" + "─".repeat(20) + "┬" + "─".repeat(20) + "┬" + "─".repeat(18) + "┬" + "─".repeat(19) + "┤");
        System.out.println("│ #  │ Consult ID   │ Patient Name       │ Doctor Name        │ Date             │ Diagnosis         │");
        System.out.println("├" + "─".repeat(4) + "┼" + "─".repeat(14) + "┼" + "─".repeat(20) + "┼" + "─".repeat(20) + "┼" + "─".repeat(18) + "┼" + "─".repeat(19) + "┤");

        for (int i = 0; i < consultations.size(); i++) {
            Consultation consultation = consultations.get(i);

            String patientName = consultation.getPatient() != null ?
                    consultation.getPatient().getName() : "N/A";
            String doctorName = consultation.getDoctor() != null ?
                    consultation.getDoctor().getName() : "N/A";
            String diagnosisName = consultation.getDiagnosis() != null ?
                    consultation.getDiagnosis().getName() : "N/A";
            String dateStr = DateTimeFormatterUtil.formatForDisplay(consultation.getConsultationTime());

            // Truncate text if too long
            String truncatedPatient = patientName.length() > 16 ? patientName.substring(0, 13) + "..." : patientName;
            String truncatedDoctor = doctorName.length() > 16 ? doctorName.substring(0, 13) + "..." : doctorName;
            String truncatedDiagnosis = diagnosisName.length() > 18 ? diagnosisName.substring(0, 15) + "..." : diagnosisName;

            System.out.printf("│ %-2d │ %-12s │ %-18s │ %-18s │ %-14s │ %-17s │%n",
                    i + 1,
                    consultation.getConsultationId(),
                    truncatedPatient,
                    truncatedDoctor,
                    dateStr,
                    truncatedDiagnosis
            );
        }

        System.out.println("└" + "─".repeat(4) + "┴" + "─".repeat(14) + "┴" + "─".repeat(20) + "┴" + "─".repeat(20) + "┴" + "─".repeat(18) + "┴" + "─".repeat(19) + "┘");
    }

    public String getTreatmentType() {
        String[] types = {"OUTPATIENT", "INPATIENT", "EMERGENCY", "FOLLOW_UP"};

        System.out.println("\n┌" + "─".repeat(40) + "┐");
        printCenteredTableHeader("SELECT TREATMENT TYPE", 40);
        System.out.println("├" + "─".repeat(3) + "┬" + "─".repeat(36) + "┤");
        System.out.println("│ # │ Type                               │");
        System.out.println("├" + "─".repeat(3) + "┼" + "─".repeat(36) + "┤");

        for (int i = 0; i < types.length; i++) {
            System.out.printf("│ %d │ %-34s │%n", i + 1, types[i]);
        }

        System.out.println("├" + "─".repeat(3) + "┼" + "─".repeat(36) + "┤");
        System.out.println("│ 0 │ Cancel                             │");
        System.out.println("└" + "─".repeat(3) + "┴" + "─".repeat(36) + "┘");

        int choice = InputHandler.getInt("Select treatment type", 0, types.length);
        if (choice == 0) {
            return null; // Cancelled
        }
        return types[choice - 1];
    }

    /**
     * Display all treatments with interactive options
     */
    public void displayAllTreatments() {
        OrderedMap<String, Treatment> treatments = treatmentController.getAllTreatments();
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

        OrderedMap<String, Treatment> treatments = treatmentController.getAllTreatments();
        if (treatments.isEmpty()) {
            System.out.println("No treatments available to update.");
            return;
        }

        displayTreatmentList(treatments, "Select Treatment to Update");

        String treatmentID = InputHandler.getString("Enter Treatment ID to update (0 to cancel)");
        if (treatmentID.equals("0")) {
            return; // Cancelled
        }
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

        OrderedMap<String, Treatment> treatments = treatmentController.getAllTreatments();
        if (treatments.isEmpty()) {
            System.out.println("No treatments available to remove.");
            return;
        }

        displayTreatmentList(treatments, "Select Treatment to Remove");

        String treatmentID = InputHandler.getString("Enter Treatment ID to remove (0 to cancel)");
        if (treatmentID.equals("0")) {
            return; // Cancelled
        }
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
        OrderedMap<String, Patient> patients = treatmentController.getPatientsWithTreatments();
        if (patients.isEmpty()) {
            System.out.println("No patients with treatments found.");
            return;
        }

        displayPatientsWithTreatments(patients);
        int choice = InputHandler.getInt(0, patients.size());
        if (choice == 0) {
            return; // Back to menu
        }

        Patient chosenPatient = patients.get(choice - 1);

        // Get treatments for selected patient
        OrderedMap<String, Treatment> patientTreatments = treatmentController.getTreatmentsByPatient(chosenPatient);

        if (patientTreatments.isEmpty()) {
            System.out.println("No treatments found for selected patient.");
            return;
        }

        // Display treatments using our reusable function
        displayTreatmentList(patientTreatments, "Treatments for Patient: " + chosenPatient.getName());

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

        System.out.println("\n┌" + "─".repeat(40) + "┐");
        System.out.println("│          VIEW TREATMENTS MENU          │");
        System.out.println("├" + "─".repeat(40) + "┤");
        System.out.println("│ 1. Scheduled                           │");
        System.out.println("│ 2. In Progress                         │");
        System.out.println("│ 3. Completed                           │");
        System.out.println("│ 0. Back to Treatment Menu              │");
        System.out.println("└" + "─".repeat(40) + "┘");


        int choice = InputHandler.getInt("Select status", 0, statusOptions.length);
        if (choice == 0) {
            return; // Back to menu
        }

        String selectedStatus = statusOptions[choice - 1];

        // Get treatments by status from controller
        OrderedMap<String, Treatment> statusTreatments = treatmentController.getTreatmentsByStatus(selectedStatus);

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
    public void displayPatientsWithTreatments(OrderedMap<String, Patient> patients) {
        if (patients.isEmpty()) {
            System.out.println("\n┌" + "─".repeat(50) + "┐");
            printCenteredTableHeader("NO PATIENTS FOUND", 50);
            System.out.println("├" + "─".repeat(50) + "┤");
            System.out.println("│ No patients with treatments found.             │");
            System.out.println("└" + "─".repeat(50) + "┘");
            return;
        }

        System.out.println("\n┌" + "─".repeat(88) + "┐");
        printCenteredTableHeader("PATIENTS WITH TREATMENTS", 88);
        System.out.println("├─────┬──────────────┬─────────────────────────┬─────┬──────────┬────────────────────────┤");
        System.out.println("│ No. │ Patient ID   │ Patient Name            │ Age │ Gender   │ Contact Number         │");
        System.out.println("├─────┼──────────────┼─────────────────────────┼─────┼──────────┼────────────────────────┤");

        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);

            String name = patient.getName();
            String contact = patient.getContactNumber();
            String gender = patient.getGender();

            // Truncate long text for table formatting
            if (name.length() > 24) name = name.substring(0, 21) + "...";
            if (contact.length() > 23) contact = contact.substring(0, 20) + "...";
            if (gender.length() > 9) gender = gender.substring(0, 6) + "...";

            System.out.printf("│ %2d. │ %-12s │ %-23s │ %3d │ %-8s │ %-22s │\n",
                    i + 1,
                    patient.getPatientId(),
                    name,
                    patient.getAge(),
                    gender,
                    contact
            );
        }

        System.out.println("├─────┴──────────────┴─────────────────────────┴─────┴──────────┴────────────────────────┤");
        System.out.println("│ 0. Back to Menu                                                                        │");
        System.out.println("├" + "─".repeat(88) + "┤");
        System.out.printf("│ Total patients with treatments: %-54d │\n", patients.size());
        System.out.println("└" + "─".repeat(88) + "┘");
    }

    /**
     * Display detailed information for a single treatment
     * @param treatment The treatment to display
     */
    public void displayTreatmentDetails(Treatment treatment) {
        if (treatment == null) {
            System.out.println("\n┌" + "─".repeat(50) + "┐");
            printCenteredTableHeader("NO TREATMENT DATA", 50);
            System.out.println("├" + "─".repeat(50) + "┤");
            System.out.println("│ No treatment data available.                  │");
            System.out.println("└" + "─".repeat(50) + "┘");
            return;
        }

        int tableWidth = TableWidths.EXTRA_WIDE;

        // Main header
        System.out.println("\n┌" + "─".repeat(tableWidth + 2) + "┐");
        printCenteredTableHeader("TREATMENT DETAILS", tableWidth + 2);
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");

        // Basic Information Section
        printCenteredTableHeader("BASIC INFORMATION", tableWidth + 2);
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");
        System.out.printf("│ %-20s : %-66s │\n", "Treatment ID", treatment.getTreatmentID());
        System.out.printf("│ %-20s : %-66s │\n", "Consultation ID", treatment.getConsultationID());
        System.out.printf("│ %-20s : %-66s │\n", "Type", treatment.getType());
        System.out.printf("│ %-20s : %-66s │\n", "Status", treatment.getStatus());
        System.out.printf("│ %-20s : %-66s │\n", "Critical Priority", treatment.isCritical() ? "⚠ YES" : "No");
        System.out.printf("│ %-20s : %-66s │\n", "Date/Time", DateTimeFormatterUtil.formatForDisplay(treatment.getTreatmentDate()));

        // Patient Information Section
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");
        printCenteredTableHeader("PATIENT INFORMATION", tableWidth + 2);
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");

        if (treatment.getPatient() != null) {
            Patient patient = treatment.getPatient();
            System.out.printf("│ %-20s : %-66s │\n", "Patient ID", patient.getPatientId());
            System.out.printf("│ %-20s : %-66s │\n", "Name", patient.getName());
            System.out.printf("│ %-20s : %-66s │\n", "Age", String.valueOf(patient.getAge()));
            System.out.printf("│ %-20s : %-66s │\n", "Gender", patient.getGender());
            System.out.printf("│ %-20s : %-66s │\n", "Contact", patient.getContactNumber());
        } else {
            System.out.println("│" + centerText("Patient information not available", tableWidth) + "│");
        }

        // Doctor Information Section
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");
        printCenteredTableHeader("DOCTOR INFORMATION", tableWidth + 2);
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");

        if (treatment.getDoctor() != null) {
            Doctor doctor = treatment.getDoctor();
            System.out.printf("│ %-20s : %-66s │\n", "Doctor ID", doctor.getDoctorID());
            System.out.printf("│ %-20s : %-66s │\n", "Name", doctor.getName());
            System.out.printf("│ %-20s : %-66s │\n", "Specialty", doctor.getSpecialty());
            System.out.printf("│ %-20s : %-66s │\n", "Contact", doctor.getPhone());
        } else {
            System.out.println("│" + centerText("Doctor information not available", tableWidth) + "│");
        }

        // Diagnosis Information Section
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");
        printCenteredTableHeader("DIAGNOSIS INFORMATION", tableWidth + 2);
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");
        System.out.println("│" + centerText("Diagnosis information available through consultation record", tableWidth + 2) + "│");

        // Procedures Section
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");
        printCenteredTableHeader("PROCEDURES", tableWidth + 2);
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");

        if (treatment.hasProcedures()) {
            System.out.printf("│ %-20s : %-66s │\n", "Number of Procedures", String.valueOf(treatment.getProcedures().size()));
            System.out.printf("│ %-20s : RM %-63.2f │\n", "Total Procedure Cost", treatment.getTotalProcedureCost());

            // Display individual procedures if available
            if (treatment.getProcedures().size() > 0) {
                System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");
                printCenteredTableHeader("PROCEDURE LIST", tableWidth + 2);
                System.out.println("├─────┬──────────────────────────┬──────────────────────────────────┬───────────────────────┤");
                System.out.println("│ No. │ Procedure Code           │ Procedure Name                   │ Cost (RM)             │");
                System.out.println("├─────┼──────────────────────────┼──────────────────────────────────┼───────────────────────┤");

                int count = 1;
                for (Procedure procedure : treatment.getProcedures()) {
                    String procName = procedure.getProcedureName();
                    String procCode = procedure.getProcedureCode();

                    // Truncate long names for proper table formatting
                    if (procName.length() > 25) procName = procName.substring(0, 22) + "...";
                    if (procCode.length() > 19) procCode = procCode.substring(0, 16) + "...";

                    System.out.printf("│ %2d. │ %-24s │ %-32s │ %21.2f │\n",
                            count++,
                            procCode,
                            procName,
                            procedure.getCost()
                    );
                }
                System.out.println("├─────┴──────────────────────────┴──────────────────────────────────┴───────────────────────┤");
                System.out.printf("│ %-72s RM %13.2f │\n", "TOTAL PROCEDURE COST:", treatment.getTotalProcedureCost());
            }
        } else {
            System.out.println("│" + centerText("No procedures recorded for this treatment", tableWidth + 2) + "│");
        }

        // Prescriptions Section
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");
        printCenteredTableHeader("PRESCRIPTIONS", tableWidth + 2);
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");

        if (treatment.hasPrescription()) {
            Prescription prescription = treatment.getPrescription();
            System.out.printf("│ %-20s : %-66s │\n", "Prescription ID", prescription.getPrescriptionID());
            System.out.printf("│ %-20s : %-66s │\n", "Number of Medicines", String.valueOf(prescription.getMedicines().size()));
            // System.out.printf("│ %-20s : RM %-63.2f │\n", "Total Price", prescription.getTotalPrice());

            // Display individual medicines if available
            if (prescription.getMedicines().size() > 0) {
                System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");
                printCenteredTableHeader("MEDICINE LIST", tableWidth + 2);
                System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");

                int count = 1;
                for (PrescribedMedicine prescribed : prescription.getMedicines()) {
                    Medicine medicine = prescribed.getMedicine();
                    String medName = prescribed.getMedicine().getName();
                    if (medName.length() > 30) medName = medName.substring(0, 27) + "...";

                    System.out.printf("│ %2d. %-55s - Qty: %-10s RM %8.2f │\n",
                            count++,
                            medName,
                            prescribed.getQuantity(),
                            medicine.getPrice()
                    );
                }
            }
        } else {
            System.out.println("│" + centerText("No prescriptions for this treatment", tableWidth + 2) + "│");
        }

        // Treatment Notes Section
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");
        printCenteredTableHeader("TREATMENT NOTES", tableWidth + 2);
        System.out.println("├" + "─".repeat(tableWidth + 2) + "┤");

        if (treatment.getNotes() != null && !treatment.getNotes().trim().isEmpty()) {
            // Wrap long notes text
            String[] noteLines = wrapText(treatment.getNotes(), tableWidth - 4);
            for (String line : noteLines) {
                System.out.printf("│ %-89s │\n", line);
            }
        } else {
            System.out.println("│" + centerText("No additional notes", tableWidth + 2) + "│");
        }

        // Footer
        System.out.println("└" + "─".repeat(tableWidth + 2) + "┘");
    }

    /**
     * Display a list of treatments with essential information
     * @param treatments OrderedMap of treatments to display
     * @param title Title for the list
     */
    public void displayTreatmentList(OrderedMap<String, Treatment> treatments, String title) {
        if (treatments.isEmpty()) {
            System.out.println("No treatments found.");
            return;
        }

        System.out.println("\n┌" + "─".repeat(104) + "┐");
        printCenteredTableHeader(title, 104);
        System.out.println("├─────┬──────────────┬─────────────────┬─────────────────┬─────────────────┬──────────────┬──────────────┤");
        System.out.println("│ No. │ Treatment ID │ Patient Name    │ Doctor Name     │ Date/Time       │ Status       │ Critical     │");
        System.out.println("├─────┼──────────────┼─────────────────┼─────────────────┼─────────────────┼──────────────┼──────────────┤");

        for (int i = 0; i < treatments.size(); i++) {
            Treatment treatment = treatments.get(i);
            if (treatment != null) {
                String patientName = treatment.getPatient() != null ?
                        treatment.getPatient().getName() : "N/A";
                String doctorName = treatment.getDoctor() != null ?
                        treatment.getDoctor().getName() : "N/A";
                String dateTime = DateTimeFormatterUtil.formatForDisplay(treatment.getTreatmentDate());
                String status = treatment.getStatus();
                String critical = treatment.isCritical() ? "⚠ YES" : "No";

                // Truncate long names for table formatting
                if (patientName.length() > 15) patientName = patientName.substring(0, 12) + "...";
                if (doctorName.length() > 15) doctorName = doctorName.substring(0, 12) + "...";
                if (dateTime.length() > 15) dateTime = dateTime.substring(0, 12) + "...";
                if (status.length() > 12) status = status.substring(0, 9) + "...";

                System.out.printf("│ %2d. │ %-12s │ %-15s │ %-15s │ %-15s │ %-12s │ %-12s │\n",
                        i + 1,
                        treatment.getTreatmentID(),
                        patientName,
                        doctorName,
                        dateTime,
                        status,
                        critical
                );
            }
        }

        System.out.println("├─────┴──────────────┴─────────────────┴─────────────────┴─────────────────┴──────────────┴──────────────┤");
        System.out.printf("│ Total treatments: %-84d │\n", treatments.size());
        System.out.println("└" + "─".repeat(104) + "┘");
    }

    /**
     * Helper method to view details of a selected treatment from a list
     */
    public void viewSelectedTreatmentDetails(OrderedMap<String, Treatment> treatments) {
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
    public void updateSelectedTreatment(OrderedMap<String, Treatment> treatments) {
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

        System.out.println("\n┌" + "─".repeat(40) + "┐");
        System.out.println("│          UPDATE TREATMENT MENU         │");
        System.out.println("├" + "─".repeat(40) + "┤");
        System.out.println("│ 1. Update Notes                        │");
        System.out.println("│ 2. Update Critical Status              │");
        System.out.println("│ 3. Update Treatment Type               │");
        System.out.println("│ 0. Back to Treatment Menu              │");
        System.out.println("└" + "─".repeat(40) + "┘");


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
        System.out.println("\n┌" + "─".repeat(50) + "┐");
        printCenteredTableHeader("TREATMENT PROCESSING MENU", 50);
        System.out.println("├" + "─".repeat(3) + "┬" + "─".repeat(46) + "┤");
        System.out.println("│ # │ Processing Option                            │");
        System.out.println("├" + "─".repeat(3) + "┼" + "─".repeat(46) + "┤");
        System.out.println("│ 1 │ Process Next Emergency Treatment             │");
        System.out.println("│ 2 │ Process Next Regular Treatment               │");
        System.out.println("│ 3 │ View Recent Processing Activities            │");
        System.out.println("│ 4 │ Processing Statistics                        │");
        System.out.println("├" + "─".repeat(3) + "┼" + "─".repeat(46) + "┤");
        System.out.println("│ 0 │ Back to Treatment Menu                       │");
        System.out.println("└" + "─".repeat(3) + "┴" + "─".repeat(46) + "┘");
    }

    /**
     * Process next emergency treatment
     */
    public void processNextEmergency() {
        System.out.println("\n┌" + "─".repeat(50) + "┐");
        printCenteredTableHeader("🚨 PROCESS NEXT EMERGENCY", 50);
        System.out.println("└" + "─".repeat(50) + "┘");

        Treatment treatment = treatmentController.processNextEmergency();

        if (treatment != null) {
            printCenteredTableHeader("✅ EMERGENCY PROCESSED", 59);
            System.out.println("├" + "─".repeat(60) + "┤");
            System.out.println("│ 🚨 Emergency treatment processed successfully!             │");
            System.out.println("│ ⏰ Processing Time: " + centerText(DateTimeFormatterUtil.getCurrentTimestamp(), 38) + " │");
            System.out.println("└" + "─".repeat(60) + "┘");

            // Display brief treatment info
            System.out.println("\n┌" + "─".repeat(40) + "┐");
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

        OrderedMap<String, String> recents = treatmentController.getRecentTreatments();

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
    /**
     * Display processing statistics using StringBuilder for better performance and file export capability
     */
    public void displayProcessingStatistics() {
        // Get all treatments for analysis
        OrderedMap<String, Treatment> allTreatments = treatmentController.getAllTreatments();

        if (allTreatments.isEmpty()) {
            System.out.println("\n┌" + "─".repeat(50) + "┐");
            printCenteredTableHeader("NO DATA AVAILABLE", 50);
            System.out.println("├" + "─".repeat(50) + "┤");
            System.out.println("│ ℹ️  No treatment data available for statistics. │");
            System.out.println("└" + "─".repeat(50) + "┘");
            return;
        }

        // Build report using StringBuilder
        StringBuilder report = buildProcessingStatisticsReport(allTreatments);

        // Display the report
        System.out.print(report.toString());

        // Ask if user wants to save the report
        System.out.println("\nWould you like to save this report to a file?");
        boolean saveReport = InputHandler.getYesNo("Save report");

        if (saveReport) {
            // Create a plain text version without table formatting for file export
            StringBuilder plainReport = buildPlainProcessingStatisticsReport(allTreatments);
            boolean saved = treatmentController.saveReportToFile(plainReport.toString(), "Processing_Statistics");
            if (!saved) {
                System.out.println("⚠️ Failed to save report to file.");
            }
        }
    }

    /**
     * Build processing statistics report with table formatting for display
     */
    private StringBuilder buildProcessingStatisticsReport(OrderedMap<String, Treatment> allTreatments) {
        StringBuilder report = new StringBuilder();

        // Count treatments by various criteria
        int scheduledCount = 0, inProgressCount = 0, completedCount = 0;
        int emergencyCount = 0, regularCount = 0, criticalCount = 0;

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

        // Calculate processing progress
        double completionRate = allTreatments.size() > 0 ?
                (double) completedCount / allTreatments.size() * 100 : 0;

        // Get recent activities count
        OrderedMap<String, String> recentActivities = treatmentController.getRecentTreatments();

        // Build formatted report
        report.append("\n┌").append("─".repeat(60)).append("┐\n");
        report.append("│").append(centerText("TREATMENT PROCESSING STATISTICS", 60)).append("│\n");
        report.append("├").append("─".repeat(60)).append("┤\n");
        report.append("│").append(centerText("📊 OVERALL SUMMARY", 60)).append("│\n");
        report.append("├").append("─".repeat(60)).append("┤\n");
        report.append(String.format("│ %-30s : %25d │\n", "Total Treatments", allTreatments.size()));
        report.append(String.format("│ %-30s : %24.1f%% │\n", "Completion Rate", completionRate));
        report.append(String.format("│ %-30s : %25d │\n", "Pending Treatments", scheduledCount + inProgressCount));
        report.append(String.format("│ %-30s : %25d │\n", "Recent Activities", recentActivities.size()));

        // Status Breakdown Section
        report.append("├").append("─".repeat(60)).append("┤\n");
        report.append("│").append(centerText("📋 STATUS BREAKDOWN", 60)).append("│\n");
        report.append("├").append("─".repeat(60)).append("┤\n");
        report.append(String.format("│ %-30s : %25d │\n", "Scheduled", scheduledCount));
        report.append(String.format("│ %-30s : %25d │\n", "In Progress", inProgressCount));
        report.append(String.format("│ %-30s : %25d │\n", "Completed", completedCount));

        // Type Breakdown Section
        report.append("├").append("─".repeat(60)).append("┤\n");
        report.append("│").append(centerText("🏥 TYPE BREAKDOWN", 60)).append("│\n");
        report.append("├").append("─".repeat(60)).append("┤\n");
        report.append(String.format("│ %-30s : %25d │\n", "Emergency Treatments", emergencyCount));
        report.append(String.format("│ %-30s : %25d │\n", "Regular Treatments", regularCount));

        // Priority Breakdown Section
        report.append("├").append("─".repeat(60)).append("┤\n");
        report.append("│").append(centerText("⚠️ PRIORITY BREAKDOWN", 60)).append("│\n");
        report.append("├").append("─".repeat(60)).append("┤\n");
        report.append(String.format("│ %-30s : %25d │\n", "Critical Treatments", criticalCount));
        report.append(String.format("│ %-30s : %25d │\n", "Non-Critical Treatments", allTreatments.size() - criticalCount));

        report.append("└").append("─".repeat(60)).append("┘\n");

        // Action Recommendations Table
        if (emergencyCount > 0 || criticalCount > 0) {
            report.append("\n┌").append("─".repeat(70)).append("┐\n");
            report.append("│").append(centerText("💡 RECOMMENDATIONS & ALERTS", 70)).append("│\n");
            report.append("├").append("─".repeat(70)).append("┤\n");

            if (criticalCount > 0) {
                report.append(String.format("│ ⚠️  ATTENTION: %-53s │\n",
                        criticalCount + " critical treatments require immediate attention."));
            }

            if (emergencyCount > 0 && scheduledCount > 0) {
                report.append(String.format("│ 💡 RECOMMENDATION: %-49s │\n",
                        "Process emergency treatments first"));
            }

            if (emergencyCount > 0) {
                report.append(String.format("│ 🚨 PRIORITY: %-55s │\n",
                        emergencyCount + " emergency treatments pending"));
            }

            if (completionRate < 50) {
                report.append(String.format("│ 📈 PERFORMANCE: %-52s │\n",
                        "Low completion rate - consider workflow review"));
            }

            report.append("└").append("─".repeat(70)).append("┘\n");
        }

        // Performance Indicators
        if (allTreatments.size() > 0) {
            report.append("\n┌").append("─".repeat(65)).append("┐\n");
            report.append("│").append(centerText("📈 PERFORMANCE INDICATORS", 65)).append("│\n");
            report.append("├").append("─".repeat(65)).append("┤\n");

            // Efficiency rating
            String efficiencyRating;
            if (completionRate >= 80) {
                efficiencyRating = "🟢 EXCELLENT";
            } else if (completionRate >= 60) {
                efficiencyRating = "🟡 GOOD";
            } else if (completionRate >= 40) {
                efficiencyRating = "🟠 MODERATE";
            } else {
                efficiencyRating = "🔴 NEEDS IMPROVEMENT";
            }

            report.append(String.format("│ %-30s : %-30s │\n", "Efficiency Rating", efficiencyRating));

            // Workload status
            int totalPending = scheduledCount + inProgressCount;
            String workloadStatus;
            if (totalPending == 0) {
                workloadStatus = "🟢 NO PENDING";
            } else if (totalPending <= 5) {
                workloadStatus = "🟡 LOW";
            } else if (totalPending <= 15) {
                workloadStatus = "🟠 MODERATE";
            } else {
                workloadStatus = "🔴 HIGH";
            }

            report.append(String.format("│ %-30s : %-30s │\n", "Current Workload", workloadStatus));

            // Priority status
            String priorityStatus;
            if (criticalCount == 0) {
                priorityStatus = "🟢 NO CRITICAL";
            } else if (criticalCount <= 2) {
                priorityStatus = "🟡 MANAGEABLE";
            } else {
                priorityStatus = "🔴 HIGH PRIORITY";
            }

            report.append(String.format("│ %-30s : %-30s │\n", "Priority Status", priorityStatus));

            report.append("└").append("─".repeat(65)).append("┘\n");
        }

        return report;
    }

    /**
     * Build plain text version of processing statistics report for file export
     */
    private StringBuilder buildPlainProcessingStatisticsReport(OrderedMap<String, Treatment> allTreatments) {
        StringBuilder report = new StringBuilder();

        // Count treatments by various criteria
        int scheduledCount = 0, inProgressCount = 0, completedCount = 0;
        int emergencyCount = 0, regularCount = 0, criticalCount = 0;

        for (Treatment treatment : allTreatments) {
            // Count by status
            switch (treatment.getStatus().toUpperCase()) {
                case "SCHEDULED": scheduledCount++; break;
                case "IN_PROGRESS": inProgressCount++; break;
                case "COMPLETED": completedCount++; break;
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

        // Calculate metrics
        double completionRate = allTreatments.size() > 0 ?
                (double) completedCount / allTreatments.size() * 100 : 0;
        double criticalPercentage = allTreatments.size() > 0 ?
                (double) criticalCount / allTreatments.size() * 100 : 0;
        double emergencyPercentage = allTreatments.size() > 0 ?
                (double) emergencyCount / allTreatments.size() * 100 : 0;

        OrderedMap<String, String> recentActivities = treatmentController.getRecentTreatments();

        // Build plain text report
        report.append("TREATMENT PROCESSING STATISTICS REPORT\n");
        report.append("=====================================\n\n");

        report.append("OVERALL SUMMARY:\n");
        report.append("---------------\n");
        report.append("Total Treatments: ").append(allTreatments.size()).append("\n");
        report.append("Completion Rate: ").append(String.format("%.1f%%", completionRate)).append("\n");
        report.append("Pending Treatments: ").append(scheduledCount + inProgressCount).append("\n");
        report.append("Recent Activities: ").append(recentActivities.size()).append("\n\n");

        report.append("STATUS BREAKDOWN:\n");
        report.append("----------------\n");
        report.append("Scheduled: ").append(scheduledCount).append("\n");
        report.append("In Progress: ").append(inProgressCount).append("\n");
        report.append("Completed: ").append(completedCount).append("\n\n");

        report.append("TREATMENT TYPE BREAKDOWN:\n");
        report.append("------------------------\n");
        report.append("Emergency Treatments: ").append(emergencyCount).append("\n");
        report.append("Regular Treatments: ").append(regularCount).append("\n\n");

        report.append("PRIORITY ANALYSIS:\n");
        report.append("-----------------\n");
        report.append("Critical Treatments: ").append(criticalCount).append("\n");
        report.append("Non-Critical Treatments: ").append(allTreatments.size() - criticalCount).append("\n\n");

        report.append("PERFORMANCE INDICATORS:\n");
        report.append("----------------------\n");
        report.append("Critical Treatment Rate: ").append(String.format("%.1f%%", criticalPercentage)).append("\n");
        report.append("Emergency Treatment Rate: ").append(String.format("%.1f%%", emergencyPercentage)).append("\n");

        return report;
    }




    /**
     * Add procedures to newly created treatment
     */
    private void addProceduresToNewTreatment(Treatment treatment) {
        System.out.println("Adding procedures to treatment: " + treatment.getTreatmentID());

        while (true) {
            // Display available procedures
            OrderedMap<String, Procedure> availableProcedures = getAvailableProcedures();

            if (availableProcedures.isEmpty()) {
                System.out.println("No procedures available to add.");
                break;
            }

            System.out.println("\n--- Available Procedures ---");
            displayProcedureSelectionList(availableProcedures);

            System.out.println("0. Finish adding procedures");
            int choice = InputHandler.getInt("Select procedure number", 0, availableProcedures.size());

            if (choice == 0) {
                break; // Finish adding procedures
            }

            // Get selected procedure
            Procedure selectedProcedure = availableProcedures.get(choice - 1);

            if (selectedProcedure == null) {
                System.out.println("Invalid selection. Please try again.");
                continue;
            }

            // Show procedure details and confirm
            displayProcedureDetails(selectedProcedure, treatment);
            boolean confirm = InputHandler.getYesNo("Add this procedure to the treatment?");

            if (confirm) {
                boolean success = treatmentController.addProcedure(treatment.getTreatmentID(), selectedProcedure);
                if (success) {
                    System.out.println("✅ Procedure '" + selectedProcedure.getProcedureName() + "' added successfully!");
                    System.out.printf("Updated total cost: RM %.2f\n", treatment.getTotalProcedureCost());
                } else {
                    System.out.println("❌ Failed to add procedure.");
                }
            }

            // Ask if user wants to add another procedure
            boolean addAnother = InputHandler.getYesNo("Add another procedure?");
            if (!addAnother) {
                break;
            }
        }
    }

    /**
     * Add prescription to newly created treatment
     */
    private void addPrescriptionToNewTreatment(Treatment treatment) {
        System.out.println("Adding prescription to treatment: " + treatment.getTreatmentID());

        try {
            // Create new prescription
            String prescriptionID = IDGenerator.generatePrescriptionID();
            Prescription prescription = new Prescription(prescriptionID, treatment.getTreatmentID());

            // Add medicines to prescription
            while (true) {
                OrderedMap<String, Medicine> availableMedicines = pharmacyController.getMedicineMap();
                if (availableMedicines.isEmpty()) {
                    System.out.println("No medicines available.");
                    break;
                }

                displayMedicineList(availableMedicines);
                System.out.println("0. Finish adding medicines");

                int choice = InputHandler.getInt("Select medicine", 0, availableMedicines.size());
                if (choice == 0) break;

                Medicine selectedMedicine = availableMedicines.get(choice - 1);

                // Get prescription details
                int quantity = InputHandler.getInt("Enter quantity", 1, 100);
                String dosage = InputHandler.getString("Enter dosage (e.g., '2 tablets')", 1, 50);
                String frequency = InputHandler.getString("Enter frequency (e.g., 'Twice daily')", 1, 50);
                String description = InputHandler.getOptionalString("Enter description");

                // Add medicine to prescription
                try {
                    prescription.addMedicine(selectedMedicine, quantity, dosage, frequency, description);
                    System.out.println("✅ Medicine added to prescription successfully!");
                } catch (IllegalArgumentException e) {
                    System.out.println("❌ Error adding medicine: " + e.getMessage());
                }

                boolean addAnother = InputHandler.getYesNo("Add another medicine?");
                if (!addAnother) break;
            }

            if (prescription.getMedicines().size() > 0) {
                prescription.calculateTotalPrice();
                boolean confirm = InputHandler.getYesNo("Confirm prescription creation?");

                if (confirm) {
                    boolean success = treatmentController.addPrescriptionToTreatment(treatment.getTreatmentID(), prescription);
                    if (success) {
                        System.out.println("✅ Prescription created and added to treatment successfully!");
                        System.out.println("Prescription ID: " + prescriptionID);
                    } else {
                        System.out.println("❌ Failed to add prescription to treatment.");
                    }
                }
            } else {
                System.out.println("No medicines added to prescription.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error creating prescription: " + e.getMessage());
        }
    }

    /**
     * Get available procedures from the initialized data
     */
    private OrderedMap<String, Procedure> getAvailableProcedures() {
        return treatmentController.getAllAvailableProcedures();
    }

    /**
     * Display procedure selection list in a compact format
     */
    private void displayProcedureSelectionList(OrderedMap<String, Procedure> procedures) {
        System.out.println("┌" + "─".repeat(70) + "┐");
        System.out.println("│" + " ".repeat(25) + "AVAILABLE PROCEDURES" + " ".repeat(25) + "│");
        System.out.println("├" + "─".repeat(70) + "┤");
        System.out.printf("│ %-3s │ %-25s │ %-20s │ %-11s │\n", "#", "Procedure Name", "Code", "Cost (RM)");
        System.out.println("├" + "─".repeat(70) + "┤");

        for (int i = 0; i < procedures.size(); i++) {
            Procedure procedure = procedures.get(i);
            String name = procedure.getProcedureName();
            String code = procedure.getProcedureCode();

            // Truncate long names for display
            if (name.length() > 24) name = name.substring(0, 21) + "...";
            if (code.length() > 19) code = code.substring(0, 16) + "...";

            System.out.printf("│ %-3d │ %-25s │ %-20s │ %11.2f │\n",
                    i + 1, name, code, procedure.getCost());
        }
        System.out.println("└" + "─".repeat(70) + "┘");
    }

    /**
     * Display detailed procedure information
     */
    private void displayProcedureDetails(Procedure procedure, Treatment treatment) {
        System.out.println("\n┌" + "─".repeat(60) + "┐");
        System.out.println("│" + " ".repeat(20) + "PROCEDURE DETAILS" + " ".repeat(23) + "│");
        System.out.println("├" + "─".repeat(60) + "┤");
        System.out.printf("│ Procedure ID    : %-40s │\n", procedure.getProcedureID());
        System.out.printf("│ Name            : %-40s │\n", procedure.getProcedureName());
        System.out.printf("│ Code            : %-40s │\n", procedure.getProcedureCode());
        System.out.printf("│ Duration        : %-40s │\n", procedure.getEstimatedDuration() + " mins");
        System.out.printf("│ Cost            : RM %-37.2f │\n", procedure.getCost());
        System.out.println("├" + "─".repeat(60) + "┤");
        System.out.printf("│ Current Total   : RM %-37.2f │\n", treatment.getTotalProcedureCost());
        System.out.printf("│ New Total       : RM %-37.2f │\n",
                treatment.getTotalProcedureCost() + procedure.getCost());
        System.out.println("└" + "─".repeat(60) + "┘");
    }

    /**
     * Display medicine list
     */
    private void displayMedicineList(OrderedMap<String, Medicine> medicines) {
        System.out.println("┌" + "─".repeat(70) + "┐");
        System.out.println("│" + " ".repeat(25) + "MEDICINES" + " ".repeat(36) + "│");
        System.out.println("├" + "─".repeat(70) + "┤");
        System.out.printf("│ %-3s │ %-15s │ %-20s │ %-21s │\n", "#", "Name", "Description", "Price (RM)");
        System.out.println("├" + "─".repeat(70) + "┤");

        for (int i = 0; i < medicines.size(); i++) {
            Medicine medicine = medicines.get(i);
            String name = medicine.getName();
            String desc = medicine.getDescription();

            if (name.length() > 14) name = name.substring(0, 11) + "...";
            if (desc.length() > 19) desc = desc.substring(0, 16) + "...";

            System.out.printf("│ %-3d │ %-15s │ %-20s │ RM %18.2f │\n",
                    i + 1, name, desc, medicine.getPrice());
        }
        System.out.println("└" + "─".repeat(70) + "┘");
    }

    /**
     * Display treatment creation summary
     */
    private void displayTreatmentCreationSummary(Treatment treatment) {
        System.out.println("\n┌" + "─".repeat(60) + "┐");
        System.out.println("│" + " ".repeat(18) + "TREATMENT CREATED" + " ".repeat(25) + "│");
        System.out.println("├" + "─".repeat(60) + "┤");
        System.out.printf("│ Treatment ID : %-43s │\n", treatment.getTreatmentID());
        System.out.printf("│ Type         : %-43s │\n", treatment.getType());
        System.out.printf("│ Patient      : %-43s │\n", treatment.getPatient() != null ? treatment.getPatient().getName() : "N/A");
        System.out.printf("│ Doctor       : %-43s │\n", treatment.getDoctor() != null ? treatment.getDoctor().getName() : "N/A");
        System.out.printf("│ Priority     : %-43s │\n", treatment.isCritical() ? "CRITICAL" : "REGULAR");
        System.out.printf("│ Status       : %-43s │\n", treatment.getStatus());
        System.out.println("└" + "─".repeat(60) + "┘");
    }

    /**
     * Display final treatment summary with all additions
     */
    private void displayFinalTreatmentSummary(Treatment treatment) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println(" ".repeat(22) + "FINAL TREATMENT SUMMARY");
        System.out.println("=".repeat(70));

        // Basic treatment info
        System.out.printf("Treatment ID    : %s\n", treatment.getTreatmentID());
        System.out.printf("Patient         : %s\n", treatment.getPatient() != null ? treatment.getPatient().getName() : "N/A");
        System.out.printf("Doctor          : %s\n", treatment.getDoctor() != null ? treatment.getDoctor().getName() : "N/A");
        System.out.printf("Status          : %s\n", treatment.getStatus());
        System.out.printf("Priority        : %s\n", treatment.isCritical() ? "CRITICAL" : "REGULAR");

        // Procedures
        if (treatment.hasProcedures()) {
            System.out.println("\nPROCEDURES:");
            int count = 1;
            for (Procedure proc : treatment.getProcedures()) {
                System.out.printf("  %d. %s - RM %.2f\n",
                        count++, proc.getProcedureName(), proc.getCost());
            }
            System.out.printf("Total Procedure Cost: RM %.2f\n", treatment.getTotalProcedureCost());
        } else {
            System.out.println("\nPROCEDURES: None added");
        }

        // Prescription
        if (treatment.hasPrescription()) {
            Prescription prescription = treatment.getPrescription();
            System.out.println("\nPRESCRIPTION:");
            System.out.printf("  Prescription ID: %s\n", prescription.getPrescriptionID());
            System.out.printf("  Medicines: %d\n", prescription.getMedicines().size());
        } else {
            System.out.println("\nPRESCRIPTION: None added");
        }

        System.out.println("=".repeat(70));
    }

    /**
     * Search Treatments Menu
     */
    public void searchTreatmentsMenu() {
        int choice;
        do {
            printSearchMenu();
            choice = InputHandler.getInt("Select search option", 0, 5);

            switch(choice) {
                case 1:
                    searchByPatientName();
                    break;
                case 2:
                    searchByDateRange();
                    break;
                case 3:
                    searchByNotes();
                    break;
                case 4:
                    searchByProcedure();
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
     * Sort Treatments Menu
     */
    public void sortTreatmentsMenu() {
        int choice;
        do {
            printSortMenu();
            choice = InputHandler.getInt("Select sort option", 0, 4);

            switch(choice) {
                case 1:
                    sortByDate();
                    break;
                case 2:
                    sortByPatientName();
                    break;
                case 3:
                    sortByStatus();
                    break;
                case 4:
                    sortByCriticalPriority();
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
     * Reports and Statistics Menu
     */
    public void reportsMenu() {
        int choice;
        do {
            printReportsMenu();
            choice = InputHandler.getInt("Select sort option", 0, 3);

            switch(choice) {
                case 1:
                    displayProcessingStatistics();
                    break;
                case 2:
                    generatePatientTreatmentSummary();
                    break;
                case 3:
                    generateFinancialSummaryReport();
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
     * Generate patient treatment summary report using StringBuilder
     */
    private void generatePatientTreatmentSummary() {
        OrderedMap<String, Treatment> allTreatments = treatmentController.getAllTreatments();

        if (allTreatments.isEmpty()) {
            System.out.println("\n┌" + "─".repeat(50) + "┐");
            printCenteredTableHeader("NO DATA AVAILABLE", 50);
            System.out.println("├" + "─".repeat(50) + "┤");
            System.out.println("│ No treatment data available for report.         │");
            System.out.println("└" + "─".repeat(50) + "┘");
            return;
        }

        // Build report using StringBuilder
        StringBuilder report = buildPatientTreatmentSummaryReport(allTreatments);

        // Display the report
        System.out.print(report.toString());

        // Ask if user wants to save the report
        System.out.println("\nWould you like to save this report to a file?");
        boolean saveReport = InputHandler.getYesNo("Save report");

        if (saveReport) {
            // Create a plain text version for file export
            StringBuilder plainReport = buildPlainPatientTreatmentSummaryReport(allTreatments);
            boolean saved = treatmentController.saveReportToFile(plainReport.toString(), "Patient_Treatment_Summary");
            if (!saved) {
                System.out.println("⚠️ Failed to save report to file.");
            }
        }
    }

    /**
     * Build patient treatment summary report with table formatting for display
     */
    private StringBuilder buildPatientTreatmentSummaryReport(OrderedMap<String, Treatment> allTreatments) {
        StringBuilder report = new StringBuilder();

        // Group treatments by patient using a separate ADT to track patient IDs
        OrderedMap<String, OrderedMap<String, Treatment>> patientTreatments = new OrderedMap<>();
        OrderedMap<String, String> patientIds = new OrderedMap<>(); // Track unique patient IDs

        // Iterate through all treatments and group by patient
        for (Treatment treatment : allTreatments) {
            if (treatment.getPatient() != null) {
                String patientId = treatment.getPatient().getPatientId();

                // Add patient ID to our tracking collection
                patientIds.put(patientId, patientId);

                if (!patientTreatments.containsKey(patientId)) {
                    patientTreatments.put(patientId, new OrderedMap<>());
                }

                patientTreatments.get(patientId).put(treatment.getTreatmentID(), treatment);
            }
        }

        report.append("\n┌").append("─".repeat(90)).append("┐\n");
        report.append("│").append(centerText("PATIENT TREATMENT SUMMARY REPORT", 90)).append("│\n");
        report.append("├").append("─".repeat(90)).append("┤\n");
        report.append(String.format("│ Report Generated: %-70s │\n", DateTimeFormatterUtil.getCurrentTimestamp()));
        report.append("├").append("─".repeat(90)).append("┤\n");

        // Iterate through patient IDs
        for (String patientId : patientIds) {
            OrderedMap<String, Treatment> treatments = patientTreatments.get(patientId);
            if (treatments != null && !treatments.isEmpty()) {
                Treatment firstTreatment = treatments.get(0);

                if (firstTreatment.getPatient() != null) {
                    Patient patient = firstTreatment.getPatient();

                    // Patient header
                    report.append("├").append("─".repeat(90)).append("┤\n");
                    report.append(String.format("│ PATIENT: %-79s │\n", patient.getName() + " (ID: " + patientId + ")"));
                    report.append("├").append("─".repeat(90)).append("┤\n");

                    // Treatment statistics
                    int completed = 0, inProgress = 0, scheduled = 0, critical = 0;
                    double totalCost = 0.0;

                    for (Treatment t : treatments) {
                        switch (t.getStatus().toUpperCase()) {
                            case "COMPLETED": completed++; break;
                            case "IN_PROGRESS": inProgress++; break;
                            case "SCHEDULED": scheduled++; break;
                        }
                        if (t.isCritical()) critical++;
                        totalCost += t.getTotalProcedureCost();
                    }

                    report.append(String.format("│ %-25s : %2d │ %-25s : %2d │ %-12s : %7.2f │\n",
                            "Total Treatments", treatments.size(),
                            "Critical Treatments", critical,
                            "Total Cost", totalCost));
                    report.append(String.format("│ %-25s : %2d │ %-25s : %2d │ %-13s : %6d │\n",
                            "Completed", completed,
                            "In Progress", inProgress,
                            "Scheduled", scheduled));
                }
            }
        }

        report.append("├").append("─".repeat(90)).append("┤\n");
        report.append(String.format("│ Total Patients with Treatments: %-56d │\n", patientTreatments.size()));
        report.append("└").append("─".repeat(90)).append("┘\n");

        return report;
    }

    /**
     * Build plain text version of patient treatment summary report for file export
     */
    private StringBuilder buildPlainPatientTreatmentSummaryReport(OrderedMap<String, Treatment> allTreatments) {
        StringBuilder report = new StringBuilder();

        // Group treatments by patient
        OrderedMap<String, OrderedMap<String, Treatment>> patientTreatments = new OrderedMap<>();
        OrderedMap<String, String> patientIds = new OrderedMap<>();

        for (Treatment treatment : allTreatments) {
            if (treatment.getPatient() != null) {
                String patientId = treatment.getPatient().getPatientId();
                patientIds.put(patientId, patientId);

                if (!patientTreatments.containsKey(patientId)) {
                    patientTreatments.put(patientId, new OrderedMap<>());
                }

                patientTreatments.get(patientId).put(treatment.getTreatmentID(), treatment);
            }
        }

        report.append("PATIENT TREATMENT SUMMARY REPORT\n");
        report.append("================================\n\n");

        for (String patientId : patientIds) {
            OrderedMap<String, Treatment> treatments = patientTreatments.get(patientId);
            if (treatments != null && !treatments.isEmpty()) {
                Treatment firstTreatment = treatments.get(0);

                if (firstTreatment.getPatient() != null) {
                    Patient patient = firstTreatment.getPatient();

                    report.append("PATIENT: ").append(patient.getName()).append(" (ID: ").append(patientId).append(")\n");
                    report.append("-".repeat(50)).append("\n");

                    // Calculate statistics
                    int completed = 0, inProgress = 0, scheduled = 0, critical = 0;
                    double totalCost = 0.0;

                    for (Treatment t : treatments) {
                        switch (t.getStatus().toUpperCase()) {
                            case "COMPLETED": completed++; break;
                            case "IN_PROGRESS": inProgress++; break;
                            case "SCHEDULED": scheduled++; break;
                        }
                        if (t.isCritical()) critical++;
                        totalCost += t.getTotalProcedureCost();
                    }

                    report.append("Total Treatments: ").append(treatments.size()).append("\n");
                    report.append("Completed: ").append(completed).append("\n");
                    report.append("In Progress: ").append(inProgress).append("\n");
                    report.append("Scheduled: ").append(scheduled).append("\n");
                    report.append("Critical Treatments: ").append(critical).append("\n");
                    report.append("Total Cost: RM ").append(String.format("%.2f", totalCost)).append("\n\n");
                }
            }
        }

        report.append("SUMMARY:\n");
        report.append("--------\n");
        report.append("Total Patients with Treatments: ").append(patientTreatments.size()).append("\n");

        return report;
    }

    /**
     * Generate financial summary report using StringBuilder
     */
    private void generateFinancialSummaryReport() {
        OrderedMap<String, Treatment> allTreatments = treatmentController.getAllTreatments();

        if (allTreatments.isEmpty()) {
            System.out.println("\n┌" + "─".repeat(50) + "┐");
            printCenteredTableHeader("NO DATA AVAILABLE", 50);
            System.out.println("├" + "─".repeat(50) + "┤");
            System.out.println("│ No treatment data available for report.         │");
            System.out.println("└" + "─".repeat(50) + "┘");
            return;
        }

        // Build report using StringBuilder
        StringBuilder report = buildFinancialSummaryReport(allTreatments);

        // Display the report
        System.out.print(report.toString());

        // Ask if user wants to save the report
        System.out.println("\nWould you like to save this report to a file?");
        boolean saveReport = InputHandler.getYesNo("Save report");

        if (saveReport) {
            // Create a plain text version for file export
            StringBuilder plainReport = buildPlainFinancialSummaryReport(allTreatments);
            boolean saved = treatmentController.saveReportToFile(plainReport.toString(), "Financial_Summary");
            if (!saved) {
                System.out.println("⚠️ Failed to save report to file.");
            }
        }
    }

    /**
     * Build financial summary report with table formatting for display
     */
    private StringBuilder buildFinancialSummaryReport(OrderedMap<String, Treatment> allTreatments) {
        StringBuilder report = new StringBuilder();

        // Financial calculations
        double totalRevenue = 0.0;
        double completedRevenue = 0.0;
        double pendingRevenue = 0.0;
        double averageTreatmentCost = 0.0;
        double highestTreatmentCost = 0.0;
        double lowestTreatmentCost = Double.MAX_VALUE;

        OrderedMap<String, Double> monthlyRevenue = new OrderedMap<>();
        OrderedMap<String, Integer> treatmentsByType = new OrderedMap<>();

        int totalTreatments = 0;
        int completedTreatments = 0;
        int pendingTreatments = 0;

        // Process all treatments
        for (Treatment treatment : allTreatments) {
            if (treatment != null) {
                totalTreatments++;
                double cost = treatment.getTotalProcedureCost();
                totalRevenue += cost;

                // Track highest and lowest costs
                if (cost > highestTreatmentCost) {
                    highestTreatmentCost = cost;
                }
                if (cost < lowestTreatmentCost && cost > 0) {
                    lowestTreatmentCost = cost;
                }

                // Revenue by status
                String status = treatment.getStatus().toUpperCase();
                if ("COMPLETED".equals(status)) {
                    completedRevenue += cost;
                    completedTreatments++;
                } else {
                    pendingRevenue += cost;
                    pendingTreatments++;
                }

                // Monthly revenue tracking
                String month = treatment.getTreatmentDate().getYear() + "-" +
                        String.format("%02d", treatment.getTreatmentDate().getMonthValue());
                Double currentMonthRevenue = monthlyRevenue.get(month);
                if (currentMonthRevenue == null) {
                    monthlyRevenue.put(month, cost);
                } else {
                    monthlyRevenue.put(month, currentMonthRevenue + cost);
                }

                // Treatment type tracking
                String type = treatment.getType();
                Integer typeCount = treatmentsByType.get(type);
                if (typeCount == null) {
                    treatmentsByType.put(type, 1);
                } else {
                    treatmentsByType.put(type, typeCount + 1);
                }
            }
        }

        // Calculate averages
        if (totalTreatments > 0) {
            averageTreatmentCost = totalRevenue / totalTreatments;
        }
        if (lowestTreatmentCost == Double.MAX_VALUE) {
            lowestTreatmentCost = 0.0;
        }

        // Build the report
        report.append("\n┌").append("─".repeat(90)).append("┐\n");
        report.append("│").append(centerText("FINANCIAL SUMMARY REPORT", 90)).append("│\n");
        report.append("├").append("─".repeat(90)).append("┤\n");
        report.append(String.format("│ Report Generated: %-70s │\n",
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        report.append("├").append("─".repeat(90)).append("┤\n");

        // Revenue Summary Section
        report.append("│").append(centerText("REVENUE SUMMARY", 90)).append("│\n");
        report.append("├").append("─".repeat(90)).append("┤\n");
        report.append(String.format("│ %-28s : RM %9.2f │ %-28s : RM %8.2f │\n",
                "Total Revenue", totalRevenue, "Completed Revenue", completedRevenue));
        report.append(String.format("│ %-28s : RM %9.2f │ %-28s : RM %8.2f │\n",
                "Pending Revenue", pendingRevenue, "Average Treatment Cost", averageTreatmentCost));
        report.append(String.format("│ %-28s : RM %9.2f │ %-28s : RM %8.2f │\n",
                "Highest Treatment Cost", highestTreatmentCost, "Lowest Treatment Cost", lowestTreatmentCost));

        // Treatment Statistics Section
        report.append("├").append("─".repeat(90)).append("┤\n");
        report.append("│").append(centerText("TREATMENT STATISTICS", 90)).append("│\n");
        report.append("├").append("─".repeat(90)).append("┤\n");
        report.append(String.format("│ %-30s : %8d │ %-30s : %11d │\n",
                "Total Treatments", totalTreatments, "Completed Treatments", completedTreatments));
        report.append(String.format("│ %-30s : %8d │ %-30s : %10.1f%% │\n",
                "Pending Treatments", pendingTreatments, "Completion Rate",
                totalTreatments > 0 ? (completedTreatments * 100.0 / totalTreatments) : 0.0));

        // Treatment Types Section
        report.append("├").append("─".repeat(90)).append("┤\n");
        report.append("│").append(centerText("REVENUE BY TREATMENT TYPE", 90)).append("│\n");
        report.append("├").append("─".repeat(90)).append("┤\n");

        // Create a list to track unique treatment types
        OrderedMap<String, String> uniqueTypes = new OrderedMap<>();
        for (Treatment treatment : allTreatments) {
            if (treatment != null && treatment.getType() != null) {
                uniqueTypes.put(treatment.getType(), treatment.getType());
            }
        }

        for (String type : uniqueTypes) {
            Integer count = treatmentsByType.get(type);
            double typeRevenue = 0.0;
            for (Treatment treatment : allTreatments) {
                if (treatment != null && type.equals(treatment.getType())) {
                    typeRevenue += treatment.getTotalProcedureCost();
                }
            }
            report.append(String.format("│ %-30s : %8d treatments │ Revenue: RM %21.2f │\n",
                    type, count, typeRevenue));
        }

        report.append("└").append("─".repeat(90)).append("┘\n");

        return report;
    }

    /**
     * Build plain text version of financial summary report for file export
     */
    private StringBuilder buildPlainFinancialSummaryReport(OrderedMap<String, Treatment> allTreatments) {
        StringBuilder report = new StringBuilder();

        // Financial calculations (same as above but simplified output)
        double totalRevenue = 0.0;
        double completedRevenue = 0.0;
        double pendingRevenue = 0.0;
        double averageTreatmentCost = 0.0;
        double highestTreatmentCost = 0.0;
        double lowestTreatmentCost = Double.MAX_VALUE;

        OrderedMap<String, Integer> treatmentsByType = new OrderedMap<>();

        int totalTreatments = 0;
        int completedTreatments = 0;
        int pendingTreatments = 0;

        // Process all treatments
        for (Treatment treatment : allTreatments) {
            if (treatment != null) {
                totalTreatments++;
                double cost = treatment.getTotalProcedureCost();
                totalRevenue += cost;

                if (cost > highestTreatmentCost) {
                    highestTreatmentCost = cost;
                }
                if (cost < lowestTreatmentCost && cost > 0) {
                    lowestTreatmentCost = cost;
                }

                String status = treatment.getStatus().toUpperCase();
                if ("COMPLETED".equals(status)) {
                    completedRevenue += cost;
                    completedTreatments++;
                } else {
                    pendingRevenue += cost;
                    pendingTreatments++;
                }

                String type = treatment.getType();
                Integer typeCount = treatmentsByType.get(type);
                if (typeCount == null) {
                    treatmentsByType.put(type, 1);
                } else {
                    treatmentsByType.put(type, typeCount + 1);
                }
            }
        }

        if (totalTreatments > 0) {
            averageTreatmentCost = totalRevenue / totalTreatments;
        }
        if (lowestTreatmentCost == Double.MAX_VALUE) {
            lowestTreatmentCost = 0.0;
        }

        // Build plain text report
        report.append("FINANCIAL SUMMARY REPORT\n");
        report.append("========================\n\n");

        report.append("REVENUE SUMMARY\n");
        report.append("---------------\n");
        report.append("Total Revenue: RM ").append(String.format("%.2f", totalRevenue)).append("\n");
        report.append("Completed Revenue: RM ").append(String.format("%.2f", completedRevenue)).append("\n");
        report.append("Pending Revenue: RM ").append(String.format("%.2f", pendingRevenue)).append("\n");
        report.append("Average Treatment Cost: RM ").append(String.format("%.2f", averageTreatmentCost)).append("\n");
        report.append("Highest Treatment Cost: RM ").append(String.format("%.2f", highestTreatmentCost)).append("\n");
        report.append("Lowest Treatment Cost: RM ").append(String.format("%.2f", lowestTreatmentCost)).append("\n\n");

        report.append("TREATMENT STATISTICS\n");
        report.append("--------------------\n");
        report.append("Total Treatments: ").append(totalTreatments).append("\n");
        report.append("Completed Treatments: ").append(completedTreatments).append("\n");
        report.append("Pending Treatments: ").append(pendingTreatments).append("\n");
        report.append("Completion Rate: ").append(String.format("%.1f",
                totalTreatments > 0 ? (completedTreatments * 100.0 / totalTreatments) : 0.0)).append("%\n\n");

        report.append("REVENUE BY TREATMENT TYPE\n");
        report.append("-------------------------\n");

        // Create a list to track unique treatment types
        OrderedMap<String, String> uniqueTypes = new OrderedMap<>();
        for (Treatment treatment : allTreatments) {
            if (treatment != null && treatment.getType() != null) {
                uniqueTypes.put(treatment.getType(), treatment.getType());
            }
        }

        for (String type : uniqueTypes) {
            Integer count = treatmentsByType.get(type);
            double typeRevenue = 0.0;
            for (Treatment treatment : allTreatments) {
                if (treatment != null && type.equals(treatment.getType())) {
                    typeRevenue += treatment.getTotalProcedureCost();
                }
            }
            report.append(type).append(": ").append(count).append(" treatments, Revenue: RM ")
                    .append(String.format("%.2f", typeRevenue)).append("\n");
        }

        return report;
    }

    /**
     * Print search menu options
     */
    private void printReportsMenu() {
        System.out.println("\n┌" + "─".repeat(40) + "┐");
        printCenteredTableHeader("REPORTS AND STATISTICS MENU", 40);
        System.out.println("├" + "─".repeat(3) + "┬" + "─".repeat(36) + "┤");
        System.out.println("│ # │ Report Option                      │");
        System.out.println("├" + "─".repeat(3) + "┼" + "─".repeat(36) + "┤");
        System.out.println("│ 1 │ Treatment Processing Statistics    │");
        System.out.println("│ 2 │ Patient Treatment Summary          │");
        System.out.println("│ 3 │ Financial Summary Report           │");
        System.out.println("├" + "─".repeat(3) + "┼" + "─".repeat(36) + "┤");
        System.out.println("│ 0 │ Back to Treatment Menu             │");
        System.out.println("└" + "─".repeat(3) + "┴" + "─".repeat(36) + "┘");
    }

    /**
     * Print search menu options
     */
    private void printSearchMenu() {
        System.out.println("\n┌" + "─".repeat(40) + "┐");
        printCenteredTableHeader("SEARCH TREATMENTS MENU", 40);
        System.out.println("├" + "─".repeat(3) + "┬" + "─".repeat(36) + "┤");
        System.out.println("│ # │ Search Option                      │");
        System.out.println("├" + "─".repeat(3) + "┼" + "─".repeat(36) + "┤");
        System.out.println("│ 1 │ Search by Patient Name             │");
        System.out.println("│ 2 │ Search by Date Range               │");
        System.out.println("│ 3 │ Search by Notes                    │");
        System.out.println("│ 4 │ Search by Procedure                │");
        System.out.println("├" + "─".repeat(3) + "┼" + "─".repeat(36) + "┤");
        System.out.println("│ 0 │ Back to Treatment Menu             │");
        System.out.println("└" + "─".repeat(3) + "┴" + "─".repeat(36) + "┘");
    }

    /**
     * Print sort menu options
     */
    private void printSortMenu() {
        System.out.println("\n┌" + "─".repeat(40) + "┐");
        printCenteredTableHeader("SORT TREATMENTS MENU", 40);
        System.out.println("├" + "─".repeat(3) + "┬" + "─".repeat(36) + "┤");
        System.out.println("│ # │ Sort Option                        │");
        System.out.println("├" + "─".repeat(3) + "┼" + "─".repeat(36) + "┤");
        System.out.println("│ 1 │ Sort by Date                       │");
        System.out.println("│ 2 │ Sort by Patient Name               │");
        System.out.println("│ 3 │ Sort by Status                     │");
        System.out.println("│ 4 │ Sort by Critical Priority          │");
        System.out.println("├" + "─".repeat(3) + "┼" + "─".repeat(36) + "┤");
        System.out.println("│ 0 │ Back to Treatment Menu             │");
        System.out.println("└" + "─".repeat(3) + "┴" + "─".repeat(36) + "┘");
    }

    /**
     * Search treatments by patient name
     */
    private void searchByPatientName() {
        System.out.println("\n=== SEARCH BY PATIENT NAME ===");
        String patientName = InputHandler.getString("Enter patient name to search");

        if (patientName == null || patientName.trim().isEmpty()) {
            System.out.println("Invalid patient name.");
            return;
        }

        OrderedMap<String, Treatment> results = treatmentController.searchTreatmentsByPatientName(patientName);

        if (results.isEmpty()) {
            System.out.println("No treatments found for patient name containing: " + patientName);
        } else {
            displayTreatmentList(results, "Treatments for Patient Name: " + patientName);

            // Option to view details
            boolean viewDetails = InputHandler.getYesNo("View treatment details?");
            if (viewDetails && !results.isEmpty()) {
                viewSelectedTreatmentDetails(results);
            }
        }
    }


    /**
     * Search treatments by date range
     */
    private void searchByDateRange() {
        System.out.println("\n=== SEARCH BY DATE RANGE ===");

        try {
            System.out.println("Enter start date:");
            LocalDateTime startDate = InputHandler.getDateTime("Start date and time");

            System.out.println("Enter end date:");
            LocalDateTime endDate = InputHandler.getDateTime("End date and time");

            if (startDate.isAfter(endDate)) {
                System.out.println("Start date cannot be after end date.");
                return;
            }

            OrderedMap<String, Treatment> results = treatmentController.getTreatmentsByDateRange(startDate, endDate);

            if (results.isEmpty()) {
                System.out.println("No treatments found in the specified date range.");
            } else {
                displayTreatmentList(results, "Treatments from " + startDate.toLocalDate() + " to " + endDate.toLocalDate());

                // Option to view details
                boolean viewDetails = InputHandler.getYesNo("View treatment details?");
                if (viewDetails && !results.isEmpty()) {
                    viewSelectedTreatmentDetails(results);
                }
            }

        } catch (Exception e) {
            System.out.println("Error parsing date: " + e.getMessage());
        }
    }

    /**
     * Search treatments by notes
     */
    private void searchByNotes() {
        System.out.println("\n=== SEARCH BY NOTES ===");
        String keyword = InputHandler.getString("Enter keyword to search in notes");

        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Invalid keyword.");
            return;
        }

        OrderedMap<String, Treatment> results = treatmentController.searchTreatmentByNotes(keyword);

        if (results.isEmpty()) {
            System.out.println("No treatments found with notes containing: " + keyword);
        } else {
            displayTreatmentList(results, "Treatments with Notes containing: " + keyword);

            // Option to view details
            boolean viewDetails = InputHandler.getYesNo("View treatment details?");
            if (viewDetails && !results.isEmpty()) {
                viewSelectedTreatmentDetails(results);
            }
        }
    }

    /**
     * Search treatments by procedure
     */
    private void searchByProcedure() {
        System.out.println("\n=== SEARCH BY PROCEDURE ===");
        String procedureName = InputHandler.getString("Enter procedure name to search");

        if (procedureName == null || procedureName.trim().isEmpty()) {
            System.out.println("Invalid procedure name.");
            return;
        }

        OrderedMap<String, Treatment> results = treatmentController.searchTreatmentsByProcedure(procedureName);

        if (results.isEmpty()) {
            System.out.println("No treatments found with procedure containing: " + procedureName);
        } else {
            displayTreatmentList(results, "Treatments with Procedure: " + procedureName);

            // Option to view details
            boolean viewDetails = InputHandler.getYesNo("View treatment details?");
            if (viewDetails && !results.isEmpty()) {
                viewSelectedTreatmentDetails(results);
            }
        }
    }

    /**
     * Sort treatments by date
     */
    private void sortByDate() {
        System.out.println("\n=== SORT BY DATE ===");

        boolean ascending = InputHandler.getYesNo("Sort in ascending order? (No for descending)");

        OrderedMap<String, Treatment> sortedTreatments = treatmentController.sortTreatmentsByDate(ascending);

        if (sortedTreatments.isEmpty()) {
            System.out.println("No treatments available to sort.");
        } else {
            String order = ascending ? "Ascending" : "Descending";
            displayTreatmentList(sortedTreatments, "Treatments Sorted by Date (" + order + ")");

            // Option to view details
            boolean viewDetails = InputHandler.getYesNo("View treatment details?");
            if (viewDetails && !sortedTreatments.isEmpty()) {
                viewSelectedTreatmentDetails(sortedTreatments);
            }
        }
    }

    /**
     * Sort treatments by patient name
     */
    private void sortByPatientName() {
        System.out.println("\n=== SORT BY PATIENT NAME ===");

        boolean ascending = InputHandler.getYesNo("Sort in ascending order? (No for descending)");

        OrderedMap<String, Treatment> sortedTreatments = treatmentController.sortTreatmentsByPatientName(ascending);

        if (sortedTreatments.isEmpty()) {
            System.out.println("No treatments available to sort.");
        } else {
            String order = ascending ? "Ascending" : "Descending";
            displayTreatmentList(sortedTreatments, "Treatments Sorted by Patient Name (" + order + ")");

            // Option to view details
            boolean viewDetails = InputHandler.getYesNo("View treatment details?");
            if (viewDetails && !sortedTreatments.isEmpty()) {
                viewSelectedTreatmentDetails(sortedTreatments);
            }
        }
    }

    /**
     * Sort treatments by status
     */
    private void sortByStatus() {
        System.out.println("\n=== SORT BY STATUS ===");

        boolean ascending = InputHandler.getYesNo("Sort in ascending order? (No for descending)");

        OrderedMap<String, Treatment> sortedTreatments = treatmentController.sortTreatmentsByStatus(ascending);

        if (sortedTreatments.isEmpty()) {
            System.out.println("No treatments available to sort.");
        } else {
            String order = ascending ? "Ascending" : "Descending";
            displayTreatmentList(sortedTreatments, "Treatments Sorted by Status (" + order + ")");

            // Option to view details
            boolean viewDetails = InputHandler.getYesNo("View treatment details?");
            if (viewDetails && !sortedTreatments.isEmpty()) {
                viewSelectedTreatmentDetails(sortedTreatments);
            }
        }
    }

    /**
     * Sort treatments by critical priority
     */
    private void sortByCriticalPriority() {
        System.out.println("\n=== SORT BY CRITICAL PRIORITY ===");

        boolean criticalFirst = InputHandler.getYesNo("Show critical treatments first? (No for regular first)");

        OrderedMap<String, Treatment> sortedTreatments = treatmentController.sortTreatmentsByCriticalPriority(criticalFirst);

        if (sortedTreatments.isEmpty()) {
            System.out.println("No treatments available to sort.");
        } else {
            String order = criticalFirst ? "Critical First" : "Regular First";
            displayTreatmentList(sortedTreatments, "Treatments Sorted by Priority (" + order + ")");

            // Option to view details
            boolean viewDetails = InputHandler.getYesNo("View treatment details?");
            if (viewDetails && !sortedTreatments.isEmpty()) {
                viewSelectedTreatmentDetails(sortedTreatments);
            }
        }
    }

    public static void printCenteredText(String text) {
        int totalWidth = 89; // width between the box borders
        int padding = (totalWidth - text.length()) / 2;
        String centeredText = String.format("│%" + (padding + text.length()) + "s%" + (totalWidth - padding - text.length()) + "s│", text, "");
        System.out.println(centeredText);
    }

    private String[] wrapText(String text, int width) {
        if (text == null) return new String[]{""};
        StringBuilder result = new StringBuilder();
        String[] words = text.split(" ");
        int lineLen = 0;
        for (String word : words) {
            if (lineLen + word.length() + (lineLen == 0 ? 0 : 1) > width) {
                result.append("\n");
                lineLen = 0;
            }
            if (lineLen > 0) {
                result.append(" ");
                lineLen++;
            }
            result.append(word);
            lineLen += word.length();
        }
        return result.toString().split("\\n");
    }


    /**
     * Helper method to center text within a given width
     * @param text The text to center
     * @param totalWidth The total width available (excluding border characters)
     * @return Centered text string with proper padding
     */
    public static String centerText(String text, int totalWidth) {
        if (text == null) text = "";

        // If text is longer than available width, truncate it
        if (text.length() > totalWidth) {
            text = text.substring(0, totalWidth - 3) + "...";
        }

        int padding = (totalWidth - text.length()) / 2;
        int rightPadding = totalWidth - text.length() - padding;

        return String.format("%" + (padding + text.length()) + "s%" + rightPadding + "s", text, "");
    }

    /**
     * Helper method to center text and return it with table borders
     * @param text The text to center
     * @param totalWidth The total width available (excluding border characters)
     * @return Complete table row with borders and centered text
     */
    public static String centerTextWithBorders(String text, int totalWidth) {
        return "│" + centerText(text, totalWidth) + "│";
    }

    /**
     * Helper method to create a centered table header line
     * @param text The header text
     * @param totalWidth The total width available (excluding border characters)
     */
    public static void printCenteredTableHeader(String text, int totalWidth) {
        System.out.println(centerTextWithBorders(text, totalWidth));
    }

    /**
     * Helper method for common table widths used in the application
     */
    public static class TableWidths {
        public static final int NARROW = 40;    // For small tables
        public static final int MEDIUM = 60;    // For medium tables
        public static final int WIDE = 80;      // For wide tables
        public static final int EXTRA_WIDE = 89; // For pharmacy-style tables
        public static final int FULL_WIDTH = 100; // For very wide tables
    }
}