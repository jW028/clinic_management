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

        Prescription p1 = new Prescription("PS001", "T001");
        Prescription p2 = new Prescription("PS002", "T002");
        Prescription p3 = new Prescription("PS003", "T003");

        p1.addMedicine(medicationMap.get("M001"), 2, "500mg", "Twice a day", "For fever");
        p1.addMedicine(medicationMap.get("M002"), 1, "200mg", "Once a day", "For pain relief");
        p2.addMedicine(medicationMap.get("M003"), 3, "250mg", "Three times a day", "For bacterial infection");
        p2.addMedicine(medicationMap.get("M004"), 1, "10mg", "Once a day", "For allergy relief");
        p3.addMedicine(medicationMap.get("M005"), 2, "20mg", "Once a day", "For acid reflux");
        pendingPrescriptionQueue.offer(p1.getPrescriptionID(), p1);
        pendingPrescriptionQueue.offer(p2.getPrescriptionID(), p2);
        pendingPrescriptionQueue.offer(p3.getPrescriptionID(), p3);

        PrescriptionDAO pendingPrescriptionDAO = new PrescriptionDAO(1);
        pendingPrescriptionDAO.saveToFile(pendingPrescriptionQueue);
        PrescriptionDAO processedPrescriptionDAO = new PrescriptionDAO(2);
        processedPrescriptionDAO.saveToFile(processedPrescriptionList);
        System.out.println("Prescriptions initialized and saved.");
    }
}
