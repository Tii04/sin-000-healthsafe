package co.wethinkcode.healthsafe;

import java.time.OffsetDateTime;

public class EquipmentFailure {
    private String wardId;
    private String equipment;
    private String status;
    private String failureType;
    private OffsetDateTime timestamp;

    public EquipmentFailure(){}
    
    public EquipmentFailure(String wardId, String equipment, String status, String failureType, OffsetDateTime timestamp){
        this.wardId = wardId;
        this.equipment = equipment;
        this.status = status;
        this.failureType = failureType;
        this.timestamp = timestamp;
    }

    public String getWardId() {
        return wardId;
    }
    public String getEquipment() {
        return equipment;
    }
    public String getStatus() {
        return status;
    }
    public String getFailureType() {
        return failureType;
    }
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    public void setWardId(String wardId) {
        this.wardId = wardId;
    }
    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }
    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
}
