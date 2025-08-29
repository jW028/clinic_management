package entity;

import java.io.Serializable;

public class Patient implements Serializable {
    private String patientId;
    private String name;
    private int age;
    private String gender;
    private String contactNumber;
    private String address;
    private boolean isEmergency;

    public Patient(String patientId, String name, int age, String gender, String contactNumber, String address, boolean isEmergency) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.isEmergency = isEmergency;
    }

    // Getters
    public String getPatientId() { return patientId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address; }
    public boolean isEmergency() { return isEmergency; }

    // Setters for updatable fields
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setEmergency(boolean isEmergency) { this.isEmergency = isEmergency; }

    @Override
    public String toString() {
        return String.format("Patient ID: %s | Name: %s | Age: %d | Gender: %s | Contact: %s | Address: %s",
                patientId, name, age, gender, contactNumber, address);
    }
}