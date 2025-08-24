package utility;

import java.io.*;

public class IDGenerator {
    private static int treatmentCounter = 1;
    private static int procedureCounter = 1;
    private static int diagnosisCounter = 1;
    private static int consultationCounter = 1;
    private static int doctorCounter = 1;
    private static int patientCounter = 1;
    private static int transactionCounter = 1;
    private static int visitCounter = 1;
    private static int medicineCounter = 6;
    private static int prescriptionCounter = 4;

    public static String generateTreatmentID() {
        return "T" + String.format("%03d", treatmentCounter++);
    }

    public static String generatePatientID() {
        return "P" + String.format("%03d", patientCounter++);
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
    public static String generateTransactionID() { return "TX" + String.format("%03d", transactionCounter++);}
    public static String generateVisitID() {
        return "V" + String.format("%03d", visitCounter++);
    }
    public static String generateMedicineID(){
        return "M" + String.format("%03d", medicineCounter++);
    }

    public static String generatePrescriptionID() {
        return "PS" + String.format("%03d", prescriptionCounter++);
    }

    public static void saveCounters(String filename){
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(treatmentCounter);
            writer.println(procedureCounter);
            writer.println(diagnosisCounter);
            writer.println(consultationCounter);
            writer.println(doctorCounter);
            writer.println(patientCounter);
            writer.println(transactionCounter);
            writer.println(visitCounter);
            writer.println(medicineCounter);
            writer.println(prescriptionCounter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadCounter(String filename){
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            treatmentCounter = Integer.parseInt(reader.readLine());
            procedureCounter = Integer.parseInt(reader.readLine());
            diagnosisCounter = Integer.parseInt(reader.readLine());
            consultationCounter = Integer.parseInt(reader.readLine());
            doctorCounter = Integer.parseInt(reader.readLine());
            patientCounter = Integer.parseInt(reader.readLine());
            transactionCounter = Integer.parseInt(reader.readLine());
            visitCounter = Integer.parseInt(reader.readLine());
            medicineCounter = Integer.parseInt(reader.readLine());
            prescriptionCounter = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        IDGenerator.saveCounters("counter.dat");
        System.out.println("Counters saved to counter.dat");
    }
}

