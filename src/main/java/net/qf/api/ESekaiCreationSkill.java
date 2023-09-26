package net.qf.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.qf.impl.ESekaiSkillUser;

import java.util.List;

public record ESekaiCreationSkill(TriggerType type, SkillInfo info, float baseDamage, ESekaiSchool school, List<ESekaiDamageTag> tags, float cost, float cooldown) {
    public static final ESekaiCreationSkill EMPTY_SKILL = new ESekaiCreationSkill(TriggerType.ERROR, null, 0F, ESekaiSchool.PHYSICAL, List.of(), 100F, 0);
    public static Codec<ESekaiCreationSkill> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("type", "passive").forGetter(o -> TriggerType.asLowerCaseName(((ESekaiCreationSkill) o).type)),
                    SkillInfo.CODEC.optionalFieldOf("info", null).forGetter(ESekaiCreationSkill::info),
                    Codec.FLOAT.optionalFieldOf("base_damage", 0F).forGetter(ESekaiCreationSkill::baseDamage),
                    Codec.STRING.optionalFieldOf("school", "physical").forGetter(o -> ESekaiSchool.asLowerName(((ESekaiCreationSkill) o).school)),
                    Codec.list(Codec.STRING).optionalFieldOf("tags", List.of()).forGetter(o -> {
                        ESekaiCreationSkill skill = (ESekaiCreationSkill) o;
                        return skill.tags.stream().map(ESekaiDamageTag::asLowerName).toList();
                    }),
                    Codec.FLOAT.optionalFieldOf("cost", 100F).forGetter(ESekaiCreationSkill::cost),
                    Codec.FLOAT.optionalFieldOf("cooldown", 0F).forGetter(ESekaiCreationSkill::cooldown)
            ).apply(instance, (type, info, damage, school, tags, cost, cooldown) -> {
                var list = tags.stream().map(tag -> ESekaiDamageTag.valueOf(tag.toUpperCase())).toList();
                return new ESekaiCreationSkill(TriggerType.valueOf(type.toUpperCase()), info ,damage, ESekaiSchool.valueOf(school.toUpperCase()), list, cost, cooldown);
            })
    );

    /**
     * cast method will be executed by Skill Info. <p>
     * if <b>AOE and ONE_TARGET</b> damage, it will execute spell immediately. <p>
     * but, <b>PROJECTILE</b> spell will just spawn projectile Entity, and does not execute spell (like damage check, etc...). <br></br>
     * when projectile entity collisions, then its spell will be activated. but of course, spell cooldown is will be cycled when entity spawns
     * @param caster LivingEntity that the caster.
     */
    public void cast(LivingEntity caster) {
        ESekaiSkillUser user = (ESekaiSkillUser) caster;
        SkillInfo.Properties<LivingEntity> properties = this.info.getProperties(LivingEntity.class, caster);
        List<LivingEntity> targets = properties.search();

        if (targets.isEmpty()) {
            if (caster instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.literal("no entities found"));
            }
            return;
        }

        ESekaiDamageCalculator.Result result = new ESekaiDamageCalculator.Result(this.school, this.baseDamage);
        var value = result.randomValue(ESekaiDamageCalculator.getCombiner(caster, school, this.tags));
            targets.forEach(livingEntity -> {
                if (!livingEntity.isPartOfGame()) {
                    return;
                }
                if (this.school != ESekaiSchool.HEAL) {
                    livingEntity.damage(caster.isPlayer() ? ESekaiDamageSource.player(school, (PlayerEntity) caster, this.tags) : ESekaiDamageSource.mob(school, caster, this.tags), value);
                } else {
                    livingEntity.heal(value);
                    if (caster instanceof ServerPlayerEntity player) {
                        player.sendMessage(Text.literal("healed entity"));
                    }
                }
            });
        return;
    }

    
    private void createParticles() {

    }

    public static class Builder {
        public static ESekaiCreationSkill create() {
            return null;
        }
    }
}
