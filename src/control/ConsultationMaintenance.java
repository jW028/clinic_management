package control;

import adt.CustomADT;
import dao.AppointmentDAO;
import dao.ConsultationDAO;
import dao.ConsultationServiceDAO;
import dao.DoctorDAO;
import dao.PaymentDAO;
import dao.ScheduleDAO;
import entity.Appointment;
import entity.Consultation;
import entity.ConsultationService;
import entity.Diagnosis;
import entity.Doctor;
import entity.Patient;
import entity.Payment;
import entity.Schedule;
import java.time.LocalDateTime;
import java.util.Comparator;
import utility.DateTimeFormatterUtil;

public class ConsultationMaintenance {
    private final CustomADT<String, Consultation> consultationMap;
    private final CustomADT<String, Patient> patientMap;
    private final CustomADT<String, Appointment> appointmentMap;
    private final CustomADT<String, ConsultationService> serviceMap;
    private final CustomADT<String, Diagnosis> diagnosisMap;
    private final CustomADT<String, Payment> paymentMap;
    private final CustomADT<String, Doctor> doctorMap;
    private final CustomADT<String, Schedule> scheduleMap;

    private final ConsultationDAO consultationDAO = new ConsultationDAO();
    // private final PatientDAO patientDAO = new PatientDAO();
    // private final DiagnosisDAO diagnosisDAO = new Diagnosis();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ConsultationServiceDAO consultationServiceDAO = new ConsultationServiceDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();

    public static final String[] VALID_APPOINTMENT_STATUSES = {
            "Scheduled", "In Progress", "Completed", "Cancelled"
    };

    public ConsultationMaintenance() {
        // Load from file or initialize empty
        this.consultationMap = consultationDAO.retrieveFromFile();
        // this.patientMap = patientDAO.retrieveFromFile();
        this.paymentMap = paymentDAO.retrieveFromFile();
        this.appointmentMap = appointmentDAO.retrieveFromFile();
        this.serviceMap = consultationServiceDAO.retrieveFromFile();
        // this.diagnosisMap = diagnosisDAO.retrieveFromFile();
        this.doctorMap = DoctorDAO.loadDoctors();
        this.scheduleMap = scheduleDAO.retrieveFromFile();

        this.patientMap = new CustomADT<>();
        this.diagnosisMap = new CustomADT<>();

        // Example initial data
        diagnosisMap.put("D001", new Diagnosis("D001", "Flu", "Mild"));

        patientMap.put("P001", new Patient("P001", "Alice", 19, "Female", "1234567890", "123 Main St", false));
        patientMap.put("P002", new Patient("P002", "Bob", 40, "Male", "0987654321", "456 Elm St", false));
        patientMap.put("P003", new Patient("P003", "Zoer", 19, "Female", "1234567890", "123 Main St", false));
        patientMap.put("P004", new Patient("P004", "Star", 40, "Male", "0987654321", "456 Elm St", false));
        patientMap.put("P005", new Patient("P005", "Lace", 19, "Female", "1234567890", "123 Main St", false));
        patientMap.put("P006", new Patient("P006", "Wayne", 40, "Male", "0987654321", "456 Elm St", false));
        patientMap.put("P007", new Patient("P007", "Din", 19, "Female", "1234567890", "123 Main St", false));
        patientMap.put("P008", new Patient("P008", "Yej", 40, "Male", "0987654321", "456 Elm St", false));
        patientMap.put("P009", new Patient("P009", "Chaer", 19, "Female", "1234567890", "123 Main St", false));
        patientMap.put("P010", new Patient("P010", "Ryu", 40, "Male", "0987654321", "456 Elm St", false));

//        doctorMap.put("DC001", new Doctor("DC001", "Lee", "Heart", "01112345678", "lee@gmail.com", "1, Street 1, Sunway", "M", "25/7/1980"));
//        doctorMap.put("DC002", new Doctor("DC002", "Yap", "Heart", "01112345678", "lee@gmail.com", "1, Street 1, Sunway", "M", "25/7/1980"));
//        doctorMap.put("DC003", new Doctor("DC003", "Hor", "Heart", "01112345678", "lee@gmail.com", "1, Street 1, Sunway", "M", "25/7/1980"));
//        doctorMap.put("DC004", new Doctor("DC004", "Ang", "Heart", "01112345678", "lee@gmail.com", "1, Street 1, Sunway", "M", "25/7/1980"));
//        doctorMap.put("DC005", new Doctor("DC005", "Sau", "Heart", "01112345678", "lee@gmail.com", "1, Street 1, Sunway", "M", "25/7/1980"));
//        doctorMap.put("DC006", new Doctor("DC006", "Lim", "Heart", "01112345678", "lee@gmail.com", "1, Street 1, Sunway", "M", "25/7/1980"));
//        doctorMap.put("DC007", new Doctor("DC007", "Chew", "Heart", "01112345678", "lee@gmail.com", "1, Street 1, Sunway", "M", "25/7/1980"));
//        doctorMap.put("DC008", new Doctor("DC008", "Chin", "Heart", "01112345678", "lee@gmail.com", "1, Street 1, Sunway", "M", "25/7/1980"));
//        doctorMap.put("DC009", new Doctor("DC009", "Ting", "Heart", "01112345678", "lee@gmail.com", "1, Street 1, Sunway", "M", "25/7/1980"));
//        doctorMap.put("DC010", new Doctor("DC010", "Lai", "Heart", "01112345678", "lee@gmail.com", "1, Street 1, Sunway", "M", "25/7/1980"));
    }

    public Consultation[] getConsultationsByPatientId(String patientId) {
        if (patientId == null) return new Consultation[0];
        java.util.List<Consultation> list = new java.util.ArrayList<>();
        for (Consultation c : consultationMap) {
            if (c != null &&
                    c.getPatient() != null &&
                    patientId.equalsIgnoreCase(c.getPatient().getPatientId())) {
                list.add(c);
            }
        }
        return list.toArray(new Consultation[0]);
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
        Consultation[] arr = consultationMap.toArray(new Consultation[0]);
        return arr;
    }

    // Appointment CRUD
    public void addAppointment(Appointment appointment) {
        appointmentMap.put(appointment.getAppointmentId(), appointment);

        // TODO Mark schedule as booked
        for (int i = 0; i < scheduleMap.size(); i++) {
            Schedule sched = scheduleMap.get(i);

            // Combine date and from-time, and parse using your utility
            String[] times = sched.getTimeslot().split("-");
            String scheduleDateTimeStr = sched.getDate() + " " + times[0]; // e.g. "10/08/2025 15:00"
            LocalDateTime scheduleDateTime;
            try {
                scheduleDateTime = DateTimeFormatterUtil.parseDisplayFormat(scheduleDateTimeStr);
            } catch (Exception e) {
                continue;
            }

            if (sched.getDoctorID().equals(appointment.getDoctorId())
                    && scheduleDateTime.equals(appointment.getAppointmentTime())
                    && sched.getStatus()) {
                sched.setStatus(false);
                scheduleMap.set(i, sched);
                break;
            }
        }

        appointmentDAO.saveToFile(appointmentMap);
    }

    public boolean removeAppointment(String appointmentId) {
        Appointment appt = appointmentMap.get(appointmentId);

        if (appt == null) {
            System.out.println("Appointment not found. ");
            return false;
        }

        if (!"Canceled".equalsIgnoreCase(appt.getStatus())) {
            System.out.println("Only appointments with status \"Canceled\" can be removed. ");
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
        return diagnosisMap.toArray(new Diagnosis[0]);
        // TODO return diagnosisDAO.retrieveFromFile().toArray(new Diagnosis[0]);
    }

    // Payment access
    public Payment getPayment(String paymentId) {
        return paymentMap.get(paymentId);
    }

    public void addPayment(Payment payment) {
        paymentMap.put(payment.getPaymentId(), payment);
        paymentDAO.saveToFile(paymentMap);
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
        Comparator<Consultation> comparator = (c1, c2) -> c1.getPatient().getName().equalsIgnoreCase(name) ? 0 : -1;
        Patient searchPatient = new Patient(null, null, 0, null, null, null, false);
        return consultationMap.filter(new Consultation(null, null, searchPatient, null, null, null, null, null, null, false, null), comparator);
    }

    public CustomADT<String, Consultation> searchConsultationsByDoctorName(String name) {
        Comparator<Consultation> comparator = (c1, c2) -> c1.getDoctor().getName().equalsIgnoreCase(name) ? 0 : -1;
        Doctor searchDoctor = new Doctor(null, name, null, null, null, null, null, null);
        return consultationMap.filter(new Consultation(null, null, null, searchDoctor, null, null, null, null, null, false, null), comparator);
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
        Comparator<Appointment> comparator = (a1, a2) -> {
            Patient p = getPatient(a1.getPatientId());
            return (p != null && p.getName().equalsIgnoreCase(name)) ? 0 : -1;
        };
        return appointmentMap.filter(new Appointment(null, null, null, null, null), comparator);
    }

    public CustomADT<String, Appointment> searchAppointmentsByDoctorName(String name) {
        Comparator<Appointment> comparator = (a1, a2) -> {
            Doctor d = getDoctor(a1.getDoctorId());
            return (d != null && d.getName().equalsIgnoreCase(name)) ? 0 : -1;
        };
        return appointmentMap.filter(new Appointment(null, null, null, null, null), comparator);
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
        System.out.println("Consultations by Patient: ");
        String[] pidArray = patientIds.toArray(new String[0]);
        for (String pid : pidArray) {
            System.out.println("Patient " + pid + ": " + byPatient.get(pid));
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
        System.out.println("Consultations by Doctor: ");
        String[] didArray = doctorIds.toArray(new String[0]);
        for (String did : didArray) {
            System.out.println("Doctor " + did + ": " + byDoctor.get(did));
        }
    }

    public void printServiceUsageReport() {
        Consultation[] consultations = consultationMap.toArray(new Consultation[0]);
        CustomADT<String, Integer> serviceUsage = new CustomADT<>();
        java.util.List<String> serviceIds = new java.util.ArrayList<>();
        for (Consultation c : consultations) {
            for (ConsultationService s : c.getServicesUsed().toArray(new ConsultationService[0])) {
                String sid = s.getServiceId();
                Integer count = serviceUsage.get(sid);
                serviceUsage.put(sid, count == null ? 1 : count + 1);
                if (!serviceIds.contains(sid)) {
                    serviceIds.add(sid);
                }
            }
        }
        System.out.println("\n=== Service Usage Report ===");
        for (String sid : serviceIds) {
            ConsultationService service = serviceMap.get(sid);
            String serviceName = service != null ? service.getServiceName() : "Unknown";
            System.out.println(serviceName + ": " + serviceUsage.get(sid) + " times");
        }
    }
}