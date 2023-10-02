package net.qf.api;

import net.minecraft.text.Text;
import net.qf.ESekai;

public enum TriggerType {
    HIT, ATTACK, HEAL, DODGE, BLOCK, CAST, ERROR;

    public String asLowerCaseName() {
        return this.name().toLowerCase();
    }
    public Text getTranslation() {
        return Text.translatable(ESekai.getTranslation("skill.trigger." + this.asLowerCaseName()));
    }

    public static String asLowerCaseName(TriggerType type) {
        return type.asLowerCaseName();
    }
}
