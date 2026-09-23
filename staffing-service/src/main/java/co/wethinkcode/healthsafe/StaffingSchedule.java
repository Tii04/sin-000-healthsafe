package co.wethinkcode.healthsafe;

public class StaffingSchedule{
    private String wardId;
    private String department;
    private int alertLevel;
    private int doctorsRequired;

    public StaffingSchedule(){}

    public StaffingSchedule(String wardId, String department, int alertLevel, int doctorsRequired){
        this.alertLevel = alertLevel;
        this.department = department;
        this.wardId = wardId;
        this.doctorsRequired = doctorsRequired;
    }

    public String getWardId() {
        return wardId;
    }

    public String getDepartment() {
        return department;
    }

    public int getAlertLevel() {
        return alertLevel;
    }

    public int getDoctorsRequired() {
        return doctorsRequired;
    }
}