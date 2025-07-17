package entity;

import java.time.LocalDateTime;
import adt.CustomADT;

public class Consultation {
    private String consultationId;
    private String patientId;
    private String doctorId;
    private LocalDateTime consultationTime;
    private CustomADT<Integer, ConsultationService> servicesUsed;
    private String diagnosis;
    private String notes;
    private Payment payment;
    private boolean followUpNeeded;
    private LocalDateTime followUpDate;

    public Consultation(String consultationId, String patientId, String doctorId, LocalDateTime consultationTime, CustomADT<Integer, ConsultationService> servicesUsed,
                        String diagnosis, String notes, Payment payment, boolean followUpNeeded, LocalDateTime followUpDate) {
        this.consultationId = consultationId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.consultationTime = consultationTime;
        this.servicesUsed = servicesUsed;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.payment = payment;
        this.followUpNeeded = followUpNeeded;
        this.followUpDate = followUpDate;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getConsultationTime() {
        return consultationTime;
    }

    public CustomADT<Integer, ConsultationService> getServicesUsed() {
        return servicesUsed;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public Payment getPayment() {
        return payment;
    }

    public boolean isFollowUpNeeded() {
        return followUpNeeded;
    }

    public LocalDateTime getFollowUpDate() {
        return followUpDate;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setConsultationTime(LocalDateTime consultationTime) {
        this.consultationTime = consultationTime;
    }

    public void setServicesUsed(CustomADT<Integer, ConsultationService> servicesUsed) {
        this.servicesUsed = servicesUsed;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void setFollowUpNeeded(boolean followUpNeeded) {
        this.followUpNeeded = followUpNeeded;
    }

    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
    }

    @Override
    public String toString() {
        return "Consultation ID: " + consultationId +
                "\nPatient ID: " + patientId +
                "\nDoctor ID: " + doctorId +
                "\nConsultation Time: " + consultationTime +
                "\nServices Used: " + servicesUsed +
                "\nDiagnosis: " + diagnosis +
                "\nNotes: " + notes +
                "\nPayment: " + payment +
                "\nFollow Up Needed: " + followUpNeeded +
                "\nFollow Up Date: " + followUpDate;
    }
}
