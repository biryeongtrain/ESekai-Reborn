package net.qf.impl.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qf.impl.register.ESekaiBlockregistry;

public class SkillCraftingTableEntity extends BlockEntity {

    public SkillCraftingTableEntity(BlockPos pos, BlockState state) {
        super(ESekaiBlockregistry.SKILL_CRAFTING_TABLE_ENTITY_TYPE, pos, state);
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T blockWithEntity) {

    }
}
