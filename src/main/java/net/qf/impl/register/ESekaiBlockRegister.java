package net.qf.impl.register;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.qf.ESekai;
import net.qf.impl.block.SkillCraftingTable;
import net.qf.impl.block.SkillCraftingTableEntity;

public class ESekaiBlockRegister {
    public static final Block SKILL_CRAFTING_TABLE = Registry.register(
            Registries.BLOCK, ESekai.getId("skill_crafting_table"), new SkillCraftingTable(FabricBlockSettings.copy(Blocks.COBBLESTONE))
    );

    public static final BlockEntityType<SkillCraftingTableEntity> SKILL_CRAFTING_TABLE_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, ESekai.getId("skill_crafting_table_block_entity"),
            FabricBlockEntityTypeBuilder.create(SkillCraftingTableEntity::new, SKILL_CRAFTING_TABLE).build()
    );
    public static void init() {
        PolymerBlockUtils.registerBlockEntity(SKILL_CRAFTING_TABLE_ENTITY_TYPE);
    }
}
