package control;

import entity.Patient;
import adt.CustomADT;

public class PatientMaintenance {
    private CustomADT<String, Patient> patientQueue;  // Using patient ID as key
    private CustomADT<String, Patient> patientRegistry;

    public PatientMaintenance() {
        this.patientQueue = new CustomADT<>();
        this.patientRegistry = new CustomADT<>();
    }

    // Patient Registration
    public boolean registerPatient(String patientId, String name) {
        if (patientRegistry.containsKey(patientId)) return false;
        patientRegistry.put(patientId, new Patient(patientId, name));
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
        return patientQueue.poll(); // Uses queue's FIFO operation
    }

    // Record Maintenance
    public void addMedicalRecord(String patientId, String recordId, String record) {
        Patient patient = patientRegistry.get(patientId);
        if (patient != null) {
            patient.addMedicalRecord(recordId, record);
        }
    }

    public String getMedicalRecord(String patientId, String recordId) {
        Patient patient = patientRegistry.get(patientId);
        if (patient != null) {
            return patient.getMedicalRecords().get(recordId);
        }
        return null;
    }

    public CustomADT<String, String> getAllMedicalRecords(String patientId) {
        Patient patient = patientRegistry.get(patientId);
        return patient != null ? patient.getMedicalRecords() : null;
    }

    // Utility methods
    public int getQueueSize() {
        return patientQueue.size();
    }

    public int getRegisteredPatientCount() {
        return patientRegistry.size();
    }
}