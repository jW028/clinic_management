package dao;

import adt.CustomADT;
import entity.Patient;

public class PatientQueueInitializer {
    public static void main(String[] args) {
        CustomADT<String, Patient> patientEmergencyQueue = new CustomADT<>();
        CustomADT<String, Patient> patientNormalQueue = new CustomADT<>();

        PatientQueueDAO emergencyQueueDAO = new PatientQueueDAO(1);
        emergencyQueueDAO.saveToFile(patientEmergencyQueue);
        PatientQueueDAO normalQueueDAO = new PatientQueueDAO(2);
        normalQueueDAO.saveToFile(patientNormalQueue);
        System.out.println("Emergency and Normal Patient queue initialized and saved to file.");
    }
}
