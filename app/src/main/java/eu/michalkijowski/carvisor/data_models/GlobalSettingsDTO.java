package eu.michalkijowski.carvisor.data_models;

public class GlobalSettingsDTO {
    int sendInterval;
    int getLocationInterval;
    int historyTimeout;

    public int getSendInterval() {
        return sendInterval;
    }

    public void setSendInterval(int sendInterval) {
        this.sendInterval = sendInterval;
    }

    public int getLocationInterval() {
        return getLocationInterval;
    }

    public void setLocationInterval(int locationInterval) {
        this.getLocationInterval = locationInterval;
    }

    public int getHistoryTimeout() {
        return historyTimeout;
    }

    public void setHistoryTimeout(int historyTimeout) {
        this.historyTimeout = historyTimeout;
    }
}