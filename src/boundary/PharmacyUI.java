package boundary;

import adt.OrderedMap;
import control.PharmacyMaintenance;
import entity.*;
import java.time.LocalDateTime;
import java.util.Scanner;
import utility.*;

public class PharmacyUI {

    private final PharmacyMaintenance pharmacyMaintenance;
    private final Scanner scanner;
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public PharmacyUI() {
        pharmacyMaintenance = new PharmacyMaintenance();
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args){
        new PharmacyUI().start();
    }


    public void start() {
        int choice = 0;
        do {
            System.out.println("\n=== Pharmacy Management System ===");
            System.out.println("1. View Medication");
            System.out.println("2. Process Next Prescription");
            System.out.println("3. View All Prescriptions");
            System.out.println("4. View All Transactions");
            System.out.println("5. Report");
            System.out.println("6. Exit");
            choice = InputHandler.getInt("Enter your choice", 1, 6);

            switch (choice){
                case 1:
                    viewMedicine();
                    break;
                case 2:
                    processNextPrescription();
                    break;
                case 3:
                    viewAllPrescriptions();
                    break;
                case 4:
                    viewAllTransactions();
                    break;
                case 5:
                    generateReport();
                    break;
                case 6:
                    System.out.println("Exiting Pharmacy Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);
    }


    public void viewMedicine() {
        int choice = 0;
        do {
            int count = 1;
            System.out.println("┌─────────────────────────────────────────────────────────────────────────────────────────┐");
            printCenteredText("Medicine List");
            System.out.println("├─────┬──────────┬────────────────┬──────────┬───────────┬────────────────────────────────┤");
            System.out.println("│ No. │ ID       │ Name           │ Quantity │ Price(RM) │ Description                    │");
            System.out.println("├─────┼──────────┼────────────────┼──────────┼───────────┼────────────────────────────────┤");
            OrderedMap<String, Medicine> medicines = pharmacyMaintenance.getMedicineMap();
            int descWidth = 30;
            for (Medicine med : medicines) {
                String[] descLines = wrapText(med.getDescription(), descWidth);
                System.out.printf("│ %2d. │ %-8s │ %-14s │ %8d │ %9.2f │ %-30s │%n",
                        count++, med.getId(), med.getName(), med.getQuantity(), med.getPrice(), descLines[0]);
                for (int i = 1; i < descLines.length; i++) {
                    System.out.printf("│     │          │                │          │           │ %-30s │%n", descLines[i]);
                }
            }
            System.out.println("└─────┴──────────┴────────────────┴──────────┴───────────┴────────────────────────────────┘");
            System.out.println("\nMedicine Management Menu:");
            System.out.println("1. Add Medicine");
            System.out.println("2. View Medicine Details");
            System.out.println("3. View Low Stock Medicine");
            System.out.println("4. Search Medicine");
            System.out.println("5. Back to Main Menu");
            choice = InputHandler.getInt("Enter your choice", 1, 5);

            switch (choice){
                case 1:
                    addMedicine();
                    break;
                case 2:
                    viewMedicineDetails();
                    break;
                case 3:
                    manageLowStock();
                    break;
                case 4:
                    searchMedicine();
                    break;
                case 5:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);
    }

    public void addMedicine() {
        System.out.println("\n~> Adding new medicine...\n");
        String name = InputHandler.getString("Enter Medicine Name");
        int quantity = InputHandler.getInt("Enter Quantity", 1, 500);
        double price = InputHandler.getDouble("Enter Price", 0.01, 5000.00);
        String description = InputHandler.getString("Enter Description");
        String id = IDGenerator.generateMedicineID();
        Medicine newMedicine = new Medicine(id, name, quantity, price, description);
        pharmacyMaintenance.addMedicine(newMedicine);
        System.out.println("Medicine added successfully!");
    }

    public void viewMedicineDetails(){
        String medId = InputHandler.getString("Enter Medication ID to view details");
        Medicine med = pharmacyMaintenance.getMedicineById(medId.toUpperCase());
        if (med == null) {
            System.out.println("Medication not found.");
            return;
        }
        displayMedicineDetails(med);

    }

    public void editMedicine(String medID){
        Medicine med = pharmacyMaintenance.getMedicineById(medID);
        if (med == null) {
            System.out.println("Medication not found.");
            return;
        }
        int choice = getUpdateMedicineField();
        String newValue = getNewValue(choice);

        if (newValue == null) {
            System.out.println("No changes made. Returning to Medicine Management Menu.");
            return;
        }

        pharmacyMaintenance.updateMedicineField(medID, choice, newValue);
    }

    public int getUpdateMedicineField(){
        System.out.println("Update Medicine Fields Menu:");
        System.out.println("1. Update Name");
        System.out.println("2. Update Quantity");
        System.out.println("3. Update Price");
        System.out.println("4. Update Description");
        System.out.println("5. Back to Medicine Management Menu");
        int choice = InputHandler.getInt("Enter your choice", 1, 5);
        return choice;
    }

    public String getNewValue(int choice){
        switch (choice) {
            case 1:
                System.out.print("Enter new Name: ");
                break;
            case 2:
                System.out.print("Enter new Quantity: ");
                break;
            case 3:
                System.out.print("Enter new Price: ");
                break;
            case 4:
                System.out.print("Enter new Description: ");
                break;
            case 5:
                System.out.println("Returning to Medicine Management Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        if (choice >= 1 && choice <= 4) {
            return scanner.nextLine();
        }
        return null;
    }

    public void deleteMedicine(String medID){
        Medicine med = pharmacyMaintenance.getMedicineById(medID);
        if (med == null) {
            System.out.println("Medication not found.");
            return;
        }
        System.out.print(" ");
        boolean confirmation = InputHandler.getYesNo("Are you sure you want to delete this medicine?");
        if (!confirmation) {
            System.out.println("Deletion cancelled.\n");
            return;
        }
        pharmacyMaintenance.removeMedicine(medID);
        System.out.println("Medicine deleted successfully!\n");
        return;
    }

    public void addPrescription(String treatmentID) {
        System.out.println("\n~> Adding new prescription...\n");
        String prescriptionID = IDGenerator.generatePrescriptionID();
        Prescription prescription = new Prescription(prescriptionID, treatmentID);
        int numMedicines = InputHandler.getInt("Enter number of medicines to add", 1, 20);
        for (int i = 0; i < numMedicines; i++) {
            System.out.println("Adding Medicine " + (i + 1) + ":");
            addPrescribedMedicine(prescription);
        }
        pharmacyMaintenance.enqueuePrescription(prescription);
        System.out.println("Prescription added successfully!");
    }

    // changed
    public void addPrescribedMedicine(Prescription prescription) {
        System.out.println("\n~> Adding prescribed medicine...\n");
        Medicine med = null;
        do {
            String medicineID = InputHandler.getString("Enter Medicine ID");
            med = pharmacyMaintenance.getMedicineById(medicineID);
            if (med == null) {
                System.out.println("Medicine with ID " + medicineID + " not found.");
            }
        } while (med == null);
        int availableQty = pharmacyMaintenance.getMedicineStock(med.getId());
        int quantity = InputHandler.getInt("Enter Quantity", 1, availableQty);
        String dosage = InputHandler.getString("Enter Dosage");
        String frequency = InputHandler.getString("Enter Frequency");
        String description = InputHandler.getString("Enter Description");

        prescription.addMedicine(med, quantity, dosage, frequency, description);
        System.out.println("Prescribed medicine added successfully!");
    }

    public void viewAllPrescriptions() {

        int choice = 0;
        do{
            OrderedMap<String, Prescription> pendingQueue = pharmacyMaintenance.getPendingPrescriptionMap();
            OrderedMap<String, Prescription> completedList = pharmacyMaintenance.getCompletedPrescriptionMap();
            OrderedMap<String, Prescription> rejectedList = pharmacyMaintenance.getRejectedPrescriptionMap();
            System.out.println("┌─────────────────────────────────────────────────────────────────────────────────────────┐");
            printCenteredText("Pending Prescriptions");
            System.out.println("├─────┬───────────────┬──────────────┬──────────────────┬───────────────────┬─────────────┤");
            System.out.println("│ No. │ Prescription  │ Treatment ID │ Total Price (RM) │ Status            │ Num of Meds │");
            System.out.println("├─────┼───────────────┼──────────────┼──────────────────┼───────────────────┼─────────────┤");
            int count = 1;
            if (pendingQueue.isEmpty()) {
                printCenteredText("No pending prescriptions found.");
            } else {
                for (Prescription p : pendingQueue) {
                    System.out.printf("│ %2d. │ %-13s │ %-12s │ %16.2f │ %-17s │ %-11d │%n",
                            count++, p.getPrescriptionID(), p.getTreatmentID(), p.getTotalPrice(), p.getStatus(), p.getMedicines().size());
                }
            }

            System.out.println("└─────────────────────────────────────────────────────────────────────────────────────────┘");

            // Display Processed Prescriptions
            System.out.println("┌─────────────────────────────────────────────────────────────────────────────────────────┐");
            printCenteredText("Processed Prescriptions");
            System.out.println("├─────┬───────────────┬──────────────┬──────────────────┬───────────────────┬─────────────┤");
            System.out.println("│ No. │ Prescription  │ Treatment ID │ Total Price (RM) │ Status            │ Num of Meds │");
            System.out.println("├─────┼───────────────┼──────────────┼──────────────────┼───────────────────┼─────────────┤");
            if (completedList.isEmpty()) {
                printCenteredText("No processed prescriptions found.");
            } else {
                count = 1;
                for (Prescription p : completedList) {
                    System.out.printf("│ %2d. │ %-13s │ %-12s │ %16.2f │ %-17s │ %-11d │%n",
                            count++, p.getPrescriptionID(), p.getTreatmentID(), p.getTotalPrice(), p.getStatus(), p.getMedicines().size());
                }
            }


            System.out.println("└─────────────────────────────────────────────────────────────────────────────────────────┘");

            // Display Rejected Prescriptions
            System.out.println("┌─────────────────────────────────────────────────────────────────────────────────────────┐");
            printCenteredText("Rejected Prescriptions");
            System.out.println("├─────┬───────────────┬──────────────┬──────────────────┬───────────────────┬─────────────┤");
            System.out.println("│ No. │ Prescription  │ Treatment ID │ Total Price (RM) │ Status            │ Num of Meds │");
            System.out.println("├─────┼───────────────┼──────────────┼──────────────────┼───────────────────┼─────────────┤");
            if (rejectedList.isEmpty()) {
                printCenteredText("No rejected prescriptions found.");
            } else {
                count = 1;
                for (Prescription p : rejectedList) {
                    System.out.printf("│ %2d. │ %-13s │ %-12s │ %16.2f │ %-17s │ %-11d │%n",
                            count++, p.getPrescriptionID(), p.getTreatmentID(), p.getTotalPrice(), p.getStatus(), p.getMedicines().size());
                }
            }
            System.out.println("└─────────────────────────────────────────────────────────────────────────────────────────┘");

            System.out.println("Prescription Management Menu");
            System.out.println("1. View Prescription details");
            System.out.println("2. Process Next Prescription");
            System.out.println("3. Back to Main Menu");

            choice = InputHandler.getInt("Enter your choice", 1, 3);
            switch (choice) {
                case 1:
                    viewPrescriptionDetails();
                    break;
                case 2:
                    processNextPrescription();
                    break;
                case 3:
                    return;
            }
        } while (choice != 3);
    }

    public void viewPrescriptionDetails() {
        String prescId = InputHandler.getString("Enter Prescription ID to view details");
        Prescription presc = pharmacyMaintenance.getPrescriptionById(prescId.toUpperCase());
        if (presc == null) {
            System.out.println("Prescription not found.");
            return;
        }
        int choice = 0;
        do {
            System.out.println("┌─────────────────────────────────────┐");
            System.out.println("│         Prescription Details        │");
            System.out.println("└─────────────────────────────────────┘");
            System.out.println(presc);
            if (presc.getStatus().equals("PENDING")) {
                System.out.println("Prescription Options:");
                System.out.println("1. Edit Prescribed Medicine");
                System.out.println("2. Add Prescribed Medicine");
                System.out.println("3. Delete Prescription");
                System.out.println("4. Delete Prescribed Medicine");
                System.out.println("5. Back to Prescription Management Menu");
                choice = InputHandler.getInt("Enter your choice", 1, 5);
                switch (choice) {
                    case 1:
                        // Edit Prescribed Medicine
                        editPrescribedMedicine(presc);
                        break;
                    case 2:
                        // Add Prescribed Medicine
                        addPrescribedMedicine(presc);
                        pharmacyMaintenance.savePrescriptionFile(presc);
                        break;
                    case 3:
                        deletePrescription(presc);
                        return;
                    case 4:
                        deletePrescribedMedicine(presc);
                        break;
                    case 5:
                        System.out.println("Returning to Prescription Management Menu...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }

            } else if (presc.getStatus().equals("COMPLETED")){
                System.out.println("-- This prescription has already been processed. --");
                System.out.println("\nPrescription options:");
                System.out.println("1. Delete prescription");
                System.out.println("2. Back to Prescription Management Menu");
                choice = InputHandler.getInt("Enter your choice", 1, 2);
                switch (choice) {
                    case 1:
                        deletePrescription(presc);
                        break;
                    case 2:
                        System.out.println("\nReturning to Prescription Management Menu...");
                        return;
                    default:
                        System.out.println("\nInvalid choice. Please try again.");
                        break;
                }
            } else {
                System.out.println("-- This prescription has been rejected due to unavailable stock for medicines --");
                System.out.println("-- Try edit and process again or delete the prescription. --");
                System.out.println("\nPrescription options:");
                System.out.println("1. Delete prescription");
                System.out.println("2. Edit Prescribed Medicine");
                System.out.println("3. Reprocess the Prescription");
                System.out.println("4. Back to Prescription Management Menu");
                choice = InputHandler.getInt("Enter your choice", 1, 4);
                switch (choice) {
                    case 1:
                        deletePrescription(presc);
                        break;
                    case 2:
                        editPrescribedMedicine(presc);
                        break;
                    case 3:
                        boolean confirm = InputHandler.getYesNo("Are you sure the medicines are available to process this prescription?");
                        if (confirm) {
                            pharmacyMaintenance.reprocessRejectedPrescription(presc.getPrescriptionID());
                            System.out.println("~> Prescription successfully reprocessed and moved to pending queue...\n");
                        } else {
                            System.out.println("Reprocess cancelled.");
                        }
                        break;
                    case 4:
                        System.out.println("\nReturning to Prescription Management Menu...");
                        return;
                    default:
                        System.out.println("\nInvalid choice. Please try again.");
                        break;
                }
            }
        } while (choice != 6);
    }

    public void deletePrescription(Prescription presc) {
        System.out.print(" ");
        boolean confirmation = InputHandler.getYesNo("Are you sure you want to delete this prescription?");
        if (!confirmation) {
            System.out.println("Deletion cancelled.\n");
            return;
        }
        boolean deleteSuccess = pharmacyMaintenance.removePrescription(presc.getPrescriptionID());
        if (!deleteSuccess) {
            System.out.println("Failed to delete prescription.\n");
            return;
        }
        System.out.println("Prescription deleted successfully!\n");
    }

    public void deletePrescribedMedicine(Prescription presc){
        System.out.println("Editing Prescription: " + presc.getPrescriptionID());
        int pmChoice = InputHandler.getInt("Enter Prescribed Medicine number to delete", 1, presc.getMedicines().size());
        PrescribedMedicine pm = presc.getMedicines().get(pmChoice - 1);
        System.out.print(" ");
        boolean confirmation = InputHandler.getYesNo("Are you sure you want to delete this prescribed medicine?");
        if (!confirmation) {
            System.out.println("Deletion cancelled.\n");
            return;
        }
        boolean isDeleted = pharmacyMaintenance.removePrescribedMedicine(presc, pm);
        if (isDeleted) {
            System.out.println("-- Prescribed Medicine deleted successfully. --");
        } else {
            System.out.println("-- Failed to delete Prescribed Medicine. --");
        }
    }

    public void editPrescribedMedicine(Prescription presc) {
        // Implementation for editing the prescription
        System.out.println("Editing Prescription: " + presc.getPrescriptionID());
        int pmChoice = InputHandler.getInt("Enter Prescribed Medicine number to edit", 1, presc.getMedicines().size());
        PrescribedMedicine pm = presc.getMedicines().get(pmChoice - 1);
        // Allow user to edit prescribed medicine details
        int choice = getUpdatePrescribedMedicineField();
        String newStringValue = null;
        int newIntValue = 0;
        switch (choice) {
            case 1:
                newIntValue = InputHandler.getInt("Enter new quantity", 1, pm.getMedicine().getQuantity());
                break;
            case 2:
                newStringValue = InputHandler.getString("Enter new dosage");
                break;
            case 3:
                newStringValue = InputHandler.getString("Enter new frequency");
                break;
            case 4:
                newStringValue = InputHandler.getString("Enter new description");
                break;
            default:
                System.out.println("Invalid choice.");
        }
        boolean isUpdated = pharmacyMaintenance.updatePrescribedMedicine(pm, newIntValue, newStringValue, choice);

        if (choice == 1){
            presc.calculateTotalPrice();
        }

        if (isUpdated) {
            System.out.println("-- Prescribed Medicine updated successfully. --");
        } else {
            System.out.println("-- Failed to update Prescribed Medicine. --");
        }
    }

    public int getUpdatePrescribedMedicineField(){
        System.out.println("Select field to update:");
        System.out.println("1. Quantity");
        System.out.println("2. Dosage");
        System.out.println("3. Frequency");
        System.out.println("4. Description");
        return InputHandler.getInt("Enter your choice", 1, 4);
    }


    public void processNextPrescription() {
        System.out.println("~> Processing next prescription...");
        Prescription prescription = pharmacyMaintenance.dequeuePrescription();
        if (prescription == null) {
            System.out.println("No prescriptions in the queue.");
            return;
        }
        System.out.println("Processing prescription: " + prescription.getPrescriptionID());
        // Further processing logic here
        boolean allProcessed = pharmacyMaintenance.processPrescription(prescription);
        if (!allProcessed) {
            System.out.println("-- Some medicines could not be processed. --");
        } else {
            System.out.println("-- All medicines processed successfully. --");
        }
    }

    public void viewAllTransactions() {
        OrderedMap<String, Transaction> transactionMap = pharmacyMaintenance.getTransactionMap();
        if (transactionMap.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.println("┌──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                                                               Transaction List                                                               │");
        System.out.println("┌─────┬────────────────┬─────────────────────┬───────────────┬──────────────────────────────────────────────────────────────┬──────────────────┐");
        System.out.println("│ No. │ Transaction ID │ Date & Time         │ Patient ID    │ Medicines (ID x Qty, Name, Subtotal)                         │ Total Price (RM) │");
        System.out.println("├─────┼────────────────┼─────────────────────┼───────────────┼──────────────────────────────────────────────────────────────┼──────────────────┤");
        for (int i = 0; i < transactionMap.size(); i++) {
            Transaction transaction = transactionMap.get(i);
            String transId = transaction.getTransactionID();
            LocalDateTime date = transaction.getDate();
            String patientId = transaction.getPatientId(); // Adjust if you get patient differently
            OrderedMap<String, PrescribedMedicine> meds = transaction.getMedicines();
            boolean firstLine = true;
            for (int j = 0; j < meds.size(); j++) {
                PrescribedMedicine pm = meds.get(j);
                Medicine med = pm.getMedicine();
                String medLine = String.format("%s x%d (%s, RM%.2f)",
                        med.getId(), pm.getQuantity(), med.getName(), pm.calculateSubtotal());
                String[] wrappedMedLines = wrapText(medLine, 60);

                for (int k = 0; k < wrappedMedLines.length; k++) {
                    if (firstLine) {
                        System.out.printf("│ %3d │ %-14s │ %-19s │ %-13s │ %-60s │ %16.2f │%n",
                                i + 1, transId, DateTimeFormatterUtil.formatForDisplay(date), patientId, wrappedMedLines[k], transaction.getTotalPrice());
                        firstLine = false;
                    } else {
                        System.out.printf("│     │                │                     │               │ %-60s │                  │%n", wrappedMedLines[k]);
                    }
                }
            }
            if (i == transactionMap.size() - 1) {
                System.out.println("└─────┴────────────────┴─────────────────────┴───────────────┴──────────────────────────────────────────────────────────────┴──────────────────┘");
            } else {
                System.out.println("├─────┼────────────────┼─────────────────────┼───────────────┼──────────────────────────────────────────────────────────────┼──────────────────┤");
            }

        }

        int choice = InputHandler.getInt("Enter number of Transaction to view details (0 to return)", 0, transactionMap.size());

        if(choice == 0){
            System.out.println("Returning back to Main menu");
            return;
        } else {
            Transaction selectedTransaction = transactionMap.get(choice - 1);
            viewTransactionDetails(selectedTransaction);
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }

    }

    public void viewTransactionDetails(Transaction transaction) {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│         Transaction Details         │");
        System.out.println("└─────────────────────────────────────┘");
        System.out.println("Transaction ID: " + transaction.getTransactionID());
        System.out.println("Date & Time: " + DateTimeFormatterUtil.formatForDisplay(transaction.getDate()));
        System.out.println("Patient ID: " + transaction.getPatientId());
        System.out.println("Medicines:");

        OrderedMap<String, PrescribedMedicine> medicines = transaction.getMedicines();
        for (int i = 0; i < medicines.size(); i++) {
            PrescribedMedicine pm = medicines.get(i);
            Medicine med = pm.getMedicine();
            System.out.printf(" - %s x%d (%s, RM%.2f)\n",
                    med.getId(), pm.getQuantity(), med.getName(), pm.calculateSubtotal());
        }
        System.out.println("Total Price (RM): " + transaction.getTotalPrice());
    }

    public void generateReport() {
        System.out.println("\n=== Pharmacy Reports ===");
        System.out.println("1. Current Medicine Stock");
        System.out.println("2. Monthly Sales Report");
        System.out.println("3. Back to Main menu");
        int choice = InputHandler.getInt("Enter your choice", 1, 3);

        switch (choice) {
            case 1:
                generateMedicineStockReport();
                break;
            case 2:
                generateMonthlySalesReport();
                break;
            case 3:
                return;
        }
    }


    private void generateMedicineStockReport() {
        StringBuilder report = new StringBuilder();
        String reportDate = DateTimeFormatterUtil.formatForDisplay(LocalDateTime.now());
        report.append("┌─────────────────────────────────────────────────────────────────────────────────────────┐\n");
        report.append("│").append(centerTextLine("Current Medicine Stock", 89)).append("│\n");
        report.append("│").append(centerTextLine("Generated on: " + reportDate, 89)).append("│\n");
        report.append("├─────┬──────────┬────────────────┬──────────┬───────────┬────────────────────────────────┤\n");
        report.append("│ No. │ ID       │ Name           │ Quantity │ Price(RM) │ Description                    │\n");
        report.append("├─────┼──────────┼────────────────┼──────────┼───────────┼────────────────────────────────┤\n");
        // Insertion sort by quantity ascending
        // Medicine[] sortedMedicines = pharmacyMaintenance.sortMedicinesByQuantityAscending(medicines);
        OrderedMap<String, Medicine> medicineMap = pharmacyMaintenance.getMedicineMap();
        pharmacyMaintenance.sortMedicinesByStock();
        int count = 1;
        int totalStock = 0;
        int descWidth = 30;
        int lowStockCount = 0;
        double totalStockValue = 0.0;
        for (Medicine med : medicineMap) {
            String[] descLines = wrapText(med.getDescription(), descWidth);
            int quantity = med.getQuantity();
            double price = med.getPrice();
            String qtyDisplay;
            if (quantity < 10) {
                qtyDisplay = "⚠ " + quantity;
                lowStockCount++;
            } else {
                qtyDisplay = String.valueOf(quantity);
            }
            report.append(String.format("│ %2d. │ %-8s │ %-14s │ %8s │ %9.2f │ %-30s │\n",
                    count++, med.getId(), med.getName(), qtyDisplay, price, descLines[0]));
            for (int i = 1; i < descLines.length; i++) {
                report.append(String.format("│     │          │                │          │           │ %-30s │\n", descLines[i]));
            }
            totalStock += quantity;
            totalStockValue += quantity * price;
        }
        report.append("├─────┴──────────┴────────────────┴──────────┴───────────┴────────────────────────────────┤\n");
        report.append(String.format("│ Total number of medicines: %-60d │\n", medicineMap.size()));
        report.append(String.format("│ Total stock quantity: %-65d │\n", totalStock));
        report.append(String.format("│ Total low stock medicines: %-60d │\n", lowStockCount));
        report.append(String.format("│ Total stock value (RM): %-63.2f │\n", totalStockValue));
        report.append("└─────────────────────────────────────────────────────────────────────────────────────────┘\n");

        System.out.print(report.toString());


        boolean choice = InputHandler.getYesNo("Would you like to save this report?");
        if (choice) {
            String filename = pharmacyMaintenance.saveMedicineStockReport(report, reportDate);
            System.out.println("Report saved successfully as " + filename);
        } else {
            System.out.println("Returning back to Main menu...");
        }
    }

    private String centerTextLine(String text, int width) {
        int padding = (width - text.length()) / 2;
        if (padding < 0) padding = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) sb.append(' ');
        sb.append(text);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }

    private void generateMonthlySalesReport() {
        System.out.println("\n=== Monthly Sales Report ===");

        int year = InputHandler.getInt("Enter year ", 2000, 2025);
        int month = InputHandler.getInt("Enter month ", 1, 12);

        StringBuilder salesReport = new StringBuilder();
        String reportDate = DateTimeFormatterUtil.formatForDisplay(LocalDateTime.now());
        salesReport.append("┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐\n");
        salesReport.append("│").append(centerTextLine("Monthly Sales Report of " + months[month - 1] + " " + year, 105)).append("│\n");
        salesReport.append("│").append(centerTextLine("Generated on: " + reportDate, 105)).append("│\n");
        salesReport.append("├─────┬──────────┬────────────────┬────────────────────────────────┬───────────┬───────────┬──────────────┤\n");
        salesReport.append("│ No. │ ID       │ Name           │ Description                    │ Quantity  │ Price(RM) │ SubTotal(RM) │\n");
        salesReport.append("├─────┼──────────┼────────────────┼────────────────────────────────┼───────────┼───────────┼──────────────┤\n");
        int count = 1;
        double totalSales = 0.0;
        boolean found = false;
        int totalMedicinesSold = 0;
        OrderedMap<String, Transaction> transactionInMonth = pharmacyMaintenance.getTransactionsInMonth(year, month);
        for (Transaction transaction : transactionInMonth) {
            OrderedMap<String, PrescribedMedicine> medicines = transaction.getMedicines();
            for (int i = 0; i < medicines.size(); i++) {
                PrescribedMedicine pm = medicines.get(i);
                Medicine med = pm.getMedicine();
                String[] descLines = wrapText(med.getDescription(), 30);
                salesReport.append(String.format("│ %2d. │ %-8s │ %-14s │ %-30s │ %9d │ %9.2f │ %12.2f │\n",
                        count++, med.getId(), med.getName(), descLines[0], pm.getQuantity(), med.getPrice(),  pm.calculateSubtotal()));
                for (int j = 1; j < descLines.length; j++) {
                    salesReport.append(String.format("│     │          │                │ %-30s │           │           │              │\n", descLines[j]));
                }
                totalSales += pm.calculateSubtotal();
                totalMedicinesSold += pm.getQuantity();
                found = true;
            }
        }

        if (!found) {
            salesReport.append("│ No transactions found for this month. │\n");
        }
        salesReport.append("├─────┴──────────┴────────────────┴────────────────────────────────┴───────────┴───────────┼──────────────┤\n");
        salesReport.append(String.format("│ %88s │ %12.2f │\n", "Total Sales(RM)", totalSales));
        salesReport.append(String.format("│ Total Number of Transactions in the Month: %-45d └──────────────┤\n", transactionInMonth.size()));
        salesReport.append(String.format("│ Total Number of Medicines Sold: %-71d │\n", totalMedicinesSold));
        salesReport.append(String.format("│ The Most Sold Medicine: %-79s │\n", pharmacyMaintenance.getMostSoldMedicine(transactionInMonth)));
        salesReport.append("└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘\n");
        System.out.print(salesReport.toString());

        boolean choice = InputHandler.getYesNo("Would you like to save this report?");
        if (choice) {
            String filename = pharmacyMaintenance.saveMonthlySalesReport(salesReport, year, month);
            System.out.println("Report saved successfully as " + filename);
        } else {
            System.out.println("Returning back to Main menu...");
        }

    }

    public static void printCenteredText(String text) {
        int totalWidth = 89; // width between the box borders
        int padding = (totalWidth - text.length()) / 2;
        String centeredText = String.format("│%" + (padding + text.length()) + "s%" + (totalWidth - padding - text.length()) + "s│", text, "");
        System.out.println(centeredText);
    }

    private String[] wrapText(String text, int width) {
        if (text == null) return new String[]{""};
        StringBuilder result = new StringBuilder();
        String[] words = text.split(" ");
        int lineLen = 0;
        for (String word : words) {
            if (lineLen + word.length() + (lineLen == 0 ? 0 : 1) > width) {
                result.append("\n");
                lineLen = 0;
            }
            if (lineLen > 0) {
                result.append(" ");
                lineLen++;
            }
            result.append(word);
            lineLen += word.length();
        }
        return result.toString().split("\\n");
    }

    public void searchMedicine() {
        System.out.print("Enter medicine name to search: ");
        String name = scanner.nextLine();

        OrderedMap<String, Medicine> results = pharmacyMaintenance.searchMedicinesByName(name.toUpperCase());
        if (results.isEmpty()) {
            System.out.println("No medicines found matching the name: " + name);
            System.out.println("Press Enter to continue ...");
            scanner.nextLine();
            return;
        }
        System.out.println("\nSearch Results:");
        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ". " + results.get(i).getName() + " (ID: " + results.get(i).getId() + ")");
        }
        int choice = InputHandler.getInt("Enter the number of the medicine to view details (0 to cancel)", 0, results.size());
        if (choice == 0) {
            return;
        }
        displayMedicineDetails(results.get(choice - 1));
    }

    public void displayMedicineDetails(Medicine med) {
        int choice = 0;
        do {
            System.out.println("┌─────────────────────────────────────┐");
            System.out.println("│         Medication Details          │");
            System.out.println("└─────────────────────────────────────┘");
            System.out.println("ID           : " + med.getId());
            System.out.println("Name         : " + med.getName());
            System.out.println("Quantity     : " + med.getQuantity());
            System.out.println("Price        : " + med.getPrice());
            System.out.println("Description  : " + med.getDescription());

            System.out.println("\nMedication Management Menu:");
            System.out.println("1. Edit Medicine");
            System.out.println("2. Delete Medicine");
            System.out.println("3. Back to Main Menu");
            choice = InputHandler.getInt("Enter your choice", 1, 3);
            switch (choice) {
                case 1:
                    editMedicine(med.getId());
                    break;
                case 2:
                    deleteMedicine(med.getId());
                    if(pharmacyMaintenance.getMedicineById(med.getId()) == null){
                        return;
                    }
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Try again!");
            }
        } while (choice != 3);
    }

    public void manageLowStock(){
        int choice = -1;
        do {
            System.out.println("┌─────────────────────────────────────────────────────────────────────────────────────────┐");
            printCenteredText("Low Stock Medicines");
            printCenteredText("(Medicine with Stock < 10)");
            System.out.println("├─────┬──────────┬────────────────┬──────────┬───────────┬────────────────────────────────┤");
            System.out.println("│ No. │ ID       │ Name           │ Quantity │ Price(RM) │ Description                    │");
            System.out.println("├─────┼──────────┼────────────────┼──────────┼───────────┼────────────────────────────────┤");
            OrderedMap<String, Medicine> lowStockMedicines = pharmacyMaintenance.getLowStockMedicines();
            if (lowStockMedicines.isEmpty()) {
                printCenteredText("No low stock medicines found.");
                System.out.println("└─────┴──────────┴────────────────┴──────────┴───────────┴────────────────────────────────┘");

                System.out.println("Press Enter to continue ...");
                scanner.nextLine();
                return;
            }
            int descWidth = 30;
            int count = 1;
            for (Medicine med : lowStockMedicines) {
                String[] descLines = wrapText(med.getDescription(), descWidth);
                System.out.printf("│ %2d. │ %-8s │ %-14s │ %8d │ %9.2f │ %-30s │%n",
                        count++, med.getId(), med.getName(), med.getQuantity(), med.getPrice(), descLines[0]);
                for (int i = 1; i < descLines.length; i++) {
                    System.out.printf("│     │          │                │          │           │ %-30s │%n", descLines[i]);
                }
            }
            System.out.println("└─────┴──────────┴────────────────┴──────────┴───────────┴────────────────────────────────┘");

            choice = InputHandler.getInt("Enter number of the medicine to add stock (0 to return)", 0, lowStockMedicines.size());

            if (choice == 0) {
                System.out.println("Returning back to Medicine Management Menu...");
            } else {
                int quantityToAdd = InputHandler.getInt("Enter quantity to add", 1, 500);
                Medicine selectedMedicine = lowStockMedicines.get(choice - 1);
                boolean success = pharmacyMaintenance.addStockToMedicine(selectedMedicine.getId(), quantityToAdd);
                if (success) {
                    System.out.println("Stock updated successfully!");
                } else {
                    System.out.println("Failed to update stock.");
                }
            }
        } while (choice != 0);
    }
}
