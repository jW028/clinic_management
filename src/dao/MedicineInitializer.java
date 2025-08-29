package dao;

import adt.OrderedMap;
import entity.Medicine;


public class MedicineInitializer {
    public static void main(String[] args) {
        OrderedMap<String, Medicine> medicationMap = new OrderedMap<>();

        medicationMap.put("M001", new Medicine("M001", "Paracetamol", 100, 5.00, "Pain reliever and fever reducer"));
        medicationMap.put("M002", new Medicine("M002", "Ibuprofen", 50, 10.00, "Anti-inflammatory and pain reliever"));
        medicationMap.put("M003", new Medicine("M003", "Amoxicillin", 30, 15.00, "Antibiotic for bacterial infections"));
        medicationMap.put("M004", new Medicine("M004", "Cetirizine", 80, 8.00, "Antihistamine for allergy relief"));
        medicationMap.put("M005", new Medicine("M005", "Omeprazole", 60, 20.00, "Proton pump inhibitor for acid reflux"));

        MedicineDAO dao = new MedicineDAO();
        dao.saveToFile(medicationMap);
        System.out.println("Medicines initialized and saved.");
    }
}
