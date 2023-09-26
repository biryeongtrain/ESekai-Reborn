package net.qf.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.qf.impl.QfDamageSource;
import net.qf.mixin.accessors.DamageSourcesAccessor;

import java.util.List;

public class ESekaiDamageSource {
    public static DamageSource create(ESekaiSchool school, LivingEntity attacker) {
        if (attacker.isPlayer()) {
            return player(school, (PlayerEntity) attacker, List.of(ESekaiDamageTag.ATTACK));
        } else {
            return mob(school, attacker, List.of(ESekaiDamageTag.ATTACK));
        }
    }

    public static DamageSource mob(ESekaiSchool school, LivingEntity attacker, List<ESekaiDamageTag> tags) {
        return create(school, "mob", attacker, tags);
    }

    public static DamageSource player(ESekaiSchool school, PlayerEntity attacker, List<ESekaiDamageTag> tags) {
        return create(school, "player", attacker, tags);
    }

    public static DamageSource create(ESekaiSchool school, String name, Entity attacker, List<ESekaiDamageTag> tags) {
        var key = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, school.getDamageTypeId());
        var registry = ((DamageSourcesAccessor)attacker.getDamageSources()).getRegistry();
        return new QfDamageSource(registry.entryOf(key), attacker, school);
    }
}
