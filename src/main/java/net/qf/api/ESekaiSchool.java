package net.qf.api;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;

import java.util.Locale;

import static net.qf.ESekai.MOD_ID;
import static net.qf.ESekai.getId;
import static net.qf.impl.stat.ESekaiAttributeStat.*;

public enum ESekaiSchool {
    FIRE(FIRE_DAMAGE_ATTRIBUTE, 0xE85238, "☀", FIRE_RESISTANCE),
    FROST(FROST_DAMAGE_ATTRIBUTE, 0x32C7F0, "❉", FROST_RESISTANCE),
    LIGHTNING(LIGHTNING_DAMAGE_ATTRIBUTE, 0xF5F064, "⚜", LIGHTNING_RESISTANCE),
    CHAOS(CHAOS_DAMAGE_ATTRIBUTE, 0x27AD24,"✿", CHAOS_RESISTANCE),
    HEAL(HEAL_EFFICIENT_ATTRIBUTE, 0xF294E9, "❤", null),
    PHYSICAL(PHYSICAL_DAMAGE_ATTRIBUTE, 0xffffff, "✦", ARMOR_ATTRIBUTE);

    ESekaiSchool(EntityAttribute scalingAttribute, int color, String icon, EntityAttribute defendingAttribute) {
        this.scalingAttribute = scalingAttribute;
        this.color = color;
        this.icon = icon;
        this.defendingAttribute = defendingAttribute;
        this.damageTypeId = getId(MOD_ID + "_" + this.name().toLowerCase(Locale.ROOT));
    }

    private final EntityAttribute scalingAttribute;
    private final EntityAttribute defendingAttribute;
    private final int color;
    private final String icon;
    private final Identifier damageTypeId;

    public EntityAttribute getScalingAttribute() {
        return scalingAttribute;
    }

    public int getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }

    public Identifier getDamageTypeId() {
        return damageTypeId;
    }

    public EntityAttribute getDefendingAttribute() {
        return defendingAttribute;
    }

    public String getTranslationText() {
        return MOD_ID + "." + "element." + this.name().toLowerCase();
    }
    public static String asLowerName(ESekaiSchool school) {
        return school.asLowerName();
    }

    public String asLowerName() {
        return this.name().toLowerCase();
    }
}
