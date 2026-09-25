package co.wethinkcode.healthsafe;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o){
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StaffingSchedule schedule = (StaffingSchedule) o;

        return wardId.equals(schedule.wardId) &&
            Objects.equals(department, schedule.department) &&
            alertLevel == schedule.alertLevel &&
            doctorsRequired == schedule.doctorsRequired;
    }

    @Override 
    public int hashCode(){
        return Objects.hash(wardId, department, alertLevel, doctorsRequired);
    }
}