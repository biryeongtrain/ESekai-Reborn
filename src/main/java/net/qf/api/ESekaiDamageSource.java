package net.qf.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.qf.impl.QfDamageSource;
import net.qf.mixin.accessors.DamageSourcesAccessor;

public class ESekaiDamageSource {
    public static DamageSource create(ESekaiSchool school, LivingEntity attacker) {
        if (attacker.isPlayer()) {
            return player(school, (PlayerEntity) attacker);
        } else {
            return mob(school, attacker);
        }
    }

    public static DamageSource mob(ESekaiSchool school, LivingEntity attacker) {
        return create(school, "mob", attacker);
    }

    public static DamageSource player(ESekaiSchool school, PlayerEntity attacker) {
        return create(school, "player", attacker);
    }

    private static DamageSource create(ESekaiSchool school, String name, Entity attacker) {
        var key = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, school.getDamageTypeId());
        var registry = ((DamageSourcesAccessor)attacker.getDamageSources()).getRegistry();
        return new QfDamageSource(registry.entryOf(key), attacker, school);
    }
}
