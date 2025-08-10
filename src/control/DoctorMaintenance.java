package control;

import adt.CustomADT;
import entity.Doctor;

public class DoctorMaintenance {
    private CustomADT<String, Doctor> doctorRegistry;

    public DoctorMaintenance() {
        doctorRegistry = DoctorDAO.loadDoctors(); // Now ADT, not ArrayList
    }

    public boolean registerDoctor(Doctor doctor) {
        if (doctorRegistry.containsKey(doctor.getDoctorID())) return false;
        doctorRegistry.put(doctor.getDoctorID(), doctor);
        DoctorDAO.saveDoctors(doctorRegistry);
        return true;
    }

    public Doctor getDoctor(String doctorID) {
        return doctorRegistry.get(doctorID);
    }

    public boolean updateDoctor(String doctorID, String name, String specialty,
                                String phone, String email, String address,
                                String gender, String dob) {
        Doctor doctor = doctorRegistry.get(doctorID);
        if (doctor != null) {
            doctor.setName(name);
            doctor.setSpecialty(specialty);
            doctor.setPhone(phone);
            doctor.setEmail(email);
            doctor.setAddress(address);
            doctor.setGender(gender);
            doctor.setDateOfBirth(dob);
            DoctorDAO.saveDoctors(doctorRegistry); // Save update
            return true;
        }
        return false;
    }

    public boolean removeDoctor(String doctorID) {
        boolean removed = doctorRegistry.remove(doctorID) != null;
        if (removed) {
            DoctorDAO.saveDoctors(doctorRegistry); // Save after removal
        }
        return removed;
    }

    public void listAllDoctors() {
        doctorRegistry.forEach(System.out::println);
    }

    public int getRegisteredDoctorCount() {
        return doctorRegistry.size();
    }
}
