package util;

public enum MAP {
    SPAWN,
    BOSS,
    TREASURE,
    INFESTED,
    CLEAR,
    VCORRIDOR,
    HCORRIDOR,
    EMPTY;

    public boolean isCorridor() {
        return this == VCORRIDOR || this == HCORRIDOR;
    }
}
