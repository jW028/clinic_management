/**
 * @author Goh Yu Jie
 */

package control;

import adt.OrderedMap;
import dao.*;
import entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import utility.IDGenerator;

public class PharmacyMaintenance {
    private final OrderedMap<String, Medicine> medicineMap;
    private final OrderedMap<String, Prescription> pendingPrescriptionMap;
    private OrderedMap<String, Prescription> processedPrescriptionMap;
    private final OrderedMap<String, Transaction> transactionMap;
    private final OrderedMap<String, Treatment> treatmentMap;
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final PrescriptionDAO pendingPrescriptionDAO = new PrescriptionDAO(1);
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final PrescriptionDAO processedPrescriptionDAO = new PrescriptionDAO(2);
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_REJECTED = "REJECTED";

    private OrderedMap<String, OrderedMap<String, Medicine>> medicineIndex = new OrderedMap<>();

    public PharmacyMaintenance() {
        this.medicineMap = medicineDAO.retrieveFromFile();
        this.pendingPrescriptionMap = pendingPrescriptionDAO.retrieveFromFile();
        this.processedPrescriptionMap = processedPrescriptionDAO.retrieveFromFile();
        this.transactionMap = transactionDAO.retrieveFromFile();
        this.treatmentMap = treatmentDAO.retrieveFromFile();
        IDGenerator.loadCounter("counter.dat");
        undoStack = new OrderedMap<>();
    }

    private OrderedMap<String, UndoCommand> undoStack;
    private OrderedMap<String, String> recentActions;
    private static final int MAX_UNDO_SIZE = 20;

    private static class UndoCommand {
        private String actionType;
        private String actionID;
        private Object data;
        private LocalDateTime timestamp;

        public UndoCommand(String actionType, String actionID, Object data) {
            this.actionType = actionType;
            this.actionID = actionID;
            this.data = data;
            this.timestamp = LocalDateTime.now();
        }

        public String getActionType() {
            return actionType;
        }

        public String getActionID() {
            return actionID;
        }

        public Object getData() {
            return data;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    /*
     * Getters for the medicine map
     * @return medicineMap
     */
    public OrderedMap<String, Medicine> getMedicineMap() {
        return medicineMap;
    }


    private void pushUndoCommand(String actionType, String actionID, Object data) {
        try {
            if (undoStack.size() >= MAX_UNDO_SIZE) {
                undoStack.removeAt(0);
            }
            UndoCommand command = new UndoCommand(actionType, actionID, data);
            undoStack.push("undo_" + System.currentTimeMillis(), command);
        } catch (Exception e) {
            System.out.println("Error pushing undo command: " + e.getMessage());
        }
    }

    public boolean undoLastAction() {
        try {
            if (undoStack.isEmpty()) {
                System.out.println("No actions to undo.");
                return false;
            }

            UndoCommand lastCommand = undoStack.top();

            if (lastCommand == null) {
                System.out.println("Invalid undo command.");
                return false;
            }

            boolean success = false;

            switch (lastCommand.getActionType()){
                case "ADD":
                    success = undoAddOperation(lastCommand);
                    break;
                case "UPDATE":
                    success = undoUpdateOperation(lastCommand);
                    break;
                case "REMOVE":
                    success = undoDeleteOperation(lastCommand);
                    break;
                default:
                    System.out.println("Unknown action type: " + lastCommand.getActionType());
                    break;
            }

            if (success) {
                undoStack.pop();
                System.out.println("Undo successful: " + lastCommand.getActionType() + " " + lastCommand.getActionID());

                String undoEntry = "[UNDO] " + lastCommand.getActionType() + " - " +
                        lastCommand.getActionID() + " at " +
                        java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                recentActions.push("UNDO_" + lastCommand.getActionID(), undoEntry);
            }

            return success;
        } catch (Exception e) {
            System.out.println("Error during undo: " + e.getMessage());
            return false;
        }
    }

    private boolean undoAddOperation(UndoCommand command){
        try {
            String medID = command.getActionID();
            Medicine med = medicineMap.get(medID);

            if (med != null) {
                medicineMap.remove(medID);
                removeFromIndex(medicineIndex, med.getName().toLowerCase(), medID);

                System.out.println("Undid addition of medicine ID: " + medID);
                return true;
            } else {
                System.out.println("Medicine ID not found for undo: " + medID);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error undoing add operation: " + e.getMessage());
            return false;
        }
    }

    private boolean undoUpdateOperation(UndoCommand command) {
        try {
            if (command.getData() instanceof Medicine) {
                Medicine oldMed = (Medicine) command.getData();
                String medID = command.getActionID();

                Medicine currentMed = medicineMap.get(medID);
                if (currentMed != null) {
                    removeFromIndex(medicineIndex, currentMed.getName().toLowerCase(), medID);
                }

                medicineMap.put(medID, oldMed);
                addToIndex(medicineIndex, oldMed.getName().toLowerCase(), oldMed, medID);

                System.out.println("Undid UPDATE: Restored medicine " + medID + " to previous state.");
                return true;
            } else {
                System.out.println("Invalid data for undoing update operation.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error undoing update operation: " + e.getMessage());
            return false;
        }
    }

    private boolean undoDeleteOperation(UndoCommand command) {
        try {
            if (command.getData() instanceof Medicine) {
                Medicine deletedMed = (Medicine) command.getData();
                String medID = command.getActionID();

                medicineMap.put(medID, deletedMed);
                addToIndex(medicineIndex, deletedMed.getName().toLowerCase(), deletedMed, medID);

                System.out.println("Undid deletion: Restored medicine " + medID);
                return true;
            } else {
                System.out.println("Invalid data for undoing delete operation.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error undoing delete operation: " + e.getMessage());
            return false;
        }
    }

    public String getUndoInfo() {
        if (undoStack.isEmpty()){
            return "No actions to undo.";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = undoStack.size() - 1; i >= 0; i--) {
            UndoCommand cmd = undoStack.get(i);
            sb.append(String.format("%d. %s - %s at %s\n", undoStack.size() - i,
                    cmd.getActionType(),
                    cmd.getActionID(),
                    cmd.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        }
        return sb.toString();
    }

    public void clearUndoHistory() {
        undoStack.clear();
        System.out.println("Undo history cleared.");
    }

    /*
     * Add a new medicine to the medicine map
     * @param newMedicine the new medicine to be added
     */
    public void addMedicine(Medicine newMedicine) {
        medicineMap.put(newMedicine.getId(), newMedicine);
        updateIndicesForAddition(newMedicine);
        recentActions.push("ADD_" + newMedicine.getId(),
                "[ADD] Medicine ID: " + newMedicine.getId() + " at " +
                        java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        pushUndoCommand("ADD", newMedicine.getId(), null);
        medicineDAO.saveToFile(medicineMap);
        IDGenerator.saveCounters("counter.dat");
    }

    /*
     * Get a medicine by its ID
     * @param id the ID of the medicine
     * @return the medicine with the given ID, or null if not found
     */
    public Medicine getMedicineById(String id) {
        return medicineMap.get(id);
    }

    private Medicine createMedicineCopy(Medicine original) {
        try {
            Medicine copy = new Medicine(
                    original.getId(),
                    original.getName(),
                    original.getQuantity(),
                    original.getPrice(),
                    original.getDescription()
            );

            return copy;
        } catch (Exception e){
            System.out.println("Error creating medicine copy: " + e.getMessage());
            return original;
        }
    }

    /* Update a specific field of a medicine
     * @param medID the ID of the medicine to be updated
     * @param choice the field to be updated (1: name, 2: quantity, 3: price, 4: description)
     * @param newValue the new value for the selected field
     */
    public void updateMedicineField(String medID, int choice, String newValue) {
        Medicine med = medicineMap.get(medID);
        boolean updated = false;
        Medicine oldMed = createMedicineCopy(med);

        try {
            switch (choice) {
                case 1:
                    if (newValue != null && newValue.isBlank()) {
                        throw new IllegalArgumentException("Medicine name cannot be empty.");
                    }
                    med.setName(newValue);
                    updated = true;
                    break;
                case 2:
                    int newQuantity = Integer.parseInt(newValue);
                    if (newQuantity < 0) {
                        throw new IllegalArgumentException("Quantity cannot be negative.");
                    }
                    med.setQuantity(newQuantity);
                    updated = true;
                    break;
                case 3:
                    double newPrice = Double.parseDouble(newValue);
                    if (newPrice < 0) {
                        throw new IllegalArgumentException("Price cannot be negative.");
                    }
                    med.setPrice(newPrice);
                    updated = true;
                    break;
                case 4:
                    if (newValue != null && newValue.isBlank()) {
                        throw new IllegalArgumentException("Description cannot be empty.");
                    }
                    med.setDescription(newValue);
                    updated = true;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field choice.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error updating medicine: " + e.getMessage());
        }

        if (updated) {
            pushUndoCommand("UPDATE", medID, oldMed);

            recentActions.push("UPDATE_" + medID,
                    "[UPDATE] Medicine ID: " + medID + " at " +
                            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }

        medicineDAO.saveToFile(medicineMap);
    }

    /*
     * Remove a medicine from the medicine map
     * @param medID the ID of the medicine to be removed
     */
    public void removeMedicine(String medID){
        Medicine removed = medicineMap.remove(medID);

        if(removed != null){
            Medicine medicineCopy = createMedicineCopy(removed);
            updateIndicesForRemoval(removed);
            pushUndoCommand("DELETE", medID, medicineCopy);
        }
        medicineDAO.saveToFile(medicineMap);
    }


    /*
     * Enqueue a new prescription to the pending prescription queue
     * @param prescription the prescription to be enqueued
     */
    public void enqueuePrescription(Prescription prescription) {
        pendingPrescriptionMap.offer(prescription.getPrescriptionID(), prescription);
        pendingPrescriptionDAO.saveToFile(pendingPrescriptionMap);
        IDGenerator.saveCounters("counter.dat");
    }

    /*
     * Getters for the pending prescription queue
     * @return pendingPrescriptionQueue
     */
    public OrderedMap<String, Prescription> getPendingPrescriptionMap() {
        return pendingPrescriptionMap;
    }

    /*
     * Getters for the processed prescription list
     * @return processedPrescriptionList
     */
    public OrderedMap<String, Prescription> getCompletedPrescriptionMap() {
        return processedPrescriptionMap.filter( new Prescription(null,null),
                (p1, p2) -> p1.getStatus().equals(STATUS_COMPLETED) ? 0 : 1);
    }

    /*
     * Get a prescription by its ID from either the pending or processed prescriptions
     * @param id the ID of the prescription
     * @return the prescription with the given ID, or null if not found
     */
    public Prescription getPrescriptionById(String id) {
        Prescription presc = pendingPrescriptionMap.get(id);
        if (presc == null) {
            presc = processedPrescriptionMap.get(id);
        }
        return presc;
    }

    /*
     * Update a specific field of a prescribed medicine within a prescription
     * @param pm the prescribed medicine to be updated
     * @param newIntValue the new integer value for the selected field (if applicable)
     * @param newStringValue the new string value for the selected field (if applicable)
     * @param choice the field to be updated (1: quantity, 2: dosage, 3: frequency, 4: description)
     * @return true if the update was successful, false otherwise
     */
    public boolean updatePrescribedMedicine(PrescribedMedicine pm, int newIntValue, String newStringValue, int choice) {

        switch (choice) {
            case 1:
                pm.setQuantity(newIntValue);
                break;
            case 2:
                pm.setDosage(newStringValue);
                break;
            case 3:
                pm.setFrequency(newStringValue);
                break;
            case 4:
                pm.setDescription(newStringValue);
                break;
            default:
                System.out.println("Invalid choice.");
                return false;
        }
        return true;
    }

    /*
     * Save the current state of the prescription maps to their respective files
     * @param prescription the prescription to be saved
     */
    public void savePrescriptionFile(Prescription prescription) {
        try {
            pendingPrescriptionDAO.saveToFile(pendingPrescriptionMap);
        } catch (Exception e) {
            System.out.println("Error saving prescription file: " + e.getMessage());
        }
    }

    /*
     * Dequeue a prescription from the pending prescription queue
     * @return the dequeued prescription, or null if the queue is empty
     */
    public Prescription dequeuePrescription() {
        return pendingPrescriptionMap.poll();
    }

    /*
     * Remove a prescription from either the pending or processed prescriptions
     * @param prescID the ID of the prescription to be removed
     * @return true if the removal was successful, false otherwise
     */
    public boolean removePrescription(String prescID) {
        if (pendingPrescriptionMap.containsKey(prescID)) {
            pendingPrescriptionMap.remove(prescID);
            pendingPrescriptionDAO.saveToFile(pendingPrescriptionMap);
        } else if (processedPrescriptionMap.containsKey(prescID)) {
            processedPrescriptionMap.remove(prescID);
            processedPrescriptionDAO.saveToFile(processedPrescriptionMap);
        } else {
            return false;
        }
        return true;
    }

    /*
     * Remove a prescribed medicine from a prescription
     * @param prescription the prescription containing the prescribed medicine
     * @param pm the prescribed medicine to be removed
     * @return true if the removal was successful, false otherwise
     */
    public boolean removePrescribedMedicine(Prescription prescription, PrescribedMedicine pm){
        PrescribedMedicine removed = prescription.getMedicines().remove(pm.getMedicineID());

        if (removed != null) {
            double totalPrice = prescription.getTotalPrice();
            totalPrice -= removed.calculateSubtotal();
            prescription.setTotalPrice(totalPrice);
            savePrescriptionFile(prescription);
            return true;
        }
        return false;
    }

    /*
     * Process a prescription by checking medicine availability, updating stock, and recording the transaction
     * @param prescription the prescription to be processed
     * @return true if the prescription was successfully processed, false otherwise
     */
    public boolean processPrescription(Prescription prescription) {
        boolean allProcessed = true;
        Transaction transaction = new Transaction(getPatientIdFromPrescription(prescription));

        for (PrescribedMedicine pm : prescription.getMedicines()){
            Medicine med = medicineMap.get(pm.getMedicineID());
            if (med == null || med.getQuantity() < pm.getQuantity()) {
                allProcessed = false;
                break;
            }
        }

        if (!allProcessed) {
            prescription.setStatus(STATUS_REJECTED);
        } else {
            for (PrescribedMedicine pm : prescription.getMedicines()){
                Medicine med = medicineMap.get(pm.getMedicineID());
                med.setQuantity(med.getQuantity() - pm.getQuantity());
                transaction.addMedicine(pm);
                addTransaction(transaction.getTransactionID(),transaction);
            }

            prescription.setStatus(STATUS_COMPLETED);
        }

        if (pendingPrescriptionMap.containsKey(prescription.getPrescriptionID())) {
            pendingPrescriptionMap.remove(prescription.getPrescriptionID());
        }

        processedPrescriptionMap.put(prescription.getPrescriptionID(), prescription);
        pendingPrescriptionDAO.saveToFile(pendingPrescriptionMap);
        processedPrescriptionDAO.saveToFile(processedPrescriptionMap);
        medicineDAO.saveToFile(medicineMap);
        transactionDAO.saveToFile(transactionMap);
        return allProcessed;
    }

    /*
     * Add a new transaction to the transaction map and save it to file
     * @param transactionID the ID of the transaction
     * @param transaction the transaction to be added
     */
    public void addTransaction(String transactionID, Transaction transaction) {
        transactionMap.put(transactionID, transaction);
        transactionDAO.saveToFile(transactionMap);
    }

    /*
     * Get the patient ID associated with a prescription
     * @param prescription the prescription for which to get the patient ID
     * @return the patient ID, or null if not found
     */
    public String getPatientIdFromPrescription(Prescription prescription) {
        String treatmentId = prescription.getTreatmentID();
        Treatment treatment = this.treatmentMap.get(treatmentId);
        if (treatment != null) {
            return treatment.getPatient().getPatientId();
        }
        return null; // or throw an exception if not found
    }

    /*
     * Getters for the transaction map
     * @return transactionMap
     */
    public OrderedMap<String, Transaction> getTransactionMap() {
        return transactionMap;
    }


    /*
     * Sort the medicines in the medicine map by their stock quantity in ascending order
     */
    public void sortMedicinesByStock(){
        medicineMap.sort(Comparator.comparingInt(Medicine::getQuantity));
    }

    /*
     * Save the medicine stock report to a text file
     */
    public String saveMedicineStockReport(StringBuilder report, String date) {
        java.io.File reportsDir = new java.io.File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
        String dateStr = date.replace(" ", "_").replace(":", "-").replace("/", "-");
        String filename = String.format("reports/medicine_stock_report_%s.txt", dateStr);
        try (java.io.FileWriter writer = new java.io.FileWriter(filename)) {
            writer.write(report.toString());
        } catch (java.io.IOException e) {
            System.out.println("Failed to save report: " + e.getMessage());
        }
        return filename;
    }

    /*
     * Save the monthly sales report to a text file
     */
    public String saveMonthlySalesReport(StringBuilder report, int year, int month) {
        java.io.File reportsDir = new java.io.File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
        String filename = String.format("reports/monthly_sales_report_%04d-%02d.txt", year, month);
        try (java.io.FileWriter writer = new java.io.FileWriter(filename)) {
            writer.write(report.toString());
        } catch (java.io.IOException e) {
            System.out.println("Failed to save report: " + e.getMessage());
        }
        return filename;
    }

    /*
     * Search for medicines by name (case-insensitive, partial match)
     * @param name the name or partial name of the medicine to search for
     * @return a OrderedMap containing the matching medicines
     */
    public OrderedMap<String, Medicine> searchMedicinesByName(String name) {
        if (name == null || name.isBlank()) {
            return new OrderedMap<>();
        }

        OrderedMap<String, Medicine> result = medicineIndex.get(name.toLowerCase());
        return result != null ? result : new OrderedMap<>();
    }

    private void buildMedicineHashIndex(){
        medicineIndex = new OrderedMap<>();

        for (Medicine med : medicineMap){
            if (med == null) continue;

            if (med.getName() != null && !med.getName().isBlank()){
                String medNameKey = med.getName().toLowerCase();
                addToIndex(medicineIndex, medNameKey, med, med.getId());
            }
        }
    }

    private void addToIndex(OrderedMap<String, OrderedMap<String, Medicine>> index, String key, Medicine med, String medId) {
        OrderedMap<String, Medicine> medicineMap = index.get(key);
        if (medicineMap == null) {
            medicineMap = new OrderedMap<>();
            index.put(key, medicineMap);
        }
        medicineMap.put(medId, med);
    }

    private void updateIndicesForAddition(Medicine medicine) {

        if (medicine != null) {
            String medNameKey = medicine.getName().toLowerCase();
            if (medNameKey != null && !medNameKey.isBlank()) {
                addToIndex(medicineIndex, medNameKey, medicine, medicine.getId());
            }
        }
    }

    private void updateIndicesForRemoval(Medicine medicine) {

        if (medicine != null) {
            String medNameKey = medicine.getName().toLowerCase();
            if (medNameKey != null && !medNameKey.isBlank()) {
                removeFromIndex(medicineIndex, medNameKey, medicine.getId());
            }
        }
    }

    private void removeFromIndex(OrderedMap<String, OrderedMap<String, Medicine>> index,
                                 String key, String medId) {
        OrderedMap<String, Medicine> medicineMap = index.get(key);
        if (medicineMap != null) {
            medicineMap.remove(medId);
            if (medicineMap.isEmpty()) {
                index.remove(key);
            }
        }
    }

    /*
     * Get all transactions that occurred within a specific month and year
     * @param year the year of the transactions to retrieve
     * @param month the month of the transactions to retrieve (1-12)
     * @return a OrderedMap containing the transactions within the specified month
     */
    public OrderedMap<String, Transaction> getTransactionsInMonth(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        Transaction min = new Transaction();
        min.setDate(start);
        Transaction max = new Transaction();
        max.setDate(end);
        Comparator<Transaction> dateComparator = (t1, t2) -> t1.getDate().compareTo(t2.getDate());
        return transactionMap.rangeSearch(min, max, dateComparator);
    }

    /*
     * Get all medicines that are low in stock (quantity below a specified threshold)
     * @return a OrderedMap containing the low stock medicines
     */
    public OrderedMap<String, Medicine> getLowStockMedicines() {
        OrderedMap<String, Medicine> lowStockMedicines = new OrderedMap<>();
        for (Medicine medicine : medicineMap) {
            if (medicine.getQuantity() < 10) {
                lowStockMedicines.put(medicine.getId(), medicine);
            }
        }
        return lowStockMedicines;
    }

    /*
     * Add stock to a specific medicine
     * @param medId the ID of the medicine to which stock will be added
     * @param quantity the quantity of stock to add
     * @return true if the stock was successfully added, false otherwise
     */
    public boolean addStockToMedicine(String medId, int quantity) {
        Medicine medicine = medicineMap.get(medId);
        if (medicine == null) {
            return false;
        }
        medicine.setQuantity(medicine.getQuantity() + quantity);
        medicineDAO.saveToFile(medicineMap);
        return true;
    }

    /*
     * Get the most sold medicine from a list of transactions
     * @param transactions the list of transactions to analyze
     * @return a string representation of the most sold medicine and its quantity, or "N/A" if no sales
     */
    public String getMostSoldMedicine(OrderedMap<String, Transaction> transactions) {
        class MedStat{
            Medicine med;
            int qty;
            MedStat(Medicine med, int qty) {
                this.med = med;
                this.qty = qty;
            }
        }
        MedStat[] stats = new MedStat[100];
        int statCount = 0;

        for (Transaction transaction : transactions){
            for (PrescribedMedicine pm : transaction.getMedicines()) {
                Medicine med = pm.getMedicine();
                int idx = -1;
                for (int j = 0; j < statCount; j++) {
                    if (stats[j].med.getId().equals(med.getId())) {
                        idx = j;
                        break;
                    }
                }
                if (idx != -1) {
                    stats[idx].qty += pm.getQuantity();
                } else {
                    stats[statCount++] = new MedStat(med, pm.getQuantity());
                }
            }
        }
        int maxQty = -1;
        Medicine mostSold = null;
        for (int i = 0; i < statCount; i++) {
            if (stats[i].qty > maxQty) {
                maxQty = stats[i].qty;
                mostSold = stats[i].med;
            }
        }
        if (mostSold == null) {
            return "N/A";
        }
        return mostSold.getName() + "(ID: " + mostSold.getId() + ", Qty: " + maxQty + ")";
    }

    /*
     * Get the current stock quantity of a specific medicine
     * @param medId the ID of the medicine
     * @return the stock quantity, or -1 if the medicine is not found
     */
    public int getMedicineStock(String medId) {
        Medicine medicine = medicineMap.get(medId);
        if (medicine != null) {
            return medicine.getQuantity();
        }
        return -1; // Medicine not found
    }

    /*
     * Getters for the rejected prescription list
     * @return rejectedPrescriptionList
     */
    public OrderedMap<String, Prescription> getRejectedPrescriptionMap(){
        return processedPrescriptionMap.filter( new Prescription(null,null),
                (p1, p2) -> p1.getStatus().equals(STATUS_REJECTED) ? 0 : 1);
    }

    /*
     * Reprocess a rejected prescription by moving it back to the pending prescriptions
     * @param prescID the ID of the rejected prescription to be reprocessed
     */
    public void reprocessRejectedPrescription(String prescID){
        Prescription presc = processedPrescriptionMap.get(prescID);
        processedPrescriptionMap.remove(prescID);
        presc.setStatus(STATUS_PENDING);
        pendingPrescriptionMap.put(prescID, presc);
        processedPrescriptionDAO.saveToFile(processedPrescriptionMap);
        pendingPrescriptionDAO.saveToFile(pendingPrescriptionMap);
        processedPrescriptionMap = processedPrescriptionDAO.retrieveFromFile();
    }
}
