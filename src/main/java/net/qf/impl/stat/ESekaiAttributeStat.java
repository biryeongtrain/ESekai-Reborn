package net.qf.impl.stat;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.qf.ESekai.getId;

public class ESekaiAttributeStat {
    public static final EntityAttribute ARMOR_ATTRIBUTE = registerAttribute(getId("generic_armor"), "attribute.generic.name.armor");
    public static final EntityAttribute DODGE_ATTRIBUTE = registerAttribute(getId("generic_dodge"), "attribute.generic.name.dodge");
    public static final EntityAttribute HEAL_EFFICIENT_ATTRIBUTE = registerAttribute(getId("generic_heal_efficient"), "attribute.generic.name.heal_efficient");
    public static final EntityAttribute FIRE_DAMAGE_ATTRIBUTE = registerAttribute(getId("generic_fire_damage"), "attribute.generic.name.fire_damage");
    public static final EntityAttribute FROST_DAMAGE_ATTRIBUTE = registerAttribute(getId("generic_frost_damage"), "attribute.generic.name.frost_damage");
    public static final EntityAttribute LIGHTNING_DAMAGE_ATTRIBUTE = registerAttribute(getId("generic_lightning_damage"), "attribute.generic.name.lightning_damage");
    public static final EntityAttribute CHAOS_DAMAGE_ATTRIBUTE = registerAttribute(getId("generic_chaos_damage"), "attribute.generic.name.chaos_damage");
    public static final EntityAttribute PHYSICAL_DAMAGE_ATTRIBUTE = registerAttribute(getId("generic_physical_damage"), "attribute.generic.name.physical_damage");
    public static final EntityAttribute FIRE_RESISTANCE = registerAttribute(getId("generic_fire_resistance"), "attribute.generic.name.fire_resistance", 0, -1000, 75);
    public static final EntityAttribute FROST_RESISTANCE = registerAttribute(getId("generic_frost_resistance"), "attribute.generic.name.frost_resistance", 0, -1000, 75);
    public static final EntityAttribute LIGHTNING_RESISTANCE = registerAttribute(getId("generic_lightning_resistance"), "attribute.generic.name.lightning_resistance", 0, -1000, 75);
    public static final EntityAttribute CHAOS_RESISTANCE = registerAttribute(getId("generic_chaos_resistance"), "attribute.generic.name.chaos_resistance", 0, -1000, 75);
    public static final EntityAttribute SPELL_DAMAGE_ATTRIBUTE = registerAttribute(getId("generic_magic_damage"), "attribute.generic.name.magic_damage");
    public static final EntityAttribute ATTACK_DAMAGE_ATTRIBUTE = registerAttribute(getId("generic_attack_damage"), "attribute.generic.name.esekai_attack_damage");
    public static final EntityAttribute PROJECTILE_DAMAGE_ATTRIBUTE = registerAttribute(getId("generic_projectile_damage"), "attribute.generic.name.projectile_damage");
    public static final EntityAttribute MELEE_DAMAGE_ATTRIBUTE = registerAttribute(getId("generic_melee_damage"), "attribute.generic.name.melee_damage");

    // Critical Modifiers
    public static final EntityAttribute SPELL_CRITICAL_CHANCE = registerAttribute(getId("generic_spell_critical_chance"), "attribute.generic.name.spell_critical_chance", 0, -100, 100);
    public static final EntityAttribute SPELL_CRITICAL_MULTIPLIER = registerAttribute(getId("generic_spell_critical_multiplier"), "attribute.generic.name.spell_critical_multiplier", 0, -1, 100);
    public static final EntityAttribute ATTACK_CRITICAL_CHANCE = registerAttribute(getId("generic_attack_critical_chance"), "attribute.generic.name.attack_critical_chance", 0, -100, 100);
    public static final EntityAttribute ATTACK_CRITICAL_MULTIPLIER = registerAttribute(getId("generic_attack_critical_multiplier"), "attribute.generic.name.attack_critical_multiplier", 0, -1, 1000);
    public static final EntityAttribute COMMON_CRITICAL_CHANCE = registerAttribute(getId("generic_common_critical_chance"), "attribute.generic.name.common_critical_chance", 5, -100, 100);
    public static final EntityAttribute COMMON_CRITICAL_MULTIPLIER = registerAttribute(getId("generic_common_critical_multiplier"), "attribute.generic.name.common_critical_multiplier", 1, -1, 100);

    // NBT ID
    public static final String NBT_ACTIVE_SKILL_NAME = "esekai_active_skill";
    public static final String NBT_COOLDOWNS_NAME = "esekai_cooldowns";

    public static final ObjectSet<EntityAttribute> ATTRIBUTES = new ObjectOpenHashSet<>();
    private static EntityAttribute registerAttribute(Identifier id, String translationKey) {
        return registerAttribute(id, translationKey, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static EntityAttribute registerAttribute(Identifier id, String translationKey, int defaultValue, int minValue, int maxValue) {
        return Registry.register(Registries.ATTRIBUTE, id, new ClampedEntityAttribute(translationKey, defaultValue, minValue, maxValue));
    }

    private static void put(EntityAttribute a) {
        ATTRIBUTES.add(a);
    }

    static {
        put(ARMOR_ATTRIBUTE);
        put(DODGE_ATTRIBUTE);
        put(HEAL_EFFICIENT_ATTRIBUTE);
        put(FIRE_DAMAGE_ATTRIBUTE);
        put(FROST_DAMAGE_ATTRIBUTE);
        put(LIGHTNING_DAMAGE_ATTRIBUTE);
        put(CHAOS_DAMAGE_ATTRIBUTE);
        put(PHYSICAL_DAMAGE_ATTRIBUTE);
        put(FIRE_RESISTANCE);
        put(FROST_RESISTANCE);
        put(LIGHTNING_RESISTANCE);
        put(CHAOS_RESISTANCE);
        put(SPELL_DAMAGE_ATTRIBUTE);
        put(MELEE_DAMAGE_ATTRIBUTE);
        put(PROJECTILE_DAMAGE_ATTRIBUTE);
        put(ATTACK_DAMAGE_ATTRIBUTE);
        put(SPELL_CRITICAL_CHANCE);
        put(SPELL_CRITICAL_MULTIPLIER);
        put(ATTACK_CRITICAL_CHANCE);
        put(ATTACK_CRITICAL_MULTIPLIER);
        put(COMMON_CRITICAL_CHANCE);
        put(COMMON_CRITICAL_MULTIPLIER);
    }
}
