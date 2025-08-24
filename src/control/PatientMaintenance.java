package control;

import entity.*;
import adt.*;
import dao.PatientDAO;
import dao.VisitHistoryDAO;
import utility.IDGenerator;
import java.time.LocalDateTime;

public class PatientMaintenance {
    private final CustomADT<String, Patient> normalQueue;
    private final CustomADT<String, Patient> emergencyQueue;
    private final CustomADT<String, Patient> patientRegistry;
    private final CustomADT<String, Patient> waitlist;
    private final CustomADT<String, VisitHistory> visitHistoryMap;
    private final PatientDAO patientDAO;
    private final VisitHistoryDAO visitHistoryDAO;
    private static final int MAX_QUEUE_SIZE = 20;
    private static final int MAX_WAITLIST_SIZE = 30;

    public PatientMaintenance() {
        this.normalQueue = new CustomADT<>();
        this.emergencyQueue = new CustomADT<>();
        this.patientDAO = new PatientDAO();
        this.visitHistoryDAO = new VisitHistoryDAO();
        this.waitlist = new CustomADT<>();
        // Load existing patients with proper casting
        CustomADTInterface<String, Patient> loadedPatients = patientDAO.retrieveFromFile();
        if (loadedPatients instanceof CustomADT) {
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

        emergencyQueue.remove(patientId);
        normalQueue.remove(patientId);
        waitlist.remove(patientId);

        patientRegistry.remove(patientId);
        saveChanges();
        return true;
    }

    // Queue Management using CustomADT queue operations
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
        // Use CustomADT's poll() method for proper queue behavior (FIFO)
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

    // Peek at next patient without removing
    public Patient peekNextPatient() {
        Patient nextPatient = emergencyQueue.peek();
        if (nextPatient == null) {
            nextPatient = normalQueue.peek();
        }
        return nextPatient;
    }

    // Waitlist Management using CustomADT
    public boolean addToWaitlist(String patientId) {
        Patient patient = patientRegistry.get(patientId);
        if (patient == null || isPatientInWaitlist(patientId)) {
            return false;
        }

        if (waitlist.size() >= MAX_WAITLIST_SIZE) {
            return false; // Waitlist is full
        }

        waitlist.offer(patientId, patient); // Use offer for proper queue behavior
        return true;
    }

    private void processWaitlistToQueue() {
        if (waitlist.isEmpty() || getTotalQueueSize() >= MAX_QUEUE_SIZE) {
            return;
        }

        // Optimized waitlist processing using iterator
        String emergencyPatientId = null;
        String normalPatientId = null;

        // Find first emergency and first normal patient
        for (int i = 0; i < waitlist.size(); i++) {
            Patient patient = waitlist.get(i);
            if (patient.isEmergency() && emergencyPatientId == null) {
                emergencyPatientId = patient.getPatientId();
            } else if (!patient.isEmergency() && normalPatientId == null) {
                normalPatientId = patient.getPatientId();
            }

            // Break early if we found both types
            if (emergencyPatientId != null && normalPatientId != null) {
                break;
            }
        }

        // Priority: Emergency patients first, then normal patients
        String patientIdToMove = emergencyPatientId != null ? emergencyPatientId : normalPatientId;

        if (patientIdToMove != null) {
            Patient patient = waitlist.remove(patientIdToMove);
            if (patient != null) {
                if (patient.isEmergency()) {
                    emergencyQueue.offer(patientIdToMove, patient);
                } else {
                    normalQueue.offer(patientIdToMove, patient);
                }
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
                "COMPLETED"
        );

        visitHistoryMap.put(visitId, initialVisit);
        visitHistoryDAO.saveToFile(visitHistoryMap);
    }

    /**
     * Add visit history record
     */
    public boolean addVisitHistory(String patientId, String visitReason, String status) {
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
                status != null ? status : "COMPLETED"
        );

        visitHistoryMap.put(visitId, visitHistory);
        visitHistoryDAO.saveToFile(visitHistoryMap);
        return true;
    }


    /**
     * Get patient visit history including treatments
     */
    public CustomADT<String, VisitHistory> getPatientVisitHistory(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return new CustomADT<>();
        }

        // Get existing visit histories with proper filtering
        CustomADT<String, VisitHistory> patientVisits = new CustomADT<>();
        for (VisitHistory visit : visitHistoryMap) {
            if (visit.getPatient() != null &&
                    visit.getPatient().getPatientId().equals(patientId)) {
                patientVisits.put(visit.getVisitId(), visit);
            }
        }


        return patientVisits;
    }

    /**
     * Get specific visit history with validation
     */
    public VisitHistory getVisitHistory(String visitId) {
        if (visitId == null || visitId.trim().isEmpty()) {
            return null;
        }
        return visitHistoryMap.get(visitId);
    }

    /**
     * Get all visit histories (defensive copy)
     */
    public CustomADT<String, VisitHistory> getAllVisitHistories() {
        CustomADT<String, VisitHistory> copy = new CustomADT<>();

        // Use CustomADT indexed access to copy all entries
        for (int i = 0; i < visitHistoryMap.size(); i++) {
            VisitHistory visit = visitHistoryMap.get(i);
            if (visit != null) {
                copy.put(visit.getVisitId(), visit);
            }
        }

        return copy;
    }

    /**
     * Update visit history with comprehensive validation
     */
    public boolean updateVisitHistory(String visitId, String visitReason, String status) {
        if (visitId == null || visitId.trim().isEmpty()) {
            return false;
        }

        VisitHistory visitHistory = visitHistoryMap.get(visitId);
        if (visitHistory == null) {
            return false;
        }

        try {
            boolean updated = false;

            if (visitReason != null && !visitReason.trim().isEmpty()) {
                visitHistory.setVisitReason(visitReason.trim());
                updated = true;
            }

            if (status != null && !status.trim().isEmpty()) {
                visitHistory.setStatus(status.trim().toUpperCase());
                updated = true;
            }

            if (updated) {
                visitHistoryDAO.saveToFile(visitHistoryMap);
                return true;
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error updating visit history: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove visit history with validation
     */
    public boolean removeVisitHistory(String visitId) {
        if (visitId == null || visitId.trim().isEmpty()) {
            return false;
        }

        try {
            VisitHistory removed = visitHistoryMap.remove(visitId);
            if (removed != null) {
                visitHistoryDAO.saveToFile(visitHistoryMap);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error removing visit history: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get visit history count for patient using CustomADT iteration
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

    // Additional utility methods leveraging CustomADT capabilities

    /**
     * Get all patients as array
     */
    public Patient[] getAllPatientsArray() {
        Patient[] patientsArray = new Patient[patientRegistry.size()];
        return patientRegistry.toArray(patientsArray);
    }

    /**
     * Clear all queues and waitlist
     */
    public void clearAllQueues() {
        normalQueue.clear();
        emergencyQueue.clear();
        waitlist.clear();
    }

    /**
     * Get queue contents for display
     */
    public CustomADT<String, Patient> getEmergencyQueue() {
        return emergencyQueue;
    }

    public CustomADT<String, Patient> getNormalQueue() {
        return normalQueue;
    }

    // Existing utility methods
    public boolean removeFromWaitlist(String patientId) {
        return waitlist.remove(patientId) != null;
    }

    public boolean isPatientInWaitlist(String patientId) {
        return waitlist.containsKey(patientId);
    }

    public boolean isPatientInQueue(String patientId) {
        return emergencyQueue.containsKey(patientId) || normalQueue.containsKey(patientId);
    }

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

    public void saveChanges() {
        patientDAO.saveToFile(patientRegistry);
    }
}