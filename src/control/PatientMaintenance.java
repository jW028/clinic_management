/**
 * @author Samuel Chew Chun Hong
 */

package control;

import adt.*;
import dao.*;
import entity.*;
import java.time.LocalDateTime;
import utility.IDGenerator;

public class PatientMaintenance {
    private final OrderedMap<String, Patient> normalQueue;
    private final OrderedMap<String, Patient> emergencyQueue;
    private OrderedMap<String, Patient> patientRegistry;
    private final OrderedMap<String, VisitHistory> visitHistoryMap;
    private final PatientDAO patientDAO;
    private final VisitHistoryDAO visitHistoryDAO;
    private static final int MAX_QUEUE_SIZE = 20;
    private OrderedMap<String, Consultation> consultationMap;
    private OrderedMap<String, Treatment> treatmentMap;
    private final ConsultationDAO consultationDAO;
    private final TreatmentDAO treatmentDAO;
    private ConsultationMaintenance consultationMaintenance;
    private TreatmentMaintenance treatmentMaintenance;
    private final PatientQueueDAO patientEmergencyQueueDAO = new PatientQueueDAO(1);
    private final PatientQueueDAO patientNormalQueueDAO = new PatientQueueDAO(2);

    private OrderedMap<String, OrderedMap<String, Patient>> nameIndex = new OrderedMap<>();
    private OrderedMap<String, OrderedMap<String, Patient>> genderIndex = new OrderedMap<>();
    private OrderedMap<String, OrderedMap<String, Patient>> ageIndex = new OrderedMap<>();

    private static PatientMaintenance instance;

    private static class UndoAction {
        String type;
        Patient patientBefore;
        Patient patientAfter;
        String field;
        String prevValue;
        String newValue;
    }

    public OrderedMap<String, UndoAction> undoStack = new OrderedMap<>();

    private void logUndoAction(UndoAction action) {
        String key = "action" + (undoStack.size() + 1);
        undoStack.put(key, action);
    }

    private void logRegisterPatient(String patientId) {
        Patient p = patientRegistry.get(patientId);
        if (p == null) return;
        UndoAction action = new UndoAction();
        action.type = "REGISTER";
        action.patientAfter = copyPatient(p);
        logUndoAction(action);
    }

    private void logUpdatePatient(String patientId, String field, String prevValue, String newValue) {
        Patient p = patientRegistry.get(patientId);
        if (p == null) return;
        UndoAction action = new UndoAction();
        action.type = "UPDATE";
        action.patientBefore = copyPatient(p); // snapshot before change
        action.field = field;
        action.prevValue = prevValue;
        action.newValue = newValue;
        logUndoAction(action);
    }

    private void logDeletePatient(String patientId) {
        Patient p = patientRegistry.get(patientId);
        if (p == null) return;
        UndoAction action = new UndoAction();
        action.type = "DELETE";
        action.patientBefore = copyPatient(p);
        logUndoAction(action);
    }

    private Patient copyPatient(Patient original) {
        if (original == null) return null;
        return new Patient(
                original.getPatientId(),
                original.getName(),
                original.getAge(),
                original.getGender(),
                original.getContactNumber(),
                original.getAddress(),
                original.isEmergency()
        );
    }

    public String undoLastAction() {
        if (undoStack.size() == 0) return "No action to undo.";
        String lastKey = "action" + undoStack.size();
        UndoAction action = undoStack.get(lastKey);
        if (action == null) return "No action to undo.";
        undoStack.remove(lastKey);

        switch (action.type) {
            case "REGISTER":
                if (action.patientAfter != null) {
                    patientRegistry.remove(action.patientAfter.getPatientId());
                    // remove from indices
                    removeFromNameIndex(action.patientAfter);
                    removeFromGenderIndex(action.patientAfter);
                    removeFromAgeIndex(action.patientAfter);
                    saveChanges();
                    return "Undo Register: Patient removed.";
                }
                return "Undo failed: missing data.";
            case "DELETE":
                if (action.patientBefore != null) {
                    patientRegistry.put(action.patientBefore.getPatientId(), copyPatient(action.patientBefore));
                    // re-index
                    addToNameIndex(action.patientBefore);
                    addToGenderIndex(action.patientBefore);
                    addToAgeIndex(action.patientBefore);
                    saveChanges();
                    return "Undo Delete: Patient restored.";
                }
                return "Undo failed: missing data.";
            case "UPDATE":
                if (action.patientBefore != null) {
                    // replace current with previous snapshot
                    patientRegistry.put(action.patientBefore.getPatientId(), copyPatient(action.patientBefore));
                    // rebuild indexes for that patient
                    removeFromNameIndex(action.patientAfter);
                    removeFromGenderIndex(action.patientAfter);
                    removeFromAgeIndex(action.patientAfter);
                    addToNameIndex(action.patientBefore);
                    addToGenderIndex(action.patientBefore);
                    addToAgeIndex(action.patientBefore);
                    saveChanges();
                    return "Undo Update: Patient reverted.";
                }
                return "Undo failed: missing data.";
            default:
                return "Unknown action type.";
        }
    }

    public PatientMaintenance() {
        this.normalQueue = patientNormalQueueDAO.retrieveFromFile();
        this.emergencyQueue = patientEmergencyQueueDAO.retrieveFromFile();
        this.patientDAO = new PatientDAO();
        this.visitHistoryDAO = new VisitHistoryDAO();
        this.consultationDAO = new ConsultationDAO();
        this.treatmentDAO = new TreatmentDAO();
        this.consultationMaintenance = new ConsultationMaintenance();
        this.treatmentMaintenance = new TreatmentMaintenance();

        OrderedMap<String, Patient> loadedPatients = patientDAO.retrieveFromFile();
        if (loadedPatients != null) {
            this.patientRegistry = (OrderedMap<String, Patient>) loadedPatients;
        } else {
            this.patientRegistry = new OrderedMap<>();
        }

        OrderedMap<String, VisitHistory> loadedVisitHistory = visitHistoryDAO.retrieveFromFile();
        if (loadedVisitHistory != null) {
            this.visitHistoryMap = (OrderedMap<String, VisitHistory>) loadedVisitHistory;
        } else {
            this.visitHistoryMap = new OrderedMap<>();
        }

        OrderedMap<String, Consultation> loadedConsultations = consultationDAO.retrieveFromFile();
        if (loadedConsultations != null) {
            this.consultationMap = (OrderedMap<String, Consultation>) loadedConsultations;
        } else {
            this.consultationMap = new OrderedMap<>();
        }

        OrderedMap<String, Treatment> loadedTreatments = treatmentDAO.retrieveFromFile();
        if (loadedTreatments != null) {
            this.treatmentMap = (OrderedMap<String, Treatment>) loadedTreatments;
        } else {
            this.treatmentMap = new OrderedMap<>();
        }

        IDGenerator.loadCounter("counter.dat");
        rebuildNameIndex();
        rebuildGenderIndex();
        rebuildAgeIndex();
    }

    public static PatientMaintenance getInstance() {
        if (instance == null) {
            instance = new PatientMaintenance();
        }
        return instance;
    }

    public OrderedMap<String, Consultation> getConsultationsByPatient(String patientId) {
        OrderedMap<String, Consultation> result = new OrderedMap<>();
        if (consultationMaintenance == null) return result;
        Consultation[] arr = consultationMaintenance.getAllConsultations();
        for (Consultation c : arr) {
            if (c != null && c.getPatient() != null &&
                    patientId.equals(c.getPatient().getPatientId())) {
                result.put(c.getConsultationId(), c);
            }
        }
        return result;
    }

    public OrderedMap<String, Treatment> getTreatmentsForPatient(String patientId) {
        OrderedMap<String, Treatment> result = new OrderedMap<>();
        if (patientId == null) return result;
        Patient p = getPatientById(patientId);
        if (p == null) return result;

        OrderedMap<String, Treatment> fetched = treatmentMaintenance.getTreatmentsByPatient(p);
        for (Treatment t : fetched) {
            if (t != null && t.getTreatmentID() != null) {
                result.put(t.getTreatmentID(), t);
            }
        }
        return result;
    }
    // ===============================
    // PATIENT MANAGEMENT SECTION
    // ===============================

    /**
     * Register a new patient in the system
     */
    public boolean registerPatient(String patientId, String name, int age, String gender,
                                   String contactNumber, String address, boolean isEmergency) {
        if (patientRegistry.containsKey(patientId)) {
            return false;
        }
        Patient newPatient = new Patient(patientId, name, age, gender, contactNumber, address, isEmergency);
        patientRegistry.put(patientId, newPatient);

        // maintain indices
        addToNameIndex(newPatient);
        addToGenderIndex(newPatient);
        addToAgeIndex(newPatient);

        saveChanges();
        IDGenerator.saveCounters("counter.dat");
        logRegisterPatient(patientId);
        return true;
    }

    /**
     * Get patient by ID
     */
    public Patient getPatientById(String patientId) {
        return patientRegistry.get(patientId);
    }

    /**
     * Update patient information
     */
    public boolean updatePatient(String patientId, String name, int age, String gender,
                                 String contactNumber, String address, boolean isEmergency) {
        Patient patient = patientRegistry.get(patientId);
        if (patient == null) {
            return false;
        }
        logUpdatePatient(patientId, "ALL_FIELDS",
                patient.getName() + "|" + patient.getAge() + "|" + patient.getGender() + "|" +
                        patient.getContactNumber() + "|" + patient.getAddress() + "|" + patient.isEmergency(),
                name + "|" + age + "|" + gender + "|" + contactNumber + "|" + address + "|" + isEmergency
        );

        removeFromNameIndex(patient);
        removeFromGenderIndex(patient);
        removeFromAgeIndex(patient);

        patient.setName(name);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setContactNumber(contactNumber);
        patient.setAddress(address);
        patient.setEmergency(isEmergency);

        addToNameIndex(patient);
        addToGenderIndex(patient);
        addToAgeIndex(patient);

        saveChanges();
        return true;
    }

    /**
     * Delete patient from system
     */
    public boolean deletePatient(String patientId) {
        if (!patientRegistry.containsKey(patientId)) {
            return false;
        }
        logDeletePatient(patientId);

        Patient removedPatient = patientRegistry.get(patientId);
        removeFromNameIndex(removedPatient);
        removeFromGenderIndex(removedPatient);
        removeFromAgeIndex(removedPatient);

        emergencyQueue.remove(patientId);
        normalQueue.remove(patientId);
        patientRegistry.remove(patientId);
        saveChanges();
        return true;
    }

    /*public OrderedMap<String, Patient> searchPatients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new OrderedMap<>();
        }
        String term = searchTerm.trim().toLowerCase();
        return patientRegistry.filter(
                null,
                (p, ref) -> {
                    if (p == null) return -1;
                    return (p.getPatientId().toLowerCase().contains(term) ||
                            p.getName().toLowerCase().contains(term)) ? 0 : -1;
                }
        );
    }*/


    // Index Maintenance Methods
    private void rebuildNameIndex() {
        nameIndex.clear();
        for (Patient patient : patientRegistry) {
            if (patient != null) addToNameIndex(patient);
        }
    }

    private void addToNameIndex(Patient patient) {
        if (patient == null || patient.getName() == null) return;
        String key = patient.getName().toLowerCase();
        OrderedMap<String, Patient> bucket = nameIndex.get(key);
        if (bucket == null) {
            bucket = new OrderedMap<>();
            nameIndex.put(key, bucket);
        }
        bucket.put(patient.getPatientId(), patient);
    }

    private void removeFromNameIndex(Patient patient) {
        if (patient == null || patient.getName() == null) return;
        String key = patient.getName().toLowerCase();
        OrderedMap<String, Patient> bucket = nameIndex.get(key);
        if (bucket != null) {
            bucket.remove(patient.getPatientId());
            if (bucket.size() == 0) {
                nameIndex.remove(key);
            }
        }
    }

    private void rebuildGenderIndex() {
        genderIndex.clear();
        for (Patient patient : patientRegistry) {
            if (patient != null) {
                addToGenderIndex(patient);
            }
        }
    }

    private void addToGenderIndex(Patient patient) {
        if (patient == null || patient.getGender() == null) return;
        String key = patient.getGender().toLowerCase();
        OrderedMap<String, Patient> bucket = genderIndex.get(key);
        if (bucket == null) {
            bucket = new OrderedMap<>();
            genderIndex.put(key, bucket);
        }
        bucket.put(patient.getPatientId(), patient);
    }

    private void removeFromGenderIndex(Patient patient) {
        if (patient == null || patient.getGender() == null) return;
        String key = patient.getGender().toLowerCase();
        OrderedMap<String, Patient> bucket = genderIndex.get(key);
        if (bucket != null) {
            bucket.remove(patient.getPatientId());
            if (bucket.isEmpty()) {
                genderIndex.remove(key);
            }
        }
    }

    private void rebuildAgeIndex() {
        ageIndex.clear();
        for (Patient patient : patientRegistry) {
            if (patient != null) {
                addToAgeIndex(patient);
            }
        }
    }

    private void addToAgeIndex(Patient patient) {
        if (patient == null) return;
        String key = Integer.toString(patient.getAge());
        OrderedMap<String, Patient> bucket = ageIndex.get(key);
        if (bucket == null) {
            bucket = new OrderedMap<>();
            ageIndex.put(key, bucket);
        }
        bucket.put(patient.getPatientId(), patient);
    }

    private void removeFromAgeIndex(Patient patient) {
        if (patient == null) return;
        String key = Integer.toString(patient.getAge());
        OrderedMap<String, Patient> bucket = ageIndex.get(key);
        if (bucket != null) {
            bucket.remove(patient.getPatientId());
            if (bucket.isEmpty()) {
                ageIndex.remove(key);
            }
        }
    }

    public OrderedMap<String, Patient> searchPatients(String searchTerm) {
        OrderedMap<String, Patient> result = new OrderedMap<>();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return result;
        }
        String term = searchTerm.trim().toLowerCase();

        // 1. O(1) patient ID lookup
        Patient byId = patientRegistry.get(searchTerm);
        if (byId != null) {
            result.put(byId.getPatientId(), byId);
            return result;
        }

        // 2. O(1) exact name lookup
        OrderedMap<String, Patient> nameBucket = nameIndex.get(term);
        if (nameBucket != null) {
            for (Patient p : nameBucket) {
                if (p != null) result.put(p.getPatientId(), p);
            }
            return result;
        }

        // 3. O(1) gender lookup (supports male/female shortcuts)
        String gKey = null;
        if (term.equals("m") || term.equals("male")) gKey = "male";
        else if (term.equals("f") || term.equals("female")) gKey = "female";
        if (gKey != null) {
            OrderedMap<String, Patient> gBucket = genderIndex.get(gKey);
            if (gBucket != null) {
                for (Patient p : gBucket) {
                    if (p != null) result.put(p.getPatientId(), p);
                }
            }
            return result;
        }

        // 4. O(1) exact age lookup (numeric term)
        boolean numeric = term.chars().allMatch(Character::isDigit);
        if (numeric) {
            OrderedMap<String, Patient> ageBucket = ageIndex.get(term);
            if (ageBucket != null) {
                for (Patient p : ageBucket) {
                    if (p != null) result.put(p.getPatientId(), p);
                }
            }
            return result;
        }

        // 5. Fallback partial name (linear)
        for (Patient p : patientRegistry) {
            if (p != null && p.getName() != null &&
                p.getName().toLowerCase().contains(term)) {
                result.put(p.getPatientId(), p);
            }
        }
        return result;
    }

    /**
     * Get all patients as array
     */
    public Patient[] getAllPatientsArray() {
        Patient[] patientsArray = new Patient[patientRegistry.size()];
        return patientRegistry.toArray(patientsArray);
    }

    /**
     * Get registered patient count
     */
    public int getRegisteredPatientCount() {
        return patientRegistry.size();
    }

    /**
     * Get patient registry as OrderedMap
     */
    public OrderedMap<String, Patient> getAllPatients() {
        // Create a copy to avoid modifying the original registry
        OrderedMap<String, Patient> sortedPatients = new OrderedMap<>();
        for (Patient patient : patientRegistry) {
            sortedPatients.put(patient.getPatientId(), patient);
        }

        // Sort by patient name
        sortedPatients.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));

        return sortedPatients;
    }

    /**
     * Save patient changes to file
     */
    public void saveChanges() {
        patientDAO.saveToFile(patientRegistry);
    }

    // ===============================
    // QUEUE MANAGEMENT SECTION
    // ===============================

    /**
     * Enqueue patient to appropriate queue
     */
    public void enqueuePatient(String patientId) {
        Patient patient = patientRegistry.get(patientId);
        if (patient == null) {
            return;
        }

        if (getTotalQueueSize() < MAX_QUEUE_SIZE) {
            if (patient.isEmergency()) {
                emergencyQueue.offer(patientId, patient);
                patientEmergencyQueueDAO.saveToFile(emergencyQueue);
            } else {
                normalQueue.offer(patientId, patient);
                patientNormalQueueDAO.saveToFile(normalQueue);
            }
        } else {
            System.out.println("Queue is full. Cannot enqueue patient.");
        }
    }

    /**
     * Serve next patient from queues
     */
    public Patient serveNextPatient() {
        // Use OrderedMap's poll() method for proper queue behavior (FIFO)
        Patient nextPatient = emergencyQueue.poll();

        if (nextPatient == null) {
            nextPatient = normalQueue.poll();
            patientNormalQueueDAO.saveToFile(normalQueue);
        } else {
            patientEmergencyQueueDAO.saveToFile(emergencyQueue);
        }


        return nextPatient;
    }

    /**
     * Peek at next patient without removing
     */
    public Patient peekNextPatient() {
        Patient nextPatient = emergencyQueue.peek();
        if (nextPatient == null) {
            nextPatient = normalQueue.peek();
        }
        return nextPatient;
    }

    /**
     * Get emergency queue contents for display
     */
    public OrderedMap<String, Patient> getEmergencyQueue() {
        return getAllQueuedPatients().filter(null, (patient, reference) -> {
            if (patient == null) return -1;
            return patient.isEmergency() ? 0 : -1;
        });
    }

    /**
     * Get normal queue contents for display
     */
    public OrderedMap<String, Patient> getNormalQueue() {
        return getAllQueuedPatients().filter(null, (patient, reference) -> {
            if (patient == null) return -1;
            return !patient.isEmergency() ? 0 : -1;
        });
    }

    /**
     * Helper method to get all patients currently in any queue
     */
    private OrderedMap<String, Patient> getAllQueuedPatients() {
        OrderedMap<String, Patient> allQueued = new OrderedMap<>();

        // Add all patients from emergency queue
        for (Patient patient : emergencyQueue) {
            allQueued.put(patient.getPatientId(), patient);
        }

        // Add all patients from normal queue
        for (Patient patient : normalQueue) {
            allQueued.put(patient.getPatientId(), patient);
        }

        return allQueued;
    }

    /**
     * Clear all queues
     */
    public void clearAllQueues() {
        normalQueue.clear();
        emergencyQueue.clear();
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

    // ===============================
    // RECORD MANAGEMENT SECTION
    // ===============================

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
        IDGenerator.saveCounters("counter.dat");
        return true;
    }

    /**
     * Get patient visit history
     */
    public OrderedMap<String, VisitHistory> getPatientVisitHistory(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return new OrderedMap<>();
        }

        // Get existing visit histories with proper filtering
        OrderedMap<String, VisitHistory> patientVisits = new OrderedMap<>();
        for (VisitHistory visit : visitHistoryMap) {
            if (visit.getPatient() != null &&
                    visit.getPatient().getPatientId().equals(patientId)) {
                patientVisits.put(visit.getVisitId(), visit);
            }
        }

        // Sort by visit date (chronological order)
        patientVisits.sort((v1, v2) -> v1.getVisitDate().compareTo(v2.getVisitDate()));

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
     * Get all visit histories
     */
    public OrderedMap<String, VisitHistory> getAllVisitHistories() {
        OrderedMap<String, VisitHistory> copy = new OrderedMap<>();

        // Use OrderedMap indexed access to copy all entries
        for (int i = 0; i < visitHistoryMap.size(); i++) {
            VisitHistory visit = visitHistoryMap.get(i);
            if (visit != null) {
                copy.put(visit.getVisitId(), visit);
            }
        }

        // Sort by visit date (most recent first)
        copy.sort((v1, v2) -> v2.getVisitDate().compareTo(v1.getVisitDate()));

        return copy;
    }

    /**
     * Update visit history with comprehensive validation
     */
    public boolean updateVisitHistory(String patientId,
                                      String visitId,
                                      String visitReason,
                                      String status) {
        if (patientId == null || patientId.isBlank() ||
                visitId == null || visitId.isBlank()) {
            return false;
        }
        VisitHistory vh = visitHistoryMap.get(visitId);
        if (vh == null) return false;
        if (vh.getPatient() == null ||
                !vh.getPatient().getPatientId().equalsIgnoreCase(patientId)) {
            return false;
        }

        boolean updated = false;
        if (visitReason != null && !visitReason.isBlank()) {
            vh.setVisitReason(visitReason.trim());
            updated = true;
        }
        if (status != null && !status.isBlank()) {
            vh.setStatus(status.trim().toUpperCase());
            updated = true;
        }
        if (updated) {
            visitHistoryDAO.saveToFile(visitHistoryMap);
        }
        return updated;
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
     * Generate patient registration summary report data
     * @return OrderedMap containing report data with keys for different metrics
     */
    public OrderedMap<String, Object> generatePatientRegistrationReport() {
        OrderedMap<String, Object> reportData = new OrderedMap<>();
        // Total counts
        reportData.put("totalPatients", patientRegistry.size());
        // Queue statistics
        reportData.put("patientsInQueue", getTotalQueueSize());
        reportData.put("emergencyQueueSize", getEmergencyQueueSize());
        reportData.put("normalQueueSize", getNormalQueueSize());
        // Gender breakdown using filter
        OrderedMap<String, Integer> genderStats = new OrderedMap<>();
        // Filter for males
        OrderedMap<String, Patient> malePatients = patientRegistry.filter(
                new Patient("", "", 0, "Male", "", "", false),
                (p1, p2) -> p1.getGender().equalsIgnoreCase(p2.getGender()) ? 0 : -1
        );
        genderStats.put("Male", malePatients.size());
        // Filter for females
        OrderedMap<String, Patient> femalePatients = patientRegistry.filter(
                new Patient("", "", 0, "Female", "", "", false),
                (p1, p2) -> p1.getGender().equalsIgnoreCase(p2.getGender()) ? 0 : -1
        );
        genderStats.put("Female", femalePatients.size());
        reportData.put("genderBreakdown", genderStats);
        // Age group breakdown using filter
        OrderedMap<String, Integer> ageGroups = new OrderedMap<>();
        // Filter for each age group
        OrderedMap<String, Patient> group0to18 = patientRegistry.filter(
                new Patient("", "", 18, "", "", "", false),
                (p1, p2) -> p1.getAge() <= 18 ? 0 : -1
        );
        ageGroups.put("0-18", group0to18.size());
        OrderedMap<String, Patient> group19to35 = patientRegistry.filter(
                new Patient("", "", 35, "", "", "", false),
                (p1, p2) -> (p1.getAge() >= 19 && p1.getAge() <= 35) ? 0 : -1
        );
        ageGroups.put("19-35", group19to35.size());

        OrderedMap<String, Patient> group36to50 = patientRegistry.filter(
                new Patient("", "", 50, "", "", "", false),
                (p1, p2) -> (p1.getAge() >= 36 && p1.getAge() <= 50) ? 0 : -1
        );
        ageGroups.put("36-50", group36to50.size());
        OrderedMap<String, Patient> group51to65 = patientRegistry.filter(
                new Patient("", "", 65, "", "", "", false),
                (p1, p2) -> (p1.getAge() >= 51 && p1.getAge() <= 65) ? 0 : -1
        );
        ageGroups.put("51-65", group51to65.size());

        OrderedMap<String, Patient> group65Plus = patientRegistry.filter(
                new Patient("", "", 66, "", "", "", false),
                (p1, p2) -> p1.getAge() > 65 ? 0 : -1
        );
        ageGroups.put("65+", group65Plus.size());
        reportData.put("ageGroupBreakdown", ageGroups);
        return reportData;
    }

    public OrderedMap<String, Object> generatePatientVisitSummaryReport() {
        OrderedMap<String, Object> report = new OrderedMap<>();

        // 1) aggregate
        OrderedMap<String, Integer> statusCounts = new OrderedMap<>();
        statusCounts.put("SCHEDULED", 0);
        statusCounts.put("IN_PROGRESS", 0);
        statusCounts.put("COMPLETED", 0);
        statusCounts.put("CANCELLED", 0);

        OrderedMap<String, Integer> visitsPerMonth = new OrderedMap<>();
        OrderedMap<String, Integer> countByPatient = new OrderedMap<>();

        OrderedMap<String, VisitHistory> allV = getAllVisitHistories();
        int total = allV.size();

        for (int i = 0; i < allV.size(); i++) {
            VisitHistory vh = allV.get(i);
            // status
            String st = vh.getStatus();
            if (!statusCounts.containsKey(st)) statusCounts.put(st, 0);
            statusCounts.put(st, statusCounts.get(st) + 1);

            // month
            String m = vh.getVisitDate().getYear() + "-" +
                    String.format("%02d", vh.getVisitDate().getMonthValue());
            if (!visitsPerMonth.containsKey(m)) visitsPerMonth.put(m, 0);
            visitsPerMonth.put(m, visitsPerMonth.get(m) + 1);

            // patient
            String pid = vh.getPatient().getPatientId();
            // normalize to uppercase so UI lookups match
            if (pid != null) pid = pid.toUpperCase();
            if (!countByPatient.containsKey(pid)) countByPatient.put(pid, 0);
            countByPatient.put(pid, countByPatient.get(pid) + 1);
        }

        int patientDenom = Math.max(1, patientRegistry.size());
        double avgPerPatient = (double) total / patientDenom;

        OrderedMap<String, Integer> topPatients = new OrderedMap<>();
        int picks = Math.min(3, countByPatient.size());
        for (int pick = 0; pick < picks; pick++) {
            String bestId = null;
            int bestCnt = -1;

            Patient[] arr = getAllPatientsArray();
            for (int i = 0; i < arr.length; i++) {
                Patient p = arr[i];
                if (p == null) continue;
                String pid = p.getPatientId();
                if (pid != null) pid = pid.toUpperCase();

                if (countByPatient.containsKey(pid)) {
                    int c = countByPatient.get(pid);
                    if (c > bestCnt && !topPatients.containsKey(pid)) {
                        bestCnt = c;
                        bestId = pid;
                    }
                }
            }
            if (bestId != null) {
                topPatients.put(bestId, bestCnt);
            }
        }
        report.put("totalVisits", total);
        report.put("averageVisitsPerPatient", avgPerPatient);
        report.put("statusCounts", statusCounts);
        report.put("visitsPerMonth", visitsPerMonth);
        report.put("topPatients", topPatients);

        return report;
    }
}