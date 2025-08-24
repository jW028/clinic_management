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
    private final CustomADT<String, Prescription> processedPrescriptionMap;
    private final CustomADT<String, Transaction> transactionMap;
    private final CustomADT<String, Treatment> treatmentMap;
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final PrescriptionDAO pendingPrescriptionDAO = new PrescriptionDAO(1);
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final PrescriptionDAO processedPrescriptionDAO = new PrescriptionDAO(2);

    public PharmacyMaintenance() {
        this.medicineMap = medicineDAO.retrieveFromFile();
        this.pendingPrescriptionMap = pendingPrescriptionDAO.retrieveFromFile();
        this.processedPrescriptionMap = processedPrescriptionDAO.retrieveFromFile();
        this.transactionMap = transactionDAO.retrieveFromFile();
        this.treatmentMap = treatmentDAO.retrieveFromFile();
        IDGenerator.loadCounter("counter.dat");
    }


    public CustomADT<String, Medicine> getMedicineMap() {
        return medicineMap;
    }

    public void addMedicine(Medicine newMedicine) {
        medicineMap.put(newMedicine.getId(), newMedicine);
        medicineDAO.saveToFile(medicineMap);
        IDGenerator.saveCounters("counter.dat");
    }

    public Medicine getMedicineById(String id) {
        return medicineMap.get(id);
    }

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


    public void removeMedicine(String medID){
        medicineMap.remove(medID);
        medicineDAO.saveToFile(medicineMap);
    }

    public void enqueuePrescription(Prescription prescription) {
        pendingPrescriptionMap.offer(prescription.getPrescriptionID(), prescription);
        pendingPrescriptionDAO.saveToFile(pendingPrescriptionMap);
        IDGenerator.saveCounters("counter.dat");
    }

    public CustomADT<String, Prescription> getPendingPrescriptionMap() {
        return pendingPrescriptionMap;
    }

    public CustomADT<String, Prescription> getProcessedPrescriptionMap() {
        return processedPrescriptionMap;
    }

    public Prescription getPrescriptionById(String id) {
        Prescription presc = pendingPrescriptionMap.get(id);
        if (presc == null) {
            presc = processedPrescriptionMap.get(id);
        }
        return presc;
    }

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

    public void savePrescriptionFile(Prescription prescription) {
        try {
            pendingPrescriptionDAO.saveToFile(pendingPrescriptionMap);
        } catch (Exception e) {
            System.out.println("Error saving prescription file: " + e.getMessage());
        }
    }

    public Prescription dequeuePrescription() {
        Prescription nextPrescription = pendingPrescriptionMap.poll();
        return nextPrescription;
    }

    public boolean processPrescription(Prescription prescription) {
        boolean allProcessed = true;
        Transaction transaction = new Transaction(getPatientIdFromPrescription(prescription, treatmentMap));
        for (int i = 0; i < prescription.getMedicines().size(); i++) {
            PrescribedMedicine pm = prescription.getMedicines().get(i);
            Medicine medInPrescription = pm.getMedicine();
            Medicine med = null;
            if (medInPrescription != null) {
                med = medicineMap.get(medInPrescription.getId());
            }
            if (med == null) {
                System.out.println("Medicine not found in system for prescription: " + (medInPrescription != null ? medInPrescription.getId() : "null"));
                allProcessed = false;
                continue;
            }
            if (med.getQuantity() < pm.getQuantity()) {
                System.out.println("Insufficient stock for " + med.getName());
                allProcessed = false;
            } else {
                med.setQuantity(med.getQuantity() - pm.getQuantity());
                System.out.println("Processed " + pm.getQuantity() + " of " + med.getName() + " for prescription: " + prescription.getPrescriptionID());
                transaction.addMedicine(pm);
                addTransaction(transaction.getTransactionID(), transaction);
            }
        }

        if (allProcessed) {
            prescription.setStatus("COMPLETED");
        } else {
            prescription.setStatus("PARTIALLY COMPLETED");
        }

        if (pendingPrescriptionMap.containsKey(prescription.getPrescriptionID())) {
            pendingPrescriptionMap.remove(prescription.getPrescriptionID());
            processedPrescriptionMap.put(prescription.getPrescriptionID(), prescription);
        }

        processedPrescriptionMap.put(prescription.getPrescriptionID(), prescription);
        pendingPrescriptionDAO.saveToFile(pendingPrescriptionMap);
        processedPrescriptionDAO.saveToFile(processedPrescriptionMap);
        medicineDAO.saveToFile(medicineMap);
        transactionDAO.saveToFile(transactionMap);
        return allProcessed;
    }

    public void addTransaction(String transactionID, Transaction transaction) {
        transactionMap.put(transactionID, transaction);
        transactionDAO.saveToFile(transactionMap);
    }

    public String getPatientIdFromPrescription(Prescription prescription, CustomADT<String, Treatment> treatmentMap) {
        String treatmentId = prescription.getTreatmentID();
        Treatment treatment = treatmentMap.get(treatmentId);
        if (treatment != null) {
            return treatment.getPatient().getPatientId();
        }
        return null; // or throw an exception if not found
    }

    public CustomADT<String, Transaction> getTransactionMap() {
        return transactionMap;
    }


    public void sortMedicinesByStock(){
        medicineMap.sort(Comparator.comparingInt(Medicine::getQuantity));
    }

    public void saveMedicineStockReport(StringBuilder report, String date) {
        String dateStr = date.replace(" ", "_").replace(":", "-").replace("/", "-");
        String filename = String.format("medicine_stock_report_%s.txt", dateStr);
        try (java.io.FileWriter writer = new java.io.FileWriter(filename)) {
            writer.write(report.toString());
            System.out.println("Report saved successfully as " + filename + ".");
        } catch (java.io.IOException e) {
            System.out.println("Failed to save report: " + e.getMessage());
        }
    }

    public void saveMonthlySalesReport(StringBuilder report, int year, int month) {
        String filename = String.format("monthly_sales_report_%04d-%02d.txt", year, month);
        try (java.io.FileWriter writer = new java.io.FileWriter(filename)) {
            writer.write(report.toString());
            System.out.println("Report saved successfully as " + filename + ".");
        } catch (java.io.IOException e) {
            System.out.println("Failed to save report: " + e.getMessage());
        }
    }

    public CustomADT<String, Medicine> searchMedicinesByName(String name) {
        CustomADT<String, Medicine> results = medicineMap.filter(
                new Medicine(null, name, 0, 0.0, null),
                (med1, med2) -> med1.getName().toLowerCase().contains(med2.getName().toLowerCase()) ? 0 : 1
        );
        return results;
    }

    public CustomADT<String, Transaction> getTransactionsInMonth(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
        Transaction min = new Transaction();
        min.setDate(start);
        Transaction max = new Transaction();
        max.setDate(end);
        Comparator<Transaction> dateComparator = (t1, t2) -> t1.getDate().compareTo(t2.getDate());
        CustomADT<String, Transaction> results = transactionMap.rangeSearch(min, max, dateComparator);
        return results;
    }

    public CustomADT<String, Medicine> getLowStockMedicines() {
        CustomADT<String, Medicine> lowStockMedicines = new CustomADT<>();
        for (Medicine medicine : medicineMap) {
            if (medicine.getQuantity() < 10) {
                lowStockMedicines.put(medicine.getId(), medicine);
            }
        }
        return lowStockMedicines;
    }

    public boolean addStockToMedicine(String medId, int quantity) {
        Medicine medicine = medicineMap.get(medId);
        if (medicine == null) {
            System.out.println("Medicine not found.");
            return false;
        }
        medicine.setQuantity(medicine.getQuantity() + quantity);
        medicineDAO.saveToFile(medicineMap);
        return true;
    }

}
