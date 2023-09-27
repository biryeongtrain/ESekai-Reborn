package net.qf.mixin;

import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.qf.api.ESekaiDamageTag;
import net.qf.api.ESekaiSchool;
import net.qf.impl.ESekaiSkillUser;
import net.qf.api.ESekaiCreationSkill;
import net.qf.impl.ESekaiStatEntity;
import net.qf.api.TriggerType;
import net.qf.impl.stat.ESekaiAttributeStat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.qf.api.TriggerType.CAST;
import static net.qf.impl.stat.ESekaiAttributeStat.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ESekaiStatEntity, ESekaiSkillUser {
    @Unique
    private ESekaiCreationSkill ACTIVE_SKILL = null;
    @Unique
    private Object2IntMap<TriggerType> ESEKAI$COOLDOWN_MAP = new Object2IntOpenHashMap<>();

    @Shadow public abstract double getAttributeValue(EntityAttribute attribute);
    @Shadow public abstract EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Inject(method = "createLivingAttributes", at = @At("RETURN"), require = 1, allow = 1)
    private static void esekai$registerCustomAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        ESekaiAttributeStat.ATTRIBUTES.forEach(attribute -> {
            cir.getReturnValue().add(attribute);
        });
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void esekai$writeCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        if (ACTIVE_SKILL != null) {
            var encodeResult = ESekaiCreationSkill.CODEC.encodeStart(NbtOps.INSTANCE, this.ACTIVE_SKILL);
            nbt.put(ESekaiAttributeStat.NBT_ACTIVE_SKILL_NAME, encodeResult.getOrThrow(false, (x)  -> {}));
        }
        if (!ESEKAI$COOLDOWN_MAP.isEmpty()) {
            NbtCompound compound = new NbtCompound();
            ESEKAI$COOLDOWN_MAP.forEach((trigger, cooldown) -> {
                compound.putInt(trigger.asLowerCaseName(), cooldown);
            });

            nbt.put(ESekaiAttributeStat.NBT_COOLDOWNS_NAME, compound);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void esekai$readCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(NBT_ACTIVE_SKILL_NAME)) {
            this.ACTIVE_SKILL = ESekaiCreationSkill.CODEC.decode(NbtOps.INSTANCE, nbt.get(NBT_ACTIVE_SKILL_NAME)).getOrThrow(false, (x) -> {}).getFirst();
        }
        if (nbt.contains(NBT_COOLDOWNS_NAME)) {
            var compound = nbt.getCompound(NBT_COOLDOWNS_NAME);
            for (TriggerType value : TriggerType.values()) {
                if (compound.contains(value.asLowerCaseName())) {
                    this.ESEKAI$COOLDOWN_MAP.put(value, compound.getInt(value.asLowerCaseName()));
                }
            }
        }
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
        return MathHelper.clamp(this.getAttributeValue(school.getDefendingAttribute()), -1000F, 75F);
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

    @Override
    public double esekai$getReduecedDamage(ESekaiSchool school, double amount) {
        if (school == ESekaiSchool.PHYSICAL) {
            return this.esekai$getTotalArmorValue() / ((this.esekai$getTotalArmorValue() + 10) * amount);
        }
        return amount * this.esekai$getTotalResistance(school);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void esekai$tick(CallbackInfo ci) {
        esekai$reduceCooldown();
    }

    @Unique
    private void esekai$reduceCooldown() {
        for (Object2IntMap.Entry<TriggerType> triggerTypeEntry : ESEKAI$COOLDOWN_MAP.object2IntEntrySet()) {
            ESEKAI$COOLDOWN_MAP.computeIntIfPresent(triggerTypeEntry.getKey(), (type, value) -> {
                if (value > 0) {
                    return value - 1;
                }
                return value;
            });
        }
    }

    @Override
    public void setCooldown(TriggerType type, int tick) {
        this.ESEKAI$COOLDOWN_MAP.put(type, tick);
    }

    @Override
    public void setSkill(ESekaiCreationSkill skill) {
        if (skill.type() == CAST) {
            this.ACTIVE_SKILL = skill;
        }
    }

    @Override
    public boolean canCastSkill(TriggerType type) {
        return this.ESEKAI$COOLDOWN_MAP.getInt(type) <= 0;
    }

    @Override
    public ActionResult castActiveSkill() {
        if (this.canCastSkill(CAST)) {
            this.ACTIVE_SKILL.cast((LivingEntity) (Object) this);
            return ActionResult.success(false);
        }

        if (((LivingEntity) (Object) this) instanceof ServerPlayerEntity player) {
            player.sendMessage(Text.literal("cooldown. remaining time : " + this.ESEKAI$COOLDOWN_MAP.getInt(CAST)));
        }
        return ActionResult.FAIL;
    }
}
