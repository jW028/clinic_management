package control;

import adt.CustomADT;
import dao.AppointmentDAO;
import dao.ConsultationDAO;
import dao.ConsultationServiceDAO;
import dao.PaymentDAO;
import dao.PatientDAO;
import entity.Appointment;
import entity.Consultation;
import entity.ConsultationService;
import entity.Diagnosis;
import entity.Payment;
import entity.Patient;
import entity.Doctor;

public class ConsultationMaintenance {
    private final CustomADT<String, Consultation> consultationMap;
    private final CustomADT<String, Patient> patientMap;
    private final CustomADT<String, Appointment> appointmentMap;
    private final CustomADT<String, ConsultationService> serviceMap;
    private final CustomADT<String, Diagnosis> diagnosisMap;
    private final CustomADT<String, Payment> paymentMap;
    private final CustomADT<String, Doctor> doctorMap;

    private final ConsultationDAO consultationDAO = new ConsultationDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    // private final DiagnosisDAO diagnosisDAO = new Diagnosis();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ConsultationServiceDAO consultationServiceDAO = new ConsultationServiceDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    // private final DoctorDAO doctorDAO = new DoctorDAO();

    public static final String[] VALID_APPOINTMENT_STATUSES = {
            "Scheduled", "Completed", "Cancelled", "In Progress"
    };

    public ConsultationMaintenance() {
        // Load from file or initialize empty
        this.consultationMap = consultationDAO.retrieveFromFile();
        this.patientMap = (CustomADT<String, Patient>) patientDAO.retrieveFromFile();
        // this.patientMap = patientDAO.retrieveFromFile();
        this.paymentMap = paymentDAO.retrieveFromFile();
        this.appointmentMap = appointmentDAO.retrieveFromFile();
        this.serviceMap = consultationServiceDAO.retrieveFromFile();
        // this.diagnosisMap = diagnosisDAO.retrieveFromFile();
        // this.doctorMap = doctorDAO.retrieveFromFile();

        // this.appointmentMap = new CustomADT<>();
        // this.serviceMap = new CustomADT<>();
        this.diagnosisMap = new CustomADT<>();
        this.doctorMap = new CustomADT<>();

        // Example initial data
        diagnosisMap.put("D001", new Diagnosis("D001", "Flu", "Mild"));
        doctorMap.put("DC001", new Doctor("DC001", "Lee", "Heart", "01112345678", "lee@gmail.com", "1, Street 1, Sunway", "M", "25/7/1980"));
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
        return consultationMap.toArray(new Consultation[0]);
    }

    // Appointment CRUD
    public void addAppointment(Appointment appointment) {
        appointmentMap.put(appointment.getAppointmentId(), appointment);
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
        // return appointmentMap.toArray(new Appointment[0]);
        return appointmentDAO.retrieveFromFile().toArray(new Appointment[0]);
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

    public void printConsultationSummaryReport() {
        Consultation[] consultations = consultationMap.toArray(new Consultation[0]);
        System.out.println("=== Consultation Summary Report ===");
        System.out.println("Total consultations: " + consultations.length);

        // By patient
        CustomADT<String, Integer> byPatient = new CustomADT<>();
        java.util.List<String> patientIds = new java.util.ArrayList<>();
        for (Consultation c : consultations) {
            String pid = c.getPatient().getPatientId();
            Integer count = byPatient.get(pid);
            byPatient.put(pid, count == null ? 1 : count + 1);
            if (!patientIds.contains(pid)) {
                patientIds.add(pid); // Track unique keys
            }
        }
        System.out.println("Consultations by Patient:");
        for (String pid : patientIds) {
            System.out.println("Patient " + pid + ": " + byPatient.get(pid));
        }

        // By doctor
        CustomADT<String, Integer> byDoctor = new CustomADT<>();
        java.util.List<String> doctorIds = new java.util.ArrayList<>();
        for (Consultation c : consultations) {
            String did = c.getDoctor().getDoctorID();
            Integer count = byDoctor.get(did);
            byDoctor.put(did, count == null ? 1 : count + 1);
            if (!doctorIds.contains(did)) {
                doctorIds.add(did);
            }
        }
        System.out.println("Consultations by Doctor:");
        for (String did : doctorIds) {
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
        System.out.println("=== Service Usage Report ===");
        for (String sid : serviceIds) {
            ConsultationService service = serviceMap.get(sid);
            String serviceName = service != null ? service.getServiceName() : "Unknown";
            System.out.println(serviceName + ": " + serviceUsage.get(sid) + " times");
        }
    }
}