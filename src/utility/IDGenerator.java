package utility;

public class IDGenerator {
    private static int treatmentCounter = 1;

    public static String generateTreatmentID() {
        return "T" + String.format("%03d", treatmentCounter++);
    }
}