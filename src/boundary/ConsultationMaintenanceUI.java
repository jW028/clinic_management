package boundary;

import adt.CustomADT;
import control.ConsultationMaintenance;
import entity.Appointment;
import entity.Consultation;
import entity.ConsultationService;
import entity.Diagnosis;
import entity.Doctor;
import entity.Patient;
import entity.Payment;
import java.time.LocalDateTime;
import java.util.Scanner;
import utility.DateTimeFormatterUtil;
import utility.InputHandler;

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
            System.out.println("5. Search Appointments by Patient Name");
            System.out.println("6. Search Appointments by Doctor Name");
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
                case 5 -> {
                    System.out.print("Enter patient name (Eg. Alice, or 0 to return): ");
                    String pName = scanner.nextLine().trim();
                    if (pName.equals("0")) return;
                    CustomADT<String, Appointment> found = maintenance.searchAppointmentsByPatientName(pName);

                    String format = "| %-15s | %-12s | %-20s | %-12s | %-20s | %-20s | %-10s |\n";
                    System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+------------+");
                    System.out.printf(format, "Appointment ID", "Patient ID", "Patient Name", "Doctor ID", "Doctor Name", "Date & Time", "Status");
                    System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+------------+");

                    for (Appointment appt : found) {
                        Patient patient = maintenance.getPatient(appt.getPatientId());
                        Doctor doctor = maintenance.getDoctor((appt.getDoctorId()));
                        System.out.printf(format,
                                appt.getAppointmentId(),
                                appt.getPatientId(),
                                patient != null ? patient.getName() : "Unknown",
                                appt.getDoctorId(),
                                doctor != null ? doctor.getName() : "Unknown",
                                DateTimeFormatterUtil.formatForDisplay(appt.getAppointmentTime()),
                                appt.getStatus()
                        );
                    }
                    System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+------------+");
                }
                case 6 -> {
                    System.out.print("Enter doctor name (Eg. Dr. Alice Tan, or 0 to return): ");
                    String dName = scanner.nextLine().trim();
                    if (dName.equals("0")) return;

                    CustomADT<String, Appointment> found = maintenance.searchAppointmentsByDoctorName(dName);
                    String format = "| %-15s | %-12s | %-20s | %-12s | %-20s | %-20s | %-10s |\n";
                    System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+------------+");
                    System.out.printf(format, "Appointment ID", "Patient ID", "Patient Name", "Doctor ID", "Doctor Name", "Date & Time", "Status");
                    System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+------------+");

                    for (Appointment appt : found) {
                        Patient patient = maintenance.getPatient(appt.getPatientId());
                        Doctor doctor = maintenance.getDoctor((appt.getDoctorId()));
                        System.out.printf(format,
                                appt.getAppointmentId(),
                                appt.getPatientId(),
                                patient != null ? patient.getName() : "Unknown",
                                appt.getDoctorId(),
                                doctor != null ? doctor.getName() : "Unknown",
                                DateTimeFormatterUtil.formatForDisplay(appt.getAppointmentTime()),
                                appt.getStatus()
                        );
                    }
                    System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+------------+");
                }
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
            System.out.println("5. Search Consultations by Patient Name");
            System.out.println("6. Search Consultations by Doctor Name");
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
                case 5 -> {
                    System.out.print("Enter patient name: ");
                    String pName = scanner.nextLine().trim();
                    CustomADT<String, Consultation> found = maintenance.searchConsultationsByPatientName(pName);

                    System.out.println("=".repeat(105));
                    System.out.printf("%-15s %-15s %-20s %-20s %-20s %-10s%n",
                            "ConsultationID", "AppointmentID", "Patient Name", "Doctor Name", "Date", "Fee");
                    System.out.println("-".repeat(105));

                    for (Consultation c : found) {
                        String consultationId = c.getConsultationId();
                        String appointmentId = c.getAppointment().getAppointmentId();
                        String patientName = c.getPatient().getName();
                        String doctorName = c.getDoctor().getName();
                        String date = DateTimeFormatterUtil.formatForDisplay(c.getAppointment().getAppointmentTime());

                        double fee = 0.0;
                        for (int i = 0; i < c.getServicesUsed().size(); i++) {
                            ConsultationService service = c.getServicesUsed().get(i);
                            fee += service.getServiceCharge();
                        }

                        System.out.printf("%-15s %-15s %-20s %-20s %-20s RM%-8.2f%n",
                                consultationId, appointmentId, patientName, doctorName, date, fee);
                        System.out.println("=".repeat(105));
                    }
                }
                case 6 -> {
                    System.out.print("Enter doctor name: ");
                    String dName = scanner.nextLine().trim();
                    CustomADT<String, Consultation> found = maintenance.searchConsultationsByDoctorName(dName);
                    System.out.println("=".repeat(105));
                    System.out.printf("%-15s %-15s %-20s %-20s %-20s %-10s%n",
                            "ConsultationID", "AppointmentID", "Patient Name", "Doctor Name", "Date", "Fee");
                    System.out.println("-".repeat(105));

                    for (Consultation c : found) {
                        String consultationId = c.getConsultationId();
                        String appointmentId = c.getAppointment().getAppointmentId();
                        String patientName = c.getPatient().getName();
                        String doctorName = c.getDoctor().getName();
                        String date = DateTimeFormatterUtil.formatForDisplay(c.getAppointment().getAppointmentTime());

                        double fee = 0.0;
                        for (int i = 0; i < c.getServicesUsed().size(); i++) {
                            ConsultationService service = c.getServicesUsed().get(i);
                            fee += service.getServiceCharge();
                        }

                        System.out.printf("%-15s %-15s %-20s %-20s %-20s RM%-8.2f%n",
                                consultationId, appointmentId, patientName, doctorName, date, fee);
                    }
                    System.out.println("=".repeat(105));
                }
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
        printAllConsultations(maintenance.getAllConsultations());

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
            System.out.print("Enter Consultation ID (or 0 to return): ");
            consultationId = scanner.nextLine().trim();
            if (consultationId.equals("0")) return;
            if (consultationId.isEmpty()) {
                System.out.println("Consultation ID cannot be empty.");
                continue;
            }
            if (!InputHandler.isValidId(consultationId, "consultation")) {
                System.out.println("Invalid format. Must be C followed by 3 digits (Eg. C001).");
                continue;
            }
            if (maintenance.getConsultation(consultationId) != null) {
                System.out.println("Consultation ID already exists. Please enter another ID.");
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
        printAllAppointments(maintenance.getAllAppointments());

        Appointment appointment = null;
        while (appointment == null) {
            System.out.print("Enter Appointment ID (or 0 to return): ");
            String appointmentId = scanner.nextLine().trim();

            if (appointmentId.equals("0")) return;
            if (!InputHandler.isValidId(appointmentId, "appointment")) {
                System.out.println("Invalid format. Must be A followed by 3 digits (Eg. A001). ");
                continue;
            }
            appointment = maintenance.getAppointment(appointmentId);
            if (appointment == null) {
                System.out.println("Appointment not found. Please enter a valid Appointment ID. ");
                continue;
            }
            if (!appointment.getStatus().equalsIgnoreCase("Scheduled")) {
                System.out.println("Consultation can only be added for Scheduled appointments. ");
                appointment = null;
            }
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
        System.out.println("+------------+---------------------------+------------+");
        System.out.printf("| %-10s | %-25s | %-10s | \n",
                "Service ID", "Name", "Fee");
        System.out.println("+------------+---------------------------+------------+");
        for (ConsultationService service : services) {
            System.out.printf("| %-10s | %-25s | %-10.2f | \n",
                    service.getServiceId(), service.getServiceName(), service.getServiceCharge());
        }
        System.out.println("+------------+---------------------------+------------+");

        int index = 1;
        String more;
        do {
            ConsultationService selectedService = null;
            while (selectedService == null) {
                System.out.print("Enter Service ID to add (or 0 to return): ");
                String serviceId = scanner.nextLine().trim();
                if (serviceId.equals("0")) return;

                selectedService = maintenance.getService(serviceId);
                if (selectedService == null) {
                    System.out.println("Service not found. Please enter a valid Service ID. ");
                }
            }
            servicesUsed.put(String.valueOf(index++), selectedService);
            while (true) {
                System.out.print("\nAdd another service? (y/n): ");
                more = scanner.nextLine().trim();
                if (more.isEmpty()) {
                    System.out.println("Cannot leave blank. Must enter 'y' or 'n'. ");
                    continue;
                }
                if (!more.equals("y") && !more.equals("n")) {
                    System.out.println("Invalid input. Please enter 'y' or 'n' only. ");
                    continue;
                }
                break;
            }
        } while (more.equalsIgnoreCase("y"));

        // Select Diagnosis
        System.out.println("Available Diagnoses:");
        System.out.println("+-----------------+----------------------+------------------------------------------+-----------------+");
        System.out.printf("| %-15s | %-20s | %-40s | %-15s | \n",
                "Diagnosis ID", "Name", "Description", "Severity");
        System.out.println("+-----------------+----------------------+------------------------------------------+-----------------+");
        for (Diagnosis diag : diagnoses) {
            System.out.printf("| %-15s | %-20s | %-40s | %-15s | \n",
                    diag.getId(), diag.getName(), diag.getDescription(), diag.getSeverity());
        }
        System.out.println("+-----------------+----------------------+------------------------------------------+-----------------+");

        Diagnosis diagnosis = null;
        while (diagnosis == null) {
            System.out.print("Enter Diagnosis ID (or 0 to return): ");
            String diagnosisId = scanner.nextLine().trim();
            if (diagnosisId.equals("0")) return;
            if (diagnosisId.isEmpty()) {
                System.out.println("Diagnosis ID cannot be empty. ");
                continue;
            }
            if (!InputHandler.isValidId(diagnosisId, "diagnosis")) {
                System.out.println("Invalid format. Must be D followed by 3 digits (Eg. DC001).");
                continue;
            }

            diagnosis = maintenance.getDiagnosis(diagnosisId);
            if (diagnosis == null) System.out.println("Diagnosis not found. Please enter a valid Diagnosis ID. ");
        }

        System.out.print("Enter notes (or leave empty to skip): ");
        String notes = scanner.nextLine();

        // Link Payment if needed
        Payment payment = null;
        System.out.print("\nLink a Payment? (y/n, or 0 to return): ");
        String linkPay = scanner.nextLine().trim();
        if (linkPay.equals("0")) return;
        if (linkPay.equalsIgnoreCase("y")) {
            Payment[] payments = maintenance.getAllPayments();
            if (payments == null || payments.length == 0) {
                System.out.println("No payment records available. Cannot link payment. ");
            } else {
                System.out.println("Available Payments:");
                // TODO Payment UI
                for (Payment p : payments) {
                    System.out.println(p.getPaymentId() + ": " + p);
                }
                while (true) {
                    System.out.print("Enter Payment ID (or 0 to return): ");
                    String paymentId = scanner.nextLine().trim();
                    if (paymentId.equals("0")) return;
                    if (paymentId.isEmpty()) {
                        System.out.println("Payment ID cannot be empty. ");
                        continue;
                    }
                    // TODO InputHandler for payment id
                    //if (!InputHandler.isValidId(paymentId, "payment")) {
                    //    System.out.println("Invalid format. Must be D followed by 3 digits (Eg. D001).");
                    //    continue;
                    //}
                    payment = maintenance.getPayment(paymentId);
                    if (payment == null) {
                        System.out.println("Payment not found. Please enter a valid Payment ID. ");
                    }
                }
            }
        }

        boolean followUpNeeded = false;
        LocalDateTime followUpDate = null;
        System.out.print("\nIs follow-up needed? (y/n, or 0 to return): ");
        String followUpAns = scanner.nextLine().trim();
        if (followUpAns.equals("0")) return;
        if (followUpAns.equalsIgnoreCase("y")) {
            followUpNeeded = true;

            // Show available slots for follow-up doctor
            String[] slots = maintenance.getAvailableSlotsForDoctor(doctor.getDoctorID());
            System.out.println("\nFollow-Up Available Slots for Doctor:");
            System.out.println("+------------+----------------------+----------------------+---------------------------+");
            System.out.printf("| %-10s | %-20s | %-20s | %-25s |\n", "Doctor ID", "Name", "Specialization", "Available Slot(s)");
            System.out.println("+------------+----------------------+----------------------+---------------------------+");

            if (slots != null && slots.length > 0) {
                // First row: show doctor info
                System.out.printf("| %-10s | %-20s | %-20s | %-25s |\n",
                        doctor.getDoctorID(),
                        doctor.getName(),
                        doctor.getSpecialty(),
                        slots[0]
                );
                // Next slot rows: leave doctor columns blank
                for (int i = 1; i < slots.length; i++) {
                    System.out.printf("| %-10s | %-20s | %-20s | %-25s |\n",
                            "", "", "", slots[i]
                    );
                }
            } else {
                // No slots: show doctor info with "No slots"
                System.out.printf("| %-10s | %-20s | %-20s | %-25s |\n",
                        doctor.getDoctorID(),
                        doctor.getName(),
                        doctor.getSpecialty(),
                        "No slots"
                );
            }

            System.out.println("+------------+----------------------+----------------------+---------------------------+");


            do {
                followUpDate = selectDoctorSlot(maintenance, doctor, scanner);
                if (followUpDate == null) return;
                if (followUpDate.isBefore(consultationTime)) {
                    System.out.println("Selected follow-up date must be after the consultation date. ");
                }
            } while (followUpDate != null && followUpDate.isBefore(consultationTime));

            // Prompt to create follow-up appointment immediately
            System.out.print("Create follow-up appointment now? (y/n): ");
            String createFollowUp = scanner.nextLine().trim();
            if (createFollowUp.equalsIgnoreCase("y")) {
                String newApptId;
                while (true) {
                    System.out.print("Follow-up Appointment ID (Eg. A001, or 0 to return): ");
                    newApptId = scanner.nextLine().trim();
                    if (newApptId.equals("0")) return;
                    if (newApptId.isEmpty()) {
                        System.out.println("Appointment ID cannot be empty.");
                        continue;
                    }
                    if (!InputHandler.isValidId(newApptId, "appointment")) {
                        System.out.println("Invalid format. Must be A followed by 3 digits (Eg. A001). ");
                        continue;
                    }
                    if (maintenance.getAppointment(newApptId) != null) {
                        System.out.println("Appointment ID already exists. Please enter a new ID. ");
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

    private void printAllConsultations(Consultation[] consultations) {
        System.out.println("Consultation List");
        System.out.println("=".repeat(105));
        System.out.printf("%-15s %-15s %-20s %-20s %-20s %-10s%n",
                "ConsultationID", "AppointmentID", "Patient Name", "Doctor Name", "Date", "Fee");
        System.out.println("-".repeat(105));

        for (Consultation c : consultations) {
            String consultationId = c.getConsultationId();
            String appointmentId = c.getAppointment().getAppointmentId();
            String patientName = c.getPatient().getName();
            String doctorName = c.getDoctor().getName();
            String date = DateTimeFormatterUtil.formatForDisplay(c.getAppointment().getAppointmentTime());

            double fee = 0.0;
            for (int i = 0; i < c.getServicesUsed().size(); i++) {
                ConsultationService service = c.getServicesUsed().get(i);
                fee += service.getServiceCharge();
            }

            System.out.printf("%-15s %-15s %-20s %-20s %-20s RM%-8.2f%n",
                    consultationId, appointmentId, patientName, doctorName, date, fee);
        }
        System.out.println("=".repeat(105));
    }

    private void viewAllConsultations() {
        System.out.println("\n-- All Consultations --");
        Consultation[] consultations = maintenance.getAllConsultations();
        if (consultations == null || consultations.length == 0) {
            System.out.println("No consultations found.");
            return;
        }
        printAllConsultations(consultations);

        // Show sorting options
        int choice;
        do {
            System.out.println("Sort consultations by: ");
            System.out.println("1. Patient Name");
            System.out.println("2. Doctor Name");
            System.out.println("3. Date");
            System.out.println("0. Return to Consultation Management Menu");

            System.out.print("Choose sorting option: ");
            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number. ");
                continue;
            }

            switch (choice) {
                case 1 -> maintenance.sortConsultationsByPatientName();
                case 2 -> maintenance.sortConsultationsByDoctorName();
                case 3 -> maintenance.sortConsultationsByDate();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice, showing unsorted list. ");
            }
            printAllConsultations(maintenance.getAllConsultations());
        } while (true);
    }

    private void searchConsultation() {
        printAllConsultations(maintenance.getAllConsultations());

        String consultationId;
        while (true) {
            System.out.print("Enter Consultation ID to delete (Eg. C001, or 0 to return): ");
            consultationId = scanner.nextLine().trim();
            if (consultationId.equals("0")) return;
            if (consultationId.isEmpty()) {
                System.out.println("Consultation ID cannot be empty.");
                continue;
            }
            if (!InputHandler.isValidId(consultationId, "consultation")) {
                System.out.println("Invalid format. Must be C followed by 3 digits (Eg. C001).");
                continue;
            }
            if (maintenance.getConsultation(consultationId) == null) {
                System.out.println("Consultation not found. Please enter a valid Consultation ID. ");
                continue;
            }
            break;
        }

        Consultation c = maintenance.getConsultation(consultationId);
        if (c != null) {
            System.out.println(c);
        } else {
            System.out.println("Consultation not found.");
        }
    }

    private void deleteConsultation() {
        System.out.println("\n-- Delete Consultation --");
        printAllConsultations(maintenance.getAllConsultations());

        String consultationId;
        while (true) {
            System.out.print("Enter Consultation ID to delete (Eg. C001, or 0 to return): ");
            consultationId = scanner.nextLine().trim();
            if (consultationId.equals("0")) return;
            if (consultationId.isEmpty()) {
                System.out.println("Consultation ID cannot be empty.");
                continue;
            }
            if (!InputHandler.isValidId(consultationId, "consultation")) {
                System.out.println("Invalid format. Must be C followed by 3 digits (Eg. C001).");
                continue;
            }
            if (maintenance.getConsultation(consultationId) == null) {
                System.out.println("Consultation not found. Please enter a valid Consultation ID. ");
                continue;
            }
            break;
        }
        boolean success = maintenance.removeConsultation(consultationId);
        if (success) {
            System.out.println("Consultation deleted.");
        } else {
            System.out.println("Consultation not found.");
        }
    }

    private void countConsultations() {
        System.out.println("Total consultations stored: " + maintenance.countConsultations());
    }

    private void printAllAppointments(Appointment[] appointments) {
        String format = "| %-15s | %-12s | %-20s | %-12s | %-20s | %-20s | %-10s |\n";
        System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+------------+");
        System.out.printf(format, "Appointment ID", "Patient ID", "Patient Name", "Doctor ID", "Doctor Name", "Date & Time", "Status");
        System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+------------+");

        for (Appointment appt : appointments) {
            Patient patient = maintenance.getPatient(appt.getPatientId());
            Doctor doctor = maintenance.getDoctor((appt.getDoctorId()));
            System.out.printf(format,
                    appt.getAppointmentId(),
                    appt.getPatientId(),
                    patient != null ? patient.getName() : "Unknown",
                    appt.getDoctorId(),
                    doctor != null ? doctor.getName() : "Unknown",
                    DateTimeFormatterUtil.formatForDisplay(appt.getAppointmentTime()),
                    appt.getStatus()
            );
        }
        System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+------------+");

        //        for (Appointment appt : appointments) {
        //            System.out.println(appt);
        //            System.out.println("----");
        //        }
    }

    public LocalDateTime selectDoctorSlot(ConsultationMaintenance maintenance, Doctor doctor, Scanner scanner) {
        String[] availableSlots = maintenance.getAvailableSlotsForDoctor(doctor.getDoctorID());
        if (availableSlots == null || availableSlots.length == 0) {
            System.out.println("No available slots for this doctor.");
            return null;
        }
        System.out.println("Available Time Slots:");
        for (int i = 0; i < availableSlots.length; i++) {
            System.out.println("  " + (i + 1) + ". " + availableSlots[i]);
        }
        int selectedSlot = -1;
        while (selectedSlot < 1 || selectedSlot > availableSlots.length) {
            System.out.print("Select slot (1-" + availableSlots.length + ", or 0 to return): ");
            try {
                selectedSlot = Integer.parseInt(scanner.nextLine().trim());
                if (selectedSlot == 0) return null;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Try again.");
            }
        }
        // Parse slot string to LocalDateTime
        String selectedSlotStr = availableSlots[selectedSlot - 1];
        String[] parts = selectedSlotStr.split(" ");
        String datePart = parts[0];
        String timeslot = parts[1];
        String[] timeParts = timeslot.split("[–-]");
        String startTime = timeParts[0].trim();
        String[] dateElems = datePart.split("-");
        String formattedDatePart = dateElems[2] + "/" + dateElems[1] + "/" + dateElems[0];
        String appointmentDateTimeStr = formattedDatePart + " " + startTime;
        return DateTimeFormatterUtil.parseDisplayFormat(appointmentDateTimeStr);
    }

    private void addAppointment() {
        System.out.println("\n-- Add Appointment --");
        printAllAppointments(maintenance.getAllAppointments());

        String appointmentId;
        while (true) {
            System.out.print("Enter Appointment ID (Eg. A001, or 0 to return): ");
            appointmentId = scanner.nextLine().trim();
            if (appointmentId.equals("0")) return;
            if (appointmentId.isEmpty()) {
                System.out.println("Appointment ID cannot be empty.");
                continue;
            }
            if (!InputHandler.isValidId(appointmentId, "appointment")) {
                System.out.println("Invalid Appointment ID. Must be in format A999. ");
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
        System.out.println("\nAvailable Patients:");
        System.out.println("+------------+----------------------+");
        System.out.printf("| %-10s | %-20s | \n", "Patient ID", "Name");
        System.out.println("+------------+----------------------+");
        for (Patient p : patients) {
            System.out.printf("| %-10s | %-20s | \n", p.getPatientId(), p.getName());
        }
        System.out.println("+------------+----------------------+");

        String patientId = "";
        Patient patient = null;
        while (patient == null) {
            System.out.print("Enter Patient ID (Eg. P001, or 0 to return): ");
            patientId = scanner.nextLine().trim();
            if (patientId.equals("0")) return;

            if (patientId.isEmpty()) {
                System.out.println("Patient ID cannot be empty.");
                continue;
            }
            if (!InputHandler.isValidId(patientId, "patient")) {
                System.out.println("Invalid Patient ID. Must be in format P999. ");
                continue;
            }

            patient = maintenance.getPatient(patientId);
            if (patient == null) System.out.println("Patient not found. Please enter a valid Patient ID.");
        }

        Doctor[] doctors = maintenance.getAllDoctors();
        if (doctors == null || doctors.length == 0) {
            System.out.println("No doctors available. Please create a doctor first.");
            return;
        }

//        System.out.println("\nAvailable Doctors:");
//        System.out.println("+------------+----------------------+----------------------+");
//        System.out.printf("| %-10s | %-20s | %-20s | \n", "Doctor ID", "Name", "Specialization");
//        System.out.println("+------------+----------------------+----------------------+");
//        for (Doctor d : doctors) {
//            System.out.printf("| %-10s | %-20s | %-20s | \n", d.getDoctorID(), d.getName(), d.getSpecialty());
//        }
//        System.out.println("+------------+----------------------+----------------------+");
//
//        String doctorId = "";
//        Doctor doctor = null;
//        while (doctor == null) {
//            System.out.print("Enter Doctor ID: ");
//            doctorId = scanner.nextLine().trim();
//            doctor = maintenance.getDoctor(doctorId);
//            if (doctor == null) System.out.println("Invalid Doctor ID.");
//        }

        // TODO To check schedule
//        LocalDateTime[] availableSlots = maintenance.getAvailableSlotsForDoctor(doctor.getDoctorID());
//        if (availableSlots == null || availableSlots.length == 0) {
//            System.out.println("No available slots for selected doctor.");
//            return;
//        }
//
//        System.out.println("Available Time Slots:");
//        for (int i = 0; i < availableSlots.length; i++) {
//            System.out.println((i + 1) + ". " + DateTimeFormatterUtil.parseDisplayFormat(String.valueOf(availableSlots[i])));
//        }

        System.out.println("\nAvailable Doctors and Slots:");
        System.out.println("+------------+----------------------+----------------------+---------------------------+");
        System.out.printf("| %-10s | %-20s | %-20s | %-25s |\n", "Doctor ID", "Name", "Specialization", "Available Slot(s)");
        System.out.println("+------------+----------------------+----------------------+---------------------------+");

        for (Doctor doctor : doctors) {
            String[] slots = maintenance.getAvailableSlotsForDoctor(doctor.getDoctorID());
            if (slots != null && slots.length > 0) {
                // First row: show doctor info
                System.out.printf("| %-10s | %-20s | %-20s | %-25s |\n",
                        doctor.getDoctorID(),
                        doctor.getName(),
                        doctor.getSpecialty(),
                        slots[0]
                );
                // Next slot rows: leave doctor columns blank
                for (int i = 1; i < slots.length; i++) {
                    System.out.printf("| %-10s | %-20s | %-20s | %-25s |\n",
                            "", "", "", slots[i]
                    );
                }
            } else {
                // No slots: show doctor info with "No slots"
                System.out.printf("| %-10s | %-20s | %-20s | %-25s |\n",
                        doctor.getDoctorID(),
                        doctor.getName(),
                        doctor.getSpecialty(),
                        "No slots"
                );
            }
        }
        System.out.println("+------------+----------------------+----------------------+---------------------------+");

        Doctor doctor = null;
        LocalDateTime appointmentTime = null;
        while (doctor == null || appointmentTime == null) {
            System.out.print("\nEnter Doctor ID (Eg. D001, or 0 to return): ");
            String doctorId = scanner.nextLine().trim();
            if (doctorId.equals("0")) return;
            if (doctorId.isEmpty()) {
                System.out.println("Doctor ID cannot be empty.");
                continue;
            }
            if (!InputHandler.isValidId(doctorId, "doctor")) {
                System.out.println("Invalid Doctor ID. Must be in format DC999. ");
                continue;
            }
            doctor = maintenance.getDoctor(doctorId);
            if (doctor == null) {
                System.out.println("Doctor not found. Please enter a valid Doctor ID.");
                continue;
            }
            appointmentTime = selectDoctorSlot(maintenance, doctor, scanner);
            if (appointmentTime == null) {
                doctor = null;
            }
        }

        String status = "Scheduled";

        Appointment appointment = new Appointment(appointmentId, patientId, doctor.getDoctorID(), appointmentTime, status);
        maintenance.addAppointment(appointment);
        System.out.println("Appointment added.");
    }

    private void removeAppointment() {
        System.out.println("\n-- Remove Appointment --");
        printAllAppointments(maintenance.getAllAppointments());

        System.out.print("\nEnter Appointment ID to remove (Eg. A001, or 0 to return): ");
        String appointmentId;
        while (true) {
            appointmentId = scanner.nextLine().trim();
            if (appointmentId.equals("0")) return;
            if (appointmentId.isEmpty()) {
                System.out.println("Appointment ID cannot be empty.");
                continue;
            }
            if (!InputHandler.isValidId(appointmentId, "appointment")) {
                System.out.println("Invalid Appointment ID. Must be in format A999. ");
                continue;
            }
            if (maintenance.getAppointment(appointmentId) == null) {
                System.out.println("Appointment not found. Please enter a valid Appointment ID. ");
                continue;
            }
            break;
        }
        boolean success = maintenance.removeAppointment(appointmentId);
        if (success) {
            System.out.println("Appointment removed.");
        } else {
            System.out.println("Failed to remove appointment.");
        }
    }

    private void updateAppointmentStatus() {
        System.out.println("\n-- Update Appointment --");
        printAllAppointments(maintenance.getAllAppointments());

        Appointment appointment = null;
        String appointmentId = "";
        while (appointment == null) {
            System.out.print("\nEnter Appointment ID to update status (Eg. A001, or 0 to return): ");
            appointmentId = scanner.nextLine().trim();
            if (appointmentId.equals("0")) return;
            if (appointmentId.isEmpty()) {
                System.out.println("Appointment ID cannot be empty.");
                continue;
            }
            if (!InputHandler.isValidId(appointmentId, "appointment")) {
                System.out.println("Invalid Appointment ID. Must be in format A999. ");
                continue;
            }
            appointment = maintenance.getAppointment(appointmentId);
            if (appointment == null) {
                System.out.println("Appointment not found. Please enter a valid Appointment ID. ");
            }
        }

        String newStatus = "";
        String[] validStatuses = ConsultationMaintenance.VALID_APPOINTMENT_STATUSES;
        while (true) {
            System.out.print("Current status: " + appointment.getStatus() + "\nEnter new status (");
            for (int i = 0; i < validStatuses.length; i++) {
                System.out.print(validStatuses[i]);
                if (i < validStatuses.length - 1) System.out.print(" / ");
            }
            System.out.print("): ");
            newStatus = scanner.nextLine().trim();
            if (!maintenance.isValidStatus(newStatus)) {
                System.out.println("Invalid status. Please enter a valid status. ");
            } else {
                break;
            }
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
        printAllAppointments(appointments);

        // Show sorting options
        int choice;
        do {
            System.out.println("\nSort appointments by: ");
            System.out.println("1. Patient Name");
            System.out.println("2. Doctor Name");
            System.out.println("3. Date");
            System.out.println("0. Return to Appointment Management Menu");

            System.out.print("Choose sorting option: ");
            String input = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number. ");
                continue;
            }

            switch (choice) {
                case 1 -> maintenance.sortAppointmentsByPatientName();
                case 2 -> maintenance.sortAppointmentsByDoctorName();
                case 3 -> maintenance.sortAppointmentsByDate();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice, showing unsorted list.");
            }
            printAllAppointments(maintenance.getAllAppointments());
        } while (true);
    }

    public static void main(String[] args) {
        new ConsultationMaintenanceUI().run();
    }
}