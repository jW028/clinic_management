package dao;

import adt.CustomADT;
import entity.Doctor;

public class DoctorInitializer {
    public static void main(String[] args) {
        CustomADT<String, Doctor> doctorRegistry = new CustomADT<>();

        doctorRegistry.put("DC001", new Doctor("DC001", "Dr. Alice Tan", "Cardiology",
                "012-3456789", "alice.tan@hospital.com", "123 Jalan Medan", "FEMALE", "1980-04-12"));

        doctorRegistry.put("DC002", new Doctor("DC002", "Dr. John Lim", "Neurology",
                "013-9876543", "john.lim@hospital.com", "45 Lorong Permai", "MALE", "1975-09-25"));

        doctorRegistry.put("DC003", new Doctor("DC003", "Dr. Sarah Lee", "Pediatrics",
                "014-1234567", "sarah.lee@hospital.com", "88 Jalan Damai", "FEMALE", "1985-02-08"));

        doctorRegistry.put("DC004", new Doctor("DC004", "Dr. Ahmad Farid", "Orthopedics",
                "017-7654321", "ahmad.farid@hospital.com", "56 Jalan Bunga Raya", "MALE", "1978-11-19"));

        doctorRegistry.put("DC005", new Doctor("DC005", "Dr. Emily Wong", "Dermatology",
                "016-5551234", "emily.wong@hospital.com", "22 Jalan Taman Hijau", "FEMALE", "1982-07-03"));

        DoctorDAO.saveDoctors(doctorRegistry);
        System.out.println("Doctors initialized and saved to file.");
    }
}
