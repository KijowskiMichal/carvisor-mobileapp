package eu.michalkijowski.carvisor.data_models;

public class ReportAddDTO {
    private String description;
    private long end;
    private int[] listOfUserIds;
    private String name;
    private long start;
    private String type;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int[] getListOfUserIds() {
        return listOfUserIds;
    }

    public void setListOfUserIds(int[] listOfUserIds) {
        this.listOfUserIds = listOfUserIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}