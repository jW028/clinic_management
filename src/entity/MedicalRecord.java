package entity;

public class MedicalRecord {
    private final String date;
    private final String diagnosis;
    private final String treatment;

    public MedicalRecord(String date, String diagnosis, String treatment) {
        this.date = date;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    @Override
    public String toString() {
        return String.format("[%s] Diagnosis: %-20s | Treatment: %s", date, diagnosis, treatment);
    }
}
