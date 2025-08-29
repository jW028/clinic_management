package dao;

import adt.OrderedMap;
import entity.Patient;

public class PatientQueueInitializer {
    public static void main(String[] args) {
        OrderedMap<String, Patient> patientEmergencyQueue = new OrderedMap<>();
        OrderedMap<String, Patient> patientNormalQueue = new OrderedMap<>();

        PatientQueueDAO emergencyQueueDAO = new PatientQueueDAO(1);
        emergencyQueueDAO.saveToFile(patientEmergencyQueue);
        PatientQueueDAO normalQueueDAO = new PatientQueueDAO(2);
        normalQueueDAO.saveToFile(patientNormalQueue);
        System.out.println("Emergency and Normal Patient queue initialized and saved to file.");
    }
}
