package eu.michalkijowski.carvisor.data_models;

public class EcopointsDTO {
    int page;
    int pageMax;
    EcopointDTO[] listOfUsers;

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

    public EcopointDTO[] getListOfEcos() {
        return listOfUsers;
    }

    public void setListOfEcos(EcopointDTO[] listOfEcos) {
        this.listOfUsers = listOfEcos;
    }
}