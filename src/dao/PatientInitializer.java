package dao;

import entity.Patient;
import adt.CustomADT;

public class PatientInitializer {
    public static CustomADT<String, Patient> initializePatients() {
        CustomADT<String, Patient> patients = new CustomADT<>();
        patients.put("P001", new Patient("P001", "Alice", 19, "Female", "1234567890", "123 Main St", false));
        patients.put("P002", new Patient("P002", "Bob", 40, "Male", "0987654321", "456 Elm St", false));
        return patients;
    }

    public static void main(String[] args) {
        CustomADT<String, Patient> patients = initializePatients();
        // Display initialized patients
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            System.out.println("Patient ID: " + patient.getPatientId() + ", Name: " + patient.getName());
        }
    }
}

//Save to file
//package dao;
//
//import entity.Patient;
//import adt.CustomADT;
//
//public class PatientInitializer {
//    public static CustomADT<String, Patient> initializePatients() {
//        CustomADT<String, Patient> patientMap = new CustomADT<>();
//        // patients.put("P001", new Patient("P001", "Alice", 19, "Female", "1234567890", "123 Main St", false));
//        // patients.put("P002", new Patient("P002", "Bob", 40, "Male", "0987654321", "456 Elm St", false));
//
//        patientMap.put("P001", new Patient("P001", "Alice", 19, "Female", "1234567890", "123 Main St", false));
//        patientMap.put("P002", new Patient("P002", "Bob", 40, "Male", "0987654321", "456 Elm St", false));
//        patientMap.put("P003", new Patient("P003", "Zoer", 19, "Female", "1234567890", "123 Main St", false));
//        patientMap.put("P004", new Patient("P004", "Star", 40, "Male", "0987654321", "456 Elm St", false));
//        patientMap.put("P005", new Patient("P005", "Lace", 19, "Female", "1234567890", "123 Main St", false));
//        patientMap.put("P006", new Patient("P006", "Wayne", 40, "Male", "0987654321", "456 Elm St", false));
//        patientMap.put("P007", new Patient("P007", "Din", 19, "Female", "1234567890", "123 Main St", false));
//        patientMap.put("P008", new Patient("P008", "Yej", 40, "Male", "0987654321", "456 Elm St", false));
//        patientMap.put("P009", new Patient("P009", "Chaer", 19, "Female", "1234567890", "123 Main St", false));
//        patientMap.put("P010", new Patient("P010", "Ryu", 40, "Male", "0987654321", "456 Elm St", false));
//
//        PatientDAO dao = new PatientDAO();
//        dao.saveToFile(patientMap);
//        System.out.println("Patients initialized and saved.");
//
//        return patientMap;
//    }
//
//    public static void main(String[] args) {
//        CustomADT<String, Patient> patients = initializePatients();
//        // Display initialized patients
//        for (int i = 0; i < patients.size(); i++) {
//            Patient patient = patients.get(i);
//            System.out.println("Patient ID: " + patient.getPatientId() + ", Name: " + patient.getName());
//        }
//    }
//}