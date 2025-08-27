package control;

import adt.CustomADT;
import dao.TreatmentDAO;
import dao.ProcedureDAO;
import entity.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import utility.IDGenerator;

public class TreatmentMaintenance {
    private CustomADT<String, Treatment> treatments;

    private CustomADT<String, Treatment> emergencyQueue;
    private CustomADT<String, Treatment> regularQueue;

    private CustomADT<String, String> recentTreatments;
    private CustomADT<String, Prescription> prescriptions;
    private CustomADT<String, Consultation> consultations;
    private final ConsultationMaintenance consultationController;
    private final PharmacyMaintenance prescriptionController;

    private TreatmentDAO treatmentDAO;
    private ProcedureDAO procedureDAO;

    public TreatmentMaintenance() {
        this.treatmentDAO = new TreatmentDAO();
        this.procedureDAO = new ProcedureDAO();
        this.treatments = new CustomADT<>();
        this.emergencyQueue = new CustomADT<>();
        this.regularQueue = new CustomADT<>();
        this.recentTreatments = new CustomADT<>();

        this.prescriptions = new CustomADT<>();
        this.consultations = new CustomADT<>();
        this.consultationController = new ConsultationMaintenance();
        this.prescriptionController = new PharmacyMaintenance();

        loadAllData();
        IDGenerator.loadCounter("counter.dat");
    }



    public boolean saveAllData() {
        boolean success = true;
        success &= treatmentDAO.saveToFile(treatments);
        success &= treatmentDAO.saveRecentActivities(recentTreatments);
        return success;
    }

    public boolean loadAllData() {
        treatments = treatmentDAO.retrieveFromFile();
        recentTreatments = treatmentDAO.retrieveRecentActivities();

        rebuildQueues();
        return true;
    }

    private void rebuildQueues() {
        emergencyQueue.clear();
        regularQueue.clear();

        for (Treatment treatment : treatments) {
            if ("SCHEDULED".equals(treatment.getStatus()) ||
                    "IN_PROGRESS".equals(treatment.getStatus())) {
                if (treatment.isCritical()) {
                    emergencyQueue.offer(treatment.getTreatmentID(), treatment);
                } else {
                    regularQueue.offer(treatment.getTreatmentID(), treatment);
                }
            }
        }
    }

    /**
     * Create a new treatment from an existing consultation
     * @param consultationID ID of the consultation to base the treatment on
     * @param treatmentType Type of treatment (OUTPATIENT, INPATIENT etc.)
     * @param isCritical Whether the treatment is critical or not
     * @param notes Additional treatment notes
     * @return The created Treatment object
     */
    public Treatment createTreatment(String consultationID, String treatmentType, boolean isCritical, String notes) {
        Consultation consultation = consultationController.getConsultation(consultationID);
        if (consultation == null) {
            throw new IllegalArgumentException("Consultation " + consultationID +" not found.");
        }
        // Generate new Treatment ID
        String treatmentID = IDGenerator.generateTreatmentID();

        // Get patient and doctor from consultation
        Patient patient = consultation.getPatient();
        Doctor doctor = consultation.getDoctor();

        // Create diagnosis from consultation
        Diagnosis diagnosis = new Diagnosis(
                "DG" + System.currentTimeMillis(), // Simple ID generation
                "Test Diagnosis",
                "No Severity"
        );

        // Create treatment
        Treatment treatment = new Treatment (
                treatmentID,
                consultationID,
                patient,
                doctor,
                LocalDateTime.now(),
                notes,
                isCritical
        );

        treatment.setType(treatmentType);

        // Add to the system
        addTreatment(treatment);

        return treatment;

    }
    
    /** TODO: KIV
     * Add a new treatment 
     * @param treatment Treatment object to add
     */
    public void addTreatment(Treatment treatment) {
        String treatmentID = treatment.getTreatmentID();
        // Store in main collection using map functionality
        treatments.put(treatmentID, treatment);

        // Add to appropriate queue
        if (treatment.isCritical()) {
            emergencyQueue.offer(treatmentID, treatment);
        } else {
            regularQueue.offer(treatmentID, treatment);
        }

        // Track recent treatments using stack functionality
        recentTreatments.push("OP_" + System.currentTimeMillis(), 
                            "Added treatment: " + treatmentID);
        saveAllData();
        IDGenerator.saveCounters("counter.dat");

        System.out.println("Treatment " + treatmentID + " added successfully.");
    }

    /**
     * Get available consultations that don't have treatments yet
     * @return CustomADT of available consultations
     */
    public CustomADT<String, Consultation> getConsultationsWithoutTreatment() {
        Consultation[] availableArr = consultationController.getAllConsultations();
        System.out.println("DEBUG: getConsultationsWithoutTreatment -> availableArr.length=" + availableArr.length);

        CustomADT<String, Consultation> available = new CustomADT<>();

        for (Consultation consultation : availableArr) {
            if (consultation == null) {
                System.out.println("DEBUG: skipping null consultation element");
                continue;
            }
            String consultId = consultation.getConsultationId();
            System.out.println("DEBUG: checking consultation id=" + consultId);

            boolean hasExistingTreatment = false;
            for (Treatment treatment : treatments) {
                if (treatment == null) continue;
                String tConsultId = treatment.getConsultationID();
                // defensive null checks
                if (tConsultId != null && consultId != null && tConsultId.equals(consultId)) {
                    hasExistingTreatment = true;
                    System.out.println("Consultation " + consultId + " already has treatment " + treatment.getTreatmentID());
                    break;
                }
            }

            if (!hasExistingTreatment) {
                // defensive: ensure consultId not null
                if (consultId == null) {
                    System.out.println("Cannot add consultation with null id, skipping");
                } else {
                    available.put(consultId, consultation);
                    System.out.println("Added consultation " + consultId + " to available");
                }
            }
        }

        System.out.println("getConsultationsWithoutTreatment -> available.size=" + available.size());
        return available;
    }

    /**
     * Remove a treatment by ID using Map functionality
     * @param treatmentID ID of the treatment to remove
     * @return true if treatment was removed successfully, false otherwise
     */
    public boolean removeTreatment(String treatmentID) {
        Treatment removed = treatments.remove(treatmentID);
        if (removed != null) {
            emergencyQueue.remove(treatmentID);
            regularQueue.remove(treatmentID);

            if (removed.hasPrescription()) {
                prescriptions.remove(removed.getPrescription().getPrescriptionID());
            }

            saveAllData();
            System.out.println("Treatment " + treatmentID + " removed successfully.");
            return true;
        }
        System.out.println("Treatment " + treatmentID + " not found.");
        return false;
    }

    /**
     * Retrieve a treatment by ID using Map functionality
     * @param treatmentID ID of the treatment to retrieve
     * @return Treatment object if found, null otherwise
     */
    public Treatment getTreatmentByID(String treatmentID) {
        Treatment treatment = treatments.get(treatmentID);
        return treatment;
    }

    /**
     * Retrieve all treatments 
     * @return CustomADT of all treatments
     */
    public CustomADT<String, Treatment> getAllTreatments() {
        return treatments;
    }

    /**
     * Get prescription for a treatment
     * @param treatmentID ID of the treatment
     * @return Prescription object if exists, null otherwise
     */
    public Prescription getPrescriptionForTreatment(String treatmentID) {
        Treatment treatment = treatments.get(treatmentID);
        if (treatment != null && treatment.hasPrescription()) {
            return treatment.getPrescription();
        }
        return null;
    }

    /**
     * Process next emergency treatment using Queue functionality
     * @return Treatment object if processed, null if no emergency treatments
     */
    public Treatment processNextEmergency() {
        Treatment next = emergencyQueue.poll();
        if (next != null) {
            next.complete(); // Mark treatment as completed
            recentTreatments.push("OP_" + System.currentTimeMillis(),
                    "Processed emergency treatment: " + next.getTreatmentID());
            System.out.println("Processed emergency treatment: " + next.getTreatmentID());
            saveAllData();
        } else {
            System.out.println("No emergency treatments in queue.");
        }

        return next;

    }

    /**
     * Process next regular treatment using Queue functionality
     * @return Treatment object if processed, null if no regular treatments 
     */
    public Treatment processNextRegular() {
        Treatment next = regularQueue.poll();
        if (next != null) {
            next.complete(); // Mark treatment as completed
            recentTreatments.push("OP_" + System.currentTimeMillis(),
                                  "Processed regular treatment: " + next.getTreatmentID());
            System.out.println("Processed regular treatment: " + next.getTreatmentID());
        } else {
            System.out.println("No regular treatments in queue.");
        }

        saveAllData();
        return next;

    }

    /**
     * Get recent treatments using Stack functionality (LIFO)
     * @return CustomADT of recent treatments
     */
    public CustomADT<String, String> getRecentTreatments() {
        return recentTreatments;
    }

    /**
     * Clear all recent treatments
     * @return true if cleared successfully, false if no recent treatments
     */
    public boolean clearRecentTreatments() {
        if (recentTreatments.isEmpty()) {
            return false;
        }
        recentTreatments.clear();
        
        saveAllData();
        return true;


    }

    /**
     * Get all patients who have existing treatments
     * @return CustomADT of patients with treatments
     */
    public CustomADT<String, Patient> getPatientsWithTreatments() {
        CustomADT<String, Patient> patientsWithTreatments = new CustomADT<>();
        // Iterate through all treatments and collect unique patient IDs
        for (Treatment treatment : treatments){
            Patient patient = treatment.getPatient();
            if (patient != null) {
                // Add patient if not already present in the collection
                if (!patientsWithTreatments.containsKey(patient.getPatientId())) {
                    patientsWithTreatments.put(patient.getPatientId(), patient);
                }
            }
        }
        return patientsWithTreatments;
    }

    /**
     * Get patients with active treatments only
     * @return CustomADT of patients with active treatments
     */
    public CustomADT<String, Patient> getPatientsWithActiveTreatments() {
        CustomADT<String, Patient> activePatients = new CustomADT<>();
        for (Treatment treatment : treatments) {
            if (("SCHEDULED".equals(treatment.getStatus()) || 
                 "IN_PROGRESS".equals(treatment.getStatus())) && 
                 treatment.getPatient() != null) {
                Patient patient = treatment.getPatient();
                if (!activePatients.containsKey(patient.getPatientId())) {
                    activePatients.put(patient.getPatientId(), patient);
                }
            }
        }
        return activePatients;
    }

    /**
     * Get patients with critical treatments
     * @return CustomADT of patients with critical treatments
     */
    public CustomADT<String, Patient> getPatientsWithCriticalTreatments() {
        CustomADT<String, Patient> criticalPatients = new CustomADT<>();
        for (Treatment treatment : treatments) {
            if (treatment.isCritical() && treatment.getPatient() != null) {
                Patient patient = treatment.getPatient();
                if (!criticalPatients.containsKey(patient.getPatientId())) {
                    criticalPatients.put(patient.getPatientId(), patient);
                }
            }
        }
        return criticalPatients;
    }

    
    /**
     * Add procedure to existing treatment
     * @param treatmentID ID of the treatment to add the procedure to
     * @param procedure Procedure object to add
     * @return true if procedure was added successfully, false otherwise
     */
    public boolean addProcedure(String treatmentID, Procedure procedure) {
        Treatment treatment = treatments.get(treatmentID);
        if (treatment != null) {
            treatment.addProcedure(procedure);
            System.out.println("Procedure " + procedure.getProcedureName() + 
                               " added to treatment " + treatmentID);
            return true;
        }
        System.out.println("Treatment " + treatmentID + " not found.");
        return false;
    }

    /**
     * Get statistics about the current state of treatments
     * @return String containing statistics summary
     */
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Treatment Statistics ===\n")
            .append("Total Treatments: ").append(treatments.size()).append("\n")
            .append("Emergency Treatments: ").append(emergencyQueue.size()).append("\n")
            .append("Regular Treatments: ").append(regularQueue.size()).append("\n")
            .append("Recent Treatments: ").append(recentTreatments.size()).append("\n");

        return sb.toString();
    } 

    /**
     * Check if a treatment exists using Map functionality
     */
    public boolean treatmentExists(String treatmentID) {
        return treatments.containsKey(treatmentID);
    }

    /**
     * Get a list of treatments for a specific patient
     * @param patient Patient object to filter treatments for
     * @return CustomADT of treatments for the patient
     */
    public CustomADT<String, Treatment> getTreatmentsByPatient(Patient patient) {
        if (patient == null) return new CustomADT<>();

        Treatment dummyTreatment = new Treatment("", "", patient, (Doctor)null, LocalDateTime.now(), "", false);
        
        return treatments.filter(dummyTreatment, (t1, t2) -> {
            if (t1.getPatient() != null && t2.getPatient() != null &&
                t1.getPatient().getPatientId().equals(t2.getPatient().getPatientId())) {
                return 0; // Match found
            }
            return 1; // No match
        });
    }


    /**
     * Get treatments by status
     * @param status The status to filter by
     * @return CustomADT of treatments with the specified status
     */
    public CustomADT<String, Treatment> getTreatmentsByStatus(String status) {
        // Create dummy treatment with the specified status
        Treatment dummyTreatment = new Treatment("", "", (Patient)null, (Doctor)null, LocalDateTime.now(), "", false);
        dummyTreatment.setStatus(status);
        // Filter treatments with the same status
        return treatments.filter(dummyTreatment, (t1, t2) -> {
            if (t1.getStatus().equalsIgnoreCase(t2.getStatus())) {
                return 0;
            }
            return -1;
        });
    }

    /**
     * Search treatments by notes keyword
     * @param keyword The keyword to search for in treatment notes
     * @return CustomADT of treatments containing the keyword in their notes
     */
    public CustomADT<String, Treatment> searchTreatmentByNotes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return new CustomADT<>();

        Treatment dummyTreatment = new Treatment("", "", (Patient)null, (Doctor)null, LocalDateTime.now(), "", false);
        dummyTreatment.setNotes(keyword);

        return treatments.filter(dummyTreatment, (t1, t2) -> {
            if (t1.getNotes() != null && t1.getNotes().toLowerCase().contains(t2.getNotes().toLowerCase())) {
                return 0; // Match found
            }
            return 1; // No match
        });
    }

    /**
     * Search treatments by date range
     * @param startDate
     * @param endDate
     * @return CustomADT of treatments within the date range
     */
    public CustomADT<String, Treatment> getTreatmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) return new CustomADT<>();

        // Sort by date first for efficient range search
        treatments.sort(Comparator.comparing(Treatment::getTreatmentDate));
        
        // Create dummy treatments for range bounds
        Treatment startDummy = new Treatment("", "", (Patient)null, (Doctor)null, startDate, "", false);
        Treatment endDummy = new Treatment("", "", (Patient)null, (Doctor)null, endDate, "", false);
        
        // Use rangeSearch with proper comparator
        return treatments.rangeSearch(startDummy, endDummy, 
            Comparator.comparing(Treatment::getTreatmentDate));
    }

    /**
     * Binary search for treatments by procedure
     * Requires treatments to be sorted by procedures
     * @param procedureName
     * @return CustomADT of treatments containing the specified procedure
     */
    public CustomADT<String, Treatment> searchTreatmentsByProcedure(String procedureName) {
        if (procedureName == null || procedureName.isEmpty()) return new CustomADT<>();

        Treatment dummyTreatment = new Treatment("", "", (Patient)null, (Doctor)null, LocalDateTime.now(), "", false);

        return treatments.filter(dummyTreatment, (t1, t2) -> {
            if (treatmentHasProcedure(t1, procedureName)) {
                return 0; // Match found
            }
            return 1; // No match
        });
    }

    /**
     * Helper method to check if treatment has procedure
     */
    private boolean treatmentHasProcedure(Treatment treatment, String procedureName) {
        if (treatment.getProcedures() == null) return false;

        for (Procedure procedure : treatment.getProcedures()) {
            if (procedure != null &&
                procedure.getProcedureName().toLowerCase().contains(procedureName.toLowerCase())) {
                    return true;
            }
        }
        return false;
    }

    /**
     * Search treatments by patient name
     * @param patientName The patient name to search for
     * @return CustomADT of treatments for patients with matching names
     */
    public CustomADT<String, Treatment> searchTreatmentsByPatientName(String patientName) {
        if (patientName == null || patientName.trim().isEmpty()) return new CustomADT<>();

        Treatment dummyTreatment = new Treatment("", "", (Patient)null, (Doctor)null, LocalDateTime.now(), "", false);

        return treatments.filter(dummyTreatment, (t1, t2) -> {
            if (t1.getPatient() != null && 
                t1.getPatient().getName().toLowerCase().contains(patientName.toLowerCase())) {
                return 0; // Match found
            }
            return 1; // No match
        });
    }

    /**
     * Sort treatments by date
     * @param ascending true for ascending order, false for descending
     * @return CustomADT of sorted treatments
     */
    public CustomADT<String, Treatment> sortTreatmentsByDate(boolean ascending) {
        CustomADT<String, Treatment> sortedTreatments = new CustomADT<>();
        
        // Copy all treatments to new CustomADT
        for (Treatment treatment : treatments) {
            sortedTreatments.put(treatment.getTreatmentID(), treatment);
        }
        
        // Sort using CustomADT sort method
        if (ascending) {
            sortedTreatments.sort(Comparator.comparing(Treatment::getTreatmentDate));
        } else {
            sortedTreatments.sort(Comparator.comparing(Treatment::getTreatmentDate).reversed());
        }
        
        return sortedTreatments;
    }

    /**
     * Sort treatments by patient name
     * @param ascending true for ascending order, false for descending
     * @return CustomADT of sorted treatments
     */
    public CustomADT<String, Treatment> sortTreatmentsByPatientName(boolean ascending) {
        CustomADT<String, Treatment> sortedTreatments = new CustomADT<>();
        
        // Copy all treatments to new CustomADT
        for (Treatment treatment : treatments) {
            sortedTreatments.put(treatment.getTreatmentID(), treatment);
        }
        
        // Sort using CustomADT sort method
        if (ascending) {
            sortedTreatments.sort(Comparator.comparing(t -> 
                t.getPatient() != null ? t.getPatient().getName() : ""));
        } else {
            sortedTreatments.sort(Comparator.comparing((Treatment t) -> 
                t.getPatient() != null ? t.getPatient().getName() : "").reversed());
        }
        
        return sortedTreatments;
    }

    /**
     * Sort treatments by status
     * @param ascending true for ascending order, false for descending
     * @return CustomADT of sorted treatments
     */
    public CustomADT<String, Treatment> sortTreatmentsByStatus(boolean ascending) {
        CustomADT<String, Treatment> sortedTreatments = new CustomADT<>();
        
        // Copy all treatments to new CustomADT
        for (Treatment treatment : treatments) {
            sortedTreatments.put(treatment.getTreatmentID(), treatment);
        }
        
        // Sort using CustomADT sort method
        if (ascending) {
            sortedTreatments.sort(Comparator.comparing(Treatment::getStatus));
        } else {
            sortedTreatments.sort(Comparator.comparing(Treatment::getStatus).reversed());
        }
        
        return sortedTreatments;
    }

    /**
     * Sort treatments by critical priority
     * @param criticalFirst true to show critical treatments first, false for regular first
     * @return CustomADT of sorted treatments
     */
    public CustomADT<String, Treatment> sortTreatmentsByCriticalPriority(boolean criticalFirst) {
        CustomADT<String, Treatment> sortedTreatments = new CustomADT<>();
        
        // Copy all treatments to new CustomADT
        for (Treatment treatment : treatments) {
            sortedTreatments.put(treatment.getTreatmentID(), treatment);
        }
        
        // Sort using CustomADT sort method
        if (criticalFirst) {
            // Critical treatments first (true > false)
            sortedTreatments.sort(Comparator.comparing((Treatment t) -> !t.isCritical()));
        } else {
            // Regular treatments first (false > true)
            sortedTreatments.sort(Comparator.comparing(Treatment::isCritical));
        }
        
        return sortedTreatments;
    }

    /**
     * Add prescription to a treatment
     * @param treatmentID ID of the treatment to add the prescription to
     * @param prescription Prescription object to add
     * @return true if prescription was added successfully, false otherwise
     */
    public boolean addPrescriptionToTreatment(String treatmentID, Prescription prescription) {
        Treatment treatment = treatments.get(treatmentID);
        if (treatment != null) {
            treatment.setPrescription(prescription);
            prescriptions.put(prescription.getPrescriptionID(), prescription);
            prescriptionController.enqueuePrescription(prescription);
            
            saveAllData();
            return true;
        }
        return false;
    }

    /**
     * Get all available procedures from the DAO
     * @return CustomADT of all procedures
     */
    public CustomADT<String, Procedure> getAllAvailableProcedures() {
        return procedureDAO.retrieveFromFile();
    }

    /**
     * Get a specific procedure by ID
     * @param procedureID The procedure ID to retrieve
     * @return Procedure object if found, null otherwise
     */
    public Procedure getProcedureByID(String procedureID) {
        CustomADT<String, Procedure> procedures = procedureDAO.retrieveFromFile();
        return procedures.get(procedureID);
    }

    /**
     * Save report content to a file
     * @param reportContent The report content to save
     * @param reportName The name of the report (without extension)
     * @return true if saved successfully, false otherwise
     */
    public boolean saveReportToFile(String reportContent, String reportName) {
        try {
            // Create reports directory if it doesn't exist
            java.io.File reportsDir = new java.io.File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            // Generate filename with timestamp
            String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = "reports/" + reportName + "_" + timestamp + ".txt";
            
            // Write to file
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
                writer.println("=".repeat(80));
                writer.println("CLINIC MANAGEMENT SYSTEM - " + reportName.toUpperCase());
                writer.println("Generated: " + java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("=".repeat(80));
                writer.println();
                writer.print(reportContent);
                writer.println();
                writer.println("=".repeat(80));
                writer.println("End of Report");
            }
            
            System.out.println("✅ Report saved successfully!");
            System.out.println("📁 File saved as: " + filename);
            return true;
            
        } catch (java.io.IOException e) {
            System.err.println("❌ Error saving report: " + e.getMessage());
            return false;
        }
    }


}