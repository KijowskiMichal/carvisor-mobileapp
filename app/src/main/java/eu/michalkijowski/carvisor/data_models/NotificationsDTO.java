package eu.michalkijowski.carvisor.data_models;

public class NotificationsDTO {
    int page;
    int pageMax;
    NotificationDTO[] listOfNotification;

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

    public NotificationDTO[] getListOfNotification() {
        return listOfNotification;
    }

    public void setListOfNotification(NotificationDTO[] listOfNotification) {
        this.listOfNotification = listOfNotification;
    }
}