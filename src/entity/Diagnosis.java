package entity;
import java.io.Serializable;

public class Diagnosis implements Serializable {
    private String id;
    private String name;
    private String description;
    private String severity;

    public Diagnosis (String id, String name, String severity) {
        this.id = id;
        this.name = name;
        this.severity = severity;
    }

    // Getters 
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSeverity() {
        return severity;
    }

    // Setters
    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    // Display
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n")
          .append("Description: ").append(description).append("\n")
          .append("Severity: ").append(severity).append("\n");
        return sb.toString();
    }

}
