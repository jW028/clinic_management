package dao;

import entity.Patient;
import adt.CustomADT;

public class PatientInitializer {
    public static CustomADT<String, Patient> initializePatients() {
        CustomADT<String, Patient> patients = new CustomADT<>();
        patients.put("P001", new Patient("P001", "Alice", 19, "Female", "1234567890", "123 Main St"));
        patients.put("P002", new Patient("P002", "Bob", 40, "Male", "0987654321", "456 Elm St"));
        return patients;
    }

    public static void main(String[] args) {
        CustomADT<String, Patient> patients = initializePatients();
        // Display initialized patients
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            System.out.println("Patient ID: " + patient.getPatientId() + ", Name: " + patient.getName());
        }
    }
}