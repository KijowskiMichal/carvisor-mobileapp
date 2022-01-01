package eu.michalkijowski.carvisor.data_models;

public class ErrorDTO {
    long date;
    String location;
    String type;
    String userName;
    String value;
    int userID;
    String deviceLicensePlate;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getDeviceLicensePlate() {
        return deviceLicensePlate;
    }

    public void setDeviceLicensePlate(String deviceLicensePlate) {
        this.deviceLicensePlate = deviceLicensePlate;
    }
}