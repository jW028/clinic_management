package control;

import control.ScheduleMaintenance;
import adt.CustomADT;
import dao.AppointmentDAO;
import dao.ConsultationDAO;
import dao.ConsultationServiceDAO;
import dao.DiagnosisDAO;
import dao.DoctorDAO;
import dao.PaymentDAO;
import dao.PatientDAO;
import dao.ScheduleDAO;
import entity.Appointment;
import entity.Consultation;
import entity.ConsultationService;
import entity.Diagnosis;
import entity.Payment;
import entity.Patient;
import entity.Doctor;
import entity.Schedule;
import utility.DateTimeFormatterUtil;
import utility.IDGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class ConsultationMaintenance {
    private final ScheduleMaintenance scheduleMaintenance;
    private final CustomADT<String, Consultation> consultationMap;
    private final CustomADT<String, Patient> patientMap;
    private final CustomADT<String, Appointment> appointmentMap;
    private final CustomADT<String, ConsultationService> serviceMap;
    private final CustomADT<String, Diagnosis> diagnosisMap;
    private final CustomADT<String, Payment> paymentMap;
    private final CustomADT<String, Doctor> doctorMap;
    private final CustomADT<String, Schedule> scheduleMap;

    private final ConsultationDAO consultationDAO = new ConsultationDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DiagnosisDAO diagnosisDAO = new DiagnosisDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ConsultationServiceDAO consultationServiceDAO = new ConsultationServiceDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();

    public static final String[] VALID_APPOINTMENT_STATUSES = {
            "Scheduled", "In Progress", "Completed", "Cancelled"
    };

    public ConsultationMaintenance() {
        this.scheduleMaintenance = new ScheduleMaintenance();
        this.consultationMap = consultationDAO.retrieveFromFile();
        this.patientMap = patientDAO.retrieveFromFile();
        this.paymentMap = paymentDAO.retrieveFromFile();
        this.appointmentMap = appointmentDAO.retrieveFromFile();
        this.serviceMap = consultationServiceDAO.retrieveFromFile();
        this.diagnosisMap = diagnosisDAO.retrieveFromFile();
        this.doctorMap = DoctorDAO.loadDoctors();
        this.scheduleMap = scheduleDAO.retrieveFromFile();

        IDGenerator.loadCounter("counter.dat");

        String highestAppointmentID = "A000";
        for (Appointment appt : appointmentMap) {
            String id = appt.getAppointmentId();
            if (id.compareTo(highestAppointmentID) > 0) {
                highestAppointmentID = id;
            }
        }
        IDGenerator.updateAppointmentCounterFromHighestID(highestAppointmentID);
        IDGenerator.saveCounters("counter.dat");

        String highestConsultationId = "C000";
        for (Consultation c : consultationMap) {
            String id = c.getConsultationId();
            if (id.compareTo(highestConsultationId) > 0) {
                highestConsultationId = id;
            }
        }
        IDGenerator.updateConsultationCounterFromHighestID(highestConsultationId);
        IDGenerator.saveCounters("counter.dat");
    }

    // Get patient info
    public Patient getPatient(String patientId) { return patientMap.get(patientId); }
    public Patient[] getAllPatients() { return patientMap.toArray(new Patient[0]); }

    // Get doctor info
    public Doctor getDoctor(String doctorId) { return doctorMap.get(doctorId); }
    public Doctor[] getAllDoctors() { return doctorMap.toArray(new Doctor[0]); }

    // CRUD for Consultation
    public void addConsultation(Consultation consultation) {
        Appointment appointment = consultation.getAppointment();
        String patientId = consultation.getPatient().getPatientId();
        String doctorId = consultation.getDoctor().getDoctorID();

        if (appointment == null || !appointmentMap.containsKey(appointment.getAppointmentId())) {
            System.out.println("Invalid or missing appointment. ");
            return;
        }

        if (!patientMap.containsKey(patientId)) {
            System.out.println("Invalid patient. ");
            return;
        }

        if (!doctorMap.containsKey(doctorId)) {
            System.out.println("Invalid doctor. ");
            return;
        }

        consultationMap.put(consultation.getConsultationId(), consultation);
        consultationDAO.saveToFile(consultationMap);
        IDGenerator.saveCounters("counter.dat");
    }

    public Consultation getConsultation(String id) {
        return consultationMap.get(id);
    }

    public boolean removeConsultation(String id) {
        Consultation removed = consultationMap.remove(id);
        consultationDAO.saveToFile(consultationMap);
        return removed != null;
    }

    public int countConsultations() {
        return consultationMap.size();
    }

    public Consultation[] getAllConsultations() {
        // return consultationMap.toArray(new Consultation[0]);
        // return consultationDAO.retrieveFromFile().toArray(new Consultation[0]);
        Consultation[] arr = consultationMap.toArray(new Consultation[0]);
        return arr;
    }

    // Appointment CRUD
    public void addAppointment(Appointment appointment) {
        appointmentMap.put(appointment.getAppointmentId(), appointment);

        // Mark assigned schedule as booked
        String doctorId = appointment.getDoctorId();
        String date = appointment.getAppointmentTime().toLocalDate().toString();
        String startTimeStr = appointment.getAppointmentTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String endTimeStr = appointment.getAppointmentTime().plusHours(1).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String timeSlot = startTimeStr + "–" + endTimeStr;
        String key = doctorId + "_" + date + "_" + timeSlot;

        Schedule sched = scheduleMap.get(key);
        if (sched != null && sched.getStatus()) {
            sched.setStatus(false); // Mark as booked
            scheduleMap.put(key, sched); // Update map
            scheduleDAO.saveToFile(scheduleMap); // Persist changes
        }

        appointmentDAO.saveToFile(appointmentMap);
        IDGenerator.saveCounters("counter.dat");
    }

    public boolean removeAppointment(String appointmentId) {
        Appointment appt = appointmentMap.get(appointmentId);

        if (appt == null) {
            System.out.println("Appointment not found. ");
            return false;
        }

        if (!"Cancelled".equalsIgnoreCase(appt.getStatus())) {
            System.out.println("Only appointments with status \"Cancelled\" can be removed. ");
            return false;
        }

        Appointment removed = appointmentMap.remove(appointmentId);
        appointmentDAO.saveToFile(appointmentMap);

        return removed != null;
    }

    public boolean updateAppointmentStatus(String appointmentId, String newStatus) {
        Appointment appointment = appointmentMap.get(appointmentId);
        if (appointment != null && isValidStatus(newStatus)) {
            appointment.setStatus(newStatus);
            appointmentDAO.saveToFile(appointmentMap);
            return true;
        }
        return false;
    }

    public boolean cancelAppointment(String appointmentId) {
        Appointment appointment = appointmentMap.get(appointmentId);
        if (appointment == null) return false;

        if ("Cancelled".equalsIgnoreCase(appointment.getStatus())) return false;

        appointment.setStatus("Cancelled");
        appointmentDAO.saveToFile(appointmentMap);

        // Free up schedule slot if the cancelled slot is in the future
        LocalDateTime now = LocalDateTime.now();
        if (appointment.getAppointmentTime().isAfter(now)) {
            String doctorId = appointment.getDoctorId();
            String date = appointment.getAppointmentTime().toLocalDate().toString();
            String startTimeStr = appointment.getAppointmentTime().toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            String endTimeStr = appointment.getAppointmentTime().plusHours(1).toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            String timeSlot = startTimeStr + "–" + endTimeStr;
            String key = doctorId + "_" + date + "_" + timeSlot;
            Schedule sched = scheduleMap.get(key);
            if (sched != null && !sched.getStatus()) {
                sched.setStatus(true); // Mark as available
                scheduleMap.put(key, sched);
                scheduleDAO.saveToFile(scheduleMap);
            }
        }
        return true;
    }

    // Appointment access
    public Appointment getAppointment(String appointmentId) {
        return appointmentMap.get(appointmentId);
    }

    public Appointment[] getAllAppointments() {
        return appointmentMap.toArray(new Appointment[0]);
        // return appointmentDAO.retrieveFromFile().toArray(new Appointment[0]);
    }

    public boolean isValidStatus(String status) {
        for (String valid : VALID_APPOINTMENT_STATUSES) {
            if (valid.equalsIgnoreCase(status)) return true;
        }
        return false;
    }

    // Service access
    public ConsultationService getService(String serviceId) {
        return serviceMap.get(serviceId);
    }

    public ConsultationService[] getAllServices() {
        // return serviceMap.toArray(new ConsultationService[0]);
        return consultationServiceDAO.retrieveFromFile().toArray(new ConsultationService[0]);
    }

    // Diagnosis access
    public Diagnosis getDiagnosis(String diagnosisId) {
        return diagnosisMap.get(diagnosisId);
    }

    public Diagnosis[] getAllDiagnoses() {
        // return diagnosisMap.toArray(new Diagnosis[0]);
        return diagnosisDAO.retrieveFromFile().toArray(new Diagnosis[0]);
    }

    // Payment access
    public Payment getPayment(String paymentId) {
        return paymentMap.get(paymentId);
    }

    public void addPayment(Payment payment) {
        paymentMap.put(payment.getPaymentId(), payment);
        paymentDAO.saveToFile(paymentMap);
        IDGenerator.saveCounters("counter.dat");
    }

    public Payment[] getAllPayments() {
        // return paymentMap.toArray(new Payment[0]);
        return paymentDAO.retrieveFromFile().toArray(new Payment[0]); // TODO
    }

    // Schedule
    public String[] getAvailableSlotsForDoctor(String doctorId) {
        // if (doctorId == null) return new LocalDateTime[0];
        if (doctorId == null) return new String[0];

        // Collect available slots for doctor
        CustomADT<String, String> slotMap = new CustomADT<>();
        int slotCounter = 1;
        for (int i = 0; i < scheduleMap.size(); i++) {
            Schedule sched = scheduleMap.get(i);
            if (sched.getDoctorID().equals(doctorId) && sched.getStatus()) {
                String displaySlot = sched.getDate() + " " + sched.getTimeslot();
                slotMap.put("slot" + slotCounter++, displaySlot);
            }
        }
        return slotMap.toArray(new String[0]);

//        CustomADT<String, LocalDateTime> slotMap = new CustomADT<>();
//        for (int i = 0; i < scheduleMap.size(); i++) {
//            Schedule sched = scheduleMap.get(i);
//            if (sched.getDoctorID().equals(doctorId) && sched.getStatus()) {
//                try {
////                    String[] times = sched.getTimeslot().split("-");
////                    String dateTimeStr = sched.getDate() + "T" + times[0];
////                    LocalDateTime slot = LocalDateTime.parse(dateTimeStr); // May need DateTimeFormatter
////                    slotMap.put(sched.getScheduleID(), slot);
//                    String[] times = sched.getTimeslot().split("[–-]");
//                    String scheduleDateTimeStr = sched.getDate() + " " + times[0].trim(); // Eg. "10/08/2025 15:00"
//                    System.out.println("Trying to parse: [" + scheduleDateTimeStr + "]");
//                    LocalDateTime slot = DateTimeFormatterUtil.parseDisplayFormat(scheduleDateTimeStr);
//                    slotMap.put(sched.getScheduleID(), slot);
//                } catch (Exception e) {
//                    System.out.println("Error parsing schedule: " + sched);
//                }
//            }
//        }
//        return slotMap.toArray(new LocalDateTime[0]);
    }

    // --- Searching and Sorting for Consultations ---
    public CustomADT<String, Consultation> searchConsultationsByPatientName(String name) {
        CustomADT<String, Consultation> result = new CustomADT<>();
        String search = name.toLowerCase();
        for (int i = 0; i < consultationMap.size(); i++) {
            Consultation c = consultationMap.get(i);
            Patient patient = c.getPatient();
            if (patient != null && patient.getName().toLowerCase().contains(search)) {
                result.put(c.getConsultationId(), c);
            }
        }
        return result;
//        Comparator<Consultation> comparator = (c1, c2) -> c1.getPatient().getName().equalsIgnoreCase(name) ? 0 : -1;
//        Patient searchPatient = new Patient(null, null, 0, null, null, null, false);
//        return consultationMap.filter(new Consultation(null, null, searchPatient, null, null, null, null, null, null, false, null), comparator);
    }

    public CustomADT<String, Consultation> searchConsultationsByDoctorName(String name) {
        CustomADT<String, Consultation> result = new CustomADT<>();
        String search = name.toLowerCase();
        for (int i = 0; i < consultationMap.size(); i++) {
            Consultation c = consultationMap.get(i);
            Doctor doctor = c.getDoctor();
            if (doctor != null && doctor.getName().toLowerCase().contains(search)) {
                result.put(c.getConsultationId(), c);
            }
        }
        return result;
//        Comparator<Consultation> comparator = (c1, c2) -> c1.getDoctor().getName().equalsIgnoreCase(name) ? 0 : -1;
//        Doctor searchDoctor = new Doctor(null, name, null, null, null, null, null, null);
//        return consultationMap.filter(new Consultation(null, null, null, searchDoctor, null, null, null, null, null, false, null), comparator);
    }

    public void sortConsultationsByID() {
        consultationMap.sort(Comparator.comparing(Consultation::getConsultationId));
    }

    public void sortConsultationsByPatientName() {
        consultationMap.sort(Comparator.comparing(c -> c.getPatient().getName(), String.CASE_INSENSITIVE_ORDER));
    }

    public void sortConsultationsByDoctorName() {
        consultationMap.sort(Comparator.comparing(c -> c.getDoctor().getName(), String.CASE_INSENSITIVE_ORDER));
    }

    public void sortConsultationsByDate() {
        consultationMap.sort(Comparator.comparing(Consultation::getConsultationTime));
    }

    // --- Searching and Sorting for Appointments ---
    public CustomADT<String, Appointment> searchAppointmentsByPatientName(String name) {
        CustomADT<String, Appointment> result = new CustomADT<>();
        String search = name.toLowerCase();
        for (int i = 0; i < appointmentMap.size(); i++) {
            Appointment appt = appointmentMap.get(i);
            Patient patient = getPatient(appt.getPatientId());
            if (patient != null && patient.getName().toLowerCase().contains(search)) {
                result.put(appt.getAppointmentId(), appt);
            }
        }
        return result;
//        Comparator<Appointment> comparator = (a1, a2) -> {
//            Patient p = getPatient(a1.getPatientId());
//            return (p != null && p.getName().equalsIgnoreCase(name)) ? 0 : -1;
//        };
//        return appointmentMap.filter(new Appointment(null, null, null, null, null), comparator);
    }

    public CustomADT<String, Appointment> searchAppointmentsByDoctorName(String name) {
        CustomADT<String, Appointment> result = new CustomADT<>();
        String search = name.toLowerCase();
        for (int i = 0; i < appointmentMap.size(); i++) {
            Appointment appt = appointmentMap.get(i);
            Doctor doctor = getDoctor(appt.getDoctorId());
            if (doctor != null && doctor.getName().toLowerCase().contains(search)) {
                result.put(appt.getAppointmentId(), appt);
            }
        }
        return result;
//        Comparator<Appointment> comparator = (a1, a2) -> {
//            Doctor d = getDoctor(a1.getDoctorId());
//            return (d != null && d.getName().equalsIgnoreCase(name)) ? 0 : -1;
//        };
//        return appointmentMap.filter(new Appointment(null, null, null, null, null), comparator);
    }

    public void sortAppointmentsByID() {
        appointmentMap.sort(Comparator.comparing(Appointment::getAppointmentId));
    }

    public void sortAppointmentsByPatientName() {
        appointmentMap.sort((a1, a2) -> {
            Patient p1 = getPatient(a1.getPatientId());
            Patient p2 = getPatient(a2.getPatientId());
            String n1 = (p1 != null) ? p1.getName() : "";
            String n2 = (p2 != null) ? p2.getName() : "";
            return n1.compareToIgnoreCase(n2);
        });
    }

    public void sortAppointmentsByDoctorName() {
        appointmentMap.sort((a1, a2) -> {
            Doctor d1 = getDoctor(a1.getDoctorId());
            Doctor d2 = getDoctor(a2.getDoctorId());
            String n1 = (d1 != null) ? d1.getName() : "";
            String n2 = (d2 != null) ? d2.getName() : "";
            return n1.compareToIgnoreCase(n2);
        });
    }

    public void sortAppointmentsByDate() {
        appointmentMap.sort(Comparator.comparing(Appointment::getAppointmentTime));
    }

    public void printConsultationSummaryReport() {
        Consultation[] consultations = consultationMap.toArray(new Consultation[0]);
        System.out.println("\n=== Consultation Summary Report ===");
        System.out.println("Total consultations: " + consultations.length);

        if (consultations.length == 0) {
            System.out.println("No consultation records to show. Please add consultation data first.");
            return;
        }

        // By patient
        CustomADT<String, Integer> byPatient = new CustomADT<>();
        CustomADT<String, String> patientIds = new CustomADT<>();

        for (Consultation c : consultations) {
            String pid = c.getPatient().getPatientId();
            Integer count = byPatient.get(pid);
            byPatient.put(pid, count == null ? 1 : count + 1);
            if (!patientIds.containsKey(pid)) {
                patientIds.put(pid, pid);
            }
        }

        // Sortable patient rows
        class PatientRow {
            String id, name;
            int count;
            PatientRow(String id, String name, int count) {
                this.id = id; this.name = name; this.count = count;
            }
        }
        CustomADT<Integer, PatientRow> rows = new CustomADT<>();
        String[] pidArray = patientIds.toArray(new String[0]);
        for (String pid : pidArray) {
            Patient patient = patientMap.get(pid);
            String pname = patient != null ? patient.getName() : "Unknown";
            int count = byPatient.get(pid) != null ? byPatient.get(pid) : 0;
            rows.put(rows.size(), new PatientRow(pid, pname, count));
        }
        rows.sort((a, b) -> b.count - a.count);

        if (rows.size() == 0) {
            System.out.println("No patient records found in consultations.");
        } else {
            System.out.println("\nConsultations by Patient:");
            System.out.println("+------------+----------------------+---------------------------+");
            System.out.printf("| %-10s | %-20s | %-25s |\n", "Patient ID", "Patient Name", "Consultation Count");
            System.out.println("+------------+----------------------+---------------------------+");
            for (int i = 0; i < rows.size(); i++) {
                PatientRow r = rows.get(i);
                System.out.printf("| %-10s | %-20s | %-25d |\n", r.id, r.name, r.count);
            }
            System.out.println("+------------+----------------------+---------------------------+");
        }

        // By doctor
        CustomADT<String, Integer> byDoctor = new CustomADT<>();
        CustomADT<String, String> doctorIds = new CustomADT<>();

        for (Consultation c : consultations) {
            String did = c.getDoctor().getDoctorID();
            Integer count = byDoctor.get(did);
            byDoctor.put(did, count == null ? 1 : count + 1);
            if (!doctorIds.containsKey(did)) {
                doctorIds.put(did, did);
            }
        }

        // Sortable doctor rows
        class DoctorRow {
            String id, name;
            int count;
            DoctorRow(String id, String name, int count) {
                this.id = id; this.name = name; this.count = count;
            }
        }
        CustomADT<Integer, DoctorRow> doctorRows = new CustomADT<>();
        String[] didArray = doctorIds.toArray(new String[0]);
        for (String did : didArray) {
            Doctor doctor = doctorMap.get(did);
            String dname = doctor != null ? doctor.getName() : "Unknown";
            int count = byDoctor.get(did) != null ? byDoctor.get(did) : 0;
            doctorRows.put(doctorRows.size(), new DoctorRow(did, dname, count));
        }
        doctorRows.sort((a, b) -> b.count - a.count);

        if (doctorRows.size() == 0) {
            System.out.println("No doctor records found in consultations.");
        } else {
            System.out.println("\nConsultations by Doctor:");
            System.out.println("+------------+----------------------+---------------------------+");
            System.out.printf("| %-10s | %-20s | %-25s |\n", "Doctor ID", "Doctor Name", "Consultation Count");
            System.out.println("+------------+----------------------+---------------------------+");
            for (int i = 0; i < doctorRows.size(); i++) {
                DoctorRow r = doctorRows.get(i);
                System.out.printf("| %-10s | %-20s | %-25d |\n", r.id, r.name, r.count);
            }
            System.out.println("+------------+----------------------+---------------------------+");
        }
    }

    public void printServiceUsageReport() {
        Consultation[] consultations = consultationMap.toArray(new Consultation[0]);
        if (consultations.length == 0) {
            System.out.println("\n=== Service Usage Report ===");
            System.out.println("No consultation records to show. Please add consultation data first.");
            return;
        }

        CustomADT<String, Integer> serviceUsage = new CustomADT<>();
        CustomADT<String, String> serviceIdsADT = new CustomADT<>();
        for (Consultation c : consultations) {
            for (ConsultationService s : c.getServicesUsed().toArray(new ConsultationService[0])) {
                String sid = s.getServiceId();
                Integer count = serviceUsage.get(sid);
                serviceUsage.put(sid, count == null ? 1 : count + 1);
                if (!serviceIdsADT.containsKey(sid)) {
                    serviceIdsADT.put(sid, sid);
                }
            }
        }

        // Sortable service rows
        class ServiceRow {
            String id, name;
            int count;
            ServiceRow(String id, String name, int count) { this.id = id; this.name = name; this.count = count; }
        }
        CustomADT<Integer, ServiceRow> rows = new CustomADT<>();
        String[] sidArray = serviceIdsADT.toArray(new String[0]);
        for (String sid : sidArray) {
            ConsultationService service = serviceMap.get(sid);
            String serviceName = service != null ? service.getServiceName() : "Unknown";
            int count = serviceUsage.get(sid) != null ? serviceUsage.get(sid) : 0;
            rows.put(rows.size(), new ServiceRow(sid, serviceName, count));
        }
        rows.sort((a, b) -> b.count - a.count);

        System.out.println("\n=== Service Usage Report ===");
        if (rows.size() == 0) {
            System.out.println("No service records found in consultations.");
            return;
        }
        System.out.println("+------------+---------------------------+------------+");
        System.out.printf("| %-10s | %-25s | %-10s |\n", "Service ID", "Service Name", "Used Times");
        System.out.println("+------------+---------------------------+------------+");
        for (int i = 0; i < rows.size(); i++) {
            ServiceRow r = rows.get(i);
            System.out.printf("| %-10s | %-25s | %-10d |\n", r.id, r.name, r.count);
        }
        System.out.println("+------------+---------------------------+------------+");
    }

    public void printAppointmentsPerDoctorReport() {
        Appointment[] appointments = appointmentMap.toArray(new Appointment[0]);
        System.out.println("\n=== Appointments Per Doctor Report ===");
        System.out.println("Total appointments: " + appointments.length);

        if (appointments.length == 0) {
            System.out.println("No appointment records to show. Please add appointment data first.");
            return;
        }

        // Count appointments per doctor
        CustomADT<String, Integer> byDoctor = new CustomADT<>();
        CustomADT<String, String> doctorIds = new CustomADT<>();
        for (Appointment a : appointments) {
            String did = a.getDoctorId();
            Integer count = byDoctor.get(did);
            byDoctor.put(did, count == null ? 1 : count + 1);
            if (!doctorIds.containsKey(did)) {
                doctorIds.put(did, did);
            }
        }

        // Sortable doctor rows
        class DoctorRow {
            String id, name;
            int count;
            DoctorRow(String id, String name, int count) { this.id = id; this.name = name; this.count = count; }
        }
        CustomADT<Integer, DoctorRow> doctorRows = new CustomADT<>();
        String[] didArray = doctorIds.toArray(new String[0]);
        for (String did : didArray) {
            Doctor doctor = doctorMap.get(did);
            String dname = doctor != null ? doctor.getName() : "Unknown";
            int count = byDoctor.get(did) != null ? byDoctor.get(did) : 0;
            doctorRows.put(doctorRows.size(), new DoctorRow(did, dname, count));
        }
        doctorRows.sort((a, b) -> b.count - a.count);

        if (doctorRows.size() == 0) {
            System.out.println("No doctor records found in appointments.");
            return;
        }

        System.out.println("\nAppointments by Doctor:");
        System.out.println("+------------+----------------------+----------------------+");
        System.out.printf("| %-10s | %-20s | %-20s |\n", "Doctor ID", "Doctor Name", "Appointment Count");
        System.out.println("+------------+----------------------+----------------------+");
        for (int i = 0; i < doctorRows.size(); i++) {
            DoctorRow r = doctorRows.get(i);
            System.out.printf("| %-10s | %-20s | %-20d |\n", r.id, r.name, r.count);
        }
        System.out.println("+------------+----------------------+----------------------+");
    }
}