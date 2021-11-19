package eu.michalkijowski.carvisor.data_models;

public class DevicesDTO {
    int page;
    int pageMax;
    DeviceDTO[] listOfDevices;

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

    public DeviceDTO[] getListOfDevices() {
        return listOfDevices;
    }

    public void setListOfDevices(DeviceDTO[] listOfDevices) {
        this.listOfDevices = listOfDevices;
    }
}