package entity;
import java.io.Serializable;

public class Schedule implements Serializable {
    private String scheduleID;
    private String doctorID;
    private String date;
    private String timeslot;
    private boolean status; // true = available, false = not available

    public Schedule(String scheduleID, String doctorID, String date, String timeslot, boolean status) {
        this.scheduleID = scheduleID;
        this.doctorID = doctorID;
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
        return String.format("Schedule ID: %s | Doctor ID: %s | Date: %s | Time Slot: %s | Available: %s",
                scheduleID, doctorID, date, timeslot, (status ? "Yes" : "No"));
    }
}
