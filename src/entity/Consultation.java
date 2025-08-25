package entity;

import adt.CustomADT;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Consultation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String consultationId;
    private Appointment appointment;
    private Appointment followUpAppointment;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime consultationTime;
    private CustomADT<String, ConsultationService> servicesUsed;
    private Diagnosis diagnosis;
    private String notes;
    private Payment payment;
    private boolean followUpNeeded;
    private LocalDateTime followUpDate;

    public Consultation(String consultationId, Appointment appointment, Patient patient, Doctor doctor, LocalDateTime consultationTime, CustomADT<String, ConsultationService> servicesUsed,
                        Diagnosis diagnosis, String notes, Payment payment, boolean followUpNeeded, LocalDateTime followUpDate) {
        this.consultationId = consultationId;
        this.appointment = appointment;
        this.patient = patient;
        this.doctor = doctor;
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

    public Appointment getAppointment() {
        return appointment;
    }

    public Appointment getFollowUpAppointment() {
        return followUpAppointment;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDateTime getConsultationTime() {
        return consultationTime;
    }

    public CustomADT<String, ConsultationService> getServicesUsed() {
        return servicesUsed;
    }

    public Diagnosis getDiagnosis() {
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

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public void setFollowUpAppointment(Appointment followUpAppointment) {
        this.followUpAppointment = followUpAppointment;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setConsultationTime(LocalDateTime consultationTime) {
        this.consultationTime = consultationTime;
    }

    public void setServicesUsed(CustomADT<String, ConsultationService> servicesUsed) {
        this.servicesUsed = servicesUsed;
    }

    public void setDiagnosis(Diagnosis diagnosis) {
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
                "\nAppointment: " + appointment +
//                "\nPatient: " + (patient != null ? patient.getName() : "None") +
                "\nPatient ID: " + patient.getPatientId() +
                "\nDoctor ID: " + doctor.getDoctorID() +
                "\nConsultation Time: " + consultationTime +
                "\nServices Used: " + servicesUsed +
                "\nDiagnosis: " + (diagnosis != null ? diagnosis.getName() : "None") +
                "\nNotes: " + notes +
                "\nPayment: " + payment +
                "\nFollow Up Needed: " + followUpNeeded +
                "\nFollow Up Date: " + followUpDate;
    }
}
