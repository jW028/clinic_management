package dao;

import adt.CustomADT;
import entity.Medicine;
import entity.Prescription;

public class PrescriptionInitializer {
    public static void main(String[] args) {
        CustomADT<String, Prescription> prescriptionQueue = new CustomADT<>();
        CustomADT<String, Medicine> medicationMap = new CustomADT<>();
        MedicineDAO medicineDAO = new MedicineDAO();
        medicationMap = medicineDAO.retrieveFromFile();

        Prescription p1 = new Prescription("PS001", "T001");
        Prescription p2 = new Prescription("PS002", "T002");
        Prescription p3 = new Prescription("PS003", "T003");

        p1.addMedicine(medicationMap.get("MED001"), 2, "500mg", "Twice a day", "For fever");
        p1.addMedicine(medicationMap.get("MED002"), 1, "200mg", "Once a day", "For pain relief");
        p2.addMedicine(medicationMap.get("MED003"), 3, "250mg", "Three times a day", "For bacterial infection");
        p2.addMedicine(medicationMap.get("MED004"), 1, "10mg", "Once a day", "For allergy relief");
        p3.addMedicine(medicationMap.get("MED005"), 2, "20mg", "Once a day", "For acid reflux");
        prescriptionQueue.offer(p1.getPrescriptionID(), p1);
        prescriptionQueue.offer(p2.getPrescriptionID(), p2);
        prescriptionQueue.offer(p3.getPrescriptionID(), p3);

        PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
        prescriptionDAO.saveToFile(prescriptionQueue);
        System.out.println("Prescriptions initialized and saved.");
    }
}
