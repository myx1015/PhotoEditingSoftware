package code;

/**
 * Tag enum
 */
public enum TagEnum {
    PERSON ("Person"),
    VIEW ("View"),
    PLACES ("Places"),
    SCHOOL ("School"),
    ANIMAL ("Animal");

    private String name;

    TagEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
