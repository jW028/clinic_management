package entity;

public class Prescription {
    private String prescriptionID;
    private String treatmentID;
    private String medicationID;
    private int quantity;

    public Prescription(String prescriptionID, String treatmentID, String medicationID, int quantity) {
        this.prescriptionID = prescriptionID;
        this.treatmentID = treatmentID;
        this.medicationID = medicationID;
        this.quantity = quantity;
    }

    public String getPrescriptionID() {
        return prescriptionID;
    }

    public String getTreatmentID() {
        return treatmentID;
    }

    public String getMedicationID() {
        return medicationID;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return String.format("Prescription ID: %s, Treatment ID: %s, Medication ID: %s, Quantity: %d",
                prescriptionID, treatmentID, medicationID, quantity);
    }
}
