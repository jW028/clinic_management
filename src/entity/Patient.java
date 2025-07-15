package entity;

import adt.CustomADT;

public class Patient {
    private String patientId;
    private String name;
    private CustomADT medicalRecords;

    public Patient(String patientId, String name) {
        this.patientId = patientId;
        this.name = name;
        this.medicalRecords = new CustomADT();
    }

    public void addMedicalRecord(String record) {
        medicalRecords.add(record);
    }

    public CustomADT getMedicalRecords() {
        return medicalRecords;
    }

    // Getters
    public String getPatientId() { return patientId; }
    public String getName() { return name; }
}