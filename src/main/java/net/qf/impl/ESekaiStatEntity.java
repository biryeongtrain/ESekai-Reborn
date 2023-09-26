package net.qf.impl;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.qf.api.ESekaiDamageTag;
import net.qf.api.ESekaiSchool;

public interface ESekaiStatEntity {
    double esekai$getTotalArmorValue();
    double esekai$getTotalDodgeValue();
    Object2DoubleMap<EntityAttributeModifier.Operation> esekai$getTotalDamageMultiplier(ESekaiDamageTag tag);
    Object2DoubleMap<EntityAttributeModifier.Operation> esekai$getTotalDamageMultiplier(ESekaiSchool school);
    double esekai$getTotalResistance(ESekaiSchool school);
    double esekai$getCommonCriticalChance();
    double esekai$getAttackCriticalChance();
    double esekai$getSpellCriticalChance();
    double esekai$getCommonCriticalMultiplier();
    double esekai$getAttackCriticalMultiplier();
    double esekai$getSpellCriticalMultiplier();
    double esekai$getReduecedDamage(ESekaiSchool school, double amount);
}
