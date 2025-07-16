package entity;

public class Schedule {
    private String scheduleID;
    private String doctorID;
    private String patientID;
    private String date;
    private String timeslot;
    private boolean status;

    public Schedule(String scheduleID, String doctorID, String patientID, String date, String timeslot, boolean status) {
        this.scheduleID = scheduleID;
        this.doctorID = doctorID;
        this.patientID = patientID;
        this.date = date;
        this.timeslot = timeslot;
        this.status = status;
    }
    public String getScheduleID() {
        return scheduleID;
    }
    public String getDoctorID() {
        return doctorID;
    }
    public String getPatientID() {
        return patientID;
    }
    public String getDate() {
        return date;
    }
    public String getTimeslot() {
        return timeslot;
    }
    public boolean getStatus() {
        return status;
    }
    public void setScheduleID(String scheduleID) {
        this.scheduleID = scheduleID;
    }
    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }
    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }
    public void setStatus(boolean status){
        this.status = status;
    }
    @Override
    public String toString(){
        return "Doctor ID: " + doctorID +
                ", Day: " + date +
                ", Time Slot: " + timeslot +
                ", Available: " + (status ? "Yes" : "No");
    }
}
