package eu.michalkijowski.carvisor.data_models;

public class SummaryDTO {
    int page;
    int pageMax;
    String name;
    float safetyPoints;
    float ecoPoints;
    int safetyRankingPosition;
    int ecoRankingPosition;
    OffenceDayDTO[] listOfTracks;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageMax() {
        return pageMax;
    }

    public void setPageMax(int pageMax) {
        this.pageMax = pageMax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getSafetyRankingPosition() {
        return safetyRankingPosition;
    }

    public void setSafetyRankingPosition(int safetyRankingPosition) {
        this.safetyRankingPosition = safetyRankingPosition;
    }

    public int getEcoRankingPosition() {
        return ecoRankingPosition;
    }

    public void setEcoRankingPosition(int ecoRankingPosition) {
        this.ecoRankingPosition = ecoRankingPosition;
    }

    public OffenceDayDTO[] getListOfTracks() {
        return listOfTracks;
    }

    public void setListOfTracks(OffenceDayDTO[] listOfTracks) {
        this.listOfTracks = listOfTracks;
    }
}