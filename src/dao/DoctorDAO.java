package dao;

import adt.CustomADT;
import entity.Doctor;

import java.io.*;

public class DoctorDAO {
    private static final String FILE_NAME = "doctors.txt";

    public static void saveDoctors(CustomADT<String, Doctor> doctors) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            doctors.forEach(doctor -> {
                writer.println(doctor.getDoctorID() + ";" +
                        doctor.getName() + ";" +
                        doctor.getSpecialty() + ";" +
                        doctor.getPhone() + ";" +
                        doctor.getEmail() + ";" +
                        doctor.getAddress() + ";" +
                        doctor.getGender() + ";" +
                        doctor.getDateOfBirth());
            });
        } catch (IOException e) {
            System.out.println("❌ Error saving doctors: " + e.getMessage());
        }
    }

    public static CustomADT<String, Doctor> loadDoctors() {
        CustomADT<String, Doctor> doctors = new CustomADT<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) return doctors;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 8) {
                    Doctor doctor = new Doctor(
                            parts[0], parts[1], parts[2], parts[3],
                            parts[4], parts[5], parts[6], parts[7]
                    );
                    doctors.put(doctor.getDoctorID(), doctor);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error loading doctors: " + e.getMessage());
        }

        return doctors;
    }
}
