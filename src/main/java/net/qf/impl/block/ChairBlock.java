package net.qf.impl.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.qf.impl.entity.SittableEntity;
import net.qf.impl.register.ESekaiItemRegistry;
import org.jetbrains.annotations.Nullable;

import static net.qf.ESekai.getId;

public class ChairBlock extends Block implements PolymerBlock, BlockWithElementHolder {
    public static final BooleanProperty IS_SOMEONE_SIT = BooleanProperty.of("sittable");
    public ChairBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(IS_SOMEONE_SIT, false));
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.BARRIER;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(IS_SOMEONE_SIT);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var superResult = super.onUse(state, world, pos, player, hand, hit);

        if (world.isClient || state.get(IS_SOMEONE_SIT)|| superResult == ActionResult.FAIL) {
            return superResult;
        }

        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
        var holder = BlockBoundAttachment.get(world, pos).holder();

        var entity = spawnSeatEntity(world, pos, state);
        world.spawnEntity(entity);
        serverPlayerEntity.startRiding(entity);

        return superResult;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (placer instanceof ServerPlayerEntity player) {
            var hand = player.getMainHandStack().getItem().equals(ESekaiItemRegistry.TEST_FURNITURE_BlOCK_ITEM) ? Hand.MAIN_HAND : Hand.OFF_HAND;
            player.swingHand(hand, true);
        }
        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }



    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public static Entity spawnSeatEntity(World world, BlockPos pos, BlockState motherState) {
        Vec3d centerPos = pos.toCenterPos();
        return new SittableEntity(world, centerPos.getX(), centerPos.getY() - 0.2, centerPos.getZ());
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new Model(world, pos.toCenterPos());
    }


    public static final class Model extends ElementHolder {
        public static final ItemStack MODEL_ITEM = Items.PAPER.getDefaultStack();

        static {
            MODEL_ITEM.getOrCreateNbt().putInt(
                    "CustomModelData", PolymerResourcePackUtils.requestModel(MODEL_ITEM.getItem(), getId("block/chair")).value());
        }

        private final ItemDisplayElement element;
        public Model(ServerWorld world, Vec3d vec3d) {
            this.element = addElement(new ItemDisplayElement(MODEL_ITEM));
        }

        @Override
        protected void onTick() {
            super.onTick();
        }


    }
}
