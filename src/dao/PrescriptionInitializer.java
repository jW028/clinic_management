package dao;

import adt.OrderedMap;
import entity.Medicine;
import entity.Prescription;

public class PrescriptionInitializer {
    public static void main(String[] args) {
        OrderedMap<String, Prescription> pendingPrescriptionQueue = new OrderedMap<>();
        OrderedMap<String, Prescription> processedPrescriptionList = new OrderedMap<>();
        OrderedMap<String, Medicine> medicationMap = new OrderedMap<>();
        MedicineDAO medicineDAO = new MedicineDAO();
        medicationMap = medicineDAO.retrieveFromFile();


        PrescriptionDAO pendingPrescriptionDAO = new PrescriptionDAO(1);
        pendingPrescriptionDAO.saveToFile(pendingPrescriptionQueue);
        PrescriptionDAO processedPrescriptionDAO = new PrescriptionDAO(2);
        processedPrescriptionDAO.saveToFile(processedPrescriptionList);
        System.out.println("Prescriptions initialized and saved.");
    }
}
