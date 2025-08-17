package boundary;

import control.PharmacyMaintenance;
import entity.*;
import java.util.Scanner;
import utility.*;
import java.time.LocalDateTime;
import adt.CustomADT;

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
            System.out.println("2. Add Prescription");
            System.out.println("3. Process Next Prescription");
            System.out.println("4. View All Prescriptions");
            System.out.println("5. View All Transactions");
            System.out.println("6. Report");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 7.");
                continue;
            }

            switch (choice){
                case 1:
                    viewMedicine();
                    break;
                case 2:
                    addPrescription();
                    break;
                case 3:
                    processNextPrescription();
                    break;
                case 4:
                    viewAllPrescriptions();
                    break;
                case 5:
                    viewAllTransactions();
                    break;
                case 6:
                    generateReport();
                    break;
                case 7:
                    System.out.println("Exiting Pharmacy Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
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
            Medicine[] medicines = pharmacyMaintenance.listAllMedicines();
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
            System.out.println("2. View Medicine");
            System.out.println("3. Search Medicine");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                continue;
            }

            switch (choice){
                case 1:
                    addMedicine();
                    break;
                case 2:
                    viewMedicineDetails();
                    break;
                case 3:
//                    searchMedicine();
                    break;
                case 4:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4);
    }

    public void addMedicine() {
        System.out.println("\n~> Adding new medicine...\n");
        System.out.print("Enter Medicine ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Medicine Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Price: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();
        Medicine newMedicine = new Medicine(id, name, quantity, price, description);
        pharmacyMaintenance.addMedicine(newMedicine);
        System.out.println("Medicine added successfully!");
    }

    public void viewMedicineDetails(){
        System.out.print("Enter Medication ID to view details: ");
        String medId = System.console().readLine();
        Medicine med = pharmacyMaintenance.getMedicineById(medId);
        if (med == null) {
            System.out.println("Medication not found.");
            return;
        }
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
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(System.console().readLine());
            switch (choice) {
                case 1:
                    editMedicine(med.getId());
                    break;
                case 2:
                    deleteMedicine(med.getId());
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Try again!");
            }
        } while (choice != 3);

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
        System.out.print("Enter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());
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
        pharmacyMaintenance.removeMedicine(medID);
    }

    public void addPrescription() {
        System.out.println("\n~> Adding new prescription...\n");
        System.out.print("Enter Prescription ID: ");
        String prescriptionID = scanner.nextLine();
        System.out.print("Enter Treatment ID: ");
        String treatmentID = scanner.nextLine();
        Prescription prescription = new Prescription(prescriptionID, treatmentID);
        System.out.println("Enter number of medicines to add: ");
        int numMedicines = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < numMedicines; i++) {
            System.out.print("Enter Medicine ID for medicine " + (i + 1) + ": ");
            String medID = scanner.nextLine();
            System.out.print("Enter Quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Dosage: ");
            String dosage = scanner.nextLine();
            System.out.print("Enter Frequency: ");
            String frequency = scanner.nextLine();
            System.out.print("Enter Description: ");
            String description = scanner.nextLine();

            Medicine med = pharmacyMaintenance.getMedicineById(medID);
            if (med != null) {
                prescription.addMedicine(med, quantity, dosage, frequency, description);
            } else {
                System.out.println("Medicine with ID " + medID + " not found.");
            }
        }
        pharmacyMaintenance.enqueuePrescription(prescription);
        System.out.println("Prescription added successfully!");
    }

    public void viewAllPrescriptions() {
        Prescription[] prescriptions = pharmacyMaintenance.listAllPrescriptions();
        if (prescriptions.length == 0) {
            System.out.println("No prescriptions found.");
            return;
        }
        System.out.println("=== Prescription List ===");
        int count = 1;
        for (Prescription prescription : prescriptions) {
            System.out.println("Prescription #" + count + ":");
            System.out.println(prescription);
            System.out.println("------------------------------------");
            count++;
        }
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
        Transaction[] transactions = pharmacyMaintenance.listAllTransactions();
        if (transactions.length == 0) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.println("=== Transaction List ===");
        int count = 1;
        for (Transaction transaction : transactions) {
            System.out.println("Transaction #" + count + ":");
            System.out.println(transaction);
            System.out.println("------------------------------------");
            count++;
        }
    }

    public void generateReport() {
        System.out.println("=== Pharmacy Reports ===");
        System.out.println("1. Current Medicine Stock");
        System.out.println("2. Monthly Sales Report");


        try {
            System.out.print("Select report type: ");
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    generateMedicineStockReport();
                    break;
                case 2:
                    generateMonthlySalesReport();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
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
        Medicine[] medicines = pharmacyMaintenance.listAllMedicines();
        // Insertion sort by quantity ascending
        Medicine[] sortedMedicines = pharmacyMaintenance.sortMedicinesByQuantityAscending(medicines);
        int count = 1;
        int totalStock = 0;
        int descWidth = 30;
        int lowStockCount = 0;
        for (Medicine med : sortedMedicines) {
            String[] descLines = wrapText(med.getDescription(), descWidth);
            String qtyDisplay;
            if (med.getQuantity() < 10) {
                qtyDisplay = "⚠ " + med.getQuantity();
                lowStockCount++;
            } else {
                qtyDisplay = String.valueOf(med.getQuantity());
            }
            report.append(String.format("│ %2d. │ %-8s │ %-14s │ %8s │ %9.2f │ %-30s │\n",
                    count++, med.getId(), med.getName(), qtyDisplay, med.getPrice(), descLines[0]));
            for (int i = 1; i < descLines.length; i++) {
                report.append(String.format("│     │          │                │          │           │ %-30s │\n", descLines[i]));
            }
            totalStock += med.getQuantity();
        }
        report.append("├─────┴──────────┴────────────────┴──────────┴───────────┴────────────────────────────────┤\n");
        report.append(String.format("│ Total number of medicines: %-60d │\n", medicines.length));
        report.append(String.format("│ Total stock quantity: %-65d │\n", totalStock));
        report.append(String.format("│ Total low stock medicines: %-60d │\n", lowStockCount));
        report.append("└─────────────────────────────────────────────────────────────────────────────────────────┘\n");

        System.out.print(report.toString());


        System.out.print("Would you like to save this report? (y/n):");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y")) {
            pharmacyMaintenance.saveMedicineStockReport(report, reportDate);
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
        System.out.println("=== Monthly Sales Report ===");

        System.out.print("Enter year (e.g. 2025): ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter month (1-12): ");
        int month = Integer.parseInt(scanner.nextLine());

        StringBuilder salesReport = new StringBuilder();
        String reportDate = DateTimeFormatterUtil.formatForDisplay(LocalDateTime.now());
        salesReport.append("┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐\n");
        salesReport.append("│").append(centerTextLine("Monthly Sales Report of " + months[month - 1] + " " + year, 105)).append("│\n");
        salesReport.append("│").append(centerTextLine("Generated on: " + reportDate, 105)).append("│\n");
        salesReport.append("├─────┬──────────┬────────────────┬────────────────────────────────┬───────────┬───────────┬──────────────┤\n");
        salesReport.append("│ No. │ ID       │ Name           │ Description                    │ Quantity  │ Price(RM) │ SubTotal(RM) │\n");
        salesReport.append("├─────┼──────────┼────────────────┼────────────────────────────────┼───────────┼───────────┼──────────────┤\n");
        Transaction[] transactions = pharmacyMaintenance.listAllTransactions();
        int count = 1;
        double totalSales = 0.0;
        boolean found = false;
        for (Transaction transaction : transactions) {
            java.time.LocalDate tDate = transaction.getDate().toLocalDate();
            if (tDate.getYear() == year && tDate.getMonthValue() == month) {
                CustomADT<String, PrescribedMedicine> medicines = transaction.getMedicines();
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
                    found = true;
                }
            }
        }
        if (!found) {
            salesReport.append("│ No transactions found for this month. │\n");
        }
        salesReport.append("├─────┴──────────┴────────────────┴────────────────────────────────┴───────────┴───────────┼──────────────┤\n");
        salesReport.append(String.format("│ %88s │ %12.2f │\n", "Total Sales(RM)", totalSales));
        salesReport.append("└──────────────────────────────────────────────────────────────────────────────────────────┴──────────────┘\n");
        System.out.print(salesReport.toString());

        System.out.print("Would you like to save this report? (y/n):");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y")) {
            pharmacyMaintenance.saveMonthlySalesReport(salesReport, year, month);
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


}
