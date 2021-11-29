package eu.michalkijowski.carvisor.data_models;

public class MapWrapperDTO {
    private MapDeviceRowDTO[] startPoints;
    private MapRowDTO[] points;
    private MapDeviceRowDTO[] endPoints;

    public MapDeviceRowDTO[] getStartPoints() {
        return startPoints;
    }

    public void setStartPoints(MapDeviceRowDTO[] startPoints) {
        this.startPoints = startPoints;
    }

    public MapRowDTO[] getPoints() {
        return points;
    }

    public void setPoints(MapRowDTO[] points) {
        this.points = points;
    }

    public MapDeviceRowDTO[] getEndPoints() {
        return endPoints;
    }

    public void setEndPoints(MapDeviceRowDTO[] endPoints) {
        this.endPoints = endPoints;
    }
}