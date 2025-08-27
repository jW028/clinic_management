package boundary;

import control.PatientMaintenance;
import entity.Patient;
import entity.VisitHistory;
import utility.IDGenerator;
import utility.InputHandler;
import adt.CustomADT;

public class StudentUI {
    private final PatientMaintenance patientMaintenance;
    private Patient currentPatient;
    private boolean exit = false;

    public StudentUI(PatientMaintenance patientMaintenance) {
        this.patientMaintenance = patientMaintenance;
    }

    public void displayMenu() {
        while (!exit) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Patient");
            System.out.println("2. Admin");
            System.out.println("0. Exit");
            int choice = InputHandler.getInt(0, 2);
            switch (choice) {
                case 1 -> patientAccessMenu();
                case 2 -> adminStub();
                case 0 -> exit = true;
            }
        }
        System.out.println("Goodbye.");
    }

    private void patientAccessMenu() {
        while (true) {
            System.out.println("\n------ Patient Access ------");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Back");
            int choice = InputHandler.getInt(0, 2);
            switch (choice) {
                case 1 -> loginPatient();
                case 2 -> registerPatient();
                case 0 -> { return; }
            }
            if (currentPatient != null) {
                patientSessionMenu();
                currentPatient = null; // logout after session ends
            }
        }
    }

    private void loginPatient() {
        String id = InputHandler.getID("Enter Patient ID", "P", 4);
        Patient p = patientMaintenance.getPatientById(id);
        if (p == null) {
            System.out.println("Patient not found.");
        } else {
            currentPatient = p;
            System.out.println("Login successful. Welcome, " + p.getName() + ".");
        }
    }

    public void registerPatient() {
        System.out.println("\n=== REGISTER NEW PATIENT ===");

        String id = IDGenerator.generatePatientID();
        System.out.println("Generated Patient ID: " + id);

        String name = InputHandler.getString("Enter Name");
        int age = InputHandler.getInt("Enter Age", 1, 120);
        String gender = InputHandler.getGender("Select Gender");
        String contactNumber = InputHandler.getPhoneNumber("Enter Contact Number");
        String address = InputHandler.getString("Enter Address");
        boolean isEmergency = InputHandler.getYesNo("Is this an emergency case?");

        if (patientMaintenance.registerPatient(id, name, age, gender, contactNumber, address, isEmergency)) {
            System.out.println("✅ You registered successfully!");
            System.out.println("Patient ID: " + id);
        } else {
            System.out.println("❌ Registration failed - ID already exists");
        }
    }

    private void patientSessionMenu() {
        boolean back = false;
        while (!back && currentPatient != null) {
            System.out.println("\n====== Patient Dashboard ======");
            System.out.println("Logged in as: " + currentPatient.getPatientId() + " | " + currentPatient.getName());
            System.out.println("1. View details");
            System.out.println("2. Update details");
            System.out.println("3. Book appointment");
            System.out.println("4. View appointment status");
            System.out.println("5. View visit history");
            System.out.println("0. Logout");
            int choice = InputHandler.getInt(0, 5);
            switch (choice) {
                case 1 -> viewDetails();
                case 2 -> updateDetails();
                case 3 -> bookAppointment();
                case 4 -> viewAppointmentStatus();
                case 5 -> viewVisitHistory();
                case 0 -> {
                    back = true;
                    System.out.println("Logged out.");
                }
            }
        }
    }

    private void viewDetails() {
        System.out.println("\n--- Patient Details ---");
        System.out.println(currentPatient);
        System.out.println("Emergency: " + (currentPatient.isEmergency() ? "YES" : "NO"));
    }

    private void updateDetails() {
        System.out.println("\n--- Update Patient Details ---");
        String name = InputHandler.getOptionalString("Name (leave blank keep)");
        String ageStr = InputHandler.getOptionalString("Age (leave blank keep)");
        String changeGender = InputHandler.getOptionalString("Change gender? (y to change)");
        String gender = currentPatient.getGender();
        if (changeGender.equalsIgnoreCase("y")) {
            gender = InputHandler.getGender("Select Gender");
        }
        String phone = InputHandler.getOptionalString("Contact Number (leave blank keep)");
        String address = InputHandler.getOptionalString("Address (leave blank keep)");
        String emerChange = InputHandler.getOptionalString("Change emergency flag? (y to change)");
        boolean emergency = currentPatient.isEmergency();
        if (emerChange.equalsIgnoreCase("y")) {
            emergency = InputHandler.getYesNo("Set emergency status now?");
        }

        String finalName = name.isBlank() ? currentPatient.getName() : name;
        int finalAge = ageStr.isBlank() ? currentPatient.getAge() :
                parsePositiveOrDefault(ageStr, currentPatient.getAge());

        boolean ok = patientMaintenance.updatePatient(
                currentPatient.getPatientId(),
                finalName,
                finalAge,
                gender,
                phone.isBlank() ? currentPatient.getContactNumber() : phone,
                address.isBlank() ? currentPatient.getAddress() : address,
                emergency
        );
        if (ok) {
            currentPatient = patientMaintenance.getPatientById(currentPatient.getPatientId());
            System.out.println("Details updated.");
        } else {
            System.out.println("Update failed.");
        }
    }

    private int parsePositiveOrDefault(String val, int def) {
        try {
            int v = Integer.parseInt(val);
            return v > 0 ? v : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private void bookAppointment() {
        System.out.println("\n--- Book Appointment (Queue Entry) ---");
        if (patientMaintenance.isPatientInQueue(currentPatient.getPatientId())) {
            System.out.println("Already in queue.");
            return;
        }
        if (patientMaintenance.isPatientInWaitlist(currentPatient.getPatientId())) {
            System.out.println("Already in waitlist.");
            return;
        }
        patientMaintenance.enqueuePatient(currentPatient.getPatientId());
        if (patientMaintenance.isPatientInQueue(currentPatient.getPatientId())) {
            System.out.println("Added to active queue.");
        } else if (patientMaintenance.isPatientInWaitlist(currentPatient.getPatientId())) {
            System.out.println("Main queue full. Added to waitlist.");
        } else {
            System.out.println("Failed to enqueue.");
        }
    }

    private void viewAppointmentStatus() {
        System.out.println("\n--- Appointment Status ---");
        String pid = currentPatient.getPatientId();
        if (patientMaintenance.isPatientInQueue(pid)) {
            int position = computeQueuePosition(pid);
            System.out.println("Status: In Queue. Position: " + position);
        } else if (patientMaintenance.isPatientInWaitlist(pid)) {
            System.out.println("Status: In Waitlist. Position (waitlist): " + computeWaitlistPosition(pid));
        } else {
            System.out.println("No active appointment (not queued).");
        }
    }

    private int computeQueuePosition(String pid) {
        int pos = 1;
        // Emergency queue first
        for (int i = 0; i < patientMaintenance.getEmergencyQueueSize(); i++) {
            Patient p = patientMaintenance.getEmergencyQueue().get(i);
            if (p.getPatientId().equals(pid)) return pos;
            pos++;
        }
        for (int i = 0; i < patientMaintenance.getNormalQueueSize(); i++) {
            Patient p = patientMaintenance.getNormalQueue().get(i);
            if (p.getPatientId().equals(pid)) return pos;
            pos++;
        }
        return -1;
    }

    private int computeWaitlistPosition(String pid) {
        for (int i = 0; i < patientMaintenance.getWaitlistSize(); i++) {
            Patient p = patientMaintenance.getWaitlist().get(i);
            if (p.getPatientId().equals(pid)) return i + 1;
        }
        return -1;
    }

    private void viewVisitHistory() {
        System.out.println("\n--- Visit History ---");
        CustomADT<String, VisitHistory> visits = patientMaintenance.getPatientVisitHistory(currentPatient.getPatientId());
        if (visits.size() == 0) {
            System.out.println("No visit history.");
            return;
        }
        for (int i = 0; i < visits.size(); i++) {
            VisitHistory v = visits.get(i);
            System.out.printf("%d. %s | %s | %s | %s\n",
                    i + 1,
                    v.getVisitId(),
                    v.getVisitDate(),
                    v.getVisitReason(),
                    v.getStatus());
        }
    }

    private void adminStub() {
        System.out.println("\n(Admin module placeholder)");
    }

    public static void main(String[] args) {
        PatientMaintenance pm = new PatientMaintenance();
        StudentUI ui = new StudentUI(pm);
        ui.displayMenu();
    }
}