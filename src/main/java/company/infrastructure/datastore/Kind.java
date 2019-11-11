package company.infrastructure.datastore;

public enum Kind {

    USER("User");

    private final String kindName;

    Kind(String kindName) {
        this.kindName = kindName;
    }

    public static Kind fromKindName(String kindName) {
        for (Kind kind : values()) {
            if (kind.kindName.equals(kindName)) {
                return kind;
            }
        }
        throw new IllegalArgumentException("Kind not found for " + kindName);
    }

    public String getKindName() {
        return kindName;
    }
}
