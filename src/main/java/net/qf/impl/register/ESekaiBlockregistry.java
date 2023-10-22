package net.qf.impl.register;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.qf.ESekai;
import net.qf.impl.block.*;

public class ESekaiBlockregistry {
    public static final Block SKILL_CRAFTING_TABLE = Registry.register(
            Registries.BLOCK, ESekai.getId("skill_crafting_table"), new SkillCraftingTable(FabricBlockSettings.copy(Blocks.COBBLESTONE))
    );

    public static final BlockEntityType<SkillCraftingTableEntity> SKILL_CRAFTING_TABLE_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, ESekai.getId("skill_crafting_table_block_entity"),
            FabricBlockEntityTypeBuilder.create(SkillCraftingTableEntity::new, SKILL_CRAFTING_TABLE).build()
    );
    public static final Block TEST_FURNITURE = Registry.register(
            Registries.BLOCK, ESekai.getId("test_furniture"), new ChairBlock(AbstractBlock.Settings.create())
    );

    public static final Block ITEM_SHOWCASE_BLOCK = Registry.register(
            Registries.BLOCK, ESekai.getId("item_showcase"), new ItemShowcaseBlock(AbstractBlock.Settings.create().burnable().hardness(5F))
    );
    public static final BlockEntityType<ItemShowcaseBlockEntity> ITEM_SHOWCASE_BLOCK_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, ESekai.getId("item_showcase_block_entity"),
            FabricBlockEntityTypeBuilder.create(ItemShowcaseBlockEntity::new, ITEM_SHOWCASE_BLOCK).build()
    );
    public static void init() {
        PolymerBlockUtils.registerBlockEntity(SKILL_CRAFTING_TABLE_ENTITY_TYPE);
        PolymerBlockUtils.registerBlockEntity(ITEM_SHOWCASE_BLOCK_ENTITY_TYPE);
    }
}
