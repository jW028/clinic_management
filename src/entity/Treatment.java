package entity;

import adt.CustomADT;
import java.time.LocalDateTime;

public class Treatment {
    // Core identifiers
    private String treatmentID;
    private String consultationID;

    // Core entities
    private Patient patient;
    private Doctor doctor;
    private Diagnosis diagnosis;
    private CustomADT<String, Procedure> procedures;

    // Treatment details
    private Prescription prescription; // Optional, can be null
    private LocalDateTime treatmentDate;
    private String notes;
    private boolean isCritical;

    // Treatment management
    private String status; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    private String type;   // OUTPATIENT, INPATIENT, EMERGENCY, FOLLOW_UP

    // Follow-up management
    private boolean requiresFollowUp;
    private LocalDateTime followUpDate;
    private String followUpInstructions;
    private String originalTreatmentID;

    // Constructors

    public Treatment(String treatmentID, String consultationID, Patient patient, Doctor doctor, 
                     Diagnosis diagnosis, LocalDateTime treatmentDate, String notes,
                     boolean isCritical) {

        // Initialize core identifiers and clinical information
        this.treatmentID = treatmentID;
        this.consultationID = consultationID;
        this.patient = patient;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.treatmentDate = treatmentDate;
        this.notes = notes;
        this.isCritical = isCritical;
        // Initialize procedures as an empty CustomADT
        this.procedures = new CustomADT<>();

        // Default values
        this.status = "SCHEDULED";
        this.type = "OUTPATIENT";
        this.requiresFollowUp = false;
    }

    public Treatment(String treatmentID, String consultationID, Patient patient, Doctor doctor, Diagnosis diagnosis,
                     Prescription prescription, LocalDateTime treatmentDate, String notes,
                     boolean isCritical) {
        this(treatmentID, consultationID, patient, doctor, diagnosis,
         treatmentDate, notes, isCritical);

        this.prescription = prescription;
        // Initialize procedures as an empty CustomADT
        this.procedures = new CustomADT<>();

        this.status = "SCHEDULED";
        this.type = "OUTPATIENT";
        this.requiresFollowUp = false;
    }

    // Constructor for follow-up treatments
    public Treatment(String treatmentID, String consultationID, Patient patient, Doctor doctor,
                     Diagnosis diagnosis, LocalDateTime treatmentDate,
                     String notes, boolean isCritical, String originalTreatmentID) {
        this(treatmentID, consultationID, patient, doctor, diagnosis,
             treatmentDate, notes, isCritical);

        this.procedures = new CustomADT<>();
        this.originalTreatmentID = originalTreatmentID;
        this.type = "FOLLOW_UP";
    }

    // Prescription Management
    public boolean hasPrescriptions() {
        return prescription != null;
    }

    // Treatment Lifecycle Management
    public void complete() {
        this.status = "COMPLETED";
    }

    public void cancel(String reason) {
        if ("SCHEDULED".equals(status)) {
            this.status = "CANCELLED";
            this.notes += "\nCancellation Reason: " + reason;
        }
    }

    public void scheduleFollowUp(LocalDateTime followUpDate, String instructions) {
        this.requiresFollowUp = true;
        this.followUpDate = followUpDate;
        this.followUpInstructions = instructions;
    }

    // Procedure Management
    public void addProcedure(Procedure procedure) {
        this.procedures.put(procedure.getProcedureID(), procedure);
    }

    public void removeProcedure(String procedureID) {
        this.procedures.remove(procedureID);
    }


    public boolean hasProcedures() {
        return procedures.size() > 0;
    }

    // Cost Management
    
    public double getTotalProcedureCost() {
        double totalCost = 0.0;
        for (int i = 0; i < procedures.size(); i++) {
            Procedure procedure = procedures.get(i);
            if (procedure != null) {
                totalCost += procedure.getCost();
            }
        }
        return totalCost;
    }

    // Validation and Business Logic
    public boolean isOverdue() {
        return "SCHEDULED".equals(status) && 
                treatmentDate != null &&
                LocalDateTime.now().isAfter(treatmentDate.plusHours(1));
    }

    // Getters
    public String getTreatmentID() { return treatmentID; }
    public String getConsultationID() { return consultationID; }
    public Patient getPatient() { return patient; }
    public Doctor getDoctor() { return doctor; }
    public Diagnosis getDiagnosis() { return diagnosis; }
    public Prescription getPrescription() { return prescription; }
    public CustomADT<String, Procedure> getProcedures() { return procedures; }
    public LocalDateTime getTreatmentDate() { return treatmentDate; }
    public String getNotes() { return notes; }
    public boolean isCritical() { return isCritical; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public boolean isRequiresFollowUp() { return requiresFollowUp; }
    public LocalDateTime getFollowUpDate() { return followUpDate; }
    public String getFollowUpInstructions() { return followUpInstructions; }
    public String getOriginalTreatmentID() { return originalTreatmentID; }


    // Setters
    public void setNotes(String notes) { this.notes = notes; }
    public void setCritical(boolean critical) { isCritical = critical; }
    public void setType(String type) { this.type = type; }
    public void setOriginalTreatmentID(String originalTreatmentID) { this.originalTreatmentID = originalTreatmentID; }
    public void setRequiresFollowUp(boolean requiresFollowUp) { this.requiresFollowUp = requiresFollowUp; }

    // toString method for displaying treatment details
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TREATMENT DETAILS ===\n")
          .append("Treatment ID: ").append(treatmentID).append("\n")
          .append("Consultation ID: ").append(consultationID).append("\n")
          .append("Status: ").append(status).append("\n")
          .append("Type: ").append(type).append("\n")
          .append("Patient: ").append(patient != null ? patient.getName() : "N/A").append("\n")
          .append("Doctor: ").append(doctor != null ? doctor.getName() : "N/A").append("\n")
          .append("Diagnosis: ").append(diagnosis != null ? diagnosis.getDescription() : "N/A").append("\n")
          .append("Treatment Date: ").append(treatmentDate != null ? treatmentDate.toString() : "N/A").append("\n");


        sb.append("Critical: ").append(isCritical ? "Yes" : "No").append("\n");

        // Prescriptions
        sb.append("\n=== PRESCRIPTIONS ===\n");
        if (prescription != null) {
            CustomADT<String, PrescribedMedicine> medicines = prescription.getMedicines();
            if (medicines != null & medicines.size() > 0) {
                medicines.forEach(medicine -> {
                    sb.append(medicine.toString()).append("\n");
                });
            }
        } else {
            sb.append("None\n");
        }

        // Procedures
        sb.append("\n=== PROCEDURES ===\n");
        if (procedures != null && !procedures.isEmpty()) {
            procedures.forEach(procedure -> {
                sb.append("- ").append(procedure.getProcedureName())
                  .append(" (RM").append(String.format("%.2f", procedure.getCost())).append(")\n");
            });
        } else {
            sb.append("None");
        }

        // Follow-up details
        
        if (requiresFollowUp) {
            sb.append("\n\n=== FOLLOW-UP DETAILS ===\n")
                .append("Follow-Up Required: Yes\n")
                .append("Follow-Up Date: ").append(followUpDate != null ? followUpDate.toString() : "N/A").append("\n")
                .append("Instructions: ").append(followUpInstructions != null ? followUpInstructions : "N/A").append("\n");
            if (followUpInstructions != null) {
                sb.append("Instructions: ").append(followUpInstructions).append("\n");
            }
        }

        sb.append("\nNotes: ").append(notes != null ? notes : "None");      
        
        return sb.toString();
    }
}