package net.qf.api;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.qf.ESekai;

import static net.qf.impl.stat.ESekaiAttributeStat.*;

public enum ESekaiDamageTag {
    PROJECTILE(PROJECTILE_DAMAGE_ATTRIBUTE),
    MELEE(MELEE_DAMAGE_ATTRIBUTE),
    SPELL(SPELL_DAMAGE_ATTRIBUTE),
    ATTACK(ATTACK_DAMAGE_ATTRIBUTE),
    HEAL_CONVERT(HEAL_EFFICIENT_ATTRIBUTE),
    HEAL(HEAL_EFFICIENT_ATTRIBUTE);


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

    public String asLowerName() {
        return this.name().toLowerCase();
    }

    public MutableText getTranslationText() {
        return Text.translatable(ESekai.getTranslation("skill.tag." + asLowerName()));
    }
}
