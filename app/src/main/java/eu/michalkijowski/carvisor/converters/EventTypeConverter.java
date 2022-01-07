package eu.michalkijowski.carvisor.converters;

public class EventTypeConverter {
    public static String getTypeFromPosition(int position) {
        switch (position) {
            case 0:
                return "SERVICE";
            case 1:
                return "CLEANING";
            case 2:
                return "OTHER";
        }
        return null;
    }

    public static int getPositionFromType(String type) {
        switch (type) {
            case "SERVICE":
                return 0;
            case "CLEANING":
                return 1;
            case "OTHER":
                return 2;
        }
        return -1;
    }
}