package entity;

import java.time.LocalDateTime;
import java.io.Serializable;

public class VisitHistory  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String visitId;
    private Patient patient;
    private LocalDateTime visitDate;
    private String visitReason;
    private String status;

    public VisitHistory(String visitId, Patient patient, LocalDateTime visitDate, String visitReason, String status) {
        this.visitId = visitId;
        this.patient = patient;
        this.visitDate = visitDate;
        this.visitReason = visitReason;
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
          .append("Status: ").append(status).append("\n");
        return sb.toString();
    }
}