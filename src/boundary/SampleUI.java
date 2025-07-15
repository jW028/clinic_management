package boundary;

import control.TreatmentMaintenance;
import entity.Treatment;
import java.util.Scanner;

public class SampleUI {
    private final TreatmentMaintenance treatmentMaintenance;
    private final Scanner scanner;

    public SampleUI(TreatmentMaintenance treatmentMaintenance) {
        this.treatmentMaintenance = treatmentMaintenance;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        System.out.println("\nTreatment Management");
        System.out.println("1. Add Treatment");
        System.out.println("2. View Critical Treatments");
        System.out.println("3. Exit");
    }

    public void handleAddTreatment() {
        System.out.println("\nAdd New Treatment");
        // Collect treatment data from user
        // Create and add treatment using treatmentMaintenance
    }
}