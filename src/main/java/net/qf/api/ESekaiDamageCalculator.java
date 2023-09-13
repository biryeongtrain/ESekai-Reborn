package net.qf.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.qf.impl.ESekaiStatEntity;

import java.security.SecureRandom;
import java.util.List;

public class ESekaiDamageCalculator {
    private static final SecureRandom rand = new SecureRandom();
    public record Result(ESekaiSchool school, double baseValue) {
        private enum CriticalType {
            NO_CRITICAL, CAN_OCCUR, MUST
        }

        public double randomValue() {
            return value(CriticalType.CAN_OCCUR, MultiplierCombiner.ZERO);
        }

        public double randomValue(MultiplierCombiner combiner) {
            return value(CriticalType.CAN_OCCUR, combiner);
        }

        public double nonCriticalValue() {
            return value(CriticalType.NO_CRITICAL, MultiplierCombiner.ZERO);
        }

        public double nonCriticalValue(MultiplierCombiner combiner) {
            return value(CriticalType.NO_CRITICAL, combiner);
        }

        public double forceCriticalValue() {
            return value(CriticalType.MUST, MultiplierCombiner.ZERO);
        }

        public double forceCriticalValue(MultiplierCombiner combiner) {
            return value(CriticalType.NO_CRITICAL, combiner);
        }
        private double value(CriticalType type, MultiplierCombiner combiner) {
            var base = baseValue * (combiner.additional) * (combiner.multiplier);
            if (type != CriticalType.NO_CRITICAL) {
                float funNum = rand.nextFloat() * 100;
                boolean isCritical = funNum < (combiner.criticalChance);

                if (isCritical) {
                    base *= combiner.criticalChanceMultiplier;
                }
            }

            return base;
        }
    }
    public static MultiplierCombiner getCombiner(LivingEntity attacker, ESekaiSchool school, List<ESekaiDamageTag> tags) {
        double additional = 1f;
        double multiplier = 1f;
        ESekaiStatEntity attackerStat = (ESekaiStatEntity) attacker;
        double criticalChance = attackerStat.esekai$getCommonCriticalChance();
        double criticalMultiplier = attackerStat.esekai$getCommonCriticalMultiplier();

        for (ESekaiDamageTag tag : tags) {
            var map = attackerStat.esekai$getTotalDamageMultiplier(tag);
            additional += map.getOrDefault(EntityAttributeModifier.Operation.MULTIPLY_BASE, 0);
            multiplier += map.getOrDefault(EntityAttributeModifier.Operation.MULTIPLY_TOTAL, 0);

            if (tag == ESekaiDamageTag.ATTACK) {
                criticalChance += attackerStat.esekai$getAttackCriticalChance();
                criticalMultiplier += attackerStat.esekai$getAttackCriticalMultiplier();
            }

            if (tag == ESekaiDamageTag.SPELL) {
                criticalChance += attackerStat.esekai$getSpellCriticalChance();
                criticalMultiplier += attackerStat.esekai$getSpellCriticalMultiplier();
            }
        }

        var schoolMap = attackerStat.esekai$getTotalDamageMultiplier(school);
        additional += schoolMap.getOrDefault(EntityAttributeModifier.Operation.MULTIPLY_BASE, 0);
        multiplier += schoolMap.getOrDefault(EntityAttributeModifier.Operation.MULTIPLY_TOTAL, 0);

        return new MultiplierCombiner(additional, multiplier, criticalChance, criticalMultiplier);
    }
    public record MultiplierCombiner(double additional, double multiplier,double criticalChance, double criticalChanceMultiplier) {
        public static final MultiplierCombiner ZERO = new MultiplierCombiner(0, 0, 0, 0);
    }
}
