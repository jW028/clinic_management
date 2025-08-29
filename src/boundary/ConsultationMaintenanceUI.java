package boundary;

import control.ConsultationMaintenance;
import control.PatientMaintenance;
import entity.Appointment;
import entity.Consultation;
import entity.ConsultationService;
import entity.Diagnosis;
import entity.Patient;
import entity.Doctor;
import entity.VisitHistory;
import adt.CustomADT;
import utility.IDGenerator;
import utility.InputHandler;
import utility.DateTimeFormatterUtil;

import java.time.LocalDateTime;
import java.util.Scanner;

public class ConsultationMaintenanceUI {
    private final ConsultationMaintenance maintenance;
    private final PatientMaintenance patientMaintenance;
    private final Scanner scanner;
    private String nearestDoctorSlotStr = null;

    public ConsultationMaintenanceUI(PatientMaintenance patientMaintenance) {
        maintenance = new ConsultationMaintenance();
        this.patientMaintenance = patientMaintenance;
        scanner = new Scanner(System.in);
    }

    public void run() {
        int choice;
        do {
            printMenu();
            choice = InputHandler.getInt("Select an option", 0, 3);

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

    public void printMenu() {
        System.out.println("\n┌" + "─".repeat(42) + "┐");
        System.out.println("│       CONSULTATION MAINTENANCE MENU      |");
        System.out.println("├" + "─".repeat(42) + "┤");
        System.out.println("│ 1. Manage Appointments                   │");
        System.out.println("│ 2. Manage Consultations                  │");
        System.out.println("│ 3. View Reports                          │");
        System.out.println("│ 0. Return to Main Menu                   │");
        System.out.println("└" + "─".repeat(42) + "┘");
    }

    private void appointmentMenu() {
        int choice;
        do {
            System.out.println("\n┌" + "─".repeat(42) + "┐");
            System.out.println("│       APPOINTMENT MANAGEMENT MENU        |");
            System.out.println("├" + "─".repeat(42) + "┤");
            System.out.println("│ 1. View All Appointments                 │");
            System.out.println("│ 2. Add Future Appointment                │");
            System.out.println("│ 3. Add Walk-In Appointment               │");
            System.out.println("│ 4. Cancel Appointment                    │");
            System.out.println("│ 5. Remove Appointment                    │");
            System.out.println("│ 6. Search Appointments by Patient Name   │");
            System.out.println("│ 7. Search Appointments by Doctor Name    │");
            System.out.println("│ 0. Return                                │");
            System.out.println("└" + "─".repeat(42) + "┘");

            choice = InputHandler.getInt("Select an option", 0, 7);

            switch (choice) {
                case 1 -> viewAllAppointments();
                case 2 -> adminMakeAppointment();
                case 3 -> adminWalkInAppointment();
                case 4 -> cancelAppointment();
                case 5 -> removeAppointment();
                case 6 -> searchAppointmentsByPatientName();
                case 7 -> searchAppointmentsByDoctorName();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice. ");
            }
        } while (true);
    }

    private void consultationMenu() {
        int choice;
        do {
            System.out.println("\n┌" + "─".repeat(42) + "┐");
            System.out.println("│       CONSULTATION MANAGEMENT MENU       |");
            System.out.println("├" + "─".repeat(42) + "┤");
            System.out.println("│ 1. View All Consultations                │");
            System.out.println("│ 2. Add Consultation                      │");
            System.out.println("│ 3. Update Consultation                   │");
            System.out.println("│ 4. Delete Consultation                   │");
            System.out.println("│ 5. Search Consultations by Patient Name  │");
            System.out.println("│ 6. Search Consultations by Doctor Name   │");
            System.out.println("│ 0. Return                                │");
            System.out.println("└" + "─".repeat(42) + "┘");

            choice = InputHandler.getInt("Select an option", 0, 6);

            switch (choice) {
                case 1 -> viewAllConsultations();
                case 2 -> addConsultation();
                case 3 -> updateConsultation();
                case 4 -> deleteConsultation();
                case 5 -> searchConsultationsByPatientName();
                case 6 -> searchConsultationsByDoctorName();
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
            System.out.println("\n┌" + "─".repeat(42) + "┐");
            System.out.println("│               REPORT MENU                |");
            System.out.println("├" + "─".repeat(42) + "┤");
            System.out.println("│ 1. Print Consultation Summary Report     │");
            System.out.println("│ 2. Print Service Usage Report            │");
            System.out.println("│ 3. Print Appointments per Doctor Report  │");
            System.out.println("│ 0. Return                                │");
            System.out.println("└" + "─".repeat(42) + "┘");

            choice = InputHandler.getInt("Select an option", 0, 3);

            switch (choice) {
                case 1 -> maintenance.printConsultationSummaryReport();
                case 2 -> maintenance.printServiceUsageReport();
                case 3 -> maintenance.printAppointmentsPerDoctorReport();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice");
            }
        } while (true);
    }

    private void viewAllConsultations() {
        Consultation[] consultations = maintenance.getAllConsultations();
        if (consultations == null || consultations.length == 0) {
            System.out.println("No consultations found.");
            return;
        }
        printAllConsultations(consultations);

        int choice;
        do {
            System.out.println("\n┌" + "─".repeat(35) + "┐");
            System.out.println("│ Sort Consultations By;            │");
            System.out.println("├" + "─".repeat(35) + "┤");
            System.out.println("│ 1. By ID                          │");
            System.out.println("│ 2. By Patient Name                │");
            System.out.println("│ 3. By Doctor Name                 │");
            System.out.println("│ 4. By Date                        │");
            System.out.println("│ 0. Exit without sorting           │");
            System.out.println("└" + "─".repeat(35) + "┘");

            choice = InputHandler.getInt("Choose option", 0, 4);

            switch (choice) {
                case 1 -> maintenance.sortConsultationsByID();
                case 2 -> maintenance.sortConsultationsByPatientName();
                case 3 -> maintenance.sortConsultationsByDoctorName();
                case 4 -> maintenance.sortConsultationsByDate();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice, showing unsorted list. ");
            }
            printAllConsultations(maintenance.getAllConsultations());
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

        /*
        Earliest 'Scheduled' Appointment for Consultation
         */
//        CustomADT<String, Appointment> allAppointmentsADT = new CustomADT<>();
//        Appointment[] allAppointments = maintenance.getAllAppointments();
//        for (Appointment a : allAppointments) {
//            allAppointmentsADT.put(a.getAppointmentId(), a);
//        }
//
//        // Find the earliest 'Scheduled' appointment
//        Appointment earliest = null;
//        for (int i = 0; i < allAppointmentsADT.size(); i++) {
//            Appointment a = allAppointmentsADT.get(i);
//            if ("Scheduled".equalsIgnoreCase(a.getStatus())) {
//                if (earliest == null || a.getAppointmentTime().isBefore(earliest.getAppointmentTime())) {
//                    earliest = a;
//                }
//            }
//        }
//        if (earliest == null) {
//            System.out.println("No scheduled appointments available. ");
//            return;
//        }
//        System.out.println("-- Earliest Scheduled Appointments --");
//        printAllAppointments(new Appointment[] { earliest });
//        boolean proceed = InputHandler.getYesNo("Proceed with consultation for this appointment? ");
//        if (!proceed) {
//            System.out.println("Consultation creation cancelled. ");
//            return;
//        }
//        Appointment appointment = earliest;

        // Select Appointment
        System.out.println("\n-- Available Appointments --");
        maintenance.sortAppointmentsByDate();
        printAllAppointments(maintenance.getAllAppointments());

        Appointment appointment = null;
        while (appointment == null) {
            System.out.print("\nEnter Appointment ID (Eg. A001, or 0 to return): ");
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
                continue;
            }

            if ("walk-in".equalsIgnoreCase(appointment.getAppointmentType())) {
                String currentPatientId = appointment.getPatientId();
                if (patientMaintenance.isPatientInQueue(currentPatientId)) {
                    Patient firstPatient = patientMaintenance.peekNextPatient();
                    if (firstPatient != null && !firstPatient.getPatientId().equals(currentPatientId)) {
                        // Patient is in queue but not first, block and prompt
                        System.out.println("Patient is in the queue, but not first. Please select the first walk-in patient for consultation.");
                        appointment = null;
                    }
                }
            }
        }

        Patient patient = maintenance.getPatient(appointment.getPatientId());
        Doctor doctor = maintenance.getDoctor(appointment.getDoctorId());

        if (patient == null || doctor == null) {
            System.out.println("Invalid patient or doctor in the appointment.");
            return;
        }

        LocalDateTime consultationTime = appointment.getAppointmentTime();

        // Select ConsultationServices
        CustomADT<String, ConsultationService> servicesUsed = new CustomADT<>();
        System.out.println("\n-- Available Services --");
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
                String serviceId = InputHandler.getString("\nEnter Service ID to add (or 0 to return)");
                if (serviceId.equals("0")) return;
                if (!InputHandler.isValidId(serviceId, "service")) {
                    System.out.println("Invalid format. Must be S followed by 3 digits (Eg. S001).");
                    continue;
                }
                selectedService = maintenance.getService(serviceId);
                if (selectedService == null) {
                    System.out.println("Service not found. Please enter a valid Service ID. ");
                }
            }
            servicesUsed.put(String.valueOf(index++), selectedService);
            while (true) {
                System.out.print("\nAdd another service? (y/n): ");
                more = scanner.nextLine().trim().toLowerCase();
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
        System.out.println("\n-- Available Diagnoses --");
        System.out.println("+-----------------+----------------------+--------------------------------------------+-----------------+");
        System.out.printf("| %-15s | %-20s | %-42s | %-15s | \n",
                "Diagnosis ID", "Name", "Description", "Severity");
        System.out.println("+-----------------+----------------------+--------------------------------------------+-----------------+");
        for (Diagnosis diag : diagnoses) {
            System.out.printf("| %-15s | %-20s | %-42s | %-15s | \n",
                    diag.getId(), diag.getName(), diag.getDescription(), diag.getSeverity());
        }
        System.out.println("+-----------------+----------------------+--------------------------------------------+-----------------+");

        Diagnosis diagnosis = null;
        while (diagnosis == null) {
            System.out.print("\nEnter Diagnosis ID (or 0 to return): ");
            String diagnosisId = scanner.nextLine().trim();
            if (diagnosisId.equals("0")) return;
            if (diagnosisId.isEmpty()) {
                System.out.println("Diagnosis ID cannot be empty. ");
                continue;
            }
            if (!InputHandler.isValidId(diagnosisId, "diagnosis")) {
                System.out.println("Invalid format. Must be D followed by 3 digits (Eg. D001).");
                continue;
            }

            diagnosis = maintenance.getDiagnosis(diagnosisId);
            if (diagnosis == null) System.out.println("Diagnosis not found. Please enter a valid Diagnosis ID. ");
        }

        System.out.print("\nEnter notes (or leave empty to skip): ");
        String notes = scanner.nextLine();

        boolean followUpNeeded = false;
        LocalDateTime followUpDate = null;
        while (true) {
            System.out.print("\nIs follow-up needed? (y/n, or 0 to return): ");
            String followUpAns = scanner.nextLine().trim().toLowerCase();
            if (followUpAns.equals("0")) return;
            if (!followUpAns.equalsIgnoreCase("y") && !followUpAns.equalsIgnoreCase("n")) {
                System.out.println("Invalid input. Please enter 'y' or 'n' only. ");
                continue;
            } else {
                if (followUpAns.equalsIgnoreCase("y")) {
                    followUpNeeded = true;
                } else {
                    followUpNeeded = false;
                }
            }
            break;
        }
        if (followUpNeeded) {
            // Get available slots for the assigned doctor
            String[] slots = maintenance.getAvailableSlotsForDoctor(doctor.getDoctorID());
            System.out.println("\nFollow-Up Available Slots for Doctor:");
            System.out.println("+------------+----------------------+----------------------+--------------------------------+");
            System.out.printf("| %-10s | %-20s | %-20s | %-30s |\n", "Doctor ID", "Name", "Specialization", "Available Slot(s)");
            System.out.println("+------------+----------------------+----------------------+--------------------------------+");

            if (slots != null && slots.length > 0) {
                int slotIndex = 1;
                CustomADT<Integer, Object[]> slotSelectionMap = new CustomADT<>();
                for (int i = 0; i < slots.length; i++) {
                    if (i == 0) {
                        System.out.printf("| %-10s | %-20s | %-20s | %3d. %-25s |\n",
                                doctor.getDoctorID(),
                                doctor.getName(),
                                doctor.getSpecialty(),
                                slotIndex, slots[i]
                        );
                    } else {
                        System.out.printf("| %-10s | %-20s | %-20s | %3d. %-25s |\n",
                                "", "", "", slotIndex, slots[i]
                        );
                    }
                    slotSelectionMap.put(slotIndex, new Object[]{doctor, slots[i]});
                    slotIndex++;
                }
                System.out.println("+------------+----------------------+----------------------+--------------------------------+");

                // Input loop for follow-up slot selection
                do {
                    String chosenStr = InputHandler.getString("Enter follow-up slot number (or 0 to skip)");
                    if (chosenStr.equals("0")) {
                        followUpDate = null;
                        break;
                    }

                    int chosenIndex;
                    try {
                        chosenIndex = Integer.parseInt(chosenStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number.");
                        continue;
                    }
                    if (!slotSelectionMap.containsKey(chosenIndex)) {
                        System.out.println("Invalid slot number. Please enter a valid index shown above.");
                        continue;
                    }
                    Object[] selection = slotSelectionMap.get(chosenIndex - 1);
                    String slotStr = (String) selection[1];

                    String[] parts = slotStr.split(" ");
                    if (parts.length < 2) {
                        System.out.println("Slot format error.");
                        continue;
                    }

                    String datePart = parts[0];
                    String timeslot = parts[1];
                    String[] timeParts = timeslot.split("[-–—]");
                    if (timeParts.length < 2) {
                        System.out.println("Timeslot format error.");
                        followUpDate = null;
                        continue;
                    }
                    String startTime = timeParts[0].trim();
                    String[] dateElems = datePart.split("-");
                    String formattedDatePart = dateElems.length == 3 ?
                            dateElems[2] + "/" + dateElems[1] + "/" + dateElems[0] : datePart;
                    String appointmentDateTimeStr = formattedDatePart + " " + startTime;
                    try {
                        followUpDate = DateTimeFormatterUtil.parseDisplayFormat(appointmentDateTimeStr);
                    } catch (Exception ex) {
                        System.out.println("Failed to parse follow-up time from slot.");
                        followUpDate = null;
                        continue;
                    }
                    if (followUpDate.isBefore(consultationTime)) {
                        System.out.println("Selected follow-up date must be after the consultation date. ");
                        followUpDate = null;
                    }
                } while (followUpDate == null);

                // Only prompt to create follow-up if a valid slot was picked
                boolean createFollowUp = InputHandler.getYesNo("Create follow-up appointment now?");
                if (createFollowUp) {
                    String newApptId = IDGenerator.generateAppointmentID();
                    System.out.println("Generated Appointment ID: " + newApptId);

                    Appointment followUp = new Appointment(newApptId, patient.getPatientId(), doctor.getDoctorID(), followUpDate, "Scheduled", "appointment");
                    maintenance.addAppointment(followUp);
                    System.out.println("Follow-up appointment created.");
                }
            } else {
                System.out.printf("| %-10s | %-20s | %-20s | %-30s |\n",
                        doctor.getDoctorID(),
                        doctor.getName(),
                        doctor.getSpecialty(),
                        "      No slots"
                );
                System.out.println("+------------+----------------------+----------------------+--------------------------------+");
                System.out.println("\n❌ No available follow-up slots for this doctor. Follow-up appointment cannot be created.");
            }
        }

        // Generate consultation id
        String consultationId = IDGenerator.generateConsultationID();
        System.out.println("Generated Consultation ID: " + consultationId);

        Consultation consultation = new Consultation(
                consultationId,
                appointment,
                patient,
                doctor,
                consultationTime,
                servicesUsed,
                diagnosis,
                notes,
                null,
                followUpNeeded,
                followUpDate
        );

        maintenance.addConsultation(consultation);
        appointment.setStatus("In Progress");
        maintenance.updateAppointmentStatus(appointment.getAppointmentId(), "In Progress");
        System.out.println("Consultation added.");

        CustomADT<String, VisitHistory> visits = patientMaintenance.getPatientVisitHistory(consultation.getPatient().getPatientId());
        for (int i = 0; i < visits.size(); i++) {
            VisitHistory vh = visits.get(i);
            if (vh.getPatient().getPatientId().equals(consultation.getPatient().getPatientId())
                    && vh.getVisitDate().toLocalDate().equals(consultation.getAppointment().getAppointmentTime().toLocalDate())
                    && "SCHEDULED".equalsIgnoreCase(vh.getStatus())) {
                patientMaintenance.updateVisitHistory(
                        consultation.getPatient().getPatientId(),
                        vh.getVisitId(),
                        vh.getVisitReason(),
                        "COMPLETED"
                );
                break;
            }
        }

        // Dequeue patient from walk-in queue
        String currentPatientId = appointment.getPatientId();
        if ("walk-in".equalsIgnoreCase(appointment.getAppointmentType())
                && "In Progress".equalsIgnoreCase(appointment.getStatus())) {
            if (patientMaintenance.isPatientInQueue(currentPatientId)) {
                Patient firstPatient = patientMaintenance.peekNextPatient();
                if (firstPatient != null && firstPatient.getPatientId().equals(currentPatientId)) {
                    patientMaintenance.serveNextPatient();
                    System.out.println("Patient dequeued. Proceeding with consultation.");
                }
            } else {
                System.out.println("Patient is not in the queue. Proceeding without dequeue.");
            }
        }
    }

    private void updateConsultation () {
        System.out.println("\n-- Update Consultation --");
        printAllConsultations(maintenance.getAllConsultations());

        String consultationId;
        Consultation consultation = null;

        while (true) {
            consultationId = InputHandler.getString("\nEnter Consultation ID to update (Eg. C001, or 0 to return)");
            if (consultationId.equals("0")) return;
            if (!InputHandler.isValidId(consultationId, "consultation")) {
                System.out.println("Invalid format. Must be C followed by 3 digits (Eg. C001).");
                continue;
            }
            consultation = maintenance.getConsultation(consultationId);
            if (consultation == null) {
                System.out.println("Consultation not found.");
                continue;
            }
            break;
        }

        boolean done = false;
        while (!done) {
            System.out.println("\nCurrent Consultation Details:");
            System.out.println("Diagnosis: " + (consultation.getDiagnosis() != null ? consultation.getDiagnosis().getName() : "None"));
            System.out.println("Notes: " + (consultation.getNotes() != null ? consultation.getNotes() : "(none)"));

            // Print services
            System.out.print("Services: ");
            if (consultation.getServicesUsed() != null && consultation.getServicesUsed().size() > 0) {
                for (int i = 0; i < consultation.getServicesUsed().size(); i++) {
                    ConsultationService cs = consultation.getServicesUsed().get(i);
                    System.out.print(cs.getServiceName());
                    if (i < consultation.getServicesUsed().size() - 1) System.out.print(", ");
                }
                System.out.println();
            } else {
                System.out.println("(none)");
            }

            // Follow-up
            System.out.println("Follow-Up Needed: " + (consultation.isFollowUpNeeded() ? "Yes" : "No"));
            System.out.println("Follow-Up Date: " + (consultation.getFollowUpDate() != null ? DateTimeFormatterUtil.formatForDisplay(consultation.getFollowUpDate()) : "(none)"));

            System.out.println("\nSelect field to update:");
            System.out.println("1. Diagnosis");
            System.out.println("2. Notes");
            System.out.println("3. Add Service");
            System.out.println("4. Remove Service");
            System.out.println("5. Change Follow-Up");
            System.out.println("0. Finish Update");
            int choice = InputHandler.getInt(0, 5);

            switch (choice) {
                case 1: {
                    Diagnosis[] diagnoses = maintenance.getAllDiagnoses();
                    CustomADT<String, Diagnosis> diagnosesADT = new CustomADT<>();
                    for (Diagnosis d : diagnoses) diagnosesADT.put(d.getId(), d);
                    System.out.println("\nAvailable Diagnoses:");
                    System.out.println("+-----------------+----------------------+--------------------------------------------+-----------------+");
                    System.out.printf("| %-15s | %-20s | %-42s | %-15s | \n",
                            "Diagnosis ID", "Name", "Description", "Severity");
                    System.out.println("+-----------------+----------------------+--------------------------------------------+-----------------+");
                    for (Diagnosis diag : diagnoses) {
                        System.out.printf("| %-15s | %-20s | %-42s | %-15s | \n",
                                diag.getId(), diag.getName(), diag.getDescription(), diag.getSeverity());
                    }
                    System.out.println("+-----------------+----------------------+--------------------------------------------+-----------------+");

                    Diagnosis newDiagnosis = null;
                    while (newDiagnosis == null) {
                        String diagnosisId = InputHandler.getString("Enter new Diagnosis ID (or 0 to cancel)");
                        if (diagnosisId.equals("0")) break;
                        if (!InputHandler.isValidId(diagnosisId, "diagnosis")) {
                            System.out.println("Invalid format. Must be D followed by 3 digits (Eg. D001).");
                            continue;
                        }
                        newDiagnosis = diagnosesADT.get(diagnosisId);
                        if (newDiagnosis == null) {
                            System.out.println("Diagnosis not found. Please enter a valid Diagnosis ID.");
                        }
                    }
                    if (newDiagnosis != null) {
                        consultation.setDiagnosis(newDiagnosis);
                        System.out.println("Diagnosis updated.");
                    }
                    break;
                }
                case 2: {
                    String newNotes = InputHandler.getOptionalString("Enter new notes (or leave empty to skip)");
                    consultation.setNotes(newNotes);
                    System.out.println("Notes updated.");
                    break;
                }
                case 3: { // Add Service
                    ConsultationService[] services = maintenance.getAllServices();
                    CustomADT<String, ConsultationService> servicesADT = new CustomADT<>();
                    for (ConsultationService s : services) servicesADT.put(s.getServiceId(), s);
                    System.out.println("\nAvailable Services:");
                    System.out.println("+------------+---------------------------+------------+");
                    System.out.printf("| %-10s | %-25s | %-10s | \n",
                            "Service ID", "Name", "Fee");
                    System.out.println("+------------+---------------------------+------------+");
                    for (ConsultationService service : services) {
                        System.out.printf("| %-10s | %-25s | %-10.2f | \n",
                                service.getServiceId(), service.getServiceName(), service.getServiceCharge());
                    }
                    System.out.println("+------------+---------------------------+------------+");
                    ConsultationService addedService = null;
                    while (addedService == null) {
                        String serviceId = InputHandler.getString("Enter Service ID to add (or 0 to cancel)");
                        if (serviceId.equals("0")) break;
                        addedService = servicesADT.get(serviceId);
                        if (addedService == null) {
                            System.out.println("Service not found. Please enter a valid Service ID.");
                        }
                    }
                    if (addedService != null) {
                        // Only add if not already present
                        boolean alreadyHas = false;
                        for (int i = 0; i < consultation.getServicesUsed().size(); i++) {
                            ConsultationService cs = consultation.getServicesUsed().get(i);
                            if (cs.getServiceId().equals(addedService.getServiceId())) {
                                alreadyHas = true;
                                break;
                            }
                        }
                        if (!alreadyHas) {
                            consultation.getServicesUsed().put(String.valueOf(consultation.getServicesUsed().size() + 1), addedService);
                            System.out.println("Service added.");
                        } else {
                            System.out.println("Service already present in this consultation.");
                        }
                    }
                    break;
                }
                case 4: { // Remove Service
                    CustomADT<String, ConsultationService> usedADT = consultation.getServicesUsed();
                    if (usedADT == null || usedADT.size() == 0) {
                        System.out.println("No services to remove.");
                        break;
                    }
                    System.out.println("Services in this consultation:");
                    for (int i = 0; i < usedADT.size(); i++) {
                        ConsultationService cs = usedADT.get(i);
                        System.out.println((i + 1) + ". " + cs.getServiceId() + ": " + cs.getServiceName());
                    }
                    int removeIndex = InputHandler.getInt("Enter service number to remove (or 0 to cancel)", 0, usedADT.size());
                    if (removeIndex == 0) break;
                    if (removeIndex < 1 || removeIndex > usedADT.size()) {
                        System.out.println("Invalid service number.");
                    } else {
                        usedADT.removeAt(removeIndex - 1);
                        System.out.println("Service removed.");
                    }
                    break;
                }
                case 5: { // Change follow-up
                    boolean followUpNeeded = InputHandler.getYesNo("Is follow-up needed?");
                    consultation.setFollowUpNeeded(followUpNeeded);
                    LocalDateTime followUpDate = null;
                    if (followUpNeeded) {
                        Doctor doctor = consultation.getDoctor();
                        String[] slots = maintenance.getAvailableSlotsForDoctor(doctor.getDoctorID());
                        while (true) {
                            if (slots != null && slots.length > 0) {
                                for (int i = 0; i < slots.length; i++) {
                                    System.out.println((i + 1) + ". " + slots[i]);
                                }
                                String chosenStr = InputHandler.getString("Enter follow-up slot number (or 0 to skip)");
                                if (chosenStr.equals("0")) {
                                    followUpDate = null;
                                    break;
                                }
                                int chosenIndex;
                                try {
                                    chosenIndex = Integer.parseInt(chosenStr);
                                } catch (NumberFormatException e) {
                                    System.out.println("Please enter a valid number.");
                                    continue;
                                }
                                if (chosenIndex < 1 || chosenIndex > slots.length) {
                                    System.out.println("Invalid slot number. Please enter a valid index.");
                                    continue;
                                }
                                String slotStr = slots[chosenIndex - 1];
                                String[] parts = slotStr.split(" ");
                                if (parts.length < 2) {
                                    System.out.println("Slot format error.");
                                    continue;
                                }
                                String datePart = parts[0];
                                String timeslot = parts[1];
                                String[] timeParts = timeslot.split("[-–—]");
                                if (timeParts.length < 2) {
                                    System.out.println("Timeslot format error.");
                                    continue;
                                }
                                String startTime = timeParts[0].trim();
                                String[] dateElems = datePart.split("-");
                                String formattedDatePart = dateElems.length == 3
                                        ? dateElems[2] + "/" + dateElems[1] + "/" + dateElems[0]
                                        : datePart;
                                String appointmentDateTimeStr = formattedDatePart + " " + startTime;
                                try {
                                    followUpDate = DateTimeFormatterUtil.parseDisplayFormat(appointmentDateTimeStr);
                                    if (followUpDate.isBefore(consultation.getAppointment().getAppointmentTime())) {
                                        System.out.println("Selected follow-up date must be after the consultation date.");
                                        continue;
                                    }
                                } catch (Exception ex) {
                                    System.out.println("Failed to parse follow-up time from slot.");
                                    continue;
                                }
                                break; // Valid slot selected
                            } else {
                                System.out.println("No follow-up slots available for this doctor.");
                                boolean retry = InputHandler.getYesNo("Do you want to retry? (Otherwise, follow-up will be skipped)");
                                if (!retry) {
                                    followUpDate = null;
                                    break;
                                } else {
                                    slots = maintenance.getAvailableSlotsForDoctor(doctor.getDoctorID());
                                }
                            }
                        }
                    }
                    consultation.setFollowUpDate(followUpDate);
                    System.out.println("Follow-up updated.");
                    break;
                }
                case 0:
                    done = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }

        System.out.println("Consultation updated.");
    }

    private void deleteConsultation() {
        System.out.println("\n-- Delete Consultation --");
        printAllConsultations(maintenance.getAllConsultations());

        String consultationId;
        while (true) {
            System.out.print("\nEnter Consultation ID to delete (Eg. C001, or 0 to return): ");
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

    private void searchConsultationsByPatientName() {
        while (true) {
            System.out.print("\nEnter patient name (Eg. Alice, or 0 to return): ");
            String pName = scanner.nextLine().trim();
            if (pName.equals("0")) return;
            CustomADT<String, Consultation> found = maintenance.searchConsultationsByPatientName(pName);

            if (found.isEmpty()) {
                System.out.println("No consultations found for patient name containing: " + pName);
                continue;
            }

            // Sort by patient name
            found.sort((c1, c2) -> {
                Patient p1 = c1.getPatient();
                Patient p2 = c2.getPatient();
                String n1 = (p1 != null) ? p1.getName() : "";
                String n2 = (p2 != null) ? p2.getName() : "";
                return n1.compareToIgnoreCase(n2);
            });

            printAllConsultations(found.toArray(new Consultation[0]));
        }
    }

    private void searchConsultationsByDoctorName() {
        while (true) {
            System.out.print("\nEnter doctor name (Eg. Alice, or 0 to return): ");
            String dName = scanner.nextLine().trim();
            if (dName.equals("0")) return;
            CustomADT<String, Consultation> found = maintenance.searchConsultationsByDoctorName(dName);

            if (found.isEmpty()) {
                System.out.println("No consultations found for doctor name containing: " + dName);
                continue;
            }

            // Sort by doctor name
            found.sort((c1, c2) -> {
                Doctor d1 = c1.getDoctor();
                Doctor d2 = c2.getDoctor();
                String n1 = (d1 != null) ? d1.getName() : "";
                String n2 = (d2 != null) ? d2.getName() : "";
                return n1.compareToIgnoreCase(n2);
            });

            printAllConsultations(found.toArray(new Consultation[0]));
        }
    }

    private void printAllConsultations(Consultation[] consultations) {
        if (consultations == null || consultations.length == 0) {
            System.out.println("No consultations found. ");
            return;
        }

        System.out.println("\n-- Consultation List --");
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

    private void viewAllAppointments() {
        System.out.println("\n-- All Appointments --");
        maintenance.sortAppointmentsByID();
        Appointment[] appointments = maintenance.getAllAppointments();
        if (appointments == null || appointments.length == 0) {
            System.out.println("No appointments found.");
            return;
        }
        printAllAppointments(appointments);

        // Show sorting options
        int choice;
        do {
            System.out.println("\n┌" + "─".repeat(35) + "┐");
            System.out.println("│ Sort Appointments By:             │");
            System.out.println("├" + "─".repeat(35) + "┤");
            System.out.println("│ 1. By ID                          │");
            System.out.println("│ 2. By Patient Name                │");
            System.out.println("│ 3. By Doctor Name                 │");
            System.out.println("│ 4. By Date                        │");
            System.out.println("│ 0. Exit without sorting           │");
            System.out.println("└" + "─".repeat(35) + "┘");

            choice = InputHandler.getInt("Choose option", 0, 5);

            switch (choice) {
                case 1 -> maintenance.sortAppointmentsByID();
                case 2 -> maintenance.sortAppointmentsByPatientName();
                case 3 -> maintenance.sortAppointmentsByDoctorName();
                case 4 -> maintenance.sortAppointmentsByDate();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice, showing unsorted list.");
            }
            printAllAppointments(maintenance.getAllAppointments());
        } while (true);
    }

    /**
     * Generic appointment creation flow.
     * Used by customer, admin, admin walk-in
     */
    public void createAppointmentFlow(String patientId, String appointmentType, Doctor[] doctorsToShow) {
        Patient patient = maintenance.getPatient(patientId);
        if (doctorsToShow == null || doctorsToShow.length == 0) {
            System.out.println("No doctors available for appointment.");
            return;
        }

        System.out.println("\nAvailable Doctors and Slots:");
        System.out.println("+------------+----------------------+----------------------+--------------------------------+");
        System.out.printf("| %-10s | %-20s | %-20s | %-30s |\n", "Doctor ID", "Name", "Specialization", "Available Slot(s)");
        System.out.println("+------------+----------------------+----------------------+--------------------------------+");

        int slotIndex = 1;
        CustomADT<Integer, Object[]> slotSelectionMap = new CustomADT<>();

        for (Doctor doctor : doctorsToShow) {
            String[] slots = maintenance.getAvailableSlotsForDoctor(doctor.getDoctorID());
            if (slots != null && slots.length > 0) {
                for (int i = 0; i < slots.length; i++) {
                    // Show slot with index
                    if (i == 0) {
                        System.out.printf("| %-10s | %-20s | %-20s | %3d. %-25s |\n",
                                doctor.getDoctorID(), doctor.getName(), doctor.getSpecialty(), slotIndex, slots[i]);
                    } else {
                        System.out.printf("| %-10s | %-20s | %-20s | %3d. %-25s |\n",
                                "", "", "", slotIndex, slots[i]);
                    }
                    slotSelectionMap.put(slotIndex, new Object[]{doctor, slots[i]});
                    slotIndex++;
                }
            } else {
                System.out.printf("| %-10s | %-20s | %-20s | %-30s |\n",
                        doctor.getDoctorID(), doctor.getName(), doctor.getSpecialty(), "    No slots");
            }
        }
        System.out.println("+------------+----------------------+----------------------+--------------------------------+");

        Doctor selectedDoctor = null;
        LocalDateTime appointmentTime = null;
        while (selectedDoctor == null || appointmentTime == null) {
            System.out.print("\nEnter slot number (or 0 to return): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Input cannot be empty.");
                continue;
            }
            if (input.equals("0")) return;

            int chosenIndex;
            try {
                chosenIndex = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }
            if (!slotSelectionMap.containsKey(chosenIndex)) {
                System.out.println("Invalid slot number. Please enter a valid index shown above.");
                continue;
            }

            Object[] selection = slotSelectionMap.get(chosenIndex - 1);
            selectedDoctor = (Doctor) selection[0];
            String slotStr = (String) selection[1];

            // Parse slot string: "dd/MM/yyyy HH:mm-HH:mm"
            String[] parts = slotStr.split(" ");
            if (parts.length < 2) {
                System.out.println("Slot format error.");
                selectedDoctor = null;
                continue;
            }
            String datePart = parts[0]; // dd/MM/yyyy
            String timeslot = parts[1]; // HH:mm-HH:mm
            String[] timeParts = timeslot.split("[–-]");
            if (timeParts.length < 2) {
                System.out.println("Timeslot format error.");
                selectedDoctor = null;
                continue;
            }
            String startTime = timeParts[0].trim(); // HH:mm
            String[] dateElems = datePart.split("-");
            String formattedDatePart = dateElems[2] + "/" + dateElems[1] + "/" + dateElems[0];
            String appointmentDateTimeStr = formattedDatePart + " " + startTime;
            try {
                appointmentTime = DateTimeFormatterUtil.parseDisplayFormat(appointmentDateTimeStr);
            } catch (Exception ex) {
                System.out.println("Failed to parse appointment time from slot.");
                selectedDoctor = null;
            }
        }

        String appointmentId = IDGenerator.generateAppointmentID();
        System.out.println("Generated Appointment ID: " + appointmentId);

        String status = "Scheduled";
        Appointment appointment = new Appointment(appointmentId, patientId, selectedDoctor.getDoctorID(), appointmentTime, status, appointmentType);
        maintenance.addAppointment(appointment);

        patientMaintenance.addVisitHistory(
                appointment.getPatientId(),
                "Appointment Scheduled",
                "SCHEDULED"
        );

        System.out.println("\nAppointment (" + appointmentId + ") booked successfully!");
        System.out.println("+-----------------+----------------------+----------------------+---------------------------+-----------------+");
        System.out.printf("| %-15s | %-20s | %-20s | %-25s | %-15s |\n", "Appointment ID", "Patient Name", "Doctor Name", "Date & Time", "Type");
        System.out.println("+-----------------+----------------------+----------------------+---------------------------+-----------------+");
        System.out.printf("| %-15s | %-20s | %-20s | %-25s | %-15s |\n",
                appointmentId, patient.getName(), selectedDoctor.getName(), DateTimeFormatterUtil.formatForDisplay(appointmentTime), appointmentType);
        System.out.println("+-----------------+----------------------+----------------------+---------------------------+-----------------+");
    }

    public void customerMakeAppointment(String currentLoginPatientId) {
        System.out.println("\n-- Add Appointment --");
        Doctor[] allDoctors = maintenance.getAllDoctors();
        createAppointmentFlow(currentLoginPatientId, "appointment", allDoctors);
    }

    public Doctor getCurrentWalkInDoctor() {
        Doctor[] doctors = maintenance.getAllDoctors();
        Doctor bestDoctor = null;
        LocalDateTime earliestSlot = null;
        String bestSlotStr = null;
        LocalDateTime now = LocalDateTime.now();

        for (Doctor doctor : doctors) {
            String[] slots = maintenance.getAvailableSlotsForDoctor(doctor.getDoctorID());
            for (String slotStr : slots) {
                // Parse slot string: "dd/MM/yyyy HH:mm-HH:mm"
                String[] parts = slotStr.split(" ");
                if (parts.length < 2) continue;
                String datePart = parts[0];
                String timeslot = parts[1];
                String[] timeParts = timeslot.split("[–-]");
                if (timeParts.length < 2) continue;
                String startTime = timeParts[0].trim();

                // If the date format is yyyy-MM-dd convert to dd/MM/yyyy
                String formattedDatePart = datePart;
                if (datePart.contains("-")) {
                    String[] dateElems = datePart.split("-");
                    formattedDatePart = dateElems[2] + "/" + dateElems[1] + "/" + dateElems[0];
                }

                String appointmentDateTimeStr = formattedDatePart + " " + startTime;
                LocalDateTime slotTime;
                try {
                    slotTime = DateTimeFormatterUtil.parseDisplayFormat(appointmentDateTimeStr);
                } catch (Exception ex) {
                    continue;
                }

                if (slotTime.isBefore(now)) continue; // Skip past slots

                if (earliestSlot == null || slotTime.isBefore(earliestSlot)) {
                    earliestSlot = slotTime;
                    bestDoctor = doctor;
                    bestSlotStr = slotStr;
                }
            }
        }
        nearestDoctorSlotStr = bestSlotStr;
        return bestDoctor;
    }

    public void adminMakeAppointment() {
        System.out.println("\n-- Add Appointment --");
        // printAllAppointments(maintenance.getAllAppointments());

//        String appointmentId = IDGenerator.generateAppointmentID();
//        System.out.println("Generated Appointment ID: " + appointmentId);

        // Select patient as usual
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
        String patientId = null;
        Patient selectedPatient = null;
        while (selectedPatient == null) {
            System.out.print("\nEnter Patient ID (Eg. P001, or 0 to return): ");
            patientId = scanner.nextLine().trim();
            if (patientId.equals("0")) return;
            if (patientId == null || patientId.isEmpty()) {
                System.out.println("Patient ID cannot be empty.");
                continue;
            }
            if (!InputHandler.isValidId(patientId, "patient")) {
                System.out.println("Invalid Patient ID. Must be in format P999. ");
                continue;
            }
            selectedPatient = maintenance.getPatient(patientId);
            if (selectedPatient == null) System.out.println("Patient not found. Please enter a valid Patient ID.");
        }

        Doctor[] allDoctors = maintenance.getAllDoctors();
        createAppointmentFlow(patientId, "appointment", allDoctors);
    }

    /*
    Admin Walk-In Appointment Wrapper
     */
    public void adminWalkInAppointment() {
        System.out.println("\n-- Add Walk-In Appointment --");
        // Select patient
        Patient[] patients = maintenance.getAllPatients();
        if (patients == null || patients.length == 0) {
            System.out.println("No patients available. Please create a patient first.");
            return;
        }
        System.out.println("\nAvailable Patients:");
        System.out.println("+------------+----------------------+");
        System.out.printf("| %-10s | %-20s |\n", "Patient ID", "Name");
        System.out.println("+------------+----------------------+");
        for (Patient p : patients) {
            System.out.printf("| %-10s | %-20s |\n", p.getPatientId(), p.getName());
        }
        System.out.println("+------------+----------------------+");

        String patientId = "";
        Patient selectedPatient = null;
        while (selectedPatient == null) {
            System.out.print("\nEnter Patient ID (Eg. P001, or 0 to return): ");
            patientId = scanner.nextLine().trim();
            if (patientId.equals("0")) return;
            if (patientId.isEmpty()) {
                System.out.println("Patient ID cannot be empty.");
                continue;
            }
            if (!InputHandler.isValidId(patientId, "patient")) {
                System.out.println("Invalid Patient ID. Must be in format P999.");
                continue;
            }
            selectedPatient = maintenance.getPatient(patientId);
            if (selectedPatient == null) System.out.println("Patient not found. Please enter a valid Patient ID.");
        }

        // Find best walk-in doctor and nearest slot
        Doctor walkInDoctor = getCurrentWalkInDoctor();
        if (walkInDoctor == null || nearestDoctorSlotStr == null) {
            System.out.println("No walk-in doctor or slot available.");
            return;
        }

        // Display only the nearest slot for confirmation
        System.out.println("\nNearest Available Walk-In Slot:");
        System.out.println("+------------+----------------------+----------------------+---------------------------+");
        System.out.printf("| %-10s | %-20s | %-20s | %-25s |\n", "Doctor ID", "Name", "Specialization", "Slot");
        System.out.println("+------------+----------------------+----------------------+---------------------------+");
        System.out.printf("| %-10s | %-20s | %-20s | %-25s |\n",
                walkInDoctor.getDoctorID(), walkInDoctor.getName(), walkInDoctor.getSpecialty(), nearestDoctorSlotStr);
        System.out.println("+------------+----------------------+----------------------+---------------------------+");

        while (true) {
            boolean confirm = InputHandler.getYesNo("\nBook this walk-in appointment?");
            if (!confirm) {
                System.out.println("Walk-in booking cancelled.");
                return;
            }
            break;
        }

        // Parse slot info for appointment creation
        String[] parts = nearestDoctorSlotStr.split(" ");
        String datePart = parts[0];
        String timeslot = parts[1];
        String[] timeParts = timeslot.split("[–-]");
        String startTime = timeParts[0].trim();
        String formattedDatePart = datePart;
        if (datePart.contains("-")) {
            String[] dateElems = datePart.split("-");
            formattedDatePart = dateElems[2] + "/" + dateElems[1] + "/" + dateElems[0];
        }
        String appointmentDateTimeStr = formattedDatePart + " " + startTime;
        LocalDateTime appointmentTime;
        try {
            appointmentTime = DateTimeFormatterUtil.parseDisplayFormat(appointmentDateTimeStr);
        } catch (Exception ex) {
            System.out.println("Failed to parse appointment time.");
            return;
        }

        String appointmentId = IDGenerator.generateAppointmentID();
        System.out.println("Generated Appointment ID: " + appointmentId);
        String status = "Scheduled";
        Appointment appointment = new Appointment(appointmentId, patientId, walkInDoctor.getDoctorID(), appointmentTime, status, "walk-in");
        appointment.setAppointmentType("walk-in");
        maintenance.addAppointment(appointment);

        // Enqueue walk-in patients
        String currentPatient = appointment.getPatientId();
        if ("walk-in".equalsIgnoreCase(appointment.getAppointmentType())) {
            if (patientMaintenance.isPatientInQueue(currentPatient)) {
                System.out.println("Already in queue.");
                return;
            }
            patientMaintenance.enqueuePatient(currentPatient);
            if (patientMaintenance.isPatientInQueue(currentPatient)) {
                System.out.println("Added to active queue.");
            }  else {
                System.out.println("Failed to enqueue.");
            }
        }

        System.out.println("\nWalk-in appointment booked successfully!");
        System.out.println("+-----------------+----------------------+----------------------+---------------------------+-----------------+");
        System.out.printf("| %-15s | %-20s | %-20s | %-25s | %-15s |\n", "Appointment ID", "Patient Name", "Doctor Name", "Date & Time", "Type");
        System.out.println("+-----------------+----------------------+----------------------+---------------------------+-----------------+");
        System.out.printf("| %-15s | %-20s | %-20s | %-25s | %-15s |\n",
                appointmentId, selectedPatient.getName(), walkInDoctor.getName(), DateTimeFormatterUtil.formatForDisplay(appointmentTime), "walk-in");
        System.out.println("+-----------------+----------------------+----------------------+---------------------------+-----------------+");
    }

    private void cancelAppointment() {
        System.out.println("\n-- Cancel Appointment --");
        printAllAppointments(maintenance.getAllAppointments());

        String appointmentId;
        Appointment appointment = null;
        while (true) {
            System.out.print("\nEnter Appointment ID to cancel (Eg. A001, or 0 to return): ");
            appointmentId = scanner.nextLine().trim();

            if (appointmentId.equals("0")) return;
            if (appointmentId.isEmpty()) {
                System.out.println("Appointment ID cannot be empty. ");
                continue;
            }
            if (!InputHandler.isValidId(appointmentId, "appointment")) {
                System.out.println("Invalid Appointment ID. Must be in format A999");
                continue;
            }
            appointment = maintenance.getAppointment(appointmentId);
            if (appointment == null) {
                System.out.println("Appointment not found. Please enter a valid Appointment ID. ");
            }
            if ("Cancelled".equalsIgnoreCase(appointment.getStatus())) {
                System.out.println("Appointment is already cancelled. ");
                continue;
            }
            break;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appTime = appointment.getAppointmentTime();
        boolean success = maintenance.cancelAppointment(appointmentId);
        if (success) {
            if (appTime.isAfter(now)) {
                System.out.println("Appointment cancelled and scheduled slot is freed. ");
            } else {
                System.out.println("Appointment cancelled successfully. ");
            }
        } else {
            System.out.println("Failed to cancel appointment.");
        }
    }

    private void removeAppointment() {
        System.out.println("\n-- Remove Appointment --");
        printAllAppointments(maintenance.getAllAppointments());

        String appointmentId;
        while (true) {
            System.out.print("\nEnter Appointment ID to remove (Eg. A001, or 0 to return): ");
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
            String status = maintenance.getAppointment(appointmentId).getStatus();
            if (!"Completed".equalsIgnoreCase(status) && !"Cancelled".equalsIgnoreCase(status)) {
                System.out.println("Only appointments with status \"Completed\" and \"Cancelled\" can be removed.");
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

    private void searchAppointmentsByPatientName() {
        while (true) {
            System.out.print("\nEnter patient name (Eg. Alice, or 0 to return): ");
            String pName = scanner.nextLine().trim();
            if (pName.equals("0")) return;
            CustomADT<String, Appointment> found = maintenance.searchAppointmentsByPatientName(pName);

            if (found.isEmpty()) {
                System.out.println("No appointments found for patient name containing: " + pName);
                continue;
            }

            // Sort by patient name
            found.sort((a1, a2) -> {
                Patient p1 = maintenance.getPatient(a1.getPatientId());
                Patient p2 = maintenance.getPatient(a2.getPatientId());
                String n1 = (p1 != null) ? p1.getName() : "";
                String n2 = (p2 != null) ? p2.getName() : "";
                return n1.compareToIgnoreCase(n2);
            });

            printAllAppointments(found.toArray(new Appointment[0]));
        }
    }

    private void searchAppointmentsByDoctorName() {
        while (true) {
            System.out.print("\nEnter doctor name (Eg. Dr. Alice Tan, or 0 to return): ");
            String dName = scanner.nextLine().trim();
            if (dName.equals("0")) return;
            CustomADT<String, Appointment> found = maintenance.searchAppointmentsByDoctorName(dName);

            if (found.isEmpty()) {
                System.out.println("No appointments found for doctor name containing: " + dName);
                continue;
            }

            // Sort by doctor name
            found.sort((a1, a2) -> {
                Doctor d1 = maintenance.getDoctor(a1.getDoctorId());
                Doctor d2 = maintenance.getDoctor(a2.getDoctorId());
                String n1 = (d1 != null) ? d1.getName() : "";
                String n2 = (d2 != null) ? d2.getName() : "";
                return n1.compareToIgnoreCase(n2);
            });

            printAllAppointments(found.toArray(new Appointment[0]));
        }
    }

    private void printAllAppointments(Appointment[] appointments) {
        if (appointments == null || appointments.length == 0) {
            System.out.println("No appointments found. ");
            return;
        }

        String format = "| %-15s | %-12s | %-20s | %-12s | %-20s | %-20s | %-12s | %-15s |\n";
        System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+--------------+-----------------+");
        System.out.printf(format, "Appointment ID", "Patient ID", "Patient Name", "Doctor ID", "Doctor Name", "Date & Time", "Status", "Type");
        System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+--------------+-----------------+");

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
                    appt.getStatus(),
                    appt.getAppointmentType()
            );
        }
        System.out.println("+-----------------+--------------+----------------------+--------------+----------------------+----------------------+--------------+-----------------+");
    }

    public static void main(String[] args) {
        PatientMaintenance pm = new PatientMaintenance();
        new ConsultationMaintenanceUI(pm).run();
    }
}