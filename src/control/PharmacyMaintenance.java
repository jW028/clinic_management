/**
 * @author Goh Yu Jie
 */

package control;

import adt.CustomADT;
import dao.*;
import entity.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import utility.IDGenerator;

public class PharmacyMaintenance {
    private final CustomADT<String, Medicine> medicineMap;
    private final CustomADT<String, Prescription> pendingPrescriptionMap;
    private CustomADT<String, Prescription> processedPrescriptionMap;
    private final CustomADT<String, Transaction> transactionMap;
    private final CustomADT<String, Treatment> treatmentMap;
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final PrescriptionDAO pendingPrescriptionDAO = new PrescriptionDAO(1);
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final PrescriptionDAO processedPrescriptionDAO = new PrescriptionDAO(2);
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_REJECTED = "REJECTED";

    public PharmacyMaintenance() {
        this.medicineMap = medicineDAO.retrieveFromFile();
        this.pendingPrescriptionMap = pendingPrescriptionDAO.retrieveFromFile();
        this.processedPrescriptionMap = processedPrescriptionDAO.retrieveFromFile();
        this.transactionMap = transactionDAO.retrieveFromFile();
        this.treatmentMap = treatmentDAO.retrieveFromFile();
        IDGenerator.loadCounter("counter.dat");
    }

    /*
     * Getters for the medicine map
     * @return medicineMap
     */
    public CustomADT<String, Medicine> getMedicineMap() {
        return medicineMap;
    }

    /*
     * Add a new medicine to the medicine map
     * @param newMedicine the new medicine to be added
     */
    public void addMedicine(Medicine newMedicine) {
        medicineMap.put(newMedicine.getId(), newMedicine);
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


    /* Update a specific field of a medicine
     * @param medID the ID of the medicine to be updated
     * @param choice the field to be updated (1: name, 2: quantity, 3: price, 4: description)
     * @param newValue the new value for the selected field
     */
    public void updateMedicineField(String medID, int choice, String newValue) {
        Medicine med = medicineMap.get(medID);

        try {
            switch (choice) {
                case 1:
                    if (newValue != null && newValue.isBlank()) {
                        throw new IllegalArgumentException("Medicine name cannot be empty.");
                    }
                    med.setName(newValue);
                    break;
                case 2:
                    int newQuantity = Integer.parseInt(newValue);
                    if (newQuantity < 0) {
                        throw new IllegalArgumentException("Quantity cannot be negative.");
                    }
                    med.setQuantity(newQuantity);
                    break;
                case 3:
                    double newPrice = Double.parseDouble(newValue);
                    if (newPrice < 0) {
                        throw new IllegalArgumentException("Price cannot be negative.");
                    }
                    med.setPrice(newPrice);
                    break;
                case 4:
                    if (newValue != null && newValue.isBlank()) {
                        throw new IllegalArgumentException("Description cannot be empty.");
                    }
                    med.setDescription(newValue);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field choice.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error updating medicine: " + e.getMessage());
        }
        medicineDAO.saveToFile(medicineMap);
    }

    /*
     * Remove a medicine from the medicine map
     * @param medID the ID of the medicine to be removed
     */
    public void removeMedicine(String medID){
        medicineMap.remove(medID);
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
    public CustomADT<String, Prescription> getPendingPrescriptionMap() {
        return pendingPrescriptionMap;
    }

    /*
     * Getters for the processed prescription list
     * @return processedPrescriptionList
     */
    public CustomADT<String, Prescription> getCompletedPrescriptionMap() {
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
    public CustomADT<String, Transaction> getTransactionMap() {
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
        String dateStr = date.replace(" ", "_").replace(":", "-").replace("/", "-");
        String filename = String.format("medicine_stock_report_%s.txt", dateStr);
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
        String filename = String.format("monthly_sales_report_%04d-%02d.txt", year, month);
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
     * @return a CustomADT containing the matching medicines
     */
    public CustomADT<String, Medicine> searchMedicinesByName(String name) {
        return medicineMap.filter(
                new Medicine(null, name, 0, 0.0, null),
                (med1, med2) -> med1.getName().toLowerCase().contains(med2.getName().toLowerCase()) ? 0 : 1
        );
    }

    /*
     * Get all transactions that occurred within a specific month and year
     * @param year the year of the transactions to retrieve
     * @param month the month of the transactions to retrieve (1-12)
     * @return a CustomADT containing the transactions within the specified month
     */
    public CustomADT<String, Transaction> getTransactionsInMonth(int year, int month) {
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
     * @return a CustomADT containing the low stock medicines
     */
    public CustomADT<String, Medicine> getLowStockMedicines() {
        CustomADT<String, Medicine> lowStockMedicines = new CustomADT<>();
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
    public String getMostSoldMedicine(CustomADT<String, Transaction> transactions) {
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
    public CustomADT<String, Prescription> getRejectedPrescriptionMap(){
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
