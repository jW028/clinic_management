package utility;

public class IDGenerator {
    private static int treatmentCounter = 1;
    private static int procedureCounter = 1;
    private static int diagnosisCounter = 1;
    private static int consultationCounter = 1;
    private static int doctorCounter = 1;

    public static String generateTreatmentID() {
        return "T" + String.format("%03d", treatmentCounter++);
    }

    public static String generateProcedureID() {
        return "P" + String.format("%03d", procedureCounter++);
    }

    public static String generateDiagnosisID() {
        return "D" + String.format("%03d", diagnosisCounter++);
    }

    public static String generateConsultationID() {
        return "C" + String.format("%03d", consultationCounter++);
    }
    public static String generateDoctorID() {
        return "D" + String.format("%03d", doctorCounter++);
    }
}