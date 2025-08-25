package boundary;

import control.DoctorMaintenance;
import control.ScheduleMaintenance;
import entity.Doctor;
import utility.IDGenerator;
import utility.InputHandler;

public class DoctorUI {
    private final DoctorMaintenance doctorMaintenance;
    private final ScheduleMaintenance scheduleMaintenance;

    public DoctorUI() {
        doctorMaintenance = new DoctorMaintenance();
        scheduleMaintenance = new ScheduleMaintenance();
    }

    public void runDoctorMenu() {
        int choice;

        do {
            System.out.println("\n=== Doctor Management Menu ===");
            System.out.println("1. Register Doctor");
            System.out.println("2. Search Doctor");
            System.out.println("3. Update Doctor Details");
            System.out.println("4. Remove Doctor");
            System.out.println("5. List All Doctors");
//            System.out.println("6. View Schedule for Doctor");
            System.out.println("0. Back to Main Menu");
            choice = InputHandler.getInt("Enter your choice", 0, 6);

            switch (choice) {
                case 1 -> registerDoctor();
                case 2 -> searchDoctor();
                case 3 -> updateDoctor();
                case 4 -> removeDoctor();
                case 5 -> listDoctors();
//                case 6 -> viewDoctorSchedule();
                case 0 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    private void registerDoctor() {
        String id = IDGenerator.generateDoctorID();
        System.out.println("Generated Doctor ID: " + id);
        String name = InputHandler.getString("Enter Name");
        String specialty = InputHandler.getString("Enter Specialty");
        String phone = InputHandler.getPhoneNumber("Enter Phone");
        String email = InputHandler.getEmail("Enter Email");
        String address = InputHandler.getString("Enter Address");
        String gender = InputHandler.getGender("Select Gender");
        String dob = InputHandler.getString("Enter Date of Birth (YYYY-MM-DD)");

        Doctor doctor = new Doctor(id, name, specialty, phone, email, address, gender, dob);
        if (doctorMaintenance.registerDoctor(doctor)) {
            System.out.println("Doctor registered successfully.");
        } else {
            System.out.println("Doctor ID already exists.");
        }
    }

    private void searchDoctor() {
        System.out.println("\n=== Search Doctor ===");
        System.out.println("1. By ID");
        System.out.println("2. By Name");
        System.out.println("3. By Gender");
        System.out.println("4. By Specialty");
        int choice = InputHandler.getInt("Choose search option", 1, 4);

        Doctor[] results = new Doctor[0];

        switch (choice) {
            case 1 -> {
                String id = InputHandler.getString("Enter Doctor ID");
                Doctor doctor = doctorMaintenance.getDoctor(id);
                if (doctor != null) {
                    results = new Doctor[]{doctor};
                }
            }
            case 2 -> {
                String name = InputHandler.getString("Enter Name");
                results = doctorMaintenance.searchByName(name);
            }
            case 3 -> {
                String gender = InputHandler.getGender("Select Gender");
                results = doctorMaintenance.searchByGender(gender);
            }
            case 4 -> {
                String specialty = InputHandler.getString("Enter Specialty");
                results = doctorMaintenance.searchBySpecialty(specialty);
            }
        }

        if (results.length == 0) {
            System.out.println("No matching doctors found.");
            return;
        }

        displayDoctorTable(results);

//        System.out.println("\nDo you want to sort the results?");
//        System.out.println("1. By ID");
//        System.out.println("2. By Name");
//        System.out.println("3. By Specialty");
//        System.out.println("4. By Gender");
//        System.out.println("0. No Sorting");
//        int sortChoice = InputHandler.getInt("Choose option", 0, 4);
//
//        Doctor[] sorted = results;
//        switch (sortChoice) {
//            case 1 -> sorted = doctorMaintenance.sortByID(true);
//            case 2 -> sorted = doctorMaintenance.sortByName(true);
//            case 3 -> sorted = doctorMaintenance.sortBySpecialty(true);
//            case 4 -> sorted = doctorMaintenance.sortByGender(true);
//        }
//
//        if (sortChoice != 0) {
//            System.out.println("\n=== Sorted Results ===");
//            displayDoctorTable(sorted);
//        }
    }
    private void listDoctors() {
        Doctor[] doctors = doctorMaintenance.getAllDoctorsArray();

        if (doctors.length == 0) {
            System.out.println("No doctors registered.");
            return;
        }

        System.out.println("\n=== All Doctors ===");
        displayDoctorTable(doctors);

        System.out.println("\nDo you want to sort the list?");
        System.out.println("1. By ID");
        System.out.println("2. By Name");
        System.out.println("3. By Specialty");
        System.out.println("4. By Gender");
        System.out.println("0. Exit without sorting");
        int sortChoice = InputHandler.getInt("Choose option", 0, 4);

        Doctor[] sorted = doctors;
        switch (sortChoice) {
            case 1 -> sorted = doctorMaintenance.sortByID(true);
            case 2 -> sorted = doctorMaintenance.sortByName(true);
            case 3 -> sorted = doctorMaintenance.sortBySpecialty(true);
            case 4 -> sorted = doctorMaintenance.sortByGender(true);
        }

        if (sortChoice != 0) {
            System.out.println("\n=== Doctors (Sorted) ===");
            displayDoctorTable(sorted);
        }
    }

    private void displayDoctorTable(Doctor[] doctors) {
        System.out.printf(
                "%-8s | %-20s | %-15s | %-13s | %-25s | %-25s | %-8s | %-12s\n",
                "ID", "Name", "Specialty", "Phone", "Email", "Address", "Gender", "DOB");
        System.out.println("-----------------------------------------------------------------------------------------------"
                + "------------------------------------------------------");

        for (Doctor doctor : doctors) {
            System.out.printf(
                    "%-8s | %-20s | %-15s | %-13s | %-25s | %-25s | %-8s | %-12s\n",
                    doctor.getDoctorID(), doctor.getName(), doctor.getSpecialty(), doctor.getPhone(),
                    doctor.getEmail(), doctor.getAddress(), doctor.getGender(), doctor.getDateOfBirth());
        }
    }


    private void updateDoctor() {
        String id = InputHandler.getString("Enter Doctor ID to update");
        Doctor doctor = doctorMaintenance.getDoctor(id);

        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        boolean keepUpdating = true;
        while (keepUpdating) {
            System.out.println("\n=== Update Doctor Details ===");
            System.out.println("1. Name        [" + doctor.getName() + "]");
            System.out.println("2. Specialty   [" + doctor.getSpecialty() + "]");
            System.out.println("3. Phone       [" + doctor.getPhone() + "]");
            System.out.println("4. Email       [" + doctor.getEmail() + "]");
            System.out.println("5. Address     [" + doctor.getAddress() + "]");
            System.out.println("6. Gender      [" + doctor.getGender() + "]");
            System.out.println("7. Date of Birth [" + doctor.getDateOfBirth() + "]");
            System.out.println("0. Done");

            int choice = InputHandler.getInt("Select field to update", 0, 7);

            switch (choice) {
                case 1 -> {
                    String newName = InputHandler.getString("Enter New Name");
                    doctorMaintenance.updateDoctorField(id, "name", newName);
                }
                case 2 -> {
                    String newSpecialty = InputHandler.getString("Enter New Specialty");
                    doctorMaintenance.updateDoctorField(id, "specialty", newSpecialty);
                }
                case 3 -> {
                    String newPhone = InputHandler.getPhoneNumber("Enter New Phone");
                    doctorMaintenance.updateDoctorField(id, "phone", newPhone);
                }
                case 4 -> {
                    String newEmail = InputHandler.getEmail("Enter New Email");
                    doctorMaintenance.updateDoctorField(id, "email", newEmail);
                }
                case 5 -> {
                    String newAddress = InputHandler.getString("Enter New Address");
                    doctorMaintenance.updateDoctorField(id, "address", newAddress);
                }
                case 6 -> {
                    String newGender = InputHandler.getGender("Select New Gender");
                    doctorMaintenance.updateDoctorField(id, "gender", newGender);
                }
                case 7 -> {
                    String newDob = InputHandler.getString("Enter New Date of Birth (YYYY-MM-DD)");
                    doctorMaintenance.updateDoctorField(id, "dob", newDob);
                }
                case 0 -> keepUpdating = false;
            }
        }

        System.out.println("Doctor details updated.");
    }

    private void removeDoctor() {
        String id = InputHandler.getString("Enter Doctor ID to remove");
        if (doctorMaintenance.removeDoctor(id)) {
            System.out.println("Doctor removed.");
        } else {
            System.out.println("Doctor not found.");
        }
    }

//    private void viewDoctorSchedule() {
//        String doctorID = InputHandler.getString("Enter Doctor ID to manage schedule");
//        scheduleMaintenance.displayCalendar(doctorID);
//
//        // Allow them to jump into date-specific management, like ScheduleUI
//        while (true) {
//            int day = InputHandler.getInt("Enter a date (1–31) or 0 to exit", 0, 31);
//            if (day == 0) break;
//            LocalDate date = LocalDate.now().withDayOfMonth(day);
//            new ScheduleUI().manageDateSchedule(doctorID, date.toString());
//        }
}