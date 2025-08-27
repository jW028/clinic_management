package control;

import entity.*;
import adt.*;
import dao.*;
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
    private CustomADT<String, Consultation> consultationMap;
    private CustomADT<String, Treatment> treatmentMap;
    private final ConsultationDAO consultationDAO;
    private final TreatmentDAO treatmentDAO;
    private ConsultationMaintenance consultationMaintenance;
    private TreatmentMaintenance treatmentMaintenance;
    public PatientMaintenance() {
        this.normalQueue = new CustomADT<>();
        this.emergencyQueue = new CustomADT<>();
        this.patientDAO = new PatientDAO();
        this.visitHistoryDAO = new VisitHistoryDAO();
        this.waitlist = new CustomADT<>();
        this.consultationDAO = new ConsultationDAO();
        this.treatmentDAO = new TreatmentDAO();
        this.consultationMaintenance = new ConsultationMaintenance();
        this.treatmentMaintenance = new TreatmentMaintenance();

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

        CustomADTInterface<String, Consultation> loadedConsultations = consultationDAO.retrieveFromFile();
        if (loadedConsultations != null) {
            this.consultationMap = (CustomADT<String, Consultation>) loadedConsultations;
        } else {
            this.consultationMap = new CustomADT<>();
        }

        CustomADTInterface<String, Treatment> loadedTreatments = treatmentDAO.retrieveFromFile();
        if (loadedTreatments != null) {
            this.treatmentMap = (CustomADT<String, Treatment>) loadedTreatments;
        } else {
            this.treatmentMap = new CustomADT<>();
        }

        IDGenerator.loadCounter("counter.dat");
    }

    // Fetch consultations for a patient
    public CustomADT<String, Consultation> getConsultationsByPatient(String patientId) {
        CustomADT<String, Consultation> result = new CustomADT<>();
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
    // Fetch treatments for a patient
    public CustomADT<String, Treatment> getTreatmentsForPatient(String patientId) {
        CustomADT<String, Treatment> result = new CustomADT<>();
        if (patientId == null) return result;
        Patient p = getPatientById(patientId);
        if (p == null) return result;

        CustomADT<String, Treatment> fetched = treatmentMaintenance.getTreatmentsByPatient(p);
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
        saveChanges();
        String visitReason = isEmergency ? "Emergency Registration" : "Regular Registration";
        IDGenerator.saveCounters("counter.dat");
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

        patient.setName(name);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setContactNumber(contactNumber);
        patient.setAddress(address);
        patient.setEmergency(isEmergency);

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

        emergencyQueue.remove(patientId);
        normalQueue.remove(patientId);
        waitlist.remove(patientId);

        patientRegistry.remove(patientId);
        saveChanges();
        return true;
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
     * Get patient registry as CustomADT
     */
    public CustomADT<String, Patient> getAllPatients() {
        // Create a copy to avoid modifying the original registry
        CustomADT<String, Patient> sortedPatients = new CustomADT<>();
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

    /**
     * Serve next patient from queues
     */
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
     * Add patient to waitlist
     */
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

    /**
     * Process waitlist to move patients to main queue
     */
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

    /**
     * Promote patient from waitlist to queue
     */
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
     * Get emergency queue contents for display
     */
    public CustomADT<String, Patient> getEmergencyQueue() {
        return getAllQueuedPatients().filter(null, (patient, reference) -> {
            if (patient == null) return -1;
            return patient.isEmergency() ? 0 : -1;
        });
    }

    /**
     * Get normal queue contents for display
     */
    public CustomADT<String, Patient> getNormalQueue() {
        return getAllQueuedPatients().filter(null, (patient, reference) -> {
            if (patient == null) return -1;
            return !patient.isEmergency() ? 0 : -1;
        });
    }

    /**
     * Get waitlist
     */
    public CustomADT<String, Patient> getWaitlist() {
        return waitlist;
    }

    /**
     * Helper method to get all patients currently in any queue
     */
    private CustomADT<String, Patient> getAllQueuedPatients() {
        CustomADT<String, Patient> allQueued = new CustomADT<>();

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
     * Remove patient from waitlist
     */
    public boolean removeFromWaitlist(String patientId) {
        return waitlist.remove(patientId) != null;
    }

    /**
     * Clear all queues and waitlist
     */
    public void clearAllQueues() {
        normalQueue.clear();
        emergencyQueue.clear();
        waitlist.clear();
    }

    // Queue Status Methods
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

    public boolean isQueueFull() {
        return getTotalQueueSize() >= MAX_QUEUE_SIZE;
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
    public CustomADT<String, VisitHistory> getAllVisitHistories() {
        CustomADT<String, VisitHistory> copy = new CustomADT<>();

        // Use CustomADT indexed access to copy all entries
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
     * @return CustomADT containing report data with keys for different metrics
     */
    public CustomADT<String, Object> generatePatientRegistrationReport() {
        CustomADT<String, Object> reportData = new CustomADT<>();

        // Total counts
        reportData.put("totalPatients", patientRegistry.size());

        // Queue statistics
        reportData.put("patientsInQueue", getTotalQueueSize());
        reportData.put("emergencyQueueSize", getEmergencyQueueSize());
        reportData.put("normalQueueSize", getNormalQueueSize());
        reportData.put("waitlistSize", getWaitlistSize());

        // Gender breakdown using filter
        CustomADT<String, Integer> genderStats = new CustomADT<>();

        // Filter for males
        CustomADT<String, Patient> malePatients = patientRegistry.filter(
                new Patient("", "", 0, "Male", "", "", false),
                (p1, p2) -> p1.getGender().equalsIgnoreCase(p2.getGender()) ? 0 : -1
        );
        genderStats.put("Male", malePatients.size());

        // Filter for females
        CustomADT<String, Patient> femalePatients = patientRegistry.filter(
                new Patient("", "", 0, "Female", "", "", false),
                (p1, p2) -> p1.getGender().equalsIgnoreCase(p2.getGender()) ? 0 : -1
        );
        genderStats.put("Female", femalePatients.size());

        reportData.put("genderBreakdown", genderStats);

        // Age group breakdown using filter
        CustomADT<String, Integer> ageGroups = new CustomADT<>();

        // Filter for each age group
        CustomADT<String, Patient> group0to18 = patientRegistry.filter(
                new Patient("", "", 18, "", "", "", false),
                (p1, p2) -> p1.getAge() <= 18 ? 0 : -1
        );
        ageGroups.put("0-18", group0to18.size());

        CustomADT<String, Patient> group19to35 = patientRegistry.filter(
                new Patient("", "", 35, "", "", "", false),
                (p1, p2) -> (p1.getAge() >= 19 && p1.getAge() <= 35) ? 0 : -1
        );
        ageGroups.put("19-35", group19to35.size());

        CustomADT<String, Patient> group36to50 = patientRegistry.filter(
                new Patient("", "", 50, "", "", "", false),
                (p1, p2) -> (p1.getAge() >= 36 && p1.getAge() <= 50) ? 0 : -1
        );
        ageGroups.put("36-50", group36to50.size());

        CustomADT<String, Patient> group51to65 = patientRegistry.filter(
                new Patient("", "", 65, "", "", "", false),
                (p1, p2) -> (p1.getAge() >= 51 && p1.getAge() <= 65) ? 0 : -1
        );
        ageGroups.put("51-65", group51to65.size());

        CustomADT<String, Patient> group65Plus = patientRegistry.filter(
                new Patient("", "", 66, "", "", "", false),
                (p1, p2) -> p1.getAge() > 65 ? 0 : -1
        );
        ageGroups.put("65+", group65Plus.size());

        reportData.put("ageGroupBreakdown", ageGroups);

        return reportData;
    }

    // Java
    public CustomADT<String, Object> generatePatientVisitSummaryReport() {
        CustomADT<String, Object> report = new CustomADT<>();
        int totalVisits = visitHistoryMap.size();
        CustomADT<String, Integer> statusCounts = new CustomADT<>();
        CustomADT<String, Integer> visitsPerPatient = new CustomADT<>();
        CustomADT<String, Integer> visitsPerMonth = new CustomADT<>();

        for (VisitHistory vh : visitHistoryMap) {
            // Status breakdown
            String status = vh.getStatus();
            statusCounts.put(status, statusCounts.get(status) == null ? 1 : statusCounts.get(status) + 1);

            // Visits per patient
            String pid = vh.getPatient().getPatientId();
            visitsPerPatient.put(pid, visitsPerPatient.get(pid) == null ? 1 : visitsPerPatient.get(pid) + 1);

            // Visits per month (format: yyyy-MM)
            String month = vh.getVisitDate().getYear() + "-" + String.format("%02d", vh.getVisitDate().getMonthValue());
            visitsPerMonth.put(month, visitsPerMonth.get(month) == null ? 1 : visitsPerMonth.get(month) + 1);
        }

        // Average visits per patient
        double avgVisits = visitsPerPatient.size() == 0 ? 0.0 : (double) totalVisits / visitsPerPatient.size();

        // Top 3 patients by visit count
        CustomADT<String, Integer> topPatients = new CustomADT<>();
        String[] patientIds = new String[visitsPerPatient.size()];
        Integer[] visitCounts = new Integer[visitsPerPatient.size()];
        for (int i = 0; i < visitsPerPatient.size(); i++) {
            patientIds[i] = visitsPerPatient.get(i) != null ? visitsPerPatient.get(i).toString() : null;
            visitCounts[i] = visitsPerPatient.get(visitsPerPatient.get(i).toString());
        }
        for (int i = 0; i < 3 && i < visitsPerPatient.size(); i++) {
            int maxIdx = -1, maxVal = -1;
            for (int j = 0; j < visitCounts.length; j++) {
                if (visitCounts[j] != null && visitCounts[j] > maxVal) {
                    maxVal = visitCounts[j];
                    maxIdx = j;
                }
            }
            if (maxIdx != -1 && patientIds[maxIdx] != null) {
                topPatients.put(patientIds[maxIdx], visitCounts[maxIdx]);
                visitCounts[maxIdx] = -1; // Mark as used
            }
        }

        report.put("totalVisits", totalVisits);
        report.put("statusCounts", statusCounts);
        report.put("averageVisitsPerPatient", avgVisits);
        report.put("topPatients", topPatients);
        report.put("visitsPerMonth", visitsPerMonth);

        return report;
    }
}