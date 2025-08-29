import boundary.*;
import control.*;
import utility.*;


public class ClinicManagement {
    
    public static void main(String[] args) {
            PatientMaintenance patientController = new PatientMaintenance();

            int choice;
            do {
                System.out.println("=======Menu=======");
                System.out.println("1. Student");
                System.out.println("2. Admin");
                System.out.println("0. Exit");
                choice = InputHandler.getInt(0, 2);
                if (choice == 1) {
                    StudentUI studentUI = new StudentUI(patientController);
                    studentUI.displayMenu();
                } else if (choice == 2) {
                    AdminUI adminUI = new AdminUI(patientController);
                    adminUI.displayMenu();
                }
            } while (choice != 0);

            System.out.println("Exiting Clinic Management System...");
    }
}
    
    