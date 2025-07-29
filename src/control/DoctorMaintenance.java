package control;
import entity.Doctor;
import adt.CustomADT;

public class DoctorMaintenance {
    private CustomADT<String, Doctor> doctorRegistry;
    public DoctorMaintenance(){
        doctorRegistry = new CustomADT<>();
    }
    public boolean registerDoctor(Doctor doctor) {
        if (doctorRegistry.containsKey(doctor.getDoctorID())){
            return false;
        }
        doctorRegistry.put(doctor.getDoctorID(), doctor);
        return true;
    }
    public Doctor getDoctor(String doctorID) {
        return doctorRegistry.get(doctorID);
    }
    public boolean updateDoctor(String doctorID, String newName,String newSpeciality,String newPhone) {
        Doctor doctor = doctorRegistry.get(doctorID);
        if(doctor != null){
            doctor.setName(newName);
            doctor.setSpecialty(newSpeciality);
            doctor.setPhone(newPhone);
            return true;
        }
        return false;
    }
    public void listAllDoctors(){
        doctorRegistry.forEach((Doctor d)-> {System.out.println(d);});
    }
    public int getRegisteredDoctorCount() {
        return doctorRegistry.size();
    }
}
