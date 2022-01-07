package eu.michalkijowski.carvisor.data_models;

public class OffenceDayDTO {
    int date;
    String locationFrom;
    String locationTo;
    float safetyPoints;
    float ecoPoints;
    OffenceDTO[] listOfOffencess;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getLocationFrom() {
        return locationFrom;
    }

    public void setLocationFrom(String locationFrom) {
        this.locationFrom = locationFrom;
    }

    public String getLocationTo() {
        return locationTo;
    }

    public void setLocationTo(String locationTo) {
        this.locationTo = locationTo;
    }

    public float getSafetyPoints() {
        return safetyPoints;
    }

    public void setSafetyPoints(float safetyPoints) {
        this.safetyPoints = safetyPoints;
    }

    public float getEcoPoints() {
        return ecoPoints;
    }

    public void setEcoPoints(float ecoPoints) {
        this.ecoPoints = ecoPoints;
    }

    public OffenceDTO[] getListOfOffencess() {
        return listOfOffencess;
    }

    public void setListOfOffencess(OffenceDTO[] listOfOffencess) {
        this.listOfOffencess = listOfOffencess;
    }
}