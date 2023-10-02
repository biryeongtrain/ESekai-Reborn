package net.qf.impl.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qf.impl.register.ESekaiBlockRegister;

public class SkillCraftingTableEntity extends BlockEntity {

    public SkillCraftingTableEntity(BlockPos pos, BlockState state) {
        super(ESekaiBlockRegister.SKILL_CRAFTING_TABLE_ENTITY_TYPE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockWithEntity blockWithEntity) {

    }
}
