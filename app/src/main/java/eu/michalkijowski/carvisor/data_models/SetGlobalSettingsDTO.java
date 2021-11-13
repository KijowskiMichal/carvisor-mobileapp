package eu.michalkijowski.carvisor.data_models;

public class SetGlobalSettingsDTO {
    String sendInterval;
    String getLocationInterval;
    String historyTimeout;

    public String getSendInterval() {
        return sendInterval;
    }

    public void setSendInterval(String sendInterval) {
        this.sendInterval = sendInterval;
    }

    public String getGetLocationInterval() {
        return getLocationInterval;
    }

    public void setGetLocationInterval(String getLocationInterval) {
        this.getLocationInterval = getLocationInterval;
    }

    public String getHistoryTimeout() {
        return historyTimeout;
    }

    public void setHistoryTimeout(String historyTimeout) {
        this.historyTimeout = historyTimeout;
    }
}