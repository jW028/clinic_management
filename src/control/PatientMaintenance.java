package control;

import entity.*;
import adt.*;
import dao.PatientDAO;

public class PatientMaintenance {
    private CustomADT<String, Patient> normalQueue;
    private CustomADT<String, Patient> emergencyQueue;
    private CustomADT<String, Patient> patientRegistry;
    private CustomADT<String, Patient> waitlist;
    private PatientDAO patientDAO;
    private static final int MAX_QUEUE_SIZE = 20;
    private static final int MAX_WAITLIST_SIZE = 30;

    public PatientMaintenance() {
        this.normalQueue = new CustomADT<>();
        this.emergencyQueue = new CustomADT<>();
        this.patientDAO = new PatientDAO();
        this.waitlist = new CustomADT<>();

        // Load existing patients
        CustomADTInterface<String, Patient> loadedPatients = patientDAO.retrieveFromFile();
        if (loadedPatients != null) {
            this.patientRegistry = (CustomADT<String, Patient>) loadedPatients;
        } else {
            this.patientRegistry = new CustomADT<>();
        }
    }

    public boolean registerPatient(String patientId, String name, int age, String gender,
                                   String contactNumber, String address, boolean isEmergency) {
        if (patientRegistry.containsKey(patientId)) {
            return false;
        }
        Patient newPatient = new Patient(patientId, name, age, gender, contactNumber, address, isEmergency);
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

    public CustomADT<String, Patient> getWaitlist() {
        return waitlist;
    }

    public boolean updatePatient(String patientId, String name, int age, String gender,
                                 String contactNumber, String address, boolean isEmergency) {
        Patient patient = patientRegistry.get(patientId);
        if (patient == null) {
            return false;
        }

        patient.setName(name);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setContactNumber(contactNumber);
        patient.setAddress(address);
        patient.setEmergency(isEmergency);

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
        if (patient == null) {
            return;
        }

        // Check if patient is already in queue or waitlist
        if (isPatientInQueue(patientId) || isPatientInWaitlist(patientId)) {
            return;
        }

        // Try to add to appropriate queue
        if (getTotalQueueSize() < MAX_QUEUE_SIZE) {
            if (patient.isEmergency()) {
                emergencyQueue.put(patientId, patient);
            } else {
                normalQueue.put(patientId, patient);
            }
        } else {
            // Queue is full, add to waitlist
            addToWaitlist(patientId);
        }
    }

    public Patient serveNextPatient() {
        Patient nextPatient = emergencyQueue.poll();
        if (nextPatient == null) {
            nextPatient = normalQueue.poll();
        }

        // If a patient was served, try to move someone from waitlist to queue
        if (nextPatient != null) {
            processWaitlistToQueue();
        }

        return nextPatient;
    }

    // Waitlist Management
    public boolean addToWaitlist(String patientId) {
        Patient patient = patientRegistry.get(patientId);
        if (patient == null || isPatientInWaitlist(patientId)) {
            return false;
        }

        if (waitlist.size() >= MAX_WAITLIST_SIZE) {
            return false; // Waitlist is full
        }

        waitlist.put(patientId, patient);
        return true;
    }

    /**
     * Moves the next patient from waitlist to appropriate queue if space is available
     */
    private void processWaitlistToQueue() {
        if (waitlist.isEmpty() || getTotalQueueSize() >= MAX_QUEUE_SIZE) {
            return;
        }

        // Priority: Emergency patients from waitlist first
        Patient nextFromWaitlist = null;
        String patientIdToMove = null;

        // Look for emergency patients in waitlist first
        for (int i = 0; i < waitlist.size(); i++) {
            Patient patient = waitlist.get(i);
            if (patient.isEmergency()) {
                nextFromWaitlist = patient;
                patientIdToMove = patient.getPatientId();
                break;
            }
        }

        // If no emergency patient, take the first normal patient
        if (nextFromWaitlist == null && !waitlist.isEmpty()) {
            nextFromWaitlist = waitlist.get(0);
            patientIdToMove = nextFromWaitlist.getPatientId();
        }

        // Move patient from waitlist to queue
        if (nextFromWaitlist != null && patientIdToMove != null) {
            waitlist.remove(patientIdToMove);
            if (nextFromWaitlist.isEmergency()) {
                emergencyQueue.put(patientIdToMove, nextFromWaitlist);
            } else {
                normalQueue.put(patientIdToMove, nextFromWaitlist);
            }
        }
    }

    /**
     * Manually promotes a patient from waitlist to queue
     */
    public boolean promoteFromWaitlist(String patientId) {
        if (!isPatientInWaitlist(patientId) || getTotalQueueSize() >= MAX_QUEUE_SIZE) {
            return false;
        }

        Patient patient = waitlist.remove(patientId);
        if (patient != null) {
            if (patient.isEmergency()) {
                emergencyQueue.put(patientId, patient);
            } else {
                normalQueue.put(patientId, patient);
            }
            return true;
        }
        return false;
    }

    public boolean removeFromWaitlist(String patientId) {
        return waitlist.remove(patientId) != null;
    }

    public boolean isPatientInWaitlist(String patientId) {
        return waitlist.containsKey(patientId);
    }

    public boolean isPatientInQueue(String patientId) {
        return emergencyQueue.containsKey(patientId) || normalQueue.containsKey(patientId);
    }

    // Utility methods
    public int getEmergencyQueueSize() {
        return emergencyQueue.size();
    }

    public int getNormalQueueSize() {
        return normalQueue.size();
    }

    public int getTotalQueueSize() {
        return emergencyQueue.size() + normalQueue.size();
    }

    public int getWaitlistSize()  {
        return waitlist.size();
    }
    public int getRegisteredPatientCount() {
        return patientRegistry.size();
    }

    /**
     * Checks if the patient queue is full
     * @return true if the queue has reached maximum capacity, false otherwise
     */
    public boolean isQueueFull() {
        return getTotalQueueSize() >= MAX_QUEUE_SIZE;
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