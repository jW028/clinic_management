package boundary;

import control.ConsultationMaintenance;
import entity.Appointment;
import entity.Consultation;
import entity.ConsultationService;
import entity.Diagnosis;
import entity.Payment;
import entity.Patient;
import entity.Doctor;
import adt.CustomADT;
import utility.MessageUI;
import utility.DateTimeFormatterUtil;

import java.time.LocalDateTime;
import java.util.Scanner;

public class ConsultationMaintenanceUI {
    private final ConsultationMaintenance maintenance;
    private final Scanner scanner;

    public ConsultationMaintenanceUI() {
        maintenance = new ConsultationMaintenance();
        scanner = new Scanner(System.in);
    }

    public void run() {
        int choice;
        do {
            System.out.println("\n=== Consultation Maintenance Menu ===");
            System.out.println("1. Manage Appointments");
            System.out.println("2. Manage Consultations");
            System.out.println("3. View Reports");
            System.out.println("0. Return to Main Menu");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number. ");
                continue;
            }

            switch (choice) {
                case 1 -> appointmentMenu();
                case 2 -> consultationMenu();
                case 3 -> reportMenu();
                case 0 -> {
                    System.out.println("Returning to Main Menu. ");
                    return;
                }
                default -> System.out.println("Invalid choice. ");
            }
        } while (true);
    }

    private void appointmentMenu() {
        int choice;
        do {
            System.out.println("\n=== Appointment Management Menu ===");
            System.out.println("1. View All Appointments");
            System.out.println("2. Add Appointment");
            System.out.println("3. Update Appointment Status");
            System.out.println("4. Remove Appointment");
            System.out.println("0. Return to Consultation Maintenance Menu");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number. ");
                continue;
            }

            switch (choice) {
                case 1 -> viewAllAppointments();
                case 2 -> addAppointment();
                case 3 -> updateAppointmentStatus();
                case 4 -> removeAppointment();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice. ");
            }
        } while (true);
    }

    private void consultationMenu() {
        int choice;
        do {
            System.out.println("\n=== Consultation Management Menu ===");
            System.out.println("1. View All Consultations");
            System.out.println("2. Add Consultation");
            System.out.println("3. Search Consultation By ID");
            System.out.println("4. Delete Consultation");
            System.out.println("0. Return to Consultation Maintenance Menu");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number. ");
                continue;
            }

            switch (choice) {
                case 1 -> viewAllConsultations();
                case 2 -> addConsultation();
                case 3 -> searchConsultation();
                case 4 -> deleteConsultation();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice. ");
            }
        } while (true);
    }

    private void reportMenu() {
        int choice;
        do {
            System.out.println("\n=== Report Menu ===");
            System.out.println("1. Print Consultation Summary Report");
            System.out.println("2. Print Service Usage Report");
            System.out.println("0. Return to Consultation Maintenance Menu");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number. ");
                continue;
            }

            switch (choice) {
                case 1 -> maintenance.printConsultationSummaryReport();
                case 2 -> maintenance.printServiceUsageReport();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice");
            }
        } while (true);
    }

    private void addConsultation() {
        System.out.println("\n-- Add Consultation --");

        Appointment[] appointments = maintenance.getAllAppointments();
        if (appointments == null || appointments.length == 0) {
            System.out.println("No appointments available. Please create an appointment first.");
            return;
        }

        Patient[] patients = maintenance.getAllPatients();
        if (patients == null || patients.length == 0) {
            System.out.println("No patients available. Please create a patient first.");
            return;
        }

        ConsultationService[] services = maintenance.getAllServices();
        if (services == null || services.length == 0) {
            System.out.println("No services available. Please add services first.");
            return;
        }

        Diagnosis[] diagnoses = maintenance.getAllDiagnoses();
        if (diagnoses == null || diagnoses.length == 0) {
            System.out.println("No diagnoses available. Please add diagnoses first.");
            return;
        }

        // TODO can use generator to generate consultation id
        // Consultation ID validation
        String consultationId;
        while (true) {
            System.out.print("Consultation ID: ");
            consultationId = scanner.nextLine().trim();
            if (consultationId.isEmpty()) {
                System.out.println("Consultation ID cannot be empty.");
                continue;
            }
            if (maintenance.getConsultation(consultationId) != null) {
                System.out.println("Consultation ID already exists. Please choose another.");
            } else {
                break;
            }
        }

        // Select Appointment
        System.out.println("Available Appointments:");
//        for (Appointment appt : appointments) {
//            System.out.println(appt.getAppointmentId() + ": " + appt);
//        }
        // TODO patient and doctor use id or name?
        for (Appointment appt : appointments) {
            System.out.printf("| %-10s | %-15s | %-15s | %-15s | %-10s | \n",
                    "Appointment ID", "Patient", "Doctor", "Date", "Status");
            System.out.println("-------------------------------------------------------");
            System.out.printf("| %-10s | %-15s | %-15s | %-15s | %-10s | \n",
                    appt.getAppointmentId(), appt.getPatientId(), appt.getDoctorId(), DateTimeFormatterUtil.parseDisplayFormat(String.valueOf(appt.getAppointmentTime())), appt.getStatus());
        }

        Appointment appointment = null;
        while (appointment == null) {
            System.out.print("Enter Appointment ID: ");
            String appointmentId = scanner.nextLine().trim();

            appointment = maintenance.getAppointment(appointmentId);
            if (appointment == null) {
                System.out.println("Invalid Appointment ID.");
            } else if ("Cancelled".equalsIgnoreCase(appointment.getStatus())) {
                System.out.println("Cannot create consultation for an appointment with status: " + appointment.getStatus());
                appointment = null;
            }
        }

        if (!appointment.getStatus().equalsIgnoreCase("Scheduled")) {
            System.out.println("Consultation can only be added for Scheduled appointments. ");
            return;
        }

        Patient patient = maintenance.getPatient(appointment.getPatientId());
        Doctor doctor = maintenance.getDoctor(appointment.getDoctorId());

        if (patient == null || doctor == null) {
            System.out.println("Invalid patient or doctor in the appointment.");
            return;
        }

        // LocalDateTime consultationTime = LocalDateTime.now();
        LocalDateTime consultationTime = appointment.getAppointmentTime();

        // Select ConsultationServices
        CustomADT<String, ConsultationService> servicesUsed = new CustomADT<>();
        System.out.println("Available Services:");
//        for (ConsultationService service : services) {
//            System.out.println(service.getServiceId() + ": " + service.getServiceName());
//        }
        for (ConsultationService service : services) {
            System.out.printf("| %-10s | %-25s | %-10s | \n",
                    "Service ID", "Name", "Fee");
            System.out.println("-----------------------------------------------------");
            System.out.printf("| %-10s | %-25s | %-10.2f | \n",
                    service.getServiceId(), service.getServiceName(), service.getServiceCharge());
        }

        int index = 1;
        String more;
        do {
            ConsultationService selectedService = null;
            while (selectedService == null) {
                System.out.print("Enter Service ID to add: ");
                String serviceId = scanner.nextLine().trim();
                selectedService = maintenance.getService(serviceId);
                if (selectedService == null) {
                    System.out.println("Service not found. Please try again.");
                }
            }
            servicesUsed.put(String.valueOf(index++), selectedService);
            System.out.print("Add another service? (y/n): ");
            more = scanner.nextLine().trim();
        } while (more.equalsIgnoreCase("y"));

        // Select Diagnosis
        System.out.println("Available Diagnoses:");
//        for (Diagnosis diag : diagnoses) {
//            System.out.println(diag.getId() + ": " + diag.getName());
//        }
        for (Diagnosis diag : diagnoses) {
            System.out.printf("| %-10s | %-20s | %-40s | %-15s | \n",
                    "Diagnosis ID", "Name", "Description", "Severity");
            System.out.println("--------------------------------------------------------------");
            System.out.printf("| %-10s | %-20s | %-40s | %-15s | \n",
                    diag.getId(), diag.getName(), diag.getDescription(), diag.getSeverity());
        }

        Diagnosis diagnosis = null;
        while (diagnosis == null) {
            System.out.print("Enter Diagnosis ID: ");
            String diagnosisId = scanner.nextLine().trim();
            diagnosis = maintenance.getDiagnosis(diagnosisId);
            if (diagnosis == null) System.out.println("Invalid Diagnosis ID.");
        }

        System.out.print("Notes: ");
        String notes = scanner.nextLine();

        // Link Payment if needed (simple demo)
        Payment payment = null;
        System.out.print("Link a Payment? (y/n): ");
        String linkPay = scanner.nextLine().trim();
        if (linkPay.equalsIgnoreCase("y")) {
            Payment[] payments = maintenance.getAllPayments();
            if (payments != null && payments.length > 0) {
                System.out.println("Available Payments:");
                // TODO Payment UI
                for (Payment p : payments) {
                    System.out.println(p.getPaymentId() + ": " + p);
                }
            }
            while (true) {
                System.out.print("Enter Payment ID: ");
                String paymentId = scanner.nextLine().trim();
                if (paymentId.isEmpty()) break; // Allow skip
                payment = maintenance.getPayment(paymentId);
                if (payment == null) {
                    System.out.println("Payment not found. Enter again or leave empty to skip.");
                } else {
                    break;
                }
            }
        }

        boolean followUpNeeded = false;
        LocalDateTime followUpDate = null;
        System.out.print("Is follow-up needed? (y/n): ");
        String followUpAns = scanner.nextLine().trim();
        if (followUpAns.equalsIgnoreCase("y")) {
            followUpNeeded = true;
            while (true) {
                System.out.print("Enter follow-up date (DD/MM/YYYY HH:MM): ");
                String followUpDateStr = scanner.nextLine().trim();
                try {
                    followUpDate = DateTimeFormatterUtil.parseDisplayFormat(followUpDateStr);
                    if (followUpDate.isBefore(consultationTime)) {
                        System.out.println("Follow-up date must be after the consultation date.");
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid format. Please try again.");
                }
            }

            // Prompt to create follow-up appointment immediately
            System.out.print("Create follow-up appointment now? (y/n): ");
            String createFollowUp = scanner.nextLine().trim();
            if (createFollowUp.equalsIgnoreCase("y")) {
                String newApptId;
                while (true) {
                    System.out.print("Follow-up Appointment ID: ");
                    newApptId = scanner.nextLine().trim();
                    if (newApptId.isEmpty()) {
                        System.out.println("Appointment ID cannot be empty.");
                        continue;
                    }
                    if (maintenance.getAppointment(newApptId) != null) {
                        System.out.println("Appointment ID already exists.");
                    } else {
                        break;
                    }
                }
                Appointment followUp = new Appointment(newApptId, patient.getPatientId(), doctor.getDoctorID(), followUpDate, "Scheduled");
                maintenance.addAppointment(followUp);
                System.out.println("Follow-up appointment created.");
            }
        }

        Consultation consultation = new Consultation(
                consultationId,
                appointment,
                patient,
                doctor,
                consultationTime,
                servicesUsed,
                diagnosis,
                notes,
                payment,
                followUpNeeded,
                followUpDate
        );

        maintenance.addConsultation(consultation);
        appointment.setStatus("Completed");
        maintenance.updateAppointmentStatus(appointment.getAppointmentId(), "Completed");
        System.out.println("Consultation added.");
    }

    private void addAppointment() {
        System.out.println("\n-- Add Appointment --");
        String appointmentId;
        while (true) {
            System.out.print("Appointment ID: ");
            appointmentId = scanner.nextLine().trim();
            if (appointmentId.isEmpty()) {
                System.out.println("Appointment ID cannot be empty.");
                continue;
            }
            if (maintenance.getAppointment(appointmentId) != null) {
                System.out.println("Appointment ID already exists.");
            } else {
                break;
            }
        }

        Patient[] patients = maintenance.getAllPatients();
        if (patients == null || patients.length == 0) {
            System.out.println("No patients available. Please create a patient first.");
            return;
        }
        System.out.println("Available Patients:");
        System.out.printf("| %-10s | %-20s | \n", "Patient ID", "Name");
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (Patient p : patients) {
            System.out.printf("| %-10s | %-20s | \n", p.getPatientId(), p.getName());
        }
//        System.out.printf("%-10s | %-20s | %-15s | %-15s | %-20s\n",
//                "Patient ID", "Name", "Gender", "Phone", "Email");
//        System.out.println("-----------------------------------------------------------------------------------------------");
//        System.out.printf("%-10s | %-20s | %-15s | %-15s | %-20s\n",
//                p.getPatientId(), p.getName(), p.getGender(), p.getPhone(), p.getEmail());

        String patientId = "";
        Patient patient = null;
        while (patient == null) {
            System.out.print("Enter Patient ID: ");
            patientId = scanner.nextLine().trim();
            patient = maintenance.getPatient(patientId);
            if (patient == null) System.out.println("Invalid Patient ID.");
        }

        Doctor[] doctors = maintenance.getAllDoctors();
        if (doctors == null || doctors.length == 0) {
            System.out.println("No doctors available. Please create a doctor first.");
            return;
        }
        System.out.println("Available Doctors:");
        System.out.printf("| %-10s | %-20s | %-20s | \n", "Doctor ID", "Name", "Specialization");
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (Doctor d : doctors) {
            System.out.printf("| %-10s | %-20s | %-20s | \n", d.getDoctorID(), d.getName(), d.getSpecialty());
        }
        String doctorId = "";
        Doctor doctor = null;
        while (doctor == null) {
            System.out.print("Enter Doctor ID: ");
            doctorId = scanner.nextLine().trim();
            doctor = maintenance.getDoctor(doctorId);
            if (doctor == null) System.out.println("Invalid Doctor ID.");
        }

        // TODO To check schedule

        LocalDateTime appointmentTime = null;
        while (appointmentTime == null) {
            System.out.print("Appointment DateTime (DD/MM/YYYY HH:MM): ");
            String dateTimeStr = scanner.nextLine().trim();
            try {
                appointmentTime = DateTimeFormatterUtil.parseDisplayFormat(dateTimeStr);
                break;
            } catch (Exception e) {
                System.out.println("Invalid format. Please try again.");
            }
        }

        // If want straightly assign "Scheduled" to new appointments
        // String status = "Scheduled";

        System.out.print("Status (");
        String[] validStatuses = ConsultationMaintenance.VALID_APPOINTMENT_STATUSES;
        for (int i = 0; i < validStatuses.length; i++) {
            System.out.print(validStatuses[i]);
            if (i < validStatuses.length - 1) System.out.print(" / ");
        }
        System.out.print("): ");
        String status = scanner.nextLine().trim();
        if (!maintenance.isValidStatus(status)) {
            System.out.println("Invalid status. Appointment not added.");
            return;
        }

        Appointment appointment = new Appointment(appointmentId, patientId, doctorId, appointmentTime, status);
        maintenance.addAppointment(appointment);
        System.out.println("Appointment added.");
    }

    private void removeAppointment() {
        System.out.print("\nEnter Appointment ID to remove: ");
        String appointmentId = scanner.nextLine().trim();
        if (appointmentId.isEmpty()) {
            System.out.println("Appointment ID cannot be empty.");
            return;
        }
        boolean success = maintenance.removeAppointment(appointmentId);
        if (success) {
            System.out.println("Appointment removed.");
        } else {
            System.out.println("Failed to remove appointment.");
        }
    }

    private void updateAppointmentStatus() {
        System.out.print("\nEnter Appointment ID to update status: ");
        String appointmentId = scanner.nextLine().trim();
        if (appointmentId.isEmpty()) {
            System.out.println("Appointment ID cannot be empty.");
            return;
        }
        Appointment appointment = maintenance.getAppointment(appointmentId);
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return;
        }
        System.out.print("Current status: " + appointment.getStatus() + "\nEnter new status (");
        String[] validStatuses = ConsultationMaintenance.VALID_APPOINTMENT_STATUSES;
        for (int i = 0; i < validStatuses.length; i++) {
            System.out.print(validStatuses[i]);
            if (i < validStatuses.length - 1) System.out.print(" / ");
        }
        System.out.print("): ");
        String newStatus = scanner.nextLine().trim();
        if (!maintenance.isValidStatus(newStatus)) {
            System.out.println("Invalid status. Update cancelled.");
            return;
        }
        boolean success = maintenance.updateAppointmentStatus(appointmentId, newStatus);
        if (success) {
            System.out.println("Appointment status updated.");
        } else {
            System.out.println("Failed to update appointment status.");
        }
    }

    private void viewAllAppointments() {
        System.out.println("\n-- All Appointments --");
        Appointment[] appointments = maintenance.getAllAppointments();
        if (appointments == null || appointments.length == 0) {
            System.out.println("No appointments found.");
            return;
        }
//        for (Appointment appt : appointments) {
//            System.out.println(appt);
//            System.out.println("----");
//        }
        String format = "| %-15s | %-12s | %-12s | %-20s | %-10s |\n";
        System.out.println("+-----------------+--------------+--------------+----------------------+------------+");
        System.out.printf(format, "Appointment ID", "Patient ID", "Doctor ID", "Date & Time", "Status");
        System.out.println("+-----------------+--------------+--------------+----------------------+------------+");

        for (Appointment appt : appointments) {
            System.out.printf(format,
                    appt.getAppointmentId(),
                    appt.getPatientId(),
                    appt.getDoctorId(),
                    DateTimeFormatterUtil.formatForDisplay(appt.getAppointmentTime()),
                    appt.getStatus()
            );
        }
        System.out.println("+-----------------+--------------+--------------+----------------------+------------+");
    }

    private void viewAllConsultations() {
        System.out.println("\n-- All Consultations --");
        Consultation[] consultations = maintenance.getAllConsultations();
        if (consultations == null || consultations.length == 0) {
            System.out.println("No consultations found.");
            return;
        }

        System.out.println("\nConsultation List");
        System.out.println("=".repeat(100));
        System.out.printf("%-15s %-15s %-20s %-15s %-15s %-10s%n",
                "ConsultationID", "AppointmentID", "Patient Name", "Doctor Name", "Date", "Fee");
        System.out.println("-".repeat(100));

        for (Consultation c : consultations) {
            String consultationId = c.getConsultationId();
            String appointmentId = c.getAppointment().getAppointmentId();
            String patientName = c.getPatient().getName();
            String doctorName = c.getDoctor().getName();
            String date = c.getAppointment().getAppointmentTime().toString();

            double fee = 0.0;
            for (int i = 0; i < c.getServicesUsed().size(); i++) {
                ConsultationService service = c.getServicesUsed().get(i);
                fee += service.getServiceCharge();
            }

            System.out.printf("%-15s %-15s %-20s %-15s %-15s RM%-8.2f%n",
                    consultationId, appointmentId, patientName, doctorName, date, fee);
        }
        System.out.println("=".repeat(100));

//        for (Consultation c : consultations) {
//            System.out.println(c);
//            System.out.println("----");
//        }
    }

    private void searchConsultation() {
        System.out.print("Enter Consultation ID: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Consultation ID cannot be empty.");
            return;
        }
        Consultation c = maintenance.getConsultation(id);
        if (c != null) {
            System.out.println(c);
        } else {
            System.out.println("Consultation not found.");
        }
    }

    private void deleteConsultation() {
        System.out.print("Enter Consultation ID to delete: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Consultation ID cannot be empty.");
            return;
        }
        boolean success = maintenance.removeConsultation(id);
        if (success) {
            System.out.println("Consultation deleted.");
        } else {
            System.out.println("Consultation not found.");
        }
    }

    private void countConsultations() {
        System.out.println("Total consultations stored: " + maintenance.countConsultations());
    }

    public static void main(String[] args) {
        new ConsultationMaintenanceUI().run();
    }
}