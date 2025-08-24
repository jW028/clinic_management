package control;

import adt.CustomADT;
import dao.*;
import entity.*;

public class PharmacyMaintenance {
    private final CustomADT<String, Medicine> medicineMap;
    private final CustomADT<String, Prescription> prescriptionMap;
    private final CustomADT<String, Transaction> transactionMap;
    private final CustomADT<String, Treatment> treatmentMap;
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public PharmacyMaintenance() {
        this.medicineMap = medicineDAO.retrieveFromFile();
        this.prescriptionMap = prescriptionDAO.retrieveFromFile();
        this.transactionMap = transactionDAO.retrieveFromFile();
        this.treatmentMap = treatmentDAO.retrieveFromFile();
    }

    public Medicine[] listAllMedicines() {
        return medicineDAO.retrieveFromFile().toArray(new Medicine[0]);
    }

    public void addMedicine(Medicine newMedicine) {
        medicineMap.put(newMedicine.getId(), newMedicine);
        medicineDAO.saveToFile(medicineMap);
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
        prescriptionMap.offer(prescription.getPrescriptionID(), prescription);
        prescriptionDAO.saveToFile(prescriptionMap);
    }

    public Prescription[] listAllPrescriptions() {
        return prescriptionDAO.retrieveFromFile().toArray(new Prescription[0]);
    }

    public Prescription dequeuePrescription() {
        Prescription nextPrescription =  prescriptionMap.poll();
        if (nextPrescription != null) {
            prescriptionDAO.saveToFile(prescriptionMap);
        }
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

        prescriptionDAO.saveToFile(prescriptionMap);
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

    public Transaction[] listAllTransactions() {
        return transactionDAO.retrieveFromFile().toArray(new Transaction[0]);
    }

    public Medicine[] sortMedicinesByQuantityAscending(Medicine[] medicines) {
        for (int i = 1; i < medicines.length; i++) {
            Medicine key = medicines[i];
            int j = i - 1;
            while (j >= 0 && medicines[j].getQuantity() > key.getQuantity()) {
                medicines[j + 1] = medicines[j];
                j--;
            }
            medicines[j + 1] = key;
        }
        return medicines;
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
}
