/**
 * @author Lee Jia Shin
 */
package control;

import adt.OrderedMap;
import dao.AppointmentDAO;
import dao.ConsultationDAO;
import dao.ConsultationServiceDAO;
import dao.DiagnosisDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import dao.ScheduleDAO;
import entity.Appointment;
import entity.Consultation;
import entity.ConsultationService;
import entity.Diagnosis;
import entity.Patient;
import entity.Doctor;
import entity.Schedule;
import utility.IDGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class ConsultationMaintenance {
    private static class UndoAction {
        String type; // "ADD", "REMOVE", "UPDATE"
        Consultation before;
        Consultation after;
        LocalDateTime timestamp;
        UndoAction(String type, Consultation before, Consultation after) {
            this.type = type;
            this.before = before;
            this.after = after;
            this.timestamp = LocalDateTime.now();
        }
    }

    private final OrderedMap<String, Consultation> consultationMap;
    private final OrderedMap<String, Patient> patientMap;
    private final OrderedMap<String, Appointment> appointmentMap;
    private final OrderedMap<String, ConsultationService> serviceMap;
    private final OrderedMap<String, Diagnosis> diagnosisMap;
    private final OrderedMap<String, Doctor> doctorMap;
    private final OrderedMap<String, Schedule> scheduleMap;

    private final ConsultationDAO consultationDAO = new ConsultationDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DiagnosisDAO diagnosisDAO = new DiagnosisDAO();
    private final ConsultationServiceDAO consultationServiceDAO = new ConsultationServiceDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();

    // Undo stack
    private final OrderedMap<Integer, UndoAction> undoHistory = new OrderedMap<>();
    private static final int MAX_UNDO_SIZE = 20;

    // search
    private OrderedMap<String, OrderedMap<String, Consultation>> patientNameIndex = new OrderedMap<>();
    private OrderedMap<String, OrderedMap<String, Consultation>> doctorNameIndex = new OrderedMap<>();

    public static final String[] VALID_APPOINTMENT_STATUSES = {
            "Scheduled", "In Progress", "Completed", "Cancelled"
    };

    public ConsultationMaintenance() {
        this.consultationMap = consultationDAO.retrieveFromFile();
        this.patientMap = patientDAO.retrieveFromFile();
        this.appointmentMap = appointmentDAO.retrieveFromFile();
        this.serviceMap = consultationServiceDAO.retrieveFromFile();
        this.diagnosisMap = diagnosisDAO.retrieveFromFile();
        this.doctorMap = doctorDAO.loadDoctors();
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

        buildNameIndices();
    }

    // Helper: Deep copy
    private Consultation copyConsultation(Consultation c) {
        if (c == null) return null;
        return new Consultation(
                c.getConsultationId(),
                c.getAppointment(),
                c.getPatient(),
                c.getDoctor(),
                c.getConsultationTime(),
                c.getServicesUsed(),
                c.getDiagnosis(),
                c.getNotes(),
                c.getPayment(),
                c.isFollowUpNeeded(),
                c.getFollowUpDate()
        );
    }
    private void logUndoAction(UndoAction action) {
        undoHistory.push(undoHistory.size(), action);
        if (undoHistory.size() > MAX_UNDO_SIZE) undoHistory.removeAt(0);
    }

    // === Undo API ===
    public String undoLastAction() {
        if (undoHistory.size() == 0) return "No action to undo.";
        UndoAction action = undoHistory.pop();
        switch (action.type) {
            case "ADD":
                consultationMap.remove(action.after.getConsultationId());
                removeFromIndices(action.after);
                consultationDAO.saveToFile(consultationMap);
                return "Undo Add: Consultation removed.";
            case "REMOVE":
                consultationMap.put(action.before.getConsultationId(), copyConsultation(action.before));
                updateIndicesForAddition(action.before);
                consultationDAO.saveToFile(consultationMap);
                return "Undo Remove: Consultation restored.";
            case "UPDATE":
                consultationMap.put(action.before.getConsultationId(), copyConsultation(action.before));
                updateIndicesForAddition(action.before);
                consultationDAO.saveToFile(consultationMap);
                return "Undo Update: Consultation reverted.";
            default:
                return "Unknown action type.";
        }
    }

    public String getUndoInfo() {
        if (undoHistory.isEmpty()) return "No operations available to undo.";
        StringBuilder sb = new StringBuilder();
        sb.append("Available undo operations (").append(undoHistory.size()).append("):\n");
        for (int i = undoHistory.size() - 1; i >= 0 && i >= undoHistory.size() - 5; i--) {
            UndoAction action = undoHistory.get(i);
            sb.append("- ").append(action.type)
                    .append(" on consultation ").append(action.after != null ? action.after.getConsultationId() :
                            action.before != null ? action.before.getConsultationId() : "(unknown)")
                    .append(" at ").append(action.timestamp)
                    .append("\n");
        }
        return sb.toString();
    }
    public void clearUndoHistory() {
        undoHistory.clear();
    }

    // === Indexing for fast search ===
    private void buildNameIndices() {
        patientNameIndex.clear();
        doctorNameIndex.clear();
        for (Consultation c : consultationMap) {
            updateIndicesForAddition(c);
        }
    }
    private void updateIndicesForAddition(Consultation c) {
        if (c == null) return;
        if (c.getPatient() != null) {
            String name = c.getPatient().getName().toLowerCase();
            if (!patientNameIndex.containsKey(name)) {
                patientNameIndex.put(name, new OrderedMap<>());
            }
            patientNameIndex.get(name).put(c.getConsultationId(), c);
        }
        if (c.getDoctor() != null) {
            String name = c.getDoctor().getName().toLowerCase();
            if (!doctorNameIndex.containsKey(name)) {
                doctorNameIndex.put(name, new OrderedMap<>());
            }
            doctorNameIndex.get(name).put(c.getConsultationId(), c);
        }
    }
    private void removeFromIndices(Consultation c) {
        if (c == null) return;
        if (c.getPatient() != null) {
            String name = c.getPatient().getName().toLowerCase();
            if (patientNameIndex.containsKey(name)) {
                patientNameIndex.get(name).remove(c.getConsultationId());
                if (patientNameIndex.get(name).isEmpty()) patientNameIndex.remove(name);
            }
        }
        if (c.getDoctor() != null) {
            String name = c.getDoctor().getName().toLowerCase();
            if (doctorNameIndex.containsKey(name)) {
                doctorNameIndex.get(name).remove(c.getConsultationId());
                if (doctorNameIndex.get(name).isEmpty()) doctorNameIndex.remove(name);
            }
        }
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
        updateIndicesForAddition(consultation);
        consultationDAO.saveToFile(consultationMap);
        IDGenerator.saveCounters("counter.dat");

        logUndoAction(new UndoAction("ADD", null, copyConsultation(consultation)));
    }

    public Consultation getConsultation(String id) {
        return consultationMap.get(id);
    }

    // Update method for undo (used for updateConsultation)
    public void updateConsultation(Consultation updated) {
        Consultation before = copyConsultation(getConsultation(updated.getConsultationId()));
        consultationMap.put(updated.getConsultationId(), updated);
        updateIndicesForAddition(updated);
        consultationDAO.saveToFile(consultationMap);
        logUndoAction(new UndoAction("UPDATE", before, copyConsultation(updated)));
    }

    public void updateFollowUpScheduleSlot(Consultation consultation, LocalDateTime newFollowUpDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oldFollowUpDate = consultation.getFollowUpDate();

        // Free the old slot if valid
        if (oldFollowUpDate != null && oldFollowUpDate.isAfter(now)) {
            String doctorId = consultation.getDoctor().getDoctorID();
            String date = oldFollowUpDate.toLocalDate().toString();
            String startTimeStr = oldFollowUpDate.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String endTimeStr = oldFollowUpDate.plusHours(1).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String timeSlot = startTimeStr + "–" + endTimeStr;
            String key = doctorId + "_" + date + "_" + timeSlot;

            Schedule oldSched = scheduleMap.get(key);
            if (oldSched != null && !oldSched.getStatus()) {
                oldSched.setStatus(true);
                scheduleMap.put(key, oldSched);
                scheduleDAO.saveToFile(scheduleMap);
            }
        }

        // Book the new slot
        if (newFollowUpDate != null) {
            String doctorId = consultation.getDoctor().getDoctorID();
            String date = newFollowUpDate.toLocalDate().toString();
            String startTimeStr = newFollowUpDate.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String endTimeStr = newFollowUpDate.plusHours(1).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String timeSlot = startTimeStr + "–" + endTimeStr;
            String key = doctorId + "_" + date + "_" + timeSlot;

            Schedule newSched = scheduleMap.get(key);
            if (newSched != null && newSched.getStatus()) {
                newSched.setStatus(false);
                scheduleMap.put(key, newSched);
                scheduleDAO.saveToFile(scheduleMap);
            }
        }
    }

    public boolean removeConsultation(String id) {
        Consultation removed = consultationMap.remove(id);
        removeFromIndices(removed);
        consultationDAO.saveToFile(consultationMap);
        if (removed != null) {
            logUndoAction(new UndoAction("REMOVE", copyConsultation(removed), null));
        }
        return removed != null;
    }

    public Consultation[] getAllConsultations() {
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

    // Schedule
    public String[] getAvailableSlotsForDoctor(String doctorId) {
        if (doctorId == null) return new String[0];

        // Collect available slots for doctor
        OrderedMap<String, String> slotMap = new OrderedMap<>();
        int slotCounter = 1;
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < scheduleMap.size(); i++) {
            Schedule sched = scheduleMap.get(i);
            if (sched.getDoctorID().equals(doctorId) && sched.getStatus()) {
                String displaySlot = sched.getDate() + " " + sched.getTimeslot();
                try {
                    String[] slotParts = sched.getTimeslot().split("[-–—]");
                    String startTime = slotParts[0].trim();
                    String[] dateParts = sched.getDate().split("-");
                    String formattedDate = dateParts.length == 3
                            ? dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0]
                            : sched.getDate();
                    String dateTimeStr = formattedDate + " " + startTime;
                    LocalDateTime slotDateTime = utility.DateTimeFormatterUtil.parseDisplayFormat(dateTimeStr);
                    if (slotDateTime.isAfter(now)) {
                        slotMap.put("slot" + slotCounter++, displaySlot);
                    }
                } catch (Exception ex) {}
            }
        }
        return slotMap.toArray(new String[0]);
    }

    // --- Searching and Sorting for Consultations ---
    public OrderedMap<String, Consultation> searchConsultationsByPatientName(String name) {
        name = name.toLowerCase();
        if (patientNameIndex.containsKey(name)) {
            return patientNameIndex.get(name);
        }
        OrderedMap<String, Consultation> result = new OrderedMap<>();
        for (int i = 0; i < consultationMap.size(); i++) {
            Consultation c = consultationMap.get(i);
            Patient patient = c.getPatient();
            if (patient != null && patient.getName().toLowerCase().contains(name)) {
                result.put(c.getConsultationId(), c);
            }
        }
        return result;
    }

    public OrderedMap<String, Consultation> searchConsultationsByDoctorName(String name) {
        name = name.toLowerCase();
        if (doctorNameIndex.containsKey(name)) {
            return doctorNameIndex.get(name);
        }
        OrderedMap<String, Consultation> result = new OrderedMap<>();
        for (int i = 0; i < consultationMap.size(); i++) {
            Consultation c = consultationMap.get(i);
            Doctor doctor = c.getDoctor();
            if (doctor != null && doctor.getName().toLowerCase().contains(name)) {
                result.put(c.getConsultationId(), c);
            }
        }
        return result;
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
    public OrderedMap<String, Appointment> searchAppointmentsByPatientName(String name) {
        OrderedMap<String, Appointment> result = new OrderedMap<>();
        String search = name.toLowerCase();
        for (int i = 0; i < appointmentMap.size(); i++) {
            Appointment appt = appointmentMap.get(i);
            Patient patient = getPatient(appt.getPatientId());
            if (patient != null && patient.getName().toLowerCase().contains(search)) {
                result.put(appt.getAppointmentId(), appt);
            }
        }
        return result;
    }

    public OrderedMap<String, Appointment> searchAppointmentsByDoctorName(String name) {
        OrderedMap<String, Appointment> result = new OrderedMap<>();
        String search = name.toLowerCase();
        for (int i = 0; i < appointmentMap.size(); i++) {
            Appointment appt = appointmentMap.get(i);
            Doctor doctor = getDoctor(appt.getDoctorId());
            if (doctor != null && doctor.getName().toLowerCase().contains(search)) {
                result.put(appt.getAppointmentId(), appt);
            }
        }
        return result;
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
        String now = java.time.ZonedDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a"));

        System.out.println();
        line();
        System.out.println(center("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY", 80));
        System.out.println(center("CONSULTATION MANAGEMENT SUBSYSTEM", 80));
        System.out.println(center("CONSULTATION SUMMARY REPORT", 80));
        line();
        System.out.println(rightInDash("generated at: " + now));
        dash(); blank();

        System.out.println(center("CONSULTATION OVERVIEW", 80));
        dash();
        System.out.printf("  %-30s │ %-30s%n", "Total consultations", consultations.length);
        dash();

        if (consultations.length == 0) {
            System.out.println("No consultation records to show. Please add consultation data first.");
            line();
            return;
        }

        // By patient
        OrderedMap<String, Integer> byPatient = new OrderedMap<>();
        OrderedMap<String, String> patientIds = new OrderedMap<>();

        for (Consultation c : consultations) {
            String pid = c.getPatient().getPatientId();
            Integer count = byPatient.get(pid);
            byPatient.put(pid, count == null ? 1 : count + 1);
            if (!patientIds.containsKey(pid)) {
                patientIds.put(pid, pid);
            }
        }

        // Find max count for patient bar scaling
        int maxPatientCount = 0;
        for (String pid : patientIds.toArray(new String[0])) {
            int count = byPatient.get(pid) != null ? byPatient.get(pid) : 0;
            if (count > maxPatientCount) maxPatientCount = count;
        }

        // Sortable patient rows
        class PatientRow {
            String id, name;
            int count;
            PatientRow(String id, String name, int count) {
                this.id = id; this.name = name; this.count = count;
            }
        }
        OrderedMap<Integer, PatientRow> rows = new OrderedMap<>();
        String[] pidArray = patientIds.toArray(new String[0]);
        for (String pid : pidArray) {
            Patient patient = patientMap.get(pid);
            String pname = patient != null ? patient.getName() : "Unknown";
            int count = byPatient.get(pid) != null ? byPatient.get(pid) : 0;
            rows.put(rows.size(), new PatientRow(pid, pname, count));
        }
        rows.sort((a, b) -> b.count - a.count);

        System.out.println(center("CONSULTATIONS BY PATIENT", 80));
        dash();
        System.out.printf("  %-12s │ %-20s │ %-10s │ %-18s%n", "Patient ID", "Patient Name", "Count", "Bar");
        dash();
        if (rows.size() == 0) {
            System.out.printf("  %-12s │ %-20s │ %-10s │ %-18s%n", "-", "-", "0", "");
        } else {
            for (int i = 0; i < rows.size(); i++) {
                PatientRow r = rows.get(i);
                int barLen = maxPatientCount == 0 ? 0 : (int) Math.round(((double) r.count / maxPatientCount) * 27);
                String bar = "█".repeat(barLen);
                System.out.printf("  %-12s │ %-20s │ %-10d │ %-18s%n", r.id, r.name, r.count, bar);
            }
        }
        dash(); blank();

        // By doctor
        OrderedMap<String, Integer> byDoctor = new OrderedMap<>();
        OrderedMap<String, String> doctorIds = new OrderedMap<>();

        for (Consultation c : consultations) {
            String did = c.getDoctor().getDoctorID();
            Integer count = byDoctor.get(did);
            byDoctor.put(did, count == null ? 1 : count + 1);
            if (!doctorIds.containsKey(did)) {
                doctorIds.put(did, did);
            }
        }

        // Find max count for doctor bar scaling
        int maxDoctorCount = 0;
        for (String did : doctorIds.toArray(new String[0])) {
            int count = byDoctor.get(did) != null ? byDoctor.get(did) : 0;
            if (count > maxDoctorCount) maxDoctorCount = count;
        }

        // Sortable doctor rows
        class DoctorRow {
            String id, name;
            int count;
            DoctorRow(String id, String name, int count) {
                this.id = id; this.name = name; this.count = count;
            }
        }
        OrderedMap<Integer, DoctorRow> doctorRows = new OrderedMap<>();
        String[] didArray = doctorIds.toArray(new String[0]);
        for (String did : didArray) {
            Doctor doctor = doctorMap.get(did);
            String dname = doctor != null ? doctor.getName() : "Unknown";
            int count = byDoctor.get(did) != null ? byDoctor.get(did) : 0;
            doctorRows.put(doctorRows.size(), new DoctorRow(did, dname, count));
        }
        doctorRows.sort((a, b) -> b.count - a.count);

        System.out.println(center("CONSULTATIONS BY DOCTOR", 80));
        dash();
        System.out.printf("  %-12s │ %-20s │ %-10s │ %-18s%n", "Doctor ID", "Doctor Name", "Count", "Bar");
        dash();
        if (doctorRows.size() == 0) {
            System.out.printf("  %-12s │ %-20s │ %-10s │ %-18s%n", "-", "-", "0", "");
        } else {
            for (int i = 0; i < doctorRows.size(); i++) {
                DoctorRow r = doctorRows.get(i);
                int barLen = maxDoctorCount == 0 ? 0 : (int) Math.round(((double) r.count / maxDoctorCount) * 27);
                String bar = "█".repeat(barLen);
                System.out.printf("  %-12s │ %-20s │ %-10d │ %-18s%n", r.id, r.name, r.count, bar);
            }
        }
        dash();
        System.out.println(center("END OF THE REPORT", 80));
        line();
    }

    public void printServiceUsageReport() {
        Consultation[] consultations = consultationMap.toArray(new Consultation[0]);
        String now = java.time.ZonedDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a"));

        System.out.println();
        line();
        System.out.println(center("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY", 80));
        System.out.println(center("CONSULTATION MANAGEMENT SUBSYSTEM", 80));
        System.out.println(center("SERVICE USAGE REPORT", 80));
        line();
        System.out.println(rightInDash("generated at: " + now));
        dash(); blank();

        if (consultations.length == 0) {
            System.out.println("No consultation records to show. Please add consultation data first.");
            line();
            return;
        }

        OrderedMap<String, Integer> serviceUsage = new OrderedMap<>();
        OrderedMap<String, String> serviceIdsADT = new OrderedMap<>();
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

        // Find max count for service bar scaling
        int maxServiceCount = 0;
        for (String sid : serviceIdsADT.toArray(new String[0])) {
            int count = serviceUsage.get(sid) != null ? serviceUsage.get(sid) : 0;
            if (count > maxServiceCount) maxServiceCount = count;
        }

        // Sortable service rows
        class ServiceRow {
            String id, name;
            int count;
            ServiceRow(String id, String name, int count) { this.id = id; this.name = name; this.count = count; }
        }
        OrderedMap<Integer, ServiceRow> rows = new OrderedMap<>();
        String[] sidArray = serviceIdsADT.toArray(new String[0]);
        for (String sid : sidArray) {
            ConsultationService service = serviceMap.get(sid);
            String serviceName = service != null ? service.getServiceName() : "Unknown";
            int count = serviceUsage.get(sid) != null ? serviceUsage.get(sid) : 0;
            rows.put(rows.size(), new ServiceRow(sid, serviceName, count));
        }
        rows.sort((a, b) -> b.count - a.count);

        System.out.println(center("SERVICE USAGE", 80));
        dash();
        System.out.printf("  %-12s │ %-25s │ %-10s │ %-18s%n", "Service ID", "Service Name", "Count", "Bar");
        dash();
        if (rows.size() == 0) {
            System.out.printf("  %-12s │ %-25s │ %-10s │ %-18s%n", "-", "-", "0", "");
        } else {
            for (int i = 0; i < rows.size(); i++) {
                ServiceRow r = rows.get(i);
                int barLen = maxServiceCount == 0 ? 0 : (int) Math.round(((double) r.count / maxServiceCount) * 22);
                String bar = "█".repeat(barLen);
                System.out.printf("  %-12s │ %-25s │ %-10d │ %-18s%n", r.id, r.name, r.count, bar);
            }
        }
        dash();
        System.out.println(center("END OF THE REPORT", 80));
        line();
    }

    public void printAppointmentsPerDoctorReport() {
        Appointment[] appointments = appointmentMap.toArray(new Appointment[0]);
        String now = java.time.ZonedDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a"));

        System.out.println();
        line();
        System.out.println(center("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY", 80));
        System.out.println(center("APPOINTMENT MANAGEMENT SUBSYSTEM", 80));
        System.out.println(center("APPOINTMENTS PER DOCTOR REPORT", 80));
        line();
        System.out.println(rightInDash("generated at: " + now));
        dash(); blank();

        System.out.println(center("APPOINTMENTS OVERVIEW", 80));
        dash();
        System.out.printf("  %-30s │ %-30s%n", "Total appointments", appointments.length);
        dash();

        if (appointments.length == 0) {
            System.out.println("No appointment records to show. Please add appointment data first.");
            line();
            return;
        }

        // Count appointments per doctor
        OrderedMap<String, Integer> byDoctor = new OrderedMap<>();
        OrderedMap<String, String> doctorIds = new OrderedMap<>();
        for (Appointment a : appointments) {
            String did = a.getDoctorId();
            Integer count = byDoctor.get(did);
            byDoctor.put(did, count == null ? 1 : count + 1);
            if (!doctorIds.containsKey(did)) {
                doctorIds.put(did, did);
            }
        }

        // Find max count for doctor appointment bar scaling
        int maxAppointmentCount = 0;
        for (String did : doctorIds.toArray(new String[0])) {
            int count = byDoctor.get(did) != null ? byDoctor.get(did) : 0;
            if (count > maxAppointmentCount) maxAppointmentCount = count;
        }

        // Sortable doctor rows
        class DoctorRow {
            String id, name;
            int count;
            DoctorRow(String id, String name, int count) { this.id = id; this.name = name; this.count = count; }
        }
        OrderedMap<Integer, DoctorRow> doctorRows = new OrderedMap<>();
        String[] didArray = doctorIds.toArray(new String[0]);
        for (String did : didArray) {
            Doctor doctor = doctorMap.get(did);
            String dname = doctor != null ? doctor.getName() : "Unknown";
            int count = byDoctor.get(did) != null ? byDoctor.get(did) : 0;
            doctorRows.put(doctorRows.size(), new DoctorRow(did, dname, count));
        }
        doctorRows.sort((a, b) -> b.count - a.count);

        System.out.println(center("APPOINTMENTS BY DOCTOR", 80));
        dash();
        System.out.printf("  %-12s │ %-20s │ %-10s │ %-18s%n", "Doctor ID", "Doctor Name", "Count", "Bar");
        dash();
        if (doctorRows.size() == 0) {
            System.out.printf("  %-12s │ %-20s │ %-10s │ %-18s%n", "-", "-", "0", "");
        } else {
            for (int i = 0; i < doctorRows.size(); i++) {
                DoctorRow r = doctorRows.get(i);
                int barLen = maxAppointmentCount == 0 ? 0 : (int) Math.round(((double) r.count / maxAppointmentCount) * 27);
                String bar = "█".repeat(barLen);
                System.out.printf("  %-12s │ %-20s │ %-10d │ %-18s%n", r.id, r.name, r.count, bar);
            }
        }
        dash();
        System.out.println(center("END OF THE REPORT", 80));
        line();
    }

    private void line() {
        System.out.println("=".repeat(80));
    }
    private void dash() {
        System.out.println("-".repeat(80));
    }
    private void blank() {
        System.out.println();
    }
    private String center(String s, int width) {
        int pad = (width - s.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + s;
    }
    private String rightInDash(String s) {
        int width = 80;
        int pad = Math.max(0, width - s.length());
        return " ".repeat(pad) + s;
    }
}