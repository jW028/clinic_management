package dao;

import adt.CustomADT;
import entity.Doctor;

public class DoctorInitializer {
    public static void main(String[] args) {
        CustomADT<String, Doctor> doctorRegistry = new CustomADT<>();

        doctorRegistry.put("D001", new Doctor("D001", "Dr. Alice Tan", "Cardiology",
                "012-3456789", "alice.tan@hospital.com", "123 Jalan Medan", "Female", "1980-04-12"));

        doctorRegistry.put("D002", new Doctor("D002", "Dr. John Lim", "Neurology",
                "013-9876543", "john.lim@hospital.com", "45 Lorong Permai", "Male", "1975-09-25"));

        doctorRegistry.put("D003", new Doctor("D003", "Dr. Sarah Lee", "Pediatrics",
                "014-1234567", "sarah.lee@hospital.com", "88 Jalan Damai", "Female", "1985-02-08"));

        doctorRegistry.put("D004", new Doctor("D004", "Dr. Ahmad Farid", "Orthopedics",
                "017-7654321", "ahmad.farid@hospital.com", "56 Jalan Bunga Raya", "Male", "1978-11-19"));

        doctorRegistry.put("D005", new Doctor("D005", "Dr. Emily Wong", "Dermatology",
                "016-5551234", "emily.wong@hospital.com", "22 Jalan Taman Hijau", "Female", "1982-07-03"));

        DoctorDAO.saveDoctors(doctorRegistry);
        System.out.println("✅ Doctors initialized and saved to file.");
    }
}
