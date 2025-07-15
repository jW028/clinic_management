package dao;

import entity.Treatment;
import adt.CustomADT;

public class TreatmentDAO {
    private final CustomADT treatmentStorage;

    public TreatmentDAO() {
        treatmentStorage = new CustomADT();
    }

    public void save(Treatment treatment) {
        treatmentStorage.enqueue(treatment);
    }

    public Treatment find(String treatmentID) {
        CustomADT temp = new CustomADT();
        Treatment found = null;

        while (!treatmentStorage.isEmpty()) {
            Treatment t = (Treatment)treatmentStorage.dequeue();
            temp.enqueue(t);
            if (t.getTreatmentID().equals(treatmentID)) {
                found = t;
                break;
            }
        }

        // Restore storage
        while (!temp.isEmpty()) {
            treatmentStorage.enqueue(temp.dequeue());
        }

        return found;
    }
}