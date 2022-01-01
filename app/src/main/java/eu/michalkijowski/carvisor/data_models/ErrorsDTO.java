package eu.michalkijowski.carvisor.data_models;

public class ErrorsDTO {
    int page;
    int pageMax;
    ErrorDTO[] listOfNotification;

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

    public ErrorDTO[] getListOfErrors() {
        return listOfNotification;
    }

    public void setListOfErrors(ErrorDTO[] listOfErrors) {
        this.listOfNotification = listOfErrors;
    }
}