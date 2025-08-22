package control;

import entity.*;
import adt.*;
import dao.PatientDAO;
import dao.VisitHistoryDAO;
import utility.IDGenerator;
import java.time.LocalDateTime;

public class PatientMaintenance {
    private CustomADT<String, Patient> normalQueue;
    private CustomADT<String, Patient> emergencyQueue;
    private CustomADT<String, Patient> patientRegistry;
    private CustomADT<String, Patient> waitlist;
    private CustomADT<String, VisitHistory> visitHistoryMap;


    private PatientDAO patientDAO;
    private VisitHistoryDAO visitHistoryDAO;

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

        // Initialize visit history
        CustomADTInterface<String, VisitHistory> loadedVisitHistory = visitHistoryDAO.retrieveFromFile();
        if (loadedVisitHistory != null) {
            this.visitHistoryMap = (CustomADT<String, VisitHistory>) loadedVisitHistory;
        } else {
            this.visitHistoryMap = new CustomADT<>();
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

        String visitReason = isEmergency ? "Emergency Registration" : "Regular Registration";
        createInitialVisitHistory(patientId, visitReason);
        return true;
    }

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

    public boolean deletePatient(String patientId) {
        if (!patientRegistry.containsKey(patientId)) {
            return false;
        }
        patientRegistry.remove(patientId);
        saveChanges();
        return true;
    }

    // Queue Management - Fixed to use proper queue operations
    public void enqueuePatient(String patientId) {
        Patient patient = patientRegistry.get(patientId);
        if (patient == null) {
            return;
        }

        // Check if patient is already in queue or waitlist
        if (isPatientInQueue(patientId) || isPatientInWaitlist(patientId)) {
            return;
        }

        if (getTotalQueueSize() < MAX_QUEUE_SIZE) {
            if (patient.isEmergency()) {
                emergencyQueue.offer(patientId, patient);
            } else {
                normalQueue.offer(patientId, patient);
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
                emergencyQueue.offer(patientIdToMove, nextFromWaitlist);
            } else {
                normalQueue.offer(patientIdToMove, nextFromWaitlist);
            }
        }
    }

    public boolean promoteFromWaitlist(String patientId) {
        if (!isPatientInWaitlist(patientId) || getTotalQueueSize() >= MAX_QUEUE_SIZE) {
            return false;
        }

        Patient patient = waitlist.remove(patientId);
        if (patient != null) {
            if (patient.isEmergency()) {
                emergencyQueue.offer(patientId, patient);
            } else {
                normalQueue.offer(patientId, patient);
            }
            return true;
        }
        return false;
    }

    /**
     * Create initial visit history when registering patient
     */
    private void createInitialVisitHistory(String patientId, String visitReason) {
        Patient patient = patientRegistry.get(patientId);
        if (patient == null) return;

        String visitId = IDGenerator.generateVisitID();
        VisitHistory initialVisit = new VisitHistory(
                visitId,
                patient,
                LocalDateTime.now(),
                visitReason != null ? visitReason : "Initial Registration",
                null, // No treatment initially
                null, // No consultation initially
                0.0,  // No cost initially
                "REGISTERED"
        );

        visitHistoryMap.put(visitId, initialVisit);
        visitHistoryDAO.saveToFile(visitHistoryMap);
    }

    /**
     * Add visit history record
     */
    public boolean addVisitHistory(String patientId, String visitReason, Treatment treatment,
                                   Consultation consultation, double visitCost, String status) {
        Patient patient = patientRegistry.get(patientId);
        if (patient == null) {
            return false;
        }

        String visitId = IDGenerator.generateVisitID();
        VisitHistory visitHistory = new VisitHistory(
                visitId,
                patient,
                LocalDateTime.now(),
                visitReason,
                treatment,
                consultation,
                visitCost,
                status
        );

        visitHistoryMap.put(visitId, visitHistory);
        visitHistoryDAO.saveToFile(visitHistoryMap);
        return true;
    }

    /**
     * Get visit history for a patient
     */
    public CustomADT<String, VisitHistory> getPatientVisitHistory(String patientId) {
        CustomADT<String, VisitHistory> patientVisits = new CustomADT<>();

        for (VisitHistory visit : visitHistoryMap) {
            if (visit.getPatient().getPatientId().equals(patientId)) {
                patientVisits.put(visit.getVisitId(), visit);
            }
        }

        return patientVisits;
    }

    /**
     * Get specific visit history
     */
    public VisitHistory getVisitHistory(String visitId) {
        return visitHistoryMap.get(visitId);
    }

    /**
     * Get all visit histories
     */
    public CustomADT<String, VisitHistory> getAllVisitHistories() {
        return visitHistoryMap;
    }

    /**
     * Update visit history
     */
    public boolean updateVisitHistory(String visitId, String visitReason, double visitCost, String status) {
        VisitHistory visitHistory = visitHistoryMap.get(visitId);
        if (visitHistory == null) {
            return false;
        }

        if (visitReason != null) visitHistory.setVisitReason(visitReason);
        if (visitCost >= 0) visitHistory.setVisitCost(visitCost);
        if (status != null) visitHistory.setStatus(status);

        visitHistoryDAO.saveToFile(visitHistoryMap);
        return true;
    }

    /**
     * Remove visit history
     */
    public boolean removeVisitHistory(String visitId) {
        VisitHistory removed = visitHistoryMap.remove(visitId);
        if (removed != null) {
            visitHistoryDAO.saveToFile(visitHistoryMap);
            return true;
        }
        return false;
    }

    /**
     * Get visit history count for patient
     */
    public int getPatientVisitCount(String patientId) {
        int count = 0;
        for (VisitHistory visit : visitHistoryMap) {
            if (visit.getPatient().getPatientId().equals(patientId)) {
                count++;
            }
        }
        return count;
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

    public int getWaitlistSize() {
        return waitlist.size();
    }

    public int getRegisteredPatientCount() {
        return patientRegistry.size();
    }

    public boolean isQueueFull() {
        return getTotalQueueSize() >= MAX_QUEUE_SIZE;
    }

    // Save current state
    public void saveChanges() {
        patientDAO.saveToFile(patientRegistry);
    }
}