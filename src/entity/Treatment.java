package entity;

import adt.CustomADT;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Treatment implements Serializable {
    // Core identifiers
    private String treatmentID;
    private String consultationID;

    // Core entities
    private Patient patient;
    private Doctor doctor;
    private CustomADT<String, Procedure> procedures;

    // Treatment details
    private Prescription prescription; // Optional, can be null
    private LocalDateTime treatmentDate;
    private String notes;
    private boolean isCritical;

    // Treatment management
    private String status; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    private String type;   // OUTPATIENT, INPATIENT, EMERGENCY, FOLLOW_UP


    // Constructors

    public Treatment(String treatmentID, String consultationID, Patient patient, Doctor doctor,
                     LocalDateTime treatmentDate, String notes, boolean isCritical) {

        // Initialize core identifiers and clinical information
        this.treatmentID = treatmentID;
        this.consultationID = consultationID;
        this.patient = patient;
        this.doctor = doctor;
        this.treatmentDate = treatmentDate;
        this.notes = notes;
        this.isCritical = isCritical;
        // Initialize procedures as an empty CustomADT
        this.procedures = new CustomADT<>();

        // Default values
        this.status = "SCHEDULED";
        this.type = "OUTPATIENT";
    }

    public Treatment(String treatmentID, String consultationID, Patient patient,
                     Prescription prescription, LocalDateTime treatmentDate, String notes,
                     boolean isCritical) {
        this.treatmentID = treatmentID;
        this.consultationID = consultationID;
        this.patient = patient;
        this.doctor = null; // No doctor specified in this constructor
        this.treatmentDate = treatmentDate;
        this.notes = notes;
        this.isCritical = isCritical;

        this.prescription = prescription;
        // Initialize procedures as an empty CustomADT
        this.procedures = new CustomADT<>();

        this.status = "SCHEDULED";
        this.type = "OUTPATIENT";
    }

    // Prescription Management
    public boolean hasPrescription() {
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
        for (Procedure procedure : procedures) {
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
    public Prescription getPrescription() { return prescription; }
    public CustomADT<String, Procedure> getProcedures() { return procedures; }
    public LocalDateTime getTreatmentDate() { return treatmentDate; }
    public String getNotes() { return notes; }
    public boolean isCritical() { return isCritical; }
    public String getStatus() { return status; }
    public String getType() { return type; }


    // Setters
    public void setNotes(String notes) { this.notes = notes; }
    public void setCritical(boolean critical) { isCritical = critical; }
    public void setType(String type) { this.type = type; }
    public void setPrescription(Prescription prescription) { this.prescription = prescription; }
    public void setStatus(String status) { this.status = status; }

    // toString method for displaying treatment details
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TREATMENT DETAILS ===\n")
          .append("Treatment ID: ").append(treatmentID).append("\n")
          .append("Consultation ID: ").append(consultationID).append("\n")
          .append("Patient: ").append(patient != null ? patient.getName() : "N/A").append("\n")
          .append("Doctor: ").append(doctor != null ? doctor.getName() : "N/A").append("\n")
          .append("Status: ").append(status).append("\n")
          .append("Type: ").append(type).append("\n")
          .append("Treatment Date: ").append(treatmentDate != null ? treatmentDate.toString() : "N/A").append("\n");


        sb.append("Critical: ").append(isCritical ? "Yes" : "No").append("\n");

        // Prescriptions
        sb.append("\n=== PRESCRIPTIONS ===\n");
        if (prescription != null) {
            CustomADT<String, PrescribedMedicine> medicines = prescription.getMedicines();
            if (medicines != null && medicines.size() > 0) {
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
            for (Procedure procedure : procedures) {
                sb.append("- ").append(procedure.getProcedureName())
                  .append(" (RM").append(String.format("%.2f", procedure.getCost())).append(")\n");
            };
        } else {
            sb.append("None");
        }

        sb.append("\nNotes: ").append(notes != null ? notes : "None");      
        
        return sb.toString();
    }
}