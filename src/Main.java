import control.PatientMaintenance;
import boundary.PatientUI;

public class Main {
    public static void main(String[] args) {
        PatientMaintenance patientMaintenance = new PatientMaintenance();
        PatientUI ui = new PatientUI(patientMaintenance);
        ui.displayMenu();
    }
}