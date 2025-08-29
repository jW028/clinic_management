package entity;
import java.io.Serializable;

public class Doctor implements Serializable{
    private String doctorID;
    private String name;
    private String specialty;
    private String phone;
    private String email;
    private String address;
    private String gender;
    private int yearsOfExperience;

    public Doctor(String doctorID, String name, String specialty, String phone, String email, String address, String gender, int yearsOfExperience) {
        this.doctorID = doctorID;
        this.name = name;
        this.specialty = specialty;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getDoctorID() {
        return doctorID;
    }
    public String getName(){
        return name;
    }
    public String getSpecialty() {
        return specialty;
    }
    public String getPhone() {
        return phone;
    }
    public String getEmail() {
        return email;
    }
    public String getAddress() {
        return address;
    }
    public String getGender() {
        return gender;
    }
    public int getYearsOfExperience() {return yearsOfExperience;}

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    @Override
    public String toString(){
        return "Doctor ID: " + doctorID +
                ", Name: " + name +
                ", Specialty: " + specialty +
                ", Phone: " + phone +
                ", Email: " + email +
                ", Address: " + address +
                ", Gender: " + gender +
                ", Years of Experience: " + yearsOfExperience;
    }
}
