package utility;

public class IDGenerator {
    private static int treatmentCounter = 1;
    private static int consultationCounter = 1;

    public static String generateTreatmentID() {
        return "T" + String.format("%03d", treatmentCounter++);
    }

    public static String generateConsultationID() {
        return "C" + String.format("%03d", consultationCounter++);
    }
}