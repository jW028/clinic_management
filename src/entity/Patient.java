package entity;

import adt.CustomADT;

public class Patient {
    private String patientId;
    private String name;
    private CustomADT<String, String> medicalRecords; // Using String keys for record IDs

    public Patient(String patientId, String name) {
        this.patientId = patientId;
        this.name = name;
        this.medicalRecords = new CustomADT<>();
    }

    public void addMedicalRecord(String recordId, String record) {
        medicalRecords.put(recordId, record);
    }

    public CustomADT<String, String> getMedicalRecords() {
        return medicalRecords;
    }

    // Getters
    public String getPatientId() { return patientId; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "Patient ID: " + patientId + " | Name: " + name;
    }
}