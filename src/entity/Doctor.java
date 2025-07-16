package entity;

public class Doctor {
    private String doctorID;
    private String name;
    private String specialty;
    private String phone;
    private String email;
    private String address;
    private String gender;
    private String dateOfBirth;

    public Doctor(String doctorID, String name, String specialty, String phone, String email, String address, String gender, String dateOfBirth) {
        this.doctorID = doctorID;
        this.name = name;
        this.specialty = specialty;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
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
    public String getDateOfBirth() {
        return dateOfBirth;
    }

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
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    @Override
    public String toString(){
//        StringBuilder sb = new StringBuilder();
//        sb.append("Doctor ID: ").append(doctorID).append("\n")
//          .append("Name: ").append(name).append("\n")
//          .append("Specialty: ").append(specialty).append("\n")
//          .append("Phone: ").append(phone).append("\n")
//          .append("Email: ").append(email).append("\n")
//          .append("Address: ").append(address).append("\n")
//          .append("Gender: ").append(gender).append("\n")
//          .append("Date of Birth: ").append(dateOfBirth).append("\n");
        return "Doctor ID: " + doctorID +
                ", Name: " + name +
                ", Specialty: " + specialty +
                ", Phone: " + phone +
                ", Email: " + email +
                ", Address: " + address +
                ", Gender: " + gender +
                ", Date of Birth: " + dateOfBirth;
    }
}
