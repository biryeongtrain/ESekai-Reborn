package net.qf.mixin;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.qf.api.ESekaiDamageTag;
import net.qf.api.ESekaiSchool;
import net.qf.impl.ESekaiStatEntity;
import net.qf.impl.stat.ESekaiAttributeStat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.qf.impl.stat.ESekaiAttributeStat.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ESekaiStatEntity {

    @Shadow public abstract double getAttributeValue(EntityAttribute attribute);
    @Shadow public abstract AttributeContainer getAttributes();
    @Shadow public abstract EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Shadow public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    @Inject(method = "createLivingAttributes", at = @At("RETURN"), require = 1, allow = 1)
    private static void esekai$registerCustomAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        ESekaiAttributeStat.ATTRIBUTES.forEach(attribute -> {
            cir.getReturnValue().add(attribute);
        });
    }
    @Override
    public double esekai$getTotalArmorValue() {
        return this.getAttributeValue(ARMOR_ATTRIBUTE);
    }

    @Override
    public double esekai$getTotalDodgeValue() {
        return this.getAttributeValue(DODGE_ATTRIBUTE);
    }

    @Override
    public Object2DoubleMap<EntityAttributeModifier.Operation> esekai$getTotalDamageMultiplier(ESekaiDamageTag tag) {

        var instance = this.getAttributeInstance(tag.getIncreaseModifier());
        if (instance == null) {
            return Object2DoubleMaps.emptyMap();
        }
        return this.esekai$getValuesByInstance(instance);
    }

    @Override
    public Object2DoubleMap<EntityAttributeModifier.Operation> esekai$getTotalDamageMultiplier(ESekaiSchool school) {
        var instance = this.getAttributeInstance(school.getScalingAttribute());
        if (instance == null) {
            return Object2DoubleMaps.emptyMap();
        }
        return this.esekai$getValuesByInstance(instance);
    }

    @Unique
    private Object2DoubleMap<EntityAttributeModifier.Operation> esekai$getValuesByInstance(EntityAttributeInstance instance) {
        double additional = 0f;
        double multiplier = 0f;
        Object2DoubleMap<EntityAttributeModifier.Operation> map = new Object2DoubleOpenHashMap<>();
        for (EntityAttributeModifier modifier : instance.getModifiers()) {
            if (modifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE) {
                additional += modifier.getValue();
                continue;
            }
            if (modifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                multiplier += modifier.getValue();
            }
        }
        map.put(EntityAttributeModifier.Operation.MULTIPLY_BASE, additional);
        map.put(EntityAttributeModifier.Operation.MULTIPLY_TOTAL, multiplier);

        return map;
    }

    @Override
    public double esekai$getTotalResistance(ESekaiSchool school) {
        if (school == ESekaiSchool.PHYSICAL) {
            throw new UnsupportedOperationException();
        }
        return this.getAttributeValue(school.getDefendingAttribute());
    }

    @Override
    public double esekai$getCommonCriticalChance() {
        return this.getAttributeValue(COMMON_CRITICAL_CHANCE);
    }

    @Override
    public double esekai$getAttackCriticalChance() {
        return this.getAttributeValue(ATTACK_CRITICAL_CHANCE);
    }

    @Override
    public double esekai$getSpellCriticalChance() {
        return this.getAttributeValue(SPELL_CRITICAL_CHANCE);
    }

    @Override
    public double esekai$getCommonCriticalMultiplier() {
        return this.getAttributeValue(COMMON_CRITICAL_MULTIPLIER);
    }

    @Override
    public double esekai$getAttackCriticalMultiplier() {
        return this.getAttributeValue(ATTACK_CRITICAL_MULTIPLIER);
    }

    @Override
    public double esekai$getSpellCriticalMultiplier() {
        return this.getAttributeValue(SPELL_CRITICAL_MULTIPLIER);
    }
}
