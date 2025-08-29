/**
 *@author Tan Jin Wei
 */
package boundary;
import adt.OrderedMap;
import control.ProcedureMaintenance;
import entity.Procedure;
import utility.InputHandler;
import utility.MessageUI;

public class ProcedureUI {
    private ProcedureMaintenance procedureMaintenance;

    public ProcedureUI() {
        this.procedureMaintenance = new ProcedureMaintenance();
    }

    /**
     * Display the main procedure management menu
     */
    public void displayMainMenu() {
        int choice;
        do {
            System.out.println("\n========================================");
            System.out.println("       PROCEDURE MANAGEMENT");
            System.out.println("========================================");
            System.out.println("1. Add Procedure");
            System.out.println("2. View All Procedures");
            System.out.println("3. Search Procedure");
            System.out.println("4. Update Procedure");
            System.out.println("5. Delete Procedure");
            System.out.println("0. Back to Main Menu");
            System.out.println("========================================");
            
            choice = InputHandler.getInt("Enter choice", 0, 5);
            
            switch (choice) {
                case 1:
                    addProcedure();
                    break;
                case 2:
                    viewAllProcedures();
                    break;
                case 3:
                    searchProcedure();
                    break;
                case 4:
                    updateProcedure();
                    break;
                case 5:
                    deleteProcedure();
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    MessageUI.displayInvalidChoiceMessage();
            }
        } while (choice != 0);
    }

    /**
     * Add a new procedure
     */
    private void addProcedure() {
        System.out.println("\n--- ADD NEW PROCEDURE ---");
        
        String procedureID = InputHandler.getString("Enter Procedure ID");
        
        if (procedureMaintenance.procedureExists(procedureID)) {
            System.out.println("Error: Procedure ID already exists!");
            return;
        }
        
        String procedureCode = InputHandler.getString("Enter Procedure Code");
        String procedureName = InputHandler.getString("Enter Procedure Name");
        String description = InputHandler.getString("Enter Description");
        int duration = InputHandler.getInt("Enter Duration (minutes)", 1, 1440);
        double cost = InputHandler.getDouble("Enter Cost (RM)", 0.01, 99999.99);
        
        Procedure newProcedure = procedureMaintenance.createProcedure(
            procedureID, procedureCode, procedureName, description, duration, cost
        );
        
        if (newProcedure != null) {
            System.out.println("\nProcedure added successfully!");
            System.out.println(newProcedure.toString());
        } else {
            System.out.println("Failed to add procedure!");
        }
        
        InputHandler.pauseForUser("");
    }

    /**
     * View all procedures
     */
    private void viewAllProcedures() {
        System.out.println("\n--- ALL PROCEDURES ---");
        
        OrderedMap<String, Procedure> allProcedures = procedureMaintenance.getAllProcedures();
        
        if (allProcedures.isEmpty()) {
            System.out.println("No procedures found.");
        } else {
            int count = 1;
            for (Procedure procedure : allProcedures) {
                System.out.println(count + ". " + procedure.toString());
                System.out.println("-".repeat(30));
                count++;
            }
            System.out.println("Total: " + allProcedures.size() + " procedures");
        }
        
        InputHandler.pauseForUser("");
    }

    /**
     * Search for a procedure
     */
    private void searchProcedure() {
        System.out.println("\n--- SEARCH PROCEDURE ---");
        
        String procedureID = InputHandler.getString("Enter Procedure ID to search");
        Procedure procedure = procedureMaintenance.getProcedureById(procedureID);
        
        if (procedure != null) {
            System.out.println("Procedure found:");
            System.out.println(procedure.toString());
        } else {
            System.out.println("Procedure not found!");
        }
        
        InputHandler.pauseForUser("");
    }

    /**
     * Update a procedure
     */
    private void updateProcedure() {
        System.out.println("\n--- UPDATE PROCEDURE ---");
        
        String procedureID = InputHandler.getString("Enter Procedure ID to update");
        Procedure procedure = procedureMaintenance.getProcedureById(procedureID);
        
        if (procedure == null) {
            System.out.println("Procedure not found!");
            InputHandler.pauseForUser("");
            return;
        }
        
        System.out.println("Current procedure:");
        System.out.println(procedure.toString());
        
        double currentCost = procedure.getCost();
        System.out.printf("Current cost: RM%.2f%n", currentCost);
        
        double newCost = InputHandler.getDouble("Enter new cost (RM)", 0.01, 99999.99);
        
        if (procedureMaintenance.updateProcedureCost(procedureID, newCost)) {
            System.out.println("Cost updated successfully!");
            System.out.printf("Old: RM%.2f -> New: RM%.2f%n", currentCost, newCost);
        } else {
            System.out.println("Failed to update cost!");
        }
        
        InputHandler.pauseForUser("");
    }

    /**
     * Delete a procedure
     */
    private void deleteProcedure() {
        System.out.println("\n--- DELETE PROCEDURE ---");
        
        String procedureID = InputHandler.getString("Enter Procedure ID to delete");
        Procedure procedure = procedureMaintenance.getProcedureById(procedureID);
        
        if (procedure == null) {
            System.out.println("Procedure not found!");
            InputHandler.pauseForUser("");
            return;
        }
        
        System.out.println("Procedure to delete:");
        System.out.println(procedure.toString());
        
        boolean confirm = InputHandler.getYesNo("Are you sure you want to delete this procedure?");
        
        if (confirm) {
            Procedure deletedProcedure = procedureMaintenance.removeProcedure(procedureID);
            if (deletedProcedure != null) {
                System.out.println("Procedure deleted successfully!");
            } else {
                System.out.println("Failed to delete procedure!");
            }
        } else {
            System.out.println("Delete cancelled.");
        }
        
        InputHandler.pauseForUser("");
    }
}
