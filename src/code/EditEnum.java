package code;

/**
 * edit enum
 */
public enum EditEnum {
    PENCIL("pen"),
    TEXT("text"),
    MODIFYDRAW("modify draw"),
    MODIFYTEXT("modify text");

    private final String mode;

    EditEnum(String mode) {
        this.mode = mode;
    }

    public String toString() {
        return mode;
    }
}
