package entity;

import adt.CustomADT;
import java.io.Serializable;

public class Patient implements Serializable {
    private String patientId;
    private String name;
    private int age;
    private String gender;
    private String contactNumber;
    private String address;
    private CustomADT<String, MedicalRecord> medicalRecords;

    public Patient(String patientId, String name, int age, String gender, String contactNumber, String address) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.medicalRecords = new CustomADT<>();
    }

    // Medical Record Management
    public void addMedicalRecord(String recordId, MedicalRecord record) {
        medicalRecords.put(recordId, record);
    }

    public void updateMedicalRecord(String recordId, MedicalRecord record) {
        if (medicalRecords.containsKey(recordId)) {
            medicalRecords.put(recordId, record);
        }
    }

    public MedicalRecord getMedicalRecord(String recordId) {
        return medicalRecords.get(recordId);
    }

    public boolean deleteMedicalRecord(String recordId) {
        return medicalRecords.remove(recordId) != null;
    }

    public CustomADT<String, MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    // Getters
    public String getPatientId() { return patientId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address; }

    // Setters for updatable fields
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return String.format("Patient ID: %s | Name: %s | Age: %d | Gender: %s | Contact: %s | Address: %s",
                patientId, name, age, gender, contactNumber, address);
    }

    public String getDetailedView() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Patient Details ===\n")
                .append("ID: ").append(patientId).append("\n")
                .append("Name: ").append(name).append("\n")
                .append("Age: ").append(age).append("\n")
                .append("Gender: ").append(gender).append("\n")
                .append("Contact: ").append(contactNumber).append("\n")
                .append("Address: ").append(address).append("\n")
                .append("\nMedical Records: ").append(medicalRecords.size());
        return sb.toString();
    }
}