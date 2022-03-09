package code;

/**
 * view enum
 */
public enum ViewEnum {
    PHOTO ("photo"),
    GRID ("grid");

    private String modeString;

    ViewEnum(String mode) {
        this.modeString = mode;
    }

    public String toString() {
        return modeString;
    }
}
