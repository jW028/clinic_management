package control;

import adt.CustomADT;
import entity.*;
import java.time.LocalDateTime;
import utility.IDGenerator;

public class TreatmentMaintenance {
    private CustomADT<String, Treatment> treatments;

    private CustomADT<String, Treatment> emergencyQueue;
    private CustomADT<String, Treatment> regularQueue;

    private CustomADT<String, String> recentTreatments;
    private CustomADT<String, Prescription> prescriptions;
    private CustomADT<String, Consultation> consultations;

    private PatientMaintenance patientMaintenance;
    private final ConsultationMaintenance consultationController;


    public TreatmentMaintenance() {
        this.treatments = new CustomADT<>();
        this.emergencyQueue = new CustomADT<>();
        this.regularQueue = new CustomADT<>();
        this.recentTreatments = new CustomADT<>();

        this.prescriptions = new CustomADT<>();
        this.consultations = new CustomADT<>();
        this.patientMaintenance = new PatientMaintenance();
        this.consultationController = new ConsultationMaintenance();

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
        Consultation consultation = consultations.get(consultationID);
        if (consultation == null) {
            throw new IllegalArgumentException("Consultation " + consultationID +" not found.");
        }
        // Generate new Treatment ID
        String treatmentID = IDGenerator.generateTreatmentID();

        // Get patient and doctor from consultation
        Patient patient = patientMaintenance.getPatientById(consultation.getPatientId());
        Doctor doctor = new Doctor(consultation.getDoctorId(), "Test Dr.", "Test Specialty", "011-12345678", "test@example.com", "Test Clinic", "Male", "12-12-1980");

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
            diagnosis,
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

        System.out.println("Treatment " + treatmentID + " added successfully.");
    }

    /**
     * Get available consultations that don't have treatments yet
     * @return CustomADT of available consultations
     */
    public CustomADT<String, Consultation> getConsultationsWithoutTreatment() {
        // TODO: Change to match consultation controller
        Consultation[] availableArr = consultationController.getAllConsultations();
        CustomADT<String, Consultation> available = new CustomADT<>();

        for (Consultation consultation : availableArr) {
            for (int i = 0; i < treatments.size(); i++) {
                if (!(treatments.get(i).getConsultationID().equals(consultation.getConsultationId()))) {
                    available.put(consultation.getConsultationId(), consultation);
                    break;
                }
            }
        }
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

            if (removed.hasPrescriptions()) {
                prescriptions.remove(removed.getPrescription().getPrescriptionID());
            }

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
        if (treatment != null && treatment.hasPrescriptions()) {
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
     * Get a list of treatments for a specific patient
     * @param patientID ID of the patient
     * @return CustomADT of treatments for the patient
     */
    public CustomADT<String, Treatment> getTreatmentsByPatient(String patientID) {
        CustomADT<String, Treatment> patientTreatments = new CustomADT<>();
        for (Treatment treatment : treatments) {
            if (treatment.getPatient().getPatientId().equals(patientID)) {
                patientTreatments.put(treatment.getTreatmentID(), treatment);
            }
        }
        return patientTreatments;
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
     * Get treatments by status
     * @param status The status to filter by
     * @return CustomADT of treatments with the specified status
     */
    public CustomADT<String, Treatment> getTreatmentsByStatus(String status) {
        CustomADT<String, Treatment> filtered = new CustomADT<>();
        
        for (Treatment treatment : treatments) {
            if (treatment.getStatus().equalsIgnoreCase(status)) {
                filtered.put(treatment.getTreatmentID(), treatment);
            }
        }

        return filtered;
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
            
            //TODO: Implement prescription controller
            return true;
        }
        return false;
    }

}