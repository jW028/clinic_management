package control;

import adt.CustomADT;
import dao.DoctorDAO;
import entity.Doctor;
import utility.IDGenerator;

public class DoctorMaintenance {
    private CustomADT<String, Doctor> doctorRegistry;

    public DoctorMaintenance() {
        doctorRegistry = DoctorDAO.loadDoctors();

        String highestID = "DC000";
        for (Doctor doctor : doctorRegistry) { // Use values() to iterate over all doctors
            String id = doctor.getDoctorID();
            if (id.compareTo(highestID) > 0) {
                highestID = id;
            }
        }
        IDGenerator.updateDoctorCounterFromHighestID(highestID);
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

    public void updateDoctorField(String doctorID, String field, String newValue) {
        Doctor doctor = doctorRegistry.get(doctorID);
        if (doctor == null) return;

        switch (field.toLowerCase()) {
            case "name" -> doctor.setName(newValue);
            case "specialty" -> doctor.setSpecialty(newValue);
            case "phone" -> doctor.setPhone(newValue);
            case "email" -> doctor.setEmail(newValue);
            case "address" -> doctor.setAddress(newValue);
            case "gender" -> doctor.setGender(newValue);
            case "dob" -> doctor.setDateOfBirth(newValue);
        }

        DoctorDAO.saveDoctors(doctorRegistry); // persist immediately
    }

    public boolean removeDoctor(String doctorID) {
        boolean removed = doctorRegistry.remove(doctorID) != null;
        if (removed) {
            DoctorDAO.saveDoctors(doctorRegistry); // Save after removal
        }
        return removed;
    }

//    public void listAllDoctors() {
//        System.out.printf("%-8s | %-20s | %-15s | %-13s | %-25s | %-25s | %-6s | %-12s\n",
//                "ID", "Name", "Specialty", "Phone", "Email", "Address", "Gender", "DOB");
//        System.out.println("-----------------------------------------------------------------------------------------------"
//                + "------------------------------------------------------");
//
//        for (Doctor doctor : doctorRegistry) {
//            System.out.printf("%-8s | %-20s | %-15s | %-13s | %-25s | %-25s | %-6s | %-12s\n",
//                    doctor.getDoctorID(), doctor.getName(), doctor.getSpecialty(), doctor.getPhone(),
//                    doctor.getEmail(), doctor.getAddress(), doctor.getGender(), doctor.getDateOfBirth());
//        }
//    }

    public Doctor[] getAllDoctorsArray() {
        return doctorRegistry.toArray(new Doctor[doctorRegistry.size()]);
    }

    public Doctor[] searchByName(String name) {
        CustomADT<String, Doctor> results = doctorRegistry.filter(
                new Doctor(null, name, null, null, null, null, null, null),
                (d1, d2) -> d1.getName().equalsIgnoreCase(d2.getName()) ? 0 : -1
        );
        return results.toArray(new Doctor[results.size()]);
    }

    public Doctor[] searchByGender(String gender) {
        CustomADT<String, Doctor> results = doctorRegistry.filter(
                new Doctor(null, null, null, null, null, null, gender, null),
                (d1, d2) -> d1.getGender().equalsIgnoreCase(d2.getGender()) ? 0 : -1
        );
        return results.toArray(new Doctor[results.size()]);
    }

    public Doctor[] searchBySpecialty(String specialty) {
        CustomADT<String, Doctor> results = doctorRegistry.filter(
                new Doctor(null, null, specialty, null, null, null, null, null),
                (d1, d2) -> d1.getSpecialty().equalsIgnoreCase(d2.getSpecialty()) ? 0 : -1
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


    public int getRegisteredDoctorCount() {
        return doctorRegistry.size();
    }
}
