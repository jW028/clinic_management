package control;

import adt.CustomADT;
import dao.DoctorDAO;
import entity.Doctor;
import entity.Schedule;
import utility.IDGenerator;
import control.ScheduleMaintenance;

public class DoctorMaintenance {
    // === Undo Feature ===
    private static class UndoAction {
        String type; // "REGISTER", "REMOVE", "UPDATE"
        Doctor doctorBefore;
        Doctor doctorAfter;
        String field;      // for update
        String prevValue;  // for update
        String newValue;   // for update
    }

    private CustomADT<String, Doctor> doctorRegistry;
    private CustomADT<Integer, UndoAction> undoHistory;
    private CustomADT<Integer, String> recentDoctorActions; // Keep this for history text
    private ScheduleMaintenance scheduleMaintenance;

    public DoctorMaintenance() {
        IDGenerator.loadCounter("counter.dat");
        this.doctorRegistry = DoctorDAO.loadDoctors();
        this.undoHistory = new CustomADT<>();
        this.recentDoctorActions = new CustomADT<>();
        this.scheduleMaintenance = new ScheduleMaintenance();

        String highestID = "DC000";
        for (Doctor doctor : doctorRegistry) {
            String id = doctor.getDoctorID();
            if (id.compareTo(highestID) > 0) {
                highestID = id;
            }
        }
        IDGenerator.updateDoctorCounterFromHighestID(highestID);
        IDGenerator.saveCounters("counter.dat");
    }

    private void logUndoAction(UndoAction action) {
        undoHistory.push(undoHistory.size(), action);
        if (undoHistory.size() > 20) undoHistory.remove(0);
    }
    private void logAction(String action) {
        recentDoctorActions.push(recentDoctorActions.size(), action);
        if (recentDoctorActions.size() > 20) {
            recentDoctorActions.remove(0);
        }
    }

    private Doctor copyDoctor(Doctor d) {
        if (d == null) return null;
        return new Doctor(
                d.getDoctorID(), d.getName(), d.getSpecialty(), d.getPhone(),
                d.getEmail(), d.getAddress(), d.getGender(), d.getYearsOfExperience()
        );
    }

    public boolean registerDoctor(Doctor doctor) {
        if (doctorRegistry.containsKey(doctor.getDoctorID())) return false;
        doctorRegistry.put(doctor.getDoctorID(), doctor);
        DoctorDAO.saveDoctors(doctorRegistry);
        IDGenerator.saveCounters("counter.dat");
        UndoAction action = new UndoAction();
        action.type = "REGISTER"; action.doctorAfter = copyDoctor(doctor);
        logUndoAction(action);
        logAction("Registered doctor [" + doctor.getDoctorID() + "] " + doctor.getName());
        return true;
    }

    public Doctor getDoctor(String doctorID) {
        return doctorRegistry.get(doctorID);
    }

    public void updateDoctorField(String doctorID, String field, String newValue) {
        Doctor doctor = doctorRegistry.get(doctorID);
        if (doctor == null) return;

        UndoAction action = new UndoAction();
        action.type = "UPDATE";
        action.doctorBefore = copyDoctor(doctor); // before update
        action.field = field;

        String oldValue = null;

        switch (field.toLowerCase()) {
            case "name" -> { oldValue = doctor.getName(); doctor.setName(newValue); action.newValue = newValue;}
            case "specialty" -> { oldValue = doctor.getSpecialty(); doctor.setSpecialty(newValue); action.newValue = newValue;}
            case "phone" -> { oldValue = doctor.getPhone(); doctor.setPhone(newValue); action.newValue = newValue;}
            case "email" -> { oldValue = doctor.getEmail(); doctor.setEmail(newValue); action.newValue = newValue;}
            case "address" -> { oldValue = doctor.getAddress(); doctor.setAddress(newValue); action.newValue = newValue;}
            case "gender" -> { oldValue = doctor.getGender(); doctor.setGender(newValue); action.newValue = newValue;}
            case "experience" -> {
                oldValue = String.valueOf(doctor.getYearsOfExperience());
                try {
                    int exp = Integer.parseInt(newValue);
                    doctor.setYearsOfExperience(exp);
                    action.newValue = newValue;
                } catch (NumberFormatException e) { return; }
            }
        }

        DoctorDAO.saveDoctors(doctorRegistry);
        IDGenerator.saveCounters("counter.dat");
        action.prevValue = oldValue;
        action.doctorAfter = copyDoctor(doctor); // after update
        logUndoAction(action);
        logAction("Updated doctor [" + doctorID + "]: " + field + " changed from '" + oldValue + "' to '" + newValue + "'");
    }

    public boolean removeDoctor(String doctorID) {
        CustomADT<Integer, Schedule> futureSchedules = scheduleMaintenance.getSchedulesByDoctor(doctorID);
        boolean hasFuture = false;
        String today = java.time.LocalDate.now().toString();
        for (int i = 0; i < futureSchedules.size(); i++) {
            Schedule s = futureSchedules.get(i);
            if (s.getDate().compareTo(today) >= 0) {
                hasFuture = true;
                break;
            }
        }
        if (hasFuture)
            System.out.println("Warning: Doctor has upcoming schedules. Consider reassigning or removing those first.");

        Doctor removedDoctor = doctorRegistry.get(doctorID);
        boolean removed = doctorRegistry.remove(doctorID) != null;
        if (removed) {
            DoctorDAO.saveDoctors(doctorRegistry);
            IDGenerator.saveCounters("counter.dat");
            UndoAction action = new UndoAction();
            action.type = "REMOVE"; action.doctorBefore = copyDoctor(removedDoctor);
            logUndoAction(action);
            logAction("Removed doctor [" + doctorID + "]");
        }
        return removed;
    }

    // === Undo method ===
    public String undoLastAction() {
        if (undoHistory.size() == 0) return "No action to undo.";
        UndoAction action = undoHistory.pop();
        switch (action.type) {
            case "REGISTER":
                doctorRegistry.remove(action.doctorAfter.getDoctorID());
                DoctorDAO.saveDoctors(doctorRegistry);
                return "Undo Register: Doctor removed.";
            case "REMOVE":
                doctorRegistry.put(action.doctorBefore.getDoctorID(), copyDoctor(action.doctorBefore));
                DoctorDAO.saveDoctors(doctorRegistry);
                return "Undo Remove: Doctor restored.";
            case "UPDATE":
                doctorRegistry.put(action.doctorBefore.getDoctorID(), copyDoctor(action.doctorBefore));
                DoctorDAO.saveDoctors(doctorRegistry);
                return "Undo Update: Doctor reverted.";
            default:
                return "Unknown action type.";
        }
    }

    public Doctor[] getAllDoctorsArray() {
        return doctorRegistry.toArray(new Doctor[doctorRegistry.size()]);
    }

    public Doctor[] searchByName(String name) {
        CustomADT<String, Doctor> results = doctorRegistry.filter(
                new Doctor(null, name, null, null, null, null, null, 0),
                (d1, d2) -> d1.getName().equalsIgnoreCase(d2.getName()) ? 0 : -1
        );
        return results.toArray(new Doctor[results.size()]);
    }

    public Doctor[] searchByGender(String gender) {
        CustomADT<String, Doctor> results = doctorRegistry.filter(
                new Doctor(null, null, null, null, null, null, gender, 0),
                (d1, d2) -> d1.getGender().equalsIgnoreCase(d2.getGender()) ? 0 : -1
        );
        return results.toArray(new Doctor[results.size()]);
    }

    public Doctor[] searchBySpecialty(String specialty) {
        CustomADT<String, Doctor> results = doctorRegistry.filter(
                new Doctor(null, null, specialty, null, null, null, null, 0),
                (d1, d2) -> d1.getSpecialty().equalsIgnoreCase(d2.getSpecialty()) ? 0 : -1
        );
        return results.toArray(new Doctor[results.size()]);
    }

    // SEARCH BY YEARS OF EXPERIENCE
    public Doctor[] searchByExperience(int years) {
        CustomADT<String, Doctor> results = doctorRegistry.filter(
                new Doctor(null, null, null, null, null, null, null, years),
                (d1, d2) -> d1.getYearsOfExperience() == d2.getYearsOfExperience() ? 0 : -1
        );
        return results.toArray(new Doctor[results.size()]);
    }

    public Doctor[] sortByName(boolean ascending) {
        doctorRegistry.sort((d1, d2) -> {
            int cmp = d1.getName().compareToIgnoreCase(d2.getName());
            return ascending ? cmp : -cmp;
        });
        return doctorRegistry.toArray(new Doctor[doctorRegistry.size()]);
    }

    public Doctor[] sortBySpecialty(boolean ascending) {
        doctorRegistry.sort((d1, d2) -> {
            int cmp = d1.getSpecialty().compareToIgnoreCase(d2.getSpecialty());
            return ascending ? cmp : -cmp;
        });
        return doctorRegistry.toArray(new Doctor[doctorRegistry.size()]);
    }

    public Doctor[] sortByGender(boolean ascending) {
        doctorRegistry.sort((d1, d2) -> {
            int cmp = d1.getGender().compareToIgnoreCase(d2.getGender());
            return ascending ? cmp : -cmp;
        });
        return doctorRegistry.toArray(new Doctor[doctorRegistry.size()]);
    }

    public Doctor[] sortByID(boolean ascending) {
        doctorRegistry.sort((d1, d2) -> {
            int cmp = d1.getDoctorID().compareToIgnoreCase(d2.getDoctorID());
            return ascending ? cmp : -cmp;
        });
        return doctorRegistry.toArray(new Doctor[doctorRegistry.size()]);
    }

    // SORT BY YEARS OF EXPERIENCE
    public Doctor[] sortByExperience(boolean ascending) {
        doctorRegistry.sort((d1, d2) -> {
            int cmp = Integer.compare(d1.getYearsOfExperience(), d2.getYearsOfExperience());
            return ascending ? cmp : -cmp;
        });
        return doctorRegistry.toArray(new Doctor[doctorRegistry.size()]);
    }

    public String[] getRecentDoctorActions(int n) {
        int sz = Math.min(n, recentDoctorActions.size());
        String[] actions = new String[sz];
        for (int i = recentDoctorActions.size() - sz, j = 0; i < recentDoctorActions.size(); i++, j++) {
            actions[j] = recentDoctorActions.get(i);
        }
        return actions;
    }

    public Schedule[] getSchedulesForDoctor(String doctorID) {
        CustomADT<Integer, Schedule> schedules = scheduleMaintenance.getSchedulesByDoctor(doctorID);
        return schedules.toArray(new Schedule[schedules.size()]);
    }

    public CustomADT<String, Integer> getSpecialtyCounts() {
        CustomADT<String, Integer> specialtyCounts = new CustomADT<>();
        for (Doctor doctor : doctorRegistry) {
            String specialty = doctor.getSpecialty();
            if (specialty == null || specialty.isEmpty()) specialty = "Unknown";
            int count = specialtyCounts.containsKey(specialty) ? specialtyCounts.get(specialty) : 0;
            specialtyCounts.put(specialty, count + 1);
        }
        return specialtyCounts;
    }
}