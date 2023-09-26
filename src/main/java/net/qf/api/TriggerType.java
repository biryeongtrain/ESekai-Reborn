package net.qf.api;

public enum TriggerType {
    HIT, ATTACK, HEAL, DODGE, BLOCK, CAST, ERROR;

    public String asLowerCaseName() {
        return this.name().toLowerCase();
    }

    public static String asLowerCaseName(TriggerType type) {
        return type.asLowerCaseName();
    }
}
