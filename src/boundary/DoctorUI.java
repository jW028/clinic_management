package boundary;

import control.DoctorMaintenance;
import control.ScheduleMaintenance;
import boundary.ScheduleUI;
import entity.Doctor;
import utility.IDGenerator;
import utility.InputHandler;
import adt.OrderedMap;

public class DoctorUI {
    private final DoctorMaintenance doctorMaintenance;
    private final ScheduleMaintenance scheduleMaintenance;
    private final ScheduleUI scheduleUI;

    public DoctorUI() {
        this.doctorMaintenance = new DoctorMaintenance();
        this.scheduleMaintenance = new ScheduleMaintenance();
        this.scheduleUI = new ScheduleUI();
    }

    public void runDoctorMenu() {
        int choice;
        do {
            printMenu();
            choice = InputHandler.getInt("Enter your choice", 0, 9);

            switch (choice) {
                case 1 -> registerDoctor();
                case 2 -> searchDoctor();
                case 3 -> updateDoctor();
                case 4 -> removeDoctor();
                case 5 -> listDoctors();
                case 6 -> scheduleUI.runScheduleMenu();
                case 7 -> showRecentActionsWithUndo();
                case 8 -> showSpecializationAnalytics();
                case 9 -> showDoctorReports();
                case 0 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    public void printMenu() {
        System.out.println("\n┌" + "─".repeat(42) + "┐");
        System.out.println("│          DOCTOR MANAGEMENT MENU          │");
        System.out.println("├" + "─".repeat(42) + "┤");
        System.out.println("│ 1. Register Doctor                       │");
        System.out.println("│ 2. Search Doctor                         │");
        System.out.println("│ 3. Update Doctor                         │");
        System.out.println("│ 4. Remove Doctor                         │");
        System.out.println("│ 5. List All Doctors                      │");
        System.out.println("│ 6. View Doctors Schedule                 │");
        System.out.println("│ 7. Recent History                        │");
        System.out.println("│ 8. Doctor Specialization Analytics       │");
        System.out.println("│ 9. Reports and Statistics                │");
        System.out.println("│ 0. Back to Main Menu                     │");
        System.out.println("└" + "─".repeat(42) + "┘");
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
        int experience = InputHandler.getInt("Enter Years of Experience", 0, 100);

        Doctor doctor = new Doctor(id, name, specialty, phone, email, address, gender, experience);
        if (doctorMaintenance.registerDoctor(doctor)) {
            System.out.println("Doctor registered successfully.");
        } else {
            System.out.println("Doctor ID already exists.");
        }
    }

    private void searchDoctor() {
        System.out.println("\n┌" + "─".repeat(35) + "┐");
        System.out.println("│            Search Doctor          │");
        System.out.println("├" + "─".repeat(35) + "┤");
        System.out.println("│ 1. By ID                          │");
        System.out.println("│ 2. By Name                        │");
        System.out.println("│ 3. By Gender                      │");
        System.out.println("│ 4. By Specialty                   │");
        System.out.println("│ 5. By Years of Experience         │");
        System.out.println("└" + "─".repeat(35) + "┘");
        int choice = InputHandler.getInt("Choose search option", 1, 5);

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
            case 5 -> {
                int exp = InputHandler.getInt("Enter Years of Experience", 0, 100);
                results = doctorMaintenance.searchByExperience(exp);
            }
        }

        if (results.length == 0) {
            System.out.println("No matching doctors found.");
            return;
        }

        displayDoctorTable(results);
    }

    private void listDoctors() {
        Doctor[] doctors = doctorMaintenance.getAllDoctorsArray();

        if (doctors.length == 0) {
            System.out.println("No doctors registered.");
            return;
        }

        System.out.println("\n=== All Doctors ===");
        displayDoctorTable(doctors);

        System.out.println("\n┌" + "─".repeat(35) + "┐");
        System.out.println("│      Sort Doctor List             │");
        System.out.println("├" + "─".repeat(35) + "┤");
        System.out.println("│ 1. By ID                          │");
        System.out.println("│ 2. By Name                        │");
        System.out.println("│ 3. By Specialty                   │");
        System.out.println("│ 4. By Gender                      │");
        System.out.println("│ 5. By Years of Experience         │");
        System.out.println("│ 0. Exit without sorting           │");
        System.out.println("└" + "─".repeat(35) + "┘");
        int sortChoice = InputHandler.getInt("Choose option", 0, 5);

        Doctor[] sorted = doctors;
        switch (sortChoice) {
            case 1 -> sorted = doctorMaintenance.sortByID(true);
            case 2 -> sorted = doctorMaintenance.sortByName(true);
            case 3 -> sorted = doctorMaintenance.sortBySpecialty(true);
            case 4 -> sorted = doctorMaintenance.sortByGender(true);
            case 5 -> sorted = doctorMaintenance.sortByExperience(true);
        }

        if (sortChoice != 0) {
            System.out.println("\n=== Doctors (Sorted) ===");
            displayDoctorTable(sorted);
        }
    }

    private void displayDoctorTable(Doctor[] doctors) {
        if (doctors == null || doctors.length == 0) {
            System.out.println("No doctors to display.");
            return;
        }
        System.out.println("+--------+----------------------+-----------------+--------------+------------------------------+-------------------------+--------+-------+");
        System.out.printf("| %-6s | %-20s | %-15s | %-12s | %-28s | %-23s | %-6s | %-5s |\n",
                "ID", "Name", "Specialty", "Phone", "Email", "Address", "Gender", "Exp");
        System.out.println("+--------+----------------------+-----------------+--------------+------------------------------+-------------------------+--------+-------+");
        for (Doctor doctor : doctors) {
            System.out.printf("| %-6s | %-20s | %-15s | %-12s | %-28s | %-23s | %-6s | %-5d |\n",
                    doctor.getDoctorID(), doctor.getName(), doctor.getSpecialty(), doctor.getPhone(),
                    doctor.getEmail(), doctor.getAddress(), doctor.getGender(), doctor.getYearsOfExperience());
        }
        System.out.println("+--------+----------------------+-----------------+--------------+------------------------------+-------------------------+--------+-------+");
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
            System.out.println("========= Update Doctor Details ==========");
            System.out.println("1. Name                 [" + doctor.getName() + "]");
            System.out.println("2. Specialty            [" + doctor.getSpecialty() + "]");
            System.out.println("3. Phone                [" + doctor.getPhone() + "]");
            System.out.println("4. Email                [" + doctor.getEmail() + "]");
            System.out.println("5. Address              [" + doctor.getAddress() + "]");
            System.out.println("6. Gender               [" + doctor.getGender() + "]");
            System.out.println("7. Years of Experience  [" + doctor.getYearsOfExperience() + "]");
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
                    int newExp = InputHandler.getInt("Enter New Years of Experience", 0, 100);
                    doctorMaintenance.updateDoctorField(id, "experience", String.valueOf(newExp));
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

    private void showRecentActionsWithUndo() {
        System.out.println("\n┌" + "─".repeat(33) + "┐");
        System.out.println("│   Recent Doctor Actions         │");
        System.out.println("└" + "─".repeat(33) + "┘");
        String[] actions = doctorMaintenance.getRecentDoctorActions(10);
        if (actions.length == 0) {
            System.out.println("No recent actions.");
        } else {
            for (int i = 0; i < actions.length; i++) {
                System.out.println((i + 1) + ". " + actions[i]);
            }
            System.out.println("\nWould you like to undo the last action?");
            System.out.println("1. Undo Last Action");
            System.out.println("0. Cancel");
            int undoChoice = InputHandler.getInt("Choose option", 0, 1);
            if (undoChoice == 1) {
                String result = doctorMaintenance.undoLastAction();
                System.out.println(result);
            } else {
                System.out.println("Undo cancelled.");
            }
        }
    }

    private void showSpecializationAnalytics() {
        System.out.println("\n┌" + "─".repeat(38) + "┐");
        System.out.println("│   Doctor Specialization Analytics    │");
        System.out.println("└" + "─".repeat(38) + "┘");
        var specialtyCounts = doctorMaintenance.getSpecialtyCounts();
        if (specialtyCounts.size() == 0) {
            System.out.println("No doctors registered.");
        } else {
            Doctor[] doctors = doctorMaintenance.getAllDoctorsArray();
            String[] specs = new String[doctors.length];
            int unique = 0;

            for (Doctor d : doctors) {
                String spec = d.getSpecialty();
                boolean found = false;
                for (int i = 0; i < unique; i++) {
                    if (specs[i].equals(spec)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    specs[unique] = spec;
                    unique++;
                }
            }

            System.out.println("+----------------------+-------+");
            System.out.printf("| %-20s | %-5s |\n", "Specialty", "Count");
            System.out.println("+----------------------+-------+");
            for (int i = 0; i < unique; i++) {
                String specialty = specs[i];
                int count = specialtyCounts.get(specialty);
                System.out.printf("| %-20s | %-5d |\n", specialty, count);
            }
            System.out.println("+----------------------+-------+");
        }
    }

    private void showDoctorReports() {
        System.out.println("\n" + "=".repeat(44));
        System.out.printf("%-44s\n", "DOCTOR REPORTS & STATISTICS");
        System.out.println("=".repeat(44));
        System.out.println("1. Number of Doctors per Specialty");
        System.out.println("2. Gender Distribution of Doctors");
        System.out.println("0. Back");
        System.out.println("=".repeat(44));
        int choice = InputHandler.getInt("Choose report", 0, 2);

        if (choice == 1) {
            String now = java.time.ZonedDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a"));

            line();
            System.out.println(center("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY", 80));
            System.out.println(center("DOCTOR MANAGEMENT SUBSYSTEM", 80));
            System.out.println(center("DOCTOR REPORTS & STATISTICS", 80));
            line();
            System.out.println(rightInDash("generated at: " + now, 80));
            dash(80); blank();

            OrderedMap<String, Integer> specialtyCount = doctorMaintenance.getDoctorCountPerSpecialty();
            Doctor[] doctors = doctorMaintenance.getAllDoctorsArray();
            OrderedMap<String, Boolean> printed = new OrderedMap<>();
            int totalDoctors = 0;
            int maxCount = 0;
            // Find max count for graph scaling
            for (int i = 0; i < doctors.length; i++) {
                String spec = doctors[i].getSpecialty();
                if (!printed.containsKey(spec)) {
                    int count = specialtyCount.get(spec) == null ? 0 : specialtyCount.get(spec);
                    if (count > maxCount) maxCount = count;
                    totalDoctors += count;
                    printed.put(spec, true);
                }
            }
            printed.clear();

            System.out.println(center("DOCTORS PER SPECIALTY", 80));
            dash(80);
            System.out.printf("  %-25s │ %-8s │ %-15s%n", "Specialty", "Count", "Graph");
            dash(80);

            for (int i = 0; i < doctors.length; i++) {
                String spec = doctors[i].getSpecialty();
                if (!printed.containsKey(spec)) {
                    int count = specialtyCount.get(spec) == null ? 0 : specialtyCount.get(spec);
                    int barLen = maxCount == 0 ? 0 : (int)Math.round(((double)count / maxCount) * 30);
                    String bar = "█".repeat(barLen);
                    System.out.printf("  %-25s │ %-8d │ %-15s%n", spec, count, bar);
                    printed.put(spec, true);
                }
            }
            dash(80);
            System.out.printf("  %-25s │ %-8d%n", "TOTAL", totalDoctors);
            dash(80);
            blank();
            System.out.println(center("END OF THE REPORT", 80));
            line();

        } else if (choice == 2) {
            String now = java.time.ZonedDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a"));

            line();
            System.out.println(center("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY", 80));
            System.out.println(center("DOCTOR MANAGEMENT SUBSYSTEM", 80));
            System.out.println(center("DOCTOR REPORTS & STATISTICS", 80));
            line();
            System.out.println(rightInDash("generated at: " + now, 80));
            dash(80); blank();

            OrderedMap<String, Integer> genderMap = doctorMaintenance.getDoctorCountByGender();
            String[] keys = {"Male", "Female", "Other"};
            int total = 0;
            int maxCount = 0;
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                int count = genderMap.get(key) == null ? 0 : genderMap.get(key);
                if (count > maxCount) maxCount = count;
                total += count;
            }
            System.out.println(center("DOCTOR GENDER DISTRIBUTION", 80));
            dash(80);
            System.out.printf("  %-10s │ %-8s │ %-15s%n", "Gender", "Count", "Graph");
            dash(80);

            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                int count = genderMap.get(key) == null ? 0 : genderMap.get(key);
                int barLen = maxCount == 0 ? 0 : (int)Math.round(((double)count / maxCount) * 30);
                String bar = "█".repeat(barLen);
                System.out.printf("  %-10s │ %-8d │ %-15s%n", key, count, bar);
            }
            dash(80);
            System.out.printf("  %-10s │ %-8d%n", "TOTAL", total);
            dash(80); blank();
            System.out.println(center("END OF THE REPORT", 80));
            line();
        }
    }

    private void line() {
        System.out.println("=".repeat(80));
    }
    private void dash(int w) {
        System.out.println("-".repeat(w));
    }
    private void blank() {
        System.out.println();
    }
    private String center(String s, int width) {
        int pad = (width - s.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + s;
    }
    private String rightInDash(String s, int width) {
        int pad = Math.max(0, width - s.length());
        return " ".repeat(pad) + s;
    }
}