package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MedicalRecord {
    private String recordId;
    private Patient patient;
    private Treatment treatment;
    private LocalDateTime createdDate;

    public MedicalRecord(String recordId, Patient patient, Treatment treatment) {
        this.recordId = recordId;
        this.patient = patient;
        this.treatment = treatment;
        this.createdDate = LocalDateTime.now();

        patient.addMedicalRecord(recordId, this);
    }

    // Getters
    public String getRecordId() { return recordId; }
    public Patient getPatient() { return patient; }
    public Treatment getTreatment() { return treatment; }
    public LocalDateTime getCreatedDate() { return createdDate; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Record ID: %s | Date: %s | Patient: %s | Diagnosis: %s",
                recordId,
                createdDate.format(formatter),
                patient.getName(),
                treatment.getDiagnosis().getName());
    }

    public String getDetailedView() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Medical Record Details ===\n")
                .append("Record ID: ").append(recordId).append("\n")
                .append("\nPatient Information:\n")
                .append(patient.toString()).append("\n")
                .append("\nTreatment Information:\n")
                .append(treatment.toString());
        return sb.toString();
    }
}