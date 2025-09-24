/**
 * @author Tan Jin Wei
 */

package control;

import adt.OrderedMap;
import dao.ProcedureDAO;
import dao.TreatmentDAO;
import entity.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import utility.IDGenerator;

public class TreatmentMaintenance {
    
    /**
     * Inner class to represent an undoable command
     */
    private static class UndoCommand {
        private String operationType;
        private String operationId;
        private Object data;
        private LocalDateTime timestamp;
        
        public UndoCommand(String operationType, String operationId, Object data) {
            this.operationType = operationType;
            this.operationId = operationId;
            this.data = data;
            this.timestamp = LocalDateTime.now();
        }
        
        // Getters
        public String getOperationType() { return operationType; }
        public String getOperationId() { return operationId; }
        public Object getData() { return data; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    private OrderedMap<String, Treatment> treatments;

    private OrderedMap<String, Treatment> emergencyQueue;
    private OrderedMap<String, Treatment> regularQueue;

    private OrderedMap<String, String> recentTreatments;
    private OrderedMap<String, UndoCommand> undoStack; // Stack for undo operations
    private static final int MAX_UNDO_SIZE = 20; // Limit undo history
    private OrderedMap<String, Prescription> prescriptions;
    private OrderedMap<String, Consultation> consultations;
    private final ConsultationMaintenance consultationController;
    private final PharmacyMaintenance prescriptionController;

    private TreatmentDAO treatmentDAO;
    private ProcedureDAO procedureDAO;
    private PaymentMaintenance paymentMaintenance;

    // Hash-based indices for O(1) lookup
    private OrderedMap<String, OrderedMap<String, Treatment>> patientIndex;
    private OrderedMap<String, OrderedMap<String, Treatment>> statusIndex;
    private OrderedMap<String, OrderedMap<String, Treatment>> procedureIndex;
    private OrderedMap<String, OrderedMap<String, Treatment>> patientNameIndex;
    
    private OrderedMap<String, OrderedMap<String, Treatment>> searchCache = new OrderedMap<>();


    public TreatmentMaintenance() {
        this.treatmentDAO = new TreatmentDAO();
        this.procedureDAO = new ProcedureDAO();
        this.treatments = new OrderedMap<>();
        this.paymentMaintenance = PaymentMaintenance.getInstance();
        this.emergencyQueue = new OrderedMap<>();
        this.regularQueue = new OrderedMap<>();
        this.recentTreatments = new OrderedMap<>();

        this.prescriptions = new OrderedMap<>();
        this.consultations = new OrderedMap<>();
        this.consultationController = new ConsultationMaintenance();
        this.prescriptionController = new PharmacyMaintenance();

        loadAllData();
        
        // Initialize undo stack
        undoStack = new OrderedMap<>();
        IDGenerator.loadCounter("counter.dat");
    }

    private void buildHashIndices() {
        patientIndex = new OrderedMap<>();
        statusIndex = new OrderedMap<>();
        procedureIndex = new OrderedMap<>();
        patientNameIndex = new OrderedMap<>();
        
        for (Treatment treatment : treatments) {
            if (treatment == null) continue;

            String treatmentId = treatment.getTreatmentID();

            // Index by patient ID for O(1) lookup
            if (treatment.getPatient() != null) {
                String patientId = treatment.getPatient().getPatientId();
                addToIndex(patientIndex, patientId, treatmentId, treatment);
            }

            // Index by patient name for O(1) lookup
            if (treatment.getPatient() != null && treatment.getPatient().getName() != null) {
                String patientName = treatment.getPatient().getName();
                addToIndex(patientNameIndex, patientName, treatmentId, treatment);
            }

            // Index by status for O(1) lookup
            String status = treatment.getStatus();
            if (status != null) {
                addToIndex(statusIndex, status, treatmentId, treatment);
            }

            // Index by procedure names for O(1) lookup
            OrderedMap<String, Procedure> procedures = treatment.getProcedures();
            if (procedures != null) {
                for (Procedure procedure : procedures) {
                    if (procedure != null && procedure.getProcedureName() != null) {
                        String procedureName = procedure.getProcedureName();
                        addToIndex(procedureIndex, procedureName, treatmentId, treatment);
                    }
                }
            }
        }
    }

    private void addToIndex(OrderedMap<String, OrderedMap<String, Treatment>> index, 
                            String key, String treatmentId, Treatment treatment) {
        OrderedMap<String, Treatment> treatmentMap = index.get(key);
        if (treatmentMap == null) {
            treatmentMap = new OrderedMap<>();
            index.put(key, treatmentMap);
        }
        treatmentMap.put(treatmentId, treatment);
    }

    /**
     * Clear search cache when data changes
     */
    private void invalidateSearchCache() {
        searchCache.clear();
    }

    /**
     * Helper method to update hash indices for treatment addition
     */
    private void updateIndicesForAddition(Treatment treatment) {
        String treatmentId = treatment.getTreatmentID();

        // Update patient index
        if (treatment.getPatient() != null) {
            String patientId = treatment.getPatient().getPatientId();
            addToIndex(patientIndex, patientId, treatmentId, treatment);

            String patientName = treatment.getPatient().getName();
            if (patientName != null) {
                addToIndex(patientNameIndex, patientName.toLowerCase(), treatmentId, treatment);
            }
        }

        // Update status index
        String status = treatment.getStatus();
        if (status != null) {
            addToIndex(statusIndex, status, treatmentId, treatment);
        }

        // Index by procedure names for O(1) lookup
        OrderedMap<String, Procedure> procedures = treatment.getProcedures();
        if (procedures != null) {
            for (Procedure procedure : procedures) {
                if (procedure != null && procedure.getProcedureName() != null) {
                    String procedureName = procedure.getProcedureName();
                    addToIndex(procedureIndex, procedureName, treatmentId, treatment);
                }
            }
        }
    }

    /**
     * Helper method to update hash indices for treatment removal
     */
    private void updateIndicesForRemoval(Treatment treatment) {
        String treatmentId = treatment.getTreatmentID();

        // Remove from patient index
        if (treatment.getPatient() != null) {
            String patientId = treatment.getPatient().getPatientId();
            removeFromIndex(patientIndex, patientId, treatmentId);

            String patientName = treatment.getPatient().getName();
            if (patientName != null) {
                removeFromIndex(patientNameIndex, patientName.toLowerCase(), treatmentId);
            }
        }

        // Remove from status index
        String status = treatment.getStatus();
        if (status != null) {
            removeFromIndex(statusIndex, status, treatmentId);
        }

        // Remove from procedure index
        OrderedMap<String, Procedure> procedures = treatment.getProcedures();
        if (procedures != null) {
            for (Procedure procedure : procedures) {
                if (procedure != null && procedure.getProcedureName() != null) {
                    String procedureName = procedure.getProcedureName();
                    removeFromIndex(procedureIndex, procedureName, treatmentId);
                }
            }
        }
    }

    /**
     * Helper method to remove treatment from index
     */
    private void removeFromIndex(OrderedMap<String, OrderedMap<String, Treatment>> index, 
                                 String key, String treatmentId) {
        OrderedMap<String, Treatment> treatmentMap = index.get(key);
        if (treatmentMap != null) {
            treatmentMap.remove(treatmentId);
            if (treatmentMap.isEmpty()) {
                index.remove(key);
            }
        }
    }

    /**
     * Undo the last operation performed on treatments
     * @return true if undo was successful, false otherwise
     */
    public boolean undoLastOperation() {
        try {
            if (undoStack.isEmpty()) {
                System.out.println("No operations to undo.");
                return false;
            }
            
            // Get the most recent undo command (top of stack)
            UndoCommand lastCommand = undoStack.top();
            
            if (lastCommand == null) {
                System.out.println("Invalid undo command.");
                return false;
            }
            
            boolean success = false;
            
            switch (lastCommand.getOperationType()) {
                case "ADD":
                    success = undoAddOperation(lastCommand);
                    break;
                case "UPDATE":
                    success = undoUpdateOperation(lastCommand);
                    break;
                case "DELETE":
                    success = undoDeleteOperation(lastCommand);
                    break;
                case "STATUS_CHANGE":
                    success = undoStatusChangeOperation(lastCommand);
                    break;
                default:
                    System.out.println("Unknown operation type: " + lastCommand.getOperationType());
                    break;
            }
            
            if (success) {
                undoStack.pop(); // Remove action from undo stack
                System.out.println("Undo successful: " + lastCommand.getOperationType() + 
                                 " operation on treatment " + lastCommand.getOperationId());
                
                // Update recent treatments to show undo
                String undoEntry = "[UNDO] " + lastCommand.getOperationType() + " - " + 
                                 lastCommand.getOperationId() + " at " + 
                                 java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                recentTreatments.push("undo_" + System.currentTimeMillis(), undoEntry);
            }
            
            return success;
            
        } catch (Exception e) {
            System.out.println("Error during undo operation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Push a command onto the undo stack
     * @param operationType Type of operation (ADD, UPDATE, DELETE, STATUS_CHANGE)
     * @param operationId ID of the treatment affected
     * @param data Backup data needed for undo
     */
    private void pushUndoCommand(String operationType, String operationId, Object data) {
        try {
            // Limit undo history size
            if (undoStack.size() >= MAX_UNDO_SIZE) {
                // Remove oledst undo command
                undoStack.removeAt(0); //
            }
            
            UndoCommand command = new UndoCommand(operationType, operationId, data);
            undoStack.push("undo_" + System.currentTimeMillis(), command);
            
        } catch (Exception e) {
            System.out.println("Error pushing undo command: " + e.getMessage());
        }
    }
    
    /**
     * Undo an ADD operation by removing the treatment
     */
    private boolean undoAddOperation(UndoCommand command) {
        try {
            String treatmentId = command.getOperationId();
            Treatment treatment = treatments.get(treatmentId);
            
            if (treatment != null) {
                // Remove from main collection
                treatments.remove(treatmentId);
                
                // Remove from all indices
                removeFromAllIndices(treatment);
                
                // Remove from queues if present
                emergencyQueue.remove(treatmentId);
                regularQueue.remove(treatmentId);
                
                System.out.println("Undid ADD: Removed treatment " + treatmentId);
                return true;
            } else {
                System.out.println("Treatment " + treatmentId + " not found for undo ADD");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error undoing ADD operation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Undo an UPDATE operation by restoring the previous treatment state
     */
    private boolean undoUpdateOperation(UndoCommand command) {
        try {
            if (command.getData() instanceof Treatment) {
                Treatment oldTreatment = (Treatment) command.getData();
                String treatmentId = command.getOperationId();
                
                // Remove current version from indices
                Treatment currentTreatment = treatments.get(treatmentId);
                if (currentTreatment != null) {
                    removeFromAllIndices(currentTreatment);
                }
                
                // Restore old version
                treatments.put(treatmentId, oldTreatment);
                addToAllIndices(oldTreatment);
                
                System.out.println("Undid UPDATE: Restored treatment " + treatmentId + " to previous state");
                return true;
            } else {
                System.out.println("Invalid data for undo UPDATE operation");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error undoing UPDATE operation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Undo a DELETE operation by restoring the deleted treatment
     */
    private boolean undoDeleteOperation(UndoCommand command) {
        try {
            if (command.getData() instanceof Treatment) {
                Treatment deletedTreatment = (Treatment) command.getData();
                String treatmentId = command.getOperationId();
                
                // Restore the treatment
                treatments.put(treatmentId, deletedTreatment);
                addToAllIndices(deletedTreatment);
                
                // Add back to appropriate queue based on type
                if (deletedTreatment.getType() != null && 
                    deletedTreatment.getType().toUpperCase().contains("EMERGENCY")) {
                    emergencyQueue.put(treatmentId, deletedTreatment);
                } else {
                    regularQueue.put(treatmentId, deletedTreatment);
                }
                
                System.out.println("Undid DELETE: Restored treatment " + treatmentId);
                return true;
            } else {
                System.out.println("Invalid data for undo DELETE operation");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error undoing DELETE operation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Undo a STATUS_CHANGE operation by reverting to previous status
     */
    private boolean undoStatusChangeOperation(UndoCommand command) {
        try {
            if (command.getData() instanceof String) {
                String previousStatus = (String) command.getData();
                String treatmentId = command.getOperationId();
                Treatment treatment = treatments.get(treatmentId);
                
                if (treatment != null) {
                    // Remove from current status index
                    removeFromIndex(statusIndex, treatment.getStatus(), treatmentId);
                    
                    // Update status
                    treatment.setStatus(previousStatus);
                    
                    // Add to new status index
                    addToIndex(statusIndex, previousStatus, treatmentId, treatment);
                    
                    System.out.println("Undid STATUS_CHANGE: Reverted treatment " + treatmentId + 
                                     " status to " + previousStatus);
                    return true;
                } else {
                    System.out.println("Treatment " + treatmentId + " not found for status change undo");
                    return false;
                }
            } else {
                System.out.println("Invalid data for undo STATUS_CHANGE operation");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error undoing STATUS_CHANGE operation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get information about available undo operations
     * @return String describing the undo stack state
     */
    public String getUndoInfo() {
        if (undoStack.isEmpty()) {
            return "No operations available to undo.";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Available undo operations (").append(undoStack.size()).append("):\n");
        
        // Show last few operations by iterating backwards
        int count = 0;
        for (int i = undoStack.size() - 1; i >= 0 && count < 5; i--, count++) {
            try {
                UndoCommand cmd = undoStack.get(i);
                if (cmd != null) {
                    info.append("- ").append(cmd.getOperationType())
                        .append(" on treatment ").append(cmd.getOperationId())
                        .append(" at ").append(cmd.getTimestamp().format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .append("\n");
                }
            } catch (Exception e) {
                // Skip invalid entries
            }
        }
        
        return info.toString();
    }
    
    /**
     * Helper method to add treatment to all indices
     */
    private void addToAllIndices(Treatment treatment) {
        String treatmentId = treatment.getTreatmentID();
        
        // Add to patient index
        if (treatment.getPatient() != null) {
            addToIndex(patientIndex, treatment.getPatient().getPatientId(), treatmentId, treatment);
        }
        
        // Add to status index
        if (treatment.getStatus() != null) {
            addToIndex(statusIndex, treatment.getStatus(), treatmentId, treatment);
        }
        
        // Add to procedure index (using first procedure if available)
        if (treatment.getProcedures() != null && !treatment.getProcedures().isEmpty()) {
            // Get first procedure for indexing
            Procedure firstProcedure = treatment.getProcedures().get(0);
            if (firstProcedure != null) {
                addToIndex(procedureIndex, firstProcedure.getProcedureName(), treatmentId, treatment);
            }
        }
        
        // Add to patient name index
        if (treatment.getPatient() != null && treatment.getPatient().getName() != null) {
            addToIndex(patientNameIndex, treatment.getPatient().getName(), treatmentId, treatment);
        }
    }
    
    /**
     * Helper method to remove treatment from all indices
     */
    private void removeFromAllIndices(Treatment treatment) {
        String treatmentId = treatment.getTreatmentID();
        
        // Remove from patient index
        if (treatment.getPatient() != null) {
            removeFromIndex(patientIndex, treatment.getPatient().getPatientId(), treatmentId);
        }
        
        // Remove from status index
        if (treatment.getStatus() != null) {
            removeFromIndex(statusIndex, treatment.getStatus(), treatmentId);
        }
        
        // Remove from procedure index (using first procedure if available)
        if (treatment.getProcedures() != null && !treatment.getProcedures().isEmpty()) {
            // Get first procedure for indexing
            Procedure firstProcedure = treatment.getProcedures().get(0);
            if (firstProcedure != null) {
                removeFromIndex(procedureIndex, firstProcedure.getProcedureName(), treatmentId);
            }
        }
        
        // Remove from patient name index
        if (treatment.getPatient() != null && treatment.getPatient().getName() != null) {
            removeFromIndex(patientNameIndex, treatment.getPatient().getName(), treatmentId);
        }
    }
    
    /**
     * Clear the undo history
     */
    public void clearUndoHistory() {
        undoStack.clear();
        System.out.println("Undo history cleared.");
    }
    
    /**
     * Helper method to create a deep copy of a treatment for undo operations
     */
    private Treatment createTreatmentCopy(Treatment original) {
        try {
            // Create new Treatment with same basic properties
            Treatment copy = new Treatment(
                original.getTreatmentID(),
                original.getConsultationID(),
                original.getPatient(),
                original.getDoctor(),
                original.getTreatmentDate(),
                original.getNotes(),
                original.isCritical()
            );
            
            // Copy status and type
            copy.setStatus(original.getStatus());
            copy.setType(original.getType());
            
            // Copy prescription if exists
            if (original.getPrescription() != null) {
                copy.setPrescription(original.getPrescription());
            }
            
            // Note: For procedures, we'll keep the reference to the same OrderedMap
            // since procedures are generally not modified after creation
            // If deep copy of procedures is needed, it would require additional logic
            
            return copy;
            
        } catch (Exception e) {
            System.out.println("Error creating treatment copy: " + e.getMessage());
            return original; // Return original if copy fails
        }
    }
    
    /**
     * Update treatment status with undo support
     * @param treatmentID The ID of the treatment to update
     * @param newStatus The new status to set
     * @return true if update was successful, false otherwise
     */
    public boolean updateTreatmentStatus(String treatmentID, String newStatus) {
        Treatment treatment = treatments.get(treatmentID);
        if (treatment != null) {
            String oldStatus = treatment.getStatus();
            
            // Remove from old status index
            removeFromIndex(statusIndex, oldStatus, treatmentID);
            
            // Update status
            treatment.setStatus(newStatus);
            
            // Add to new status index
            addToIndex(statusIndex, newStatus, treatmentID, treatment);
            
            // Add undo command with previous status
            pushUndoCommand("STATUS_CHANGE", treatmentID, oldStatus);
            
            // Update recent treatments
            recentTreatments.push("STATUS_" + System.currentTimeMillis(), 
                                "Status changed: " + treatmentID + " from " + oldStatus + " to " + newStatus);
            
            // Invalidate caches
            invalidateSearchCache();
            
            saveAllData();
            
            System.out.println("Treatment " + treatmentID + " status updated from " + 
                             oldStatus + " to " + newStatus);
            return true;
        }
        
        System.out.println("Treatment " + treatmentID + " not found for status update");
        return false;
    }
    
    /**
     * Update treatment fields with undo support
     * @param treatmentID The treatment ID to update
     * @param fieldName The field name being updated (notes, type, critical)
     * @param newValue The new value
     * @return true if updated successfully, false otherwise
     */
    public boolean updateTreatment(String treatmentID, String fieldName, Object newValue) {
        Treatment treatment = getTreatmentByID(treatmentID);
        if (treatment == null) {
            System.out.println("Treatment " + treatmentID + " not found");
            return false;
        }
        
        // Create a copy of the current treatment for undo
        Treatment oldTreatment = createTreatmentCopy(treatment);
        
        // Apply the update
        boolean updated = false;
        switch (fieldName.toLowerCase()) {
            case "notes":
                treatment.setNotes((String) newValue);
                updated = true;
                break;
            case "type":
                treatment.setType((String) newValue);
                updated = true;
                break;
            case "critical":
                treatment.setCritical((Boolean) newValue);
                updated = true;
                break;
            default:
                System.out.println("Unknown field: " + fieldName);
                return false;
        }
        
        if (updated) {
            // Push undo command
            pushUndoCommand("UPDATE", treatmentID, oldTreatment);
            
            // Update recent treatments
            recentTreatments.push("UPDATE_" + System.currentTimeMillis(), 
                               "Updated " + fieldName + " for treatment " + treatmentID + 
                               " (Patient: " + (treatment.getPatient() != null ? treatment.getPatient().getName() : "N/A") + ")");
            
            // Save data
            saveAllData();
            
            System.out.println("Treatment " + treatmentID + " " + fieldName + " updated successfully");
            return true;
        }
        
        return false;
    }

    

    public boolean saveAllData() {
        boolean success = true;
        success &= treatmentDAO.saveToFile(treatments);
        success &= treatmentDAO.saveRecentActivities(recentTreatments);
        return success;
    }

    public boolean loadAllData() {
        treatments = treatmentDAO.retrieveFromFile();
        recentTreatments = treatmentDAO.retrieveRecentActivities();

        buildHashIndices();

        rebuildQueues();
        return true;
    }

    private void rebuildQueues() {
        emergencyQueue.clear();
        regularQueue.clear();

        for (Treatment treatment : treatments) {
            if ("SCHEDULED".equals(treatment.getStatus()) ||
                    "IN_PROGRESS".equals(treatment.getStatus())) {
                if (treatment.isCritical()) {
                    emergencyQueue.offer(treatment.getTreatmentID(), treatment);
                } else {
                    regularQueue.offer(treatment.getTreatmentID(), treatment);
                }
            }
        }
    }

    /**
     * Create a new treatment from an existing consultation
     * @param consultationID ID of the consultation to base the treatment on
     * @param treatmentType Type of treatment (OUTPATIENT, INPATIENT etc.)
     * @param isCritical Whether the treatment is critical or not
     * @param notes Additional treatment notes
     * @return The created Treatment object
     */
    public Treatment createTreatment(String consultationID, String treatmentType, boolean isCritical, String notes) {
        Consultation consultation = consultationController.getConsultation(consultationID);
        if (consultation == null) {
            throw new IllegalArgumentException("Consultation " + consultationID +" not found.");
        }
        // Generate new Treatment ID
        String treatmentID = IDGenerator.generateTreatmentID();

        // Get patient and doctor from consultation
        Patient patient = consultation.getPatient();
        Doctor doctor = consultation.getDoctor();

        // Create treatment
        Treatment treatment = new Treatment (
                treatmentID,
                consultationID,
                patient,
                doctor,
                LocalDateTime.now(),
                notes,
                isCritical
        );

        treatment.setType(treatmentType);

        // Add to the system
        addTreatment(treatment);

        return treatment;

    }

    /**
     * Add a new treatment
     * @param treatment Treatment object to add
     */
    public void addTreatment(Treatment treatment) {
        String treatmentID = treatment.getTreatmentID();
        // Store in main collection using map functionality
        treatments.put(treatmentID, treatment);

        // Add to appropriate queue
        if (treatment.isCritical()) {
            emergencyQueue.offer(treatmentID, treatment);
        } else {
            regularQueue.offer(treatmentID, treatment);
        }

        updateIndicesForAddition(treatment);

        // Track recent treatments using stack functionality
        recentTreatments.push("OP_" + System.currentTimeMillis(), 
                            "Added treatment: " + treatmentID);

        // Add undo command
        pushUndoCommand("ADD", treatmentID, null);

        // Invalidate caches
        invalidateSearchCache();

        saveAllData();
        IDGenerator.saveCounters("counter.dat");

        System.out.println("Treatment " + treatmentID + " added successfully.");
    }

    /**
     * Get available consultations that don't have treatments yet
     * @return OrderedMap of available consultations
     */
    public OrderedMap<String, Consultation> getConsultationsWithoutTreatment() {
        Consultation[] availableArr = consultationController.getAllConsultations();

        OrderedMap<String, Consultation> available = new OrderedMap<>();

        for (Consultation consultation : availableArr) {
            if (consultation == null) {
                // Skip null consultations
                continue;
            }
            String consultId = consultation.getConsultationId();

            boolean hasExistingTreatment = false;
            for (Treatment treatment : treatments) {
                if (treatment == null) continue;
                String tConsultId = treatment.getConsultationID();
                // defensive null checks
                if (tConsultId != null && consultId != null && tConsultId.equals(consultId)) {
                    hasExistingTreatment = true;
                    System.out.println("Consultation " + consultId + " already has treatment " + treatment.getTreatmentID());
                    break;
                }
            }

            if (!hasExistingTreatment) {
                // defensive: ensure consultId not null
                if (consultId == null) {
                    System.out.println("Cannot add consultation with null id, skipping");
                } else {
                    available.put(consultId, consultation);
                }
            }
        }

        return available;
    }

    /**
     * Remove a treatment by ID using Map functionality
     * @param treatmentID ID of the treatment to remove
     * @return true if treatment was removed successfully, false otherwise
     */
    public boolean removeTreatment(String treatmentID) {
        Treatment removed = treatments.remove(treatmentID);
        if (removed != null) {
            // Store copy for undo before modifying
            Treatment treatmentCopy = createTreatmentCopy(removed);
            
            emergencyQueue.remove(treatmentID);
            regularQueue.remove(treatmentID);

            if (removed.hasPrescription()) {
                prescriptions.remove(removed.getPrescription().getPrescriptionID());
            }

            // Update hash indices
            updateIndicesForRemoval(removed);

            // Add undo command with full treatment data
            pushUndoCommand("DELETE", treatmentID, treatmentCopy);

            // Invalidate caches
            invalidateSearchCache();

            saveAllData();
            System.out.println("Treatment " + treatmentID + " removed successfully.");
            return true;
        }
        System.out.println("Treatment " + treatmentID + " not found.");
        return false;
    }

    /**
     * Retrieve a treatment by ID using Map functionality
     * @param treatmentID ID of the treatment to retrieve
     * @return Treatment object if found, null otherwise
     */
    public Treatment getTreatmentByID(String treatmentID) {
        Treatment treatment = treatments.get(treatmentID);
        return treatment;
    }

    /**
     * Retrieve all treatments 
     * @return OrderedMap of all treatments
     */
    public OrderedMap<String, Treatment> getAllTreatments() {
        return treatments;
    }

    /**
     * Get prescription for a treatment
     * @param treatmentID ID of the treatment
     * @return Prescription object if exists, null otherwise
     */
    public Prescription getPrescriptionForTreatment(String treatmentID) {
        Treatment treatment = treatments.get(treatmentID);
        if (treatment != null && treatment.hasPrescription()) {
            return treatment.getPrescription();
        }
        return null;
    }

    /**
     * Process next emergency treatment using Queue functionality
     * @return Treatment object if processed, null if no emergency treatments
     */
    public Treatment processNextEmergency() {
        Treatment next = emergencyQueue.poll();
        if (next != null) {
            next.complete(); // Mark treatment as completed
            updateAppointmentStatusForTreatment(next);
            recentTreatments.push("OP_" + System.currentTimeMillis(),
                    "Processed emergency treatment: " + next.getTreatmentID());
            System.out.println("Processed emergency treatment: " + next.getTreatmentID());
            saveAllData();
        } else {
            System.out.println("No emergency treatments in queue.");
        }

        return next;

    }

    /**
     * Process next regular treatment using Queue functionality
     * @return Treatment object if processed, null if no regular treatments 
     */
    public Treatment processNextRegular() {
        Treatment next = regularQueue.poll();
        if (next != null) {
            next.complete(); // Mark treatment as completed
            updateAppointmentStatusForTreatment(next);
            recentTreatments.push("OP_" + System.currentTimeMillis(),
                                  "Processed regular treatment: " + next.getTreatmentID());
            System.out.println("Processed regular treatment: " + next.getTreatmentID());
        } else {
            System.out.println("No regular treatments in queue.");
        }

        saveAllData();
        return next;

    }

    private void updateAppointmentStatusForTreatment(Treatment treatment) {
        try {
            String consultationId = treatment.getConsultationID();
            if (consultationId != null) {
                // Get the consultation associated with this treatment
                Consultation consultation = consultationController.getConsultation(consultationId);
                if (consultation != null && consultation.getAppointment() != null) {
                    String appointmentId = consultation.getAppointment().getAppointmentId();
                    
                    // Update appointment status to "Completed"
                    boolean updated = consultationController.updateAppointmentStatus(appointmentId, "Completed");
                    
                    if (updated) {
                        System.out.println("Updated appointment " + appointmentId + " status to 'Completed'");
                    } else {
                        System.out.println("Warning: Could not update appointment status for appointment " + appointmentId);
                    }
                } else {
                    System.out.println("Warning: No associated appointment found for treatment " + treatment.getTreatmentID());
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating appointment status for treatment " + treatment.getTreatmentID() + ": " + e.getMessage());
        }
    }

    /**
     * Get recent treatments using Stack functionality (LIFO)
     * @return OrderedMap of recent treatments
     */
    public OrderedMap<String, String> getRecentTreatments() {
        return recentTreatments;
    }

    /**
     * Clear all recent treatments
     * @return true if cleared successfully, false if no recent treatments
     */
    public boolean clearRecentTreatments() {
        if (recentTreatments.isEmpty()) {
            return false;
        }
        recentTreatments.clear();
        
        saveAllData();
        return true;


    }

    /**
     * Get all patients who have existing treatments
     * @return OrderedMap of patients with treatments
     */
    public OrderedMap<String, Patient> getPatientsWithTreatments() {
        OrderedMap<String, Patient> patientsWithTreatments = new OrderedMap<>();
        // Iterate through all treatments and collect unique patient IDs
        for (Treatment treatment : treatments){
            Patient patient = treatment.getPatient();
            if (patient != null) {
                // Add patient if not already present in the collection
                if (!patientsWithTreatments.containsKey(patient.getPatientId())) {
                    patientsWithTreatments.put(patient.getPatientId(), patient);
                }
            }
        }
        return patientsWithTreatments;
    }

    /**
     * Get patients with active treatments only
     * @return OrderedMap of patients with active treatments
     */
    public OrderedMap<String, Patient> getPatientsWithActiveTreatments() {
        OrderedMap<String, Patient> activePatients = new OrderedMap<>();
        for (Treatment treatment : treatments) {
            if (("SCHEDULED".equals(treatment.getStatus()) || 
                 "IN_PROGRESS".equals(treatment.getStatus())) && 
                 treatment.getPatient() != null) {
                Patient patient = treatment.getPatient();
                if (!activePatients.containsKey(patient.getPatientId())) {
                    activePatients.put(patient.getPatientId(), patient);
                }
            }
        }
        return activePatients;
    }

    /**
     * Get patients with critical treatments
     * @return OrderedMap of patients with critical treatments
     */
    public OrderedMap<String, Patient> getPatientsWithCriticalTreatments() {
        OrderedMap<String, Patient> criticalPatients = new OrderedMap<>();
        for (Treatment treatment : treatments) {
            if (treatment.isCritical() && treatment.getPatient() != null) {
                Patient patient = treatment.getPatient();
                if (!criticalPatients.containsKey(patient.getPatientId())) {
                    criticalPatients.put(patient.getPatientId(), patient);
                }
            }
        }
        return criticalPatients;
    }

    
    /**
     * Add procedure to existing treatment
     * @param treatmentID ID of the treatment to add the procedure to
     * @param procedure Procedure object to add
     * @return true if procedure was added successfully, false otherwise
     */
    public boolean addProcedure(String treatmentID, Procedure procedure) {
        Treatment treatment = treatments.get(treatmentID);
        if (treatment != null) {
            treatment.addProcedure(procedure);
            System.out.println("Procedure " + procedure.getProcedureName() + 
                               " added to treatment " + treatmentID);
            return true;
        }
        System.out.println("Treatment " + treatmentID + " not found.");
        return false;
    }

    /**
     * Check if a treatment exists using Map functionality
     */
    public boolean treatmentExists(String treatmentID) {
        return treatments.containsKey(treatmentID);
    }

    /**
     * Get a list of treatments for a specific patient
     * @param patient Patient object to filter treatments for
     * @return OrderedMap of treatments for the patient
     */
    public OrderedMap<String, Treatment> getTreatmentsByPatient(Patient patient) {
        if (patient == null) return new OrderedMap<>();

        String patientId = patient.getPatientId();

        // O(1) hash lookup using patientIndex
        OrderedMap<String, Treatment> result = patientIndex.get(patientId);
        return result != null ? result : new OrderedMap<>();
    }


    /**
     * Get treatments by status
     * @param status The status to filter by
     * @return OrderedMap of treatments with the specified status
     */
    public OrderedMap<String, Treatment> getTreatmentsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) return new OrderedMap<>();

        // O(1) hash lookup using statusIndex
        OrderedMap<String, Treatment> result = statusIndex.get(status);
        return result != null ? result : new OrderedMap<>();
    }

    /**
     * Cached search of treatments by notes keyword with hash-based memoization
     * @param keyword The keyword to search for in treatment notes
     * @return OrderedMap of treatments containing the keyword in their notes
     */
    public OrderedMap<String, Treatment> searchTreatmentsByNotes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return new OrderedMap<>();

        String cacheKey = "notes_search_" + keyword.toLowerCase();

        // O(1) cache lookup
        OrderedMap<String, Treatment> cachedResult = searchCache.get(cacheKey);
        if (cachedResult != null) {
            // Cache hit for keyword
            return cachedResult;
        }

        Treatment dummyTreatment = new Treatment("", "", (Patient)null, (Doctor)null, LocalDateTime.now(), "", false);
        dummyTreatment.setNotes(keyword);

        // Cache miss, perform search
        OrderedMap<String, Treatment> result = treatments.filter(dummyTreatment, (t1, t2) -> {
            if (t1.getNotes() != null && t1.getNotes().toLowerCase().contains(t2.getNotes().toLowerCase())) {
                return 0; // Match found
            }
            return 1; // No match
        });

        // Cache the result for future lookups
        searchCache.put(cacheKey, result);
        return result;
    }

    /**
     * Search treatments by date range
     * @param startDate
     * @param endDate
     * @return OrderedMap of treatments within the date range
     */
    public OrderedMap<String, Treatment> getTreatmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) return new OrderedMap<>();

        // Sort by date first for efficient range search
        treatments.sort(Comparator.comparing(Treatment::getTreatmentDate));
        
        // Create dummy treatments for range bounds
        Treatment startDummy = new Treatment("", "", (Patient)null, (Doctor)null, startDate, "", false);
        Treatment endDummy = new Treatment("", "", (Patient)null, (Doctor)null, endDate, "", false);
        
        // Use rangeSearch with proper comparator
        return treatments.rangeSearch(startDummy, endDummy, 
            Comparator.comparing(Treatment::getTreatmentDate));
    }

    /**
     * Binary search for treatments by procedure
     * Requires treatments to be sorted by procedures
     * @param procedureName
     * @return OrderedMap of treatments containing the specified procedure
     */
    public OrderedMap<String, Treatment> searchTreatmentsByProcedure(String procedureName) {
        if (procedureName == null || procedureName.isEmpty()) return new OrderedMap<>();

        // O(1) hash lookup using procedureIndex
        OrderedMap<String, Treatment> result = procedureIndex.get(procedureName);
        return result != null ? result : new OrderedMap<>();
    }

    /**
     * Helper method to check if treatment has procedure
     */
    private boolean treatmentHasProcedure(Treatment treatment, String procedureName) {
        if (treatment.getProcedures() == null) return false;

        for (Procedure procedure : treatment.getProcedures()) {
            if (procedure != null &&
                procedure.getProcedureName().toLowerCase().contains(procedureName.toLowerCase())) {
                    return true;
            }
        }
        return false;
    }

    /**
     * Search treatments by patient name
     * @param patientName The patient name to search for
     * @return OrderedMap of treatments for patients with matching names
     */
    public OrderedMap<String, Treatment> searchTreatmentsByPatientName(String patientName) {
        if (patientName == null || patientName.trim().isEmpty()) return new OrderedMap<>();

        // O(1) hash lookup using patientNameIndex
        OrderedMap<String, Treatment> result = patientNameIndex.get(patientName.toLowerCase());
        return result != null ? result : new OrderedMap<>();
    }

    /**
     * Sort treatments by date
     * @param ascending true for ascending order, false for descending
     * @return OrderedMap of sorted treatments
     */
    public OrderedMap<String, Treatment> sortTreatmentsByDate(boolean ascending) {
        OrderedMap<String, Treatment> sortedTreatments = new OrderedMap<>();
        
        // Copy all treatments to new OrderedMap
        for (Treatment treatment : treatments) {
            sortedTreatments.put(treatment.getTreatmentID(), treatment);
        }
        
        // Sort using OrderedMap sort method
        if (ascending) {
            sortedTreatments.sort(Comparator.comparing(Treatment::getTreatmentDate));
        } else {
            sortedTreatments.sort(Comparator.comparing(Treatment::getTreatmentDate).reversed());
        }
        
        return sortedTreatments;
    }

    /**
     * Sort treatments by patient name
     * @param ascending true for ascending order, false for descending
     * @return OrderedMap of sorted treatments
     */
    public OrderedMap<String, Treatment> sortTreatmentsByPatientName(boolean ascending) {
        OrderedMap<String, Treatment> sortedTreatments = new OrderedMap<>();
        
        // Copy all treatments to new OrderedMap
        for (Treatment treatment : treatments) {
            sortedTreatments.put(treatment.getTreatmentID(), treatment);
        }
        
        // Sort using OrderedMap sort method
        if (ascending) {
            sortedTreatments.sort(Comparator.comparing(t -> 
                t.getPatient() != null ? t.getPatient().getName() : ""));
        } else {
            sortedTreatments.sort(Comparator.comparing((Treatment t) -> 
                t.getPatient() != null ? t.getPatient().getName() : "").reversed());
        }
        
        return sortedTreatments;
    }

    /**
     * Sort treatments by status
     * @param ascending true for ascending order, false for descending
     * @return OrderedMap of sorted treatments
     */
    public OrderedMap<String, Treatment> sortTreatmentsByStatus(boolean ascending) {
        OrderedMap<String, Treatment> sortedTreatments = new OrderedMap<>();
        
        // Copy all treatments to new OrderedMap
        for (Treatment treatment : treatments) {
            sortedTreatments.put(treatment.getTreatmentID(), treatment);
        }
        
        // Sort using OrderedMap sort method
        if (ascending) {
            sortedTreatments.sort(Comparator.comparing(Treatment::getStatus));
        } else {
            sortedTreatments.sort(Comparator.comparing(Treatment::getStatus).reversed());
        }
        
        return sortedTreatments;
    }

    /**
     * Sort treatments by critical priority
     * @param criticalFirst true to show critical treatments first, false for regular first
     * @return OrderedMap of sorted treatments
     */
    public OrderedMap<String, Treatment> sortTreatmentsByCriticalPriority(boolean criticalFirst) {
        OrderedMap<String, Treatment> sortedTreatments = new OrderedMap<>();
        
        // Copy all treatments to new OrderedMap
        for (Treatment treatment : treatments) {
            sortedTreatments.put(treatment.getTreatmentID(), treatment);
        }
        
        // Sort using OrderedMap sort method
        if (criticalFirst) {
            // Critical treatments first (true > false)
            sortedTreatments.sort(Comparator.comparing((Treatment t) -> !t.isCritical()));
        } else {
            // Regular treatments first (false > true)
            sortedTreatments.sort(Comparator.comparing(Treatment::isCritical));
        }
        
        return sortedTreatments;
    }

    /**
     * Add prescription to a treatment
     * @param treatmentID ID of the treatment to add the prescription to
     * @param prescription Prescription object to add
     * @return true if prescription was added successfully, false otherwise
     */
    public boolean addPrescriptionToTreatment(String treatmentID, Prescription prescription) {
        Treatment treatment = treatments.get(treatmentID);
        if (treatment != null) {
            treatment.setPrescription(prescription);
            prescriptions.put(prescription.getPrescriptionID(), prescription);
            prescriptionController.enqueuePrescription(prescription);
            
            saveAllData();
            return true;
        }
        return false;
    }

    /**
     * Get all available procedures from the DAO
     * @return OrderedMap of all procedures
     */
    public OrderedMap<String, Procedure> getAllAvailableProcedures() {
        return procedureDAO.retrieveFromFile();
    }

    /**
     * Get a specific procedure by ID
     * @param procedureID The procedure ID to retrieve
     * @return Procedure object if found, null otherwise
     */
    public Procedure getProcedureByID(String procedureID) {
        OrderedMap<String, Procedure> procedures = procedureDAO.retrieveFromFile();
        return procedures.get(procedureID);
    }

    /**
     * Save report content to a file
     * @param reportContent The report content to save
     * @param reportName The name of the report (without extension)
     * @return true if saved successfully, false otherwise
     */
    public boolean saveReportToFile(String reportContent, String reportName) {
        try {
            // Create reports directory if it doesn't exist
            java.io.File reportsDir = new java.io.File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            // Generate filename with timestamp
            String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = "reports/" + reportName + "_" + timestamp + ".txt";
            
            // Write to file
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
                writer.println("=".repeat(80));
                writer.println("CLINIC MANAGEMENT SYSTEM - " + reportName.toUpperCase());
                writer.println("Generated: " + java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("=".repeat(80));
                writer.println();
                writer.print(reportContent);
                writer.println();
                writer.println("=".repeat(80));
                writer.println("End of Report");
            }
            
            System.out.println("Report saved successfully!");
            System.out.println("File saved as: " + filename);
            return true;
            
        } catch (java.io.IOException e) {
            System.err.println("Error saving report: " + e.getMessage());
            return false;
        }
    }

    // Simple payment linking consultation, treatment, and prescription costs
    public Payment createSimplePayment(String consultationId, String treatmentId) {
        try {
            // Get consultation and calculate consultation costs
            Consultation consultation = consultationController.getConsultation(consultationId);
            if (consultation == null) {
                System.out.println("Consultation not found");
                return null;
            }

            // Get treatment and calculate treatment costs
            Treatment treatment = getTreatmentByID(treatmentId);
            if (treatment == null) {
                System.out.println("Treatment not found");
                return null;
            }

            // Calculate total costs
            double consultationCost = calculateConsultationCost(consultation);
            double treatmentCost = calculateTreatmentCost(treatment);
            double prescriptionCost = calculatePrescriptionCost(treatment.getPrescription());
            double totalAmount = consultationCost + treatmentCost + prescriptionCost;

            // Create payment breakdown
            OrderedMap<String, Double> breakdown = new OrderedMap<>();
            breakdown.put("Consultation Services", consultationCost);
            breakdown.put("Treatment Procedures", treatmentCost);
            breakdown.put("Prescription Medicines", prescriptionCost);

            // Generate payment ID
            String paymentId = "PAY" + System.currentTimeMillis();

            // Create payment
            Payment payment = new Payment(paymentId, consultationId, totalAmount, 0.0, "PENDING", breakdown);
            paymentMaintenance.addPayment(payment);

            // Link payment to consultation
            consultation.setPayment(payment);
            consultationController.addConsultation(consultation);

            return payment;
        } catch (Exception e) {
            System.err.println("Error creating payment: " + e.getMessage());
            return null;
        }
    }

    private double calculateConsultationCost(Consultation consultation) {
        double total = 0.0;
        if (consultation.getServicesUsed() != null) {
            for (ConsultationService service : consultation.getServicesUsed().toArray(new ConsultationService[0])) {
                total += service.getServiceCharge();
            }
        }
        return total;
    }

    private double calculateTreatmentCost(Treatment treatment) {
        double total = 0.0;
        if (treatment.getProcedures() != null) {
            for (Procedure procedure : treatment.getProcedures().toArray(new Procedure[0])) {
                total += procedure.getCost();
            }
        }
        return total;
    }

    private double calculatePrescriptionCost(Prescription prescription) {
        if (prescription == null) {
            return 0.0;
        }
        double total = 0.0;
        if (prescription.getMedicines() != null) {
            for (PrescribedMedicine prescribedMedicine : prescription.getMedicines().toArray(new PrescribedMedicine[0])) {
                total += prescribedMedicine.calculateSubtotal();
            }
        }
        return total;
    }

    public String processPayment(String paymentId, double paidAmount) {
        try {
            Payment payment = paymentMaintenance.getPayment(paymentId);
            
            if (payment == null) {
                return "Payment not found";
            }

            payment.setPaidAmount(paidAmount);
            
            if (paidAmount >= payment.getTotalAmount()) {
                payment.setPaymentStatus("COMPLETED");
            } else if (paidAmount > 0) {
                payment.setPaymentStatus("PARTIAL");
            } else {
                payment.setPaymentStatus("PENDING");
            }

            paymentMaintenance.addPayment(payment);
            return "Payment processed successfully. Status: " + payment.getPaymentStatus();
        } catch (Exception e) {
            return "Error processing payment: " + e.getMessage();
        }
    }

    public Procedure[] getAvailableProcedures() {
        if (procedureDAO == null) {
            return new Procedure[0];
        }
        OrderedMap<String, Procedure> procedureMap = procedureDAO.retrieveFromFile();
        return procedureMap.toArray(new Procedure[0]);
    }

}