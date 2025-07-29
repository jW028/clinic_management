package control;
import adt.CustomADT;
import entity.Schedule;

public class ScheduleMaintanance {
    private CustomADT<String, Schedule> scheduleList;
    private ScheduleMaintanance(){
        scheduleList = new CustomADT<>();
    }
    public boolean assignSchedule(Schedule schedule){
        String key = generateKey(schedule.getDoctorID(), schedule.getDate(),schedule.getTimeslot());
        if(scheduleList.containsKey(key)){
            return false;
        }
        scheduleList.put(key, schedule);
        return true;
    }
    public Schedule getSchedule(String doctorID,String date,String timeSlot){
        String key = generateKey(doctorID,date,timeSlot);
        return scheduleList.get(key);
    }
    public boolean updateAvailability(String doctorID, String date,String timeSlot, boolean available){
        Schedule schedule = getSchedule(doctorID,date,timeSlot);
        if(schedule != null){
            schedule.setStatus(available);
            return true;
        }
        return false;
    }
    public boolean removeSchedule(String doctorID, String date, String timeSlot){
        String key = generateKey(doctorID,date,timeSlot);
        return scheduleList.remove(key) != null;
    }
    public void listAllSchedules(){
        scheduleList.forEach((Schedule s)->{System.out.println(s);});
    }
    private String generateKey(String doctorID, String date, String timeSlot){
        return doctorID + "_" + date + "_" + timeSlot;
    }
    public int getScheduleCount(){
        return scheduleList.size();
    }
}
