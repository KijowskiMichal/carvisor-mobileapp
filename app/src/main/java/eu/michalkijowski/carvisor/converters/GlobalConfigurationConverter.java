package eu.michalkijowski.carvisor.converters;

public class GlobalConfigurationConverter {
    public static String getStringFromHistoryInterval(int historyInterval) {
        switch (historyInterval) {
            case 10:
                return "10 Dni";
            case 15:
                return "15 Dni";
            case 30:
                return "Miesiąc";
            case 90:
                return "3 miesiące";
            case 180:
                return "6 miesięcy";
            default:
                return "Bez ograniczeń";
        }
    }
    public static int getIntFromHistoryInterval(String historyInterval) {
        switch (historyInterval) {
            case "10 Dni":
                return 10;
            case "15 Dni":
                return 15;
            case "Miesiąc":
                return 30;
            case "3 miesiące":
                return 90;
            case "6 miesięcy":
                return 180;
            default:
                return 0;
        }
    }
    public static String getStringFromLocationInterval(int location) {
        switch (location) {
            case 5:
                return "5 sekund";
            case 10:
                return "10 sekund";
            case 15:
                return "15 sekund";
            default:
                return "Automatycznie";
        }
    }
    public static int getIntFromLocationInterval(String location) {
        switch (location) {
            case "5 sekund":
                return 5;
            case "10 sekund":
                return 10;
            case "15 sekund":
                return 15;
            default:
                return 0;
        }
    }
    public static String getStringFromSendInterval(int sendInterval) {
        switch (sendInterval) {
            case 15:
                return "15 sekund";
            case 30:
                return "30 sekund";
            case 60:
                return "1 minuta";
            case 180:
                return "3 minuty";
            case 300:
                return "5 minut";
            default:
                return "Automatycznie";
        }
    }
    public static int getIntFromSendInterval(String sendInterval) {
        switch (sendInterval) {
            case "15 sekund":
                return 15;
            case "30 sekund":
                return 30;
            case "1 minuta":
                return 60;
            case "3 minuty":
                return 180;
            case "5 minut":
                return 300;
            default:
                return 0;
        }
    }
}