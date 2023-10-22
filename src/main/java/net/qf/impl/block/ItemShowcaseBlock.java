package net.qf.impl.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
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
import net.qf.impl.register.ESekaiItemRegistry;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import static net.qf.ESekai.getId;

public class ItemShowcaseBlock extends Block implements PolymerBlock, BlockWithElementHolder, BlockEntityProvider {
    public static final BooleanProperty IS_OPENED = BooleanProperty.of("is_opened");

    public ItemShowcaseBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(IS_OPENED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(IS_OPENED);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.BARRIER;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (placer instanceof ServerPlayerEntity player) {
            var hand = player.getMainHandStack().getItem().equals(ESekaiItemRegistry.ITEM_SHOWCASE_ITEM) ? Hand.MAIN_HAND : Hand.OFF_HAND;
            player.swingHand(hand, true);
        }
        world.playSound(null, pos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        world.playSound(null, pos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);

        ItemShowcaseBlockEntity blockEntity = (ItemShowcaseBlockEntity) world.getBlockEntity(pos);

        var stack = blockEntity.getHoldingItem();

        if (!stack.isEmpty()) {
            player.dropStack(stack);
        }
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new Model(world, pos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ItemShowcaseBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var result = super.onUse(state, world, pos, player, hand, hit);
        if (hand != Hand.MAIN_HAND) {
            return result;
        }

        if (!state.get(IS_OPENED)) { // skip method call when showcase is closed
            return result;
        }

        ItemShowcaseBlockEntity blockEntity = (ItemShowcaseBlockEntity) world.getBlockEntity(pos);
        assert blockEntity != null;
        if (player.getStackInHand(hand).isEmpty()) {
            if (blockEntity.hasHoldingItem()) {
                player.setStackInHand(Hand.MAIN_HAND, blockEntity.extractHoldingItem());
                Model model = (Model) BlockBoundAttachment.get(world, pos).holder();
                model.removeElement(model.holdingItemElement);
                model.holdingItemElement = null;
            }
        } else {
            if (!blockEntity.hasHoldingItem()) {
                ItemStack itemStack = player.getStackInHand(hand).copy();
                blockEntity.insertHoldingItem(itemStack, player.getYaw());
                player.setStackInHand(hand, ItemStack.EMPTY);

                Model model = (Model) BlockBoundAttachment.get(world, pos).holder();
                model.holdingItemElement = new ItemDisplayElement(itemStack);
                model.setHoldingItemPosition();
                model.holdingItemElement.setYaw(blockEntity.itemYaw);
                model.addElement(model.holdingItemElement);
            }
        }

        return result;
    }

    @Override
    public BlockSoundGroup getSoundGroup(BlockState state) {
        return BlockSoundGroup.WOOD;
    }

    @Override
    public BlockState getPolymerBreakEventBlockState(BlockState state, ServerPlayerEntity player) {
        return Blocks.STRIPPED_OAK_LOG.getDefaultState();
    }



    public class Model extends ElementHolder {
        public static ItemStack FRAME = Items.LEATHER_HORSE_ARMOR.getDefaultStack();
        public ItemDisplayElement holdingItemElement;
        static {
            FRAME.getOrCreateNbt().putInt(
                    "CustomModelData", PolymerResourcePackUtils.requestModel(FRAME.getItem(), getId("block/item_showcase")).value()
            );
            FRAME.getOrCreateSubNbt("display").putInt("color", 5677120);
        }
        public final BlockDisplayElement GLASS_CASE;
        public final ItemDisplayElement FRAME_ITEM_ENTITY;
        public final InteractionElement GLASS_OPENER_INTERACTION;
        public Model(World world, BlockPos pos) {
            this.FRAME_ITEM_ENTITY = addElement(new ItemDisplayElement(FRAME));
            this.FRAME_ITEM_ENTITY.setShadowRadius(0);
            this.FRAME_ITEM_ENTITY.setShadowStrength(0);

            this.GLASS_CASE = new BlockDisplayElement(Blocks.GLASS.getDefaultState());
            this.GLASS_CASE.setOffset(new Vec3d(-0.375, 0.5f, -0.375));
            this.GLASS_CASE.setScale(new Vector3f(0.75f, 0.75f, 0.75f));
            this.addElement(this.GLASS_CASE);

            this.GLASS_OPENER_INTERACTION = this.addElement(
                    new InteractionElement(new GlassOpenerInteractionHandler(this, pos))
            );
            this.GLASS_OPENER_INTERACTION.setOffset(new Vec3d(0F, 0.5f, 0F));
            this.GLASS_OPENER_INTERACTION.setSize(1.0f, 0.75f);
        }

        public void setHoldingItemPosition() {
            if (this.holdingItemElement == null) {
                return;
            }
            this.holdingItemElement.setScale(new Vector3f(0.5f));
            this.holdingItemElement.setOffset(new Vec3d(0, 0.75f, 0));
        }

        @Override
        public boolean startWatching(ServerPlayNetworkHandler player) {
            if (this.getElements().contains(this.holdingItemElement)) {
                return super.startWatching(player);
            }

            ItemShowcaseBlockEntity blockEntity = (ItemShowcaseBlockEntity) player.getPlayer().getWorld().getBlockEntity(BlockBoundAttachment.get(this).getBlockPos());

            assert blockEntity != null;
            if (!blockEntity.getHoldingItem().isEmpty()) {
                this.holdingItemElement = new ItemDisplayElement(blockEntity.getHoldingItem());
                this.holdingItemElement.setYaw(blockEntity.itemYaw);
                addElement(this.holdingItemElement);
                this.setHoldingItemPosition();
                return super.startWatching(player);
            }

            return super.startWatching(player);
        }
    }

    public class GlassOpenerInteractionHandler implements VirtualElement.InteractionHandler {
        private final BlockPos pos;
        private final Model model;

        public GlassOpenerInteractionHandler(Model model, BlockPos pos) {
            this.pos = pos;
            this.model = model;
        }

        @Override
        public void interact(ServerPlayerEntity player, Hand hand) {
            var world = player.getWorld();
            if (world.isClient) {
                return;
            }

            var blockState = world.getBlockState(this.pos);

            boolean isOpened = blockState.get(IS_OPENED);

            if (isOpened) {
                this.model.addElement(this.model.GLASS_CASE);
                world.setBlockState(pos, blockState.with(IS_OPENED, false));
                world.playSound(null, this.pos, SoundEvents.BLOCK_GLASS_FALL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            } else {
                this.model.removeElement(this.model.GLASS_CASE);
                world.setBlockState(pos, blockState.with(IS_OPENED, true));
                world.playSound(null, this.pos, SoundEvents.BLOCK_GLASS_STEP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }
}
