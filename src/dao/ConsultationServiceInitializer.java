package dao;

import adt.CustomADT;
import entity.ConsultationService;

public class ConsultationServiceInitializer {
    public static void main(String[] args) {
        CustomADT<String, ConsultationService> serviceMap = new CustomADT<>();
        serviceMap.put("S001", new ConsultationService("S001", "Blood Test", 50.0));
        serviceMap.put("S002", new ConsultationService("S002", "X-Ray", 100.0));
        serviceMap.put("S003", new ConsultationService("S003", "ECG", 150.0));
        serviceMap.put("S004", new ConsultationService("S004", "MRI", 200.0));
        serviceMap.put("S005", new ConsultationService("S005", "Ultrasound", 80.0));

        ConsultationServiceDAO dao = new ConsultationServiceDAO();
        dao.saveToFile(serviceMap);
        System.out.println("Consultation services initialized and saved.");
    }
}
