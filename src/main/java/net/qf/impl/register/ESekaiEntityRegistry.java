package net.qf.impl.register;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.qf.impl.entity.PenguinPetEntity;
import net.qf.impl.entity.SittableEntity;

import static net.qf.ESekai.MOD_ID;

public class ESekaiEntityRegistry {
    public static EntityType<SittableEntity> SITTABLE_ENTITY = registerEntity(MOD_ID +":sit",
            EntityType.Builder.<SittableEntity>create(SittableEntity::new, SpawnGroup.MISC).setDimensions(0, 0)
                    .maxTrackingRange(10).disableSummon().makeFireImmune());
    public static EntityType<PenguinPetEntity> TEST_PET_ENTITY = registerEntity(MOD_ID + ":test_pet",
            EntityType.Builder.<PenguinPetEntity>create(PenguinPetEntity::new, SpawnGroup.MISC).setDimensions(0.7f, 0.7f)
    );

    private static <T extends Entity> EntityType<T> registerEntity(String id, EntityType.Builder<T> type) {
        var built = type.build(id);
        Registry.register(Registries.ENTITY_TYPE, id, built);
        PolymerEntityUtils.registerType(built);

        return built;
    }

    public static void init() {
        FabricDefaultAttributeRegistry.register(TEST_PET_ENTITY, PenguinPetEntity.getTestPetAttributes());
    }
}
