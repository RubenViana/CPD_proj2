package utils;

public enum MessageType {
    MESSAGE("\\msg"),
    LOGIN("\\login"),
    REGISTER("\\register"),
    DISCONNECT("\\disconnect"),
    HELP("\\help");
    private String shortValue;

    MessageType(String shortValue) {
        this.shortValue = shortValue;
    }

    public String getShortValue() {
        return shortValue;
    }
}
