package util;

public enum MAP {
    SPAWN,
    BOSS,
    NORMAL,
    TREASURE,
    STAIR,
    VCORRIDOR,
    HCORRIDOR,
    EMPTY;

    public boolean isCorridor() {
        return this == VCORRIDOR || this == HCORRIDOR;
    }
}
