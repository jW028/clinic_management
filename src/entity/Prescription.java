package entity;

import adt.CustomADT;

import java.io.Serializable;

public class Prescription implements Serializable {
    private String prescriptionID;
    private String treatmentID;
    private CustomADT<String, PrescribedMedicine> medicines;
    private double totalPrice;
    private String status;


    public Prescription(String prescriptionID,String treatmentID){
        this.prescriptionID = prescriptionID;
        this.treatmentID = treatmentID;
        this.medicines = new CustomADT<String, PrescribedMedicine>();
        this.totalPrice = 0.0;
        this.status = "PENDING";
    }

    public void addMedicine(Medicine medicine, int quantity, String dosage, String frequency, String description) {
        PrescribedMedicine prescribedMedicine = new PrescribedMedicine(medicine, quantity, dosage, frequency, description);
        if (prescribedMedicine.isValidDosage()) {
            medicines.put(medicine.getId(), prescribedMedicine);
            totalPrice += prescribedMedicine.calculateSubtotal();
        } else {
            throw new IllegalArgumentException("Invalid dosage or frequency for medicine: " + medicine.getName());
        }
    }

    public CustomADT<String, PrescribedMedicine> getMedicines() {
        return medicines;
    }

    public void calculateTotalPrice() {
        totalPrice = 0.0;
        for (int i = 0; i < medicines.size(); i++) {
            PrescribedMedicine pm = medicines.get(i);
            totalPrice += pm.calculateSubtotal();
        }
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getPrescriptionID() {
        return prescriptionID;
    }

    public String getTreatmentID(){
        return treatmentID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Prescription ID: ").append(prescriptionID)
                .append("\nTreatment ID: ").append(treatmentID)
                .append("\nTotal Price: ").append(totalPrice)
                .append("\nPrescribed Medicines:\n");
        for (int i = 0; i < medicines.size(); i++) {
            PrescribedMedicine pm = medicines.get(i);
            sb.append(i + 1).append(". ").append(pm.toString()).append("\n");
        }
        return sb.toString();
    }

}
