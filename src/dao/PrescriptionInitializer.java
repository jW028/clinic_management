package dao;

import adt.CustomADT;
import entity.Medicine;
import entity.Prescription;

public class PrescriptionInitializer {
    public static void main(String[] args) {
        CustomADT<String, Prescription> pendingPrescriptionQueue = new CustomADT<>();
        CustomADT<String, Prescription> processedPrescriptionList = new CustomADT<>();
        CustomADT<String, Medicine> medicationMap = new CustomADT<>();
        MedicineDAO medicineDAO = new MedicineDAO();
        medicationMap = medicineDAO.retrieveFromFile();


        PrescriptionDAO pendingPrescriptionDAO = new PrescriptionDAO(1);
        pendingPrescriptionDAO.saveToFile(pendingPrescriptionQueue);
        PrescriptionDAO processedPrescriptionDAO = new PrescriptionDAO(2);
        processedPrescriptionDAO.saveToFile(processedPrescriptionList);
        System.out.println("Prescriptions initialized and saved.");
    }
}
