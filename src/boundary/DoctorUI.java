package boundary;

import control.DoctorMaintenance;
import control.ScheduleMaintenance;
import entity.Doctor;
import entity.Schedule;
import utility.IDGenerator;
import utility.InputHandler;

import java.time.LocalDate;

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
            System.out.println("2. View Doctor by ID");
            System.out.println("3. Update Doctor Details");
            System.out.println("4. Remove Doctor");
            System.out.println("5. List All Doctors");
//            System.out.println("6. View Schedule for Doctor");
            System.out.println("0. Back to Main Menu");
            choice = InputHandler.getInt("Enter your choice", 0, 6);

            switch (choice) {
                case 1 -> registerDoctor();
                case 2 -> viewDoctor();
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
            System.out.println("✅ Doctor registered successfully.");
        } else {
            System.out.println("❌ Doctor ID already exists.");
        }
    }

    private void viewDoctor() {
        String id = InputHandler.getString("Enter Doctor ID");
        Doctor doctor = doctorMaintenance.getDoctor(id);
        if (doctor != null) {
            System.out.println("\n" + doctor);
        } else {
            System.out.println("❌ Doctor not found.");
        }
    }

    private void updateDoctor() {
        String id = InputHandler.getString("Enter Doctor ID to update");
        Doctor doctor = doctorMaintenance.getDoctor(id);

        if (doctor != null) {
            System.out.println("Leave blank to keep existing value.");

            System.out.print("New Name [" + doctor.getName() + "]: ");
            String name = InputHandler.getOptionalString("");
            System.out.print("New Specialty [" + doctor.getSpecialty() + "]: ");
            String specialty = InputHandler.getOptionalString("");
            System.out.print("New Phone [" + doctor.getPhone() + "]: ");
            String phone = InputHandler.getOptionalString("");
            System.out.print("New Email [" + doctor.getEmail() + "]: ");
            String email = InputHandler.getOptionalString("");
            System.out.print("New Address [" + doctor.getAddress() + "]: ");
            String address = InputHandler.getOptionalString("");
            System.out.print("New Gender [" + doctor.getGender() + "]: ");
            String gender = InputHandler.getOptionalString("");
            System.out.print("New Date of Birth [" + doctor.getDateOfBirth() + "]: ");
            String dob = InputHandler.getOptionalString("");

            if (!name.isEmpty()) doctor.setName(name);
            if (!specialty.isEmpty()) doctor.setSpecialty(specialty);
            if (!phone.isEmpty()) doctor.setPhone(phone);
            if (!email.isEmpty()) doctor.setEmail(email);
            if (!address.isEmpty()) doctor.setAddress(address);
            if (!gender.isEmpty()) doctor.setGender(gender);
            if (!dob.isEmpty()) doctor.setDateOfBirth(dob);

            System.out.println("✅ Doctor details updated.");
        } else {
            System.out.println("❌ Doctor not found.");
        }
    }

    private void removeDoctor() {
        String id = InputHandler.getString("Enter Doctor ID to remove");
        if (doctorMaintenance.removeDoctor(id)) {
            System.out.println("🗑️ Doctor removed.");
        } else {
            System.out.println("❌ Doctor not found.");
        }
    }

    private void listDoctors() {
        System.out.println("\n--- All Registered Doctors ---");
        doctorMaintenance.listAllDoctors();
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