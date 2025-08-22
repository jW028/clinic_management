package entity;

import java.time.LocalDateTime;

public class VisitHistory {
    private String visitId;
    private Patient patient;
    private LocalDateTime visitDate;
    private String visitReason;
    private Treatment treatment;
    private Consultation consultation;
    private double visitCost;
    private String status;

    public VisitHistory(String visitId, Patient patient, LocalDateTime visitDate, String visitReason, Treatment treatment, Consultation consultation, double visitCost, String status) {
        this.visitId = visitId;
        this.patient = patient;
        this.visitDate = visitDate;
        this.visitReason = visitReason;
        this.treatment = treatment;
        this.consultation = consultation;
        this.visitCost = visitCost;
        this.status = status;
    }

    public String getVisitId() {
        return visitId;
    }
    public Patient getPatient() {
        return patient;
    }
    public LocalDateTime getVisitDate() {
        return visitDate;}
    public String getVisitReason() {
        return visitReason;
    }
    public Treatment getTreatment() {
        return treatment;
    }
    public Consultation getConsultation() {
        return consultation;
    }
    public double getVisitCost() {
        return visitCost;
    }
    public String getStatus() {
        return status;
    }
    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }
    public void setVisitReason(String visitReason) {
        this.visitReason = visitReason;
    }
    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }
    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }
    public void setVisitCost(double visitCost) {
        this.visitCost = visitCost;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== VISIT HISTORY ===\n");
        sb.append("Visit ID: ").append(visitId).append("\n")
          .append("Patient: ").append(patient.getName()).append("\n")
          .append("Visit Date: ").append(visitDate).append("\n")
          .append("Visit Reason: ").append(visitReason).append("\n")
          .append("Treatment: ").append(treatment.getTreatmentID()).append("\n")
          .append("Consultation: ").append(consultation.getConsultationId()).append("\n")
          .append("Visit Cost: $").append(visitCost).append("\n")
          .append("Status: ").append(status).append("\n");
        return sb.toString();
    }
}