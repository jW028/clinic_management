package control;

import entity.Patient;
import adt.CustomADT;

public class PatientMaintenance {
    private CustomADT patientQueue;  // For queue management
    private CustomADT patientRegistry;  // For registration

    public PatientMaintenance() {
        patientQueue = new CustomADT();
        patientRegistry = new CustomADT();
    }

    // Patient Registration
    public boolean registerPatient(String patientId, String name) {
        if (findPatient(patientId) != null) return false;
        patientRegistry.add(new Patient(patientId, name));
        return true;
    }

    // Queue Management
    public void enqueuePatient(String patientId) {
        Patient patient = findPatient(patientId);
        if (patient != null && !patientQueue.contains(patient)) {
            patientQueue.enqueue(patient);
        }
    }

    public Patient serveNextPatient() {
        return (Patient)patientQueue.dequeue();
    }

    // Record Maintenance
    public void addMedicalRecord(String patientId, String record) {
        Patient patient = findPatient(patientId);
        if (patient != null) {
            patient.addMedicalRecord(record);
        }
    }

    public CustomADT getMedicalRecords(String patientId) {
        Patient patient = findPatient(patientId);
        return patient != null ? patient.getMedicalRecords() : null;
    }

    // Helper method
    private Patient findPatient(String patientId) {
        for (int i = 0; i < patientRegistry.size(); i++) {
            Patient p = (Patient)patientRegistry.get(i);
            if (p.getPatientId().equals(patientId)) return p;
        }
        return null;
    }
}