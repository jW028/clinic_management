package control;

import entity.Patient;
import adt.*;
import dao.PatientDAO;

public class PatientMaintenance {
    private CustomADT<String, Patient> patientQueue;
    private CustomADT<String, Patient> patientRegistry;
    private PatientDAO patientDAO;

    public PatientMaintenance() {
        this.patientQueue = new CustomADT<>();
        this.patientDAO = new PatientDAO();

        // Load existing patients
        CustomADTInterface<String, Patient> loadedPatients = patientDAO.retrieveFromFile();
        if (loadedPatients != null) {
            // This logic can be simplified
            this.patientRegistry = (CustomADT<String, Patient>) loadedPatients;
        } else {
            // This case is unlikely now since retrieveFromFile returns an empty list on error
            this.patientRegistry = new CustomADT<>();
        }
    }

    // Create (Updated with more details)
    public boolean registerPatient(String patientId, String name, int age, String gender,
                                   String contactNumber, String address) {
        if (patientRegistry.containsKey(patientId)) {
            return false;
        }
        Patient newPatient = new Patient(patientId, name, age, gender, contactNumber, address);
        patientRegistry.put(patientId, newPatient);
        saveChanges();
        return true;
    }

    // Read operations
    public Patient getPatientById(String patientId) {
        return patientRegistry.get(patientId);
    }

    public CustomADT<String, Patient> getAllPatients() {
        return patientRegistry;
    }

    public boolean updatePatient(String patientId, String name, int age, String gender,
                                 String contactNumber, String address) {
        Patient patient = patientRegistry.get(patientId);
        if (patient == null) {
            return false;
        }

        patient.setName(name);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setContactNumber(contactNumber);
        patient.setAddress(address);

        saveChanges();
        return true;
    }

    // Delete
    public boolean deletePatient(String patientId) {
        if (!patientRegistry.containsKey(patientId)) {
            return false;
        }
        patientRegistry.remove(patientId);
        saveChanges();
        return true;
    }

    // Queue Management
    public void enqueuePatient(String patientId) {
        Patient patient = patientRegistry.get(patientId);
        if (patient != null && !patientQueue.containsKey(patientId)) {
            patientQueue.put(patientId, patient);
        }
    }

    public Patient serveNextPatient() {
        return patientQueue.poll();
    }

    // Utility methods
    public int getQueueSize() {
        return patientQueue.size();
    }

    public int getRegisteredPatientCount() {
        return patientRegistry.size();
    }

    // Save current state
    public void saveChanges() {
        CustomADT<String, Patient> saveData = new CustomADT<>();
        for (int i = 0; i < patientRegistry.size(); i++) {
            Patient patient = patientRegistry.get(i);
            saveData.put(patient.getPatientId(), patient);
        }
        patientDAO.saveToFile(saveData);
    }
}