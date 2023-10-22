package net.qf.impl.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.MobAnchorElement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPetEntity extends TameableEntity implements PolymerEntity {
    @NotNull
    public final ElementHolder holder;
    protected final EntityAttachment attachment;
    @NotNull
    protected final MobAnchorElement rideAnchor = new MobAnchorElement();
    public float deathAngle;
    public float previousSpeed = Float.MIN_NORMAL;
    public float previousLimbPos = Float.MIN_NORMAL;
    protected AbstractPetEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);

        this.holder = this.getHolder();
        rideAnchor.setHolder(this.holder);
        this.attachment = new EntityAttachment(this.holder, this, false);
    }
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new PetEscapeDanger(1.5));
        this.goalSelector.add(2, new PounceAtTargetGoal(this, this.getPounceVelocity()));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(4, new FollowOwnerGoal(this, 1, 5F, 2F, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));

        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this, new Class[0]));
    }



    @Override
    public boolean isInLove() {
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().getTime() % 2 == 1) {
            return;
        }
        this.holder.tick();
    }

    public abstract float getPounceVelocity();

    @Override
    public EntityView method_48926() {
        return super.getWorld();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    protected abstract ElementHolder getHolder();

    private class PetEscapeDanger extends EscapeDangerGoal {

        public PetEscapeDanger(double speed) {
            super(AbstractPetEntity.this, speed);
        }

        @Override
        protected boolean isInDanger() {
            return this.mob.shouldEscapePowderSnow() || this.mob.isOnFire();
        }
    }
}
