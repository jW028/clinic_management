package entity;
import java.io.Serializable;

public class Procedure implements Serializable {
    // Core identifiers
    private String procedureID;
    private String procedureCode;
    private String procedureName;

    // Clinical details
    private String description;
    private int estimatedDuration; // in minutes

    // Cost and billing
    private double cost;

    public Procedure(String procedureID, String procedureCode, String procedureName, 
                    String description, int estimatedDuration, double cost) {
        this.procedureID = procedureID;
        this.procedureCode = procedureCode;
        this.procedureName = procedureName;
        this.description = description;
        this.estimatedDuration = estimatedDuration;
        this.cost = cost;
    }

    // Getters
    public String getProcedureID() { return procedureID; }
    public String getProcedureCode() { return procedureCode; }
    public String getProcedureName() { return procedureName; }
    public String getDescription() { return description; }
    public int getEstimatedDuration() { return estimatedDuration; }
    public double getCost() { return cost; }

    // Setters
    public void setCost(double cost) { this.cost = cost; }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Procedure: ").append(procedureName)
            .append(" (").append(procedureCode).append(")\n")
            .append("Description: ").append(description).append("\n")
            .append("Estimated Duration: ").append(estimatedDuration).append(" minutes\n")
            .append("Cost: RM").append(String.format("%.2f", cost));

            return sb.toString();
    }
}
