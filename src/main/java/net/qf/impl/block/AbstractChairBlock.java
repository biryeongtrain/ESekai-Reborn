package net.qf.impl.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.qf.impl.entity.SittableEntity;
import net.qf.impl.register.ESekaiItemRegistry;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractChairBlock extends Block implements PolymerBlock {

    public AbstractChairBlock(Settings settings) {
        super(settings);
    }

    public abstract SoundEvent getPlaceSound();

    public abstract SoundEvent getBreakSound();

    /**
     * 해당 메소드는 블럭이 아닌 Polymer Block Item 으로 설치 시
     * 사운드와 Swing Hand 이벤트가 작동하지 않아 대안으로 작성한 로직입니다.
     */
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (placer instanceof ServerPlayerEntity player) {
            var hand = player.getMainHandStack().getItem().equals(ESekaiItemRegistry.TEST_FURNITURE_BlOCK_ITEM) ? Hand.MAIN_HAND : Hand.OFF_HAND;
            player.swingHand(hand, true);
        }
        world.playSound(null, pos, this.getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        world.playSound(null, pos, this.getBreakSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var superResult = super.onUse(state, world, pos, player, hand, hit);

        if (world.isClient || superResult == ActionResult.FAIL) {
            return superResult;
        }

        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
        var holder = BlockBoundAttachment.get(world, pos).holder();

        var entity = spawnSeatEntity(world, pos, state);
        world.spawnEntity(entity);
        serverPlayerEntity.startRiding(entity);

        return superResult;
    }

    public Entity spawnSeatEntity(World world, BlockPos pos, BlockState motherState) {
        Vec3d centerPos = pos.toCenterPos();
        return new SittableEntity(world, centerPos.getX(), centerPos.getY() - this.getYCorrectionValue(), centerPos.getZ());
    }

    /**
     * Y 좌표는 블록의 정 중앙부분이기때문에, 0.5 의 y값을 가집니다. (반블록의 맨 윗 좌표)
     * 즉 이를 생각해서 보정값을 입력해 주세요.
     * @return 엔티티 스폰 보정값
     */
    public abstract float getYCorrectionValue();
}
