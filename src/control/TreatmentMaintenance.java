package control;

import entity.*;
import adt.CustomADT;

public class TreatmentMaintenance {
    private final CustomADT treatments;

    public TreatmentMaintenance() {
        treatments = new CustomADT();
    }

    public void addTreatment(Treatment treatment) {
        treatments.enqueue(treatment);
    }

    public CustomADT getCriticalTreatments() {
        CustomADT critical = new CustomADT();
        CustomADT temp = new CustomADT();

        while (!treatments.isEmpty()) {
            Treatment t = (Treatment)treatments.dequeue();
            temp.enqueue(t);
            if (t.isCritical()) {
                critical.enqueue(t);
            }
        }

        // Restore original treatments
        while (!temp.isEmpty()) {
            treatments.enqueue(temp.dequeue());
        }

        return critical;
    }
}