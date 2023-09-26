package net.qf.impl.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class EntityChecker {
    public static boolean isTamed(LivingEntity entity) {
        if (!(entity instanceof TameableEntity tameableEntity)) {
            return false;
        }

        return tameableEntity.isTamed();
    }
}
