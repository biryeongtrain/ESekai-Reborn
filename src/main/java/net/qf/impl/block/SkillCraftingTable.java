package net.qf.impl.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qf.impl.register.ESekaiBlockRegister;
import org.jetbrains.annotations.Nullable;

public class SkillCraftingTable extends BlockWithEntity implements PolymerBlock {
    public SkillCraftingTable(Settings settings) {
        super(settings);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return SkillCraftingTable.checkType(type, ESekaiBlockRegister.SKILL_CRAFTING_TABLE_ENTITY_TYPE, SkillCraftingTableEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SkillCraftingTableEntity(pos, state);
    }
}
