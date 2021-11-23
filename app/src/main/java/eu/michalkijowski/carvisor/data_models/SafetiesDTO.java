package eu.michalkijowski.carvisor.data_models;

public class SafetiesDTO {
    int page;
    int pageMax;
    SafetyDTO[] listOfUsers;

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

    public SafetyDTO[] getListOfEcos() {
        return listOfUsers;
    }

    public void setListOfEcos(SafetyDTO[] listOfEcos) {
        this.listOfUsers = listOfEcos;
    }
}