package entity;

public class PrescribedMedicine {
    private Medicine medicine;
    private int quantity;
    private String dosage;
    private String frequency;
    private String description;

    public PrescribedMedicine(Medicine medicine, int quantity, String dosage, String frequency, String description) {
        this.medicine = medicine;
        this.quantity = quantity;
        this.dosage = dosage;
        this.frequency = frequency;
        this.description = description;
    }

    public double calculateSubtotal() {
        return medicine.getPrice() * quantity;
    }

    public boolean isValidDosage(){
        return dosage != null && !dosage.trim().isEmpty() &&
                frequency != null && !frequency.trim().isEmpty()
                && quantity > 0;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDosage() {
        return dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getDescription() {
        return description;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }




    @Override
    public String toString() {
        return String.format(
                "Medicine: %s | Quantity: %d | Dosage: %s | Frequency: %s | Description: %s",
                medicine.getName(), quantity, dosage, frequency, description
        );
    }
}

