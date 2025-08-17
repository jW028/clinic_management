package dao;

import adt.CustomADT;
import entity.Medicine;


public class MedicineInitializer {
    public static void main(String[] args) {
        CustomADT<String, Medicine> medicationMap = new CustomADT<>();

        medicationMap.put("MED001", new Medicine("MED001", "Paracetamol", 100, 5.00, "Pain reliever and fever reducer"));
        medicationMap.put("MED002", new Medicine("MED002", "Ibuprofen", 50, 10.00, "Anti-inflammatory and pain reliever"));
        medicationMap.put("MED003", new Medicine("MED003", "Amoxicillin", 30, 15.00, "Antibiotic for bacterial infections"));
        medicationMap.put("MED004", new Medicine("MED004", "Cetirizine", 80, 8.00, "Antihistamine for allergy relief"));
        medicationMap.put("MED005", new Medicine("MED005", "Omeprazole", 60, 20.00, "Proton pump inhibitor for acid reflux"));

        MedicineDAO dao = new MedicineDAO();
        dao.saveToFile(medicationMap);
        System.out.println("Medicines initialized and saved.");
    }
}
