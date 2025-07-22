package entity;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

public class Treatment {
    private String treatmentID;
    private String consultationID;
    private Patient patient;
    private Doctor doctor;
    private Diagnosis diagnosis;
    private Prescription prescription;
    private String[] proceduresPerformed;
    private LocalDateTime treatmentDate;
    private String notes;
    private boolean isCritical;

    public Treatment(String treatmentID, String consultationID, Patient patient, Doctor doctor, Diagnosis diagnosis, String[] proceduresPerformed, LocalDateTime treatmentDate, String notes,
                     boolean isCritical) {
        this.treatmentID = treatmentID;
        this.consultationID = consultationID;
        this.patient = patient;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.proceduresPerformed = proceduresPerformed;
        this.treatmentDate = treatmentDate;
        this.notes = notes;
        this.isCritical = isCritical;
    }
    public Treatment(String treatmentID, String consultationID, Patient patient, Doctor doctor, Diagnosis diagnosis,
                     Prescription prescription, String[] proceduresPerformed, LocalDateTime treatmentDate, String notes,
                     boolean isCritical) {
        this.treatmentID = treatmentID;
        this.consultationID = consultationID;
        this.patient = patient;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.proceduresPerformed = proceduresPerformed;
        this.treatmentDate = treatmentDate;
        this.notes = notes;
        this.isCritical = isCritical;
    }

    // Getters

    public String getTreatmentID() {
        return treatmentID;
    }

    public String getConsultationID() {
        return consultationID;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Diagnosis getDiagnosis() {
        return diagnosis;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public String[] getProceduresPerformed() {
        return proceduresPerformed;
    }

    public LocalDateTime getTreatmentDate() {
        return treatmentDate;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isCritical() {
        return isCritical;
    }

    // Setters
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCritical(boolean critical) {
        isCritical = critical;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Treatment ID: ").append(treatmentID).append("\n")
          .append("Consultation ID: ").append(consultationID).append("\n")
          .append("Patient: ").append(patient.getName()).append("\n")
          .append("Doctor: ").append(doctor.getName()).append("\n")
          .append("Diagnosis: ").append(diagnosis.getDescription()).append("\n")
          .append("Prescribed Medicines: ");
          if (prescription != null && prescription.length > 0) {
            for ()
          } else {
            sb.append("None");
        }
        sb.append("\nProcedures Performed: ");
        if (proceduresPerformed != null && proceduresPerformed.length > 0) {
            for (String procedure : proceduresPerformed) {
                sb.append(procedure).append(", ");
            }
        } else {
            sb.append("None");
        }
        sb.append("\nTreatment Date: ").append(treatmentDate.toString()).append("\n")
            .append("Notes: ").append(notes).append("\n")
            .append("Is Critical: ").append(isCritical ? "Yes" : "No");

        return sb.toString();
    }
}