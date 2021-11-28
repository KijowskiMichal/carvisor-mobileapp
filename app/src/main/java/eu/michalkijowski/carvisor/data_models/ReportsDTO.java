package eu.michalkijowski.carvisor.data_models;

public class ReportsDTO {
    int page;
    int pageMax;
    ReportDTO[] listOfRaports;

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

    public ReportDTO[] getListOfRaports() {
        return listOfRaports;
    }

    public void setListOfRaports(ReportDTO[] listOfRaports) {
        this.listOfRaports = listOfRaports;
    }
}