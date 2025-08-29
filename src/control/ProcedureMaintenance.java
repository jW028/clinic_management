/**
 * @author Tan Jin Wei
 */

package control;
import adt.OrderedMap;
import dao.ProcedureDAO;
import entity.*;
import utility.IDGenerator;

public class ProcedureMaintenance {
    private OrderedMap<String, Procedure> procedures;
    private final ProcedureDAO procedureDAO;
    

    public ProcedureMaintenance() {
        this.procedureDAO = new ProcedureDAO();
        this.procedures = procedureDAO.retrieveFromFile();
        
        // Load ID counter if exists
        try {
            IDGenerator.loadCounter("counter.dat");
        } catch (Exception e) {
            System.out.println("Could not load ID counter, using defaults");
        }
    }

    // ===============================
    // CREATE OPERATIONS
    // ===============================

    /**
     * Create a new procedure
     * @param procedureID The unique identifier for the procedure
     * @param procedureCode The procedure code
     * @param procedureName The name of the procedure
     * @param description The description of the procedure
     * @param estimatedDuration Duration in minutes
     * @param cost The cost of the procedure
     * @return The created procedure, or null if ID already exists
     */
    public Procedure createProcedure(String procedureID, String procedureCode, String procedureName, 
                                   String description, int estimatedDuration, double cost) {
        // Check if procedure already exists
        if (procedures.containsKey(procedureID)) {
            System.out.println("Error: Procedure with ID " + procedureID + " already exists.");
            return null;
        }
        
        // Create new procedure
        Procedure procedure = new Procedure(procedureID, procedureCode, procedureName, 
                                          description, estimatedDuration, cost);
        procedures.put(procedureID, procedure);
        
        // Save to file after creating
        saveToFile();
        IDGenerator.saveCounters("counter.dat");
        
        return procedure;
    }

    // ===============================
    // READ OPERATIONS
    // ===============================

    /**
     * Get a procedure by ID
     * @param procedureID The procedure ID to search for
     * @return The procedure if found, null otherwise
     */
    public Procedure getProcedureById(String procedureID) {
        return procedures.get(procedureID);
    }

    /**
     * Get all procedures
     * @return OrderedMap containing all procedures
     */
    public OrderedMap<String, Procedure> getAllProcedures() {
        return procedures;
    }

    /**
     * Search procedures by name (partial match)
     * @param name The name to search for
     * @return OrderedMap containing matching procedures
     */
    public OrderedMap<String, Procedure> searchProceduresByName(String name) {
        OrderedMap<String, Procedure> results = new OrderedMap<>();
        
        for (Procedure procedure : procedures) {
            if (procedure.getProcedureName().toLowerCase().contains(name.toLowerCase())) {
                results.put(procedure.getProcedureID(), procedure);
            }
        }
        
        return results;
    }

    /**
     * Get the total number of procedures
     * @return The count of procedures
     */
    public int getProcedureCount() {
        return procedures.size();
    }

    // ===============================
    // UPDATE OPERATIONS
    // ===============================

    /**
     * Update the cost of a procedure
     * @param procedureID The procedure ID
     * @param newCost The new cost
     * @return true if updated successfully, false otherwise
     */
    public boolean updateProcedureCost(String procedureID, double newCost) {
        Procedure procedure = procedures.get(procedureID);
        if (procedure != null) {
            procedure.setCost(newCost);
            saveToFile(); // Save after update
            return true;
        }
        return false;
    }

    // ===============================
    // DELETE OPERATIONS
    // ===============================

    /**
     * Remove a procedure by ID
     * @param procedureID The procedure ID to remove
     * @return The removed procedure, or null if not found
     */
    public Procedure removeProcedure(String procedureID) {
        Procedure removed = procedures.remove(procedureID);
        if (removed != null) {
            saveToFile(); // Save after removal
        }
        return removed;
    }

    /**
     * Check if a procedure exists
     * @param procedureID The procedure ID to check
     * @return true if exists, false otherwise
     */
    public boolean procedureExists(String procedureID) {
        return procedures.containsKey(procedureID);
    }

    // ===============================
    // UTILITY OPERATIONS
    // ===============================

    /**
     * Display all procedures
     */
    public void displayAllProcedures() {
        if (procedures.isEmpty()) {
            System.out.println("No procedures found.");
            return;
        }
        
        System.out.println("=== All Procedures ===");
        int count = 1;
        for (Procedure procedure : procedures) {
            System.out.println(count + ". " + procedure.toString());
            System.out.println("-".repeat(40));
            count++;
        }
    }

    /**
     * Clear all procedures
     */
    public void clearAllProcedures() {
        procedures.clear();
        saveToFile(); // Save after clearing
    }

    // ===============================
    // DATA PERSISTENCE OPERATIONS
    // ===============================

    /**
     * Save procedures to file using DAO
     * @return true if successful, false otherwise
     */
    public boolean saveToFile() {
        return procedureDAO.saveToFile(procedures);
    }

    /**
     * Reload procedures from file
     * @return true if successful, false otherwise
     */
    public boolean loadFromFile() {
        try {
            this.procedures = procedureDAO.retrieveFromFile();
            return true;
        } catch (Exception e) {
            System.err.println("Error loading procedures from file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if procedures file exists
     * @return true if file exists and has data
     */
    public boolean hasDataFile() {
        return procedureDAO.proceduresFileExists();
    }

    /**
     * Get the current file path being used
     * @return the file path as String
     */
    public String getDataFilePath() {
        return procedureDAO.getFilePath();
    }

    /**
     * Create a procedure with auto-generated ID
     * @param procedureCode The procedure code
     * @param procedureName The name of the procedure
     * @param description The description of the procedure
     * @param estimatedDuration Duration in minutes
     * @param cost The cost of the procedure
     * @return The created procedure, or null if creation failed
     */
    public Procedure createProcedureWithAutoID(String procedureCode, String procedureName, 
                                             String description, int estimatedDuration, double cost) {
        String procedureID = IDGenerator.generateProcedureID();
        return createProcedure(procedureID, procedureCode, procedureName, description, estimatedDuration, cost);
    }
}
