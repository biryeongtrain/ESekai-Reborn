package net.qf.api;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;

import static net.qf.impl.stat.ESekaiAttributeStat.*;

public enum ESekaiDamageTag {
    PROJECTILE(PROJECTILE_DAMAGE_ATTRIBUTE),
    MELEE(MELEE_DAMAGE_ATTRIBUTE),
    SPELL(SPELL_DAMAGE_ATTRIBUTE),
    ATTACK(ATTACK_DAMAGE_ATTRIBUTE),
    HEAL_CONVERT(HEAL_EFFICIENT_ATTRIBUTE);


    ESekaiDamageTag(EntityAttribute increaseModifier) {
        this.increaseModifier = increaseModifier;
    }

    private final EntityAttribute increaseModifier;

    public EntityAttribute getIncreaseModifier() {
        return increaseModifier;
    }
    public boolean hasCriticalModifier() {
        return this == ATTACK || this == SPELL;
    }
}
