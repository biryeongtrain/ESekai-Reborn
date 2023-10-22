package net.qf.impl.entity;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.EntityTrackedData;
import eu.pb4.polymer.virtualentity.mixin.accessors.EntityAccessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.qf.ESekai;
import net.qf.impl.entity.anim.AnimationLoader;
import net.qf.impl.register.ESekaiEntityRegistry;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.item.Items.*;

public class PenguinPetEntity extends AbstractPetEntity {
    private final InteractionElement hitbox = InteractionElement.redirect(this);
    public int penguinAnimateAge = 0;
    public boolean alreadyStopped = false;
    public boolean isGlowing = true;
    public boolean isStunned = false;
    public int stunnedTick = 0;
    public PenguinPetEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        PenguinModel model = (PenguinModel) this.holder;
        this.hitbox.setSize(0.7f, 1.15f);
        VirtualEntityUtils.addVirtualPassenger(this, model.BODY.getEntityId(), model.HEAD.getEntityId(), model.LEFT_FEET.getEntityId(), model.RIGHT_FEET.getEntityId(), model.LEFT_WING.getEntityId(), model.RIGHT_WING.getEntityId(), this.hitbox.getEntityId());

        this.holder.addElement(hitbox);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(7, new LookAtEntityGoal(this, PenguinPetEntity.class, 5F));
    }

    public PenguinPetEntity(World world, double x, double y, double z) {
        this(ESekaiEntityRegistry.TEST_PET_ENTITY, world);

        this.setPosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        this.setOwner(player);
        return super.interactAt(player, hitPos, hand);
    }

    public static DefaultAttributeContainer.Builder getTestPetAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5F)
                ;
    }

    @Override
    public boolean hasStatusEffect(StatusEffect effect) {
        if (effect.equals(StatusEffects.DOLPHINS_GRACE)) {
            return true;
        }
        return super.hasStatusEffect(effect);
    }

    @Override
    public float getPounceVelocity() {
        return 0.4f;
    }

    @Override
    protected ElementHolder getHolder() {
        return new PenguinModel();
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        data.add(DataTracker.SerializedEntry.of(
                EntityTrackedData.FLAGS, (byte) (1 << EntityTrackedData.INVISIBLE_FLAG_INDEX))
        );
        data.add(new DataTracker.SerializedEntry(
                EntityAccessor.getNO_GRAVITY().getId(), EntityAccessor.getNO_GRAVITY().getType(), true)
        );
        data.add(DataTracker.SerializedEntry.of(
                ArmorStandEntity.ARMOR_STAND_FLAGS, (byte) (ArmorStandEntity.SMALL_FLAG | ArmorStandEntity.MARKER_FLAG))
        );

    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void onEntityPacketSent(Consumer<Packet<?>> consumer, Packet<?> packet) {
        if (packet instanceof EntityPassengersSetS2CPacket passengersSetS2CPacket) {
            PenguinModel model = (PenguinModel) this.holder;
            IntList list = new IntArrayList(passengersSetS2CPacket.getPassengerIds());
            list.addAll(IntList.of(model.BODY.getEntityId(), model.HEAD.getEntityId(),
                    model.LEFT_FEET.getEntityId(), model.RIGHT_FEET.getEntityId(), model.RIGHT_WING.getEntityId(),
                    model.LEFT_WING.getEntityId()
            ));
            consumer.accept(VirtualEntityUtils.createRidePacket(this.rideAnchor.getEntityId(), list));
            return;
        }

        consumer.accept(packet);
    }

    @Override
    public void tick() {
        super.tick();
        PenguinModel model = (PenguinModel)this.holder;
        model.animateModel(this, this.penguinAnimateAge);

        if (this.hasStatusEffect(StatusEffects.GLOWING)) {
            if (!isGlowing) {
                model.HEAD.setGlowing(true);
                model.BODY.setGlowing(true);
                model.LEFT_WING.setGlowing(true);
                model.RIGHT_WING.setGlowing(true);
                model.LEFT_FEET.setGlowing(true);
                model.RIGHT_FEET.setGlowing(true);

                this.isGlowing = true;
            }

        } else {
            if (isGlowing) {
                model.HEAD.setGlowing(false);
                model.BODY.setGlowing(false);
                model.LEFT_WING.setGlowing(false);
                model.RIGHT_WING.setGlowing(false);
                model.LEFT_FEET.setGlowing(false);
                model.RIGHT_FEET.setGlowing(false);

                this.isGlowing = false;
            }
        }

        if (this.isStunned) {
            if (stunnedTick == 100) {
                stunnedTick = 0;
                this.isStunned = false;
            }
        }
        this.penguinAnimateAge = ++this.penguinAnimateAge % 25;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 20;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        super.onPlayerCollision(player);


    }

    public class PenguinModel extends ElementHolder {
        private static final String CUSTOM_MODEL_DATA = "CustomModelData";
        public static final ItemStack BODY_STACK = new ItemStack(BLACK_DYE);
        public static final ItemStack HEAD_STACK = new ItemStack(WHITE_DYE);
        public static final ItemStack LEFT_WING_STACK = new ItemStack(YELLOW_DYE);
        public static final ItemStack RIGHT_WING_STACK = new ItemStack(GREEN_DYE);
        public static final ItemStack LEFT_FEET_STACK = new ItemStack(ORANGE_DYE);
        public static final ItemStack RIGHT_FEET_STACK = new ItemStack(CYAN_DYE);

        static {
            BODY_STACK.getOrCreateNbt().putInt(
                    CUSTOM_MODEL_DATA, PolymerResourcePackUtils.requestModel(BODY_STACK.getItem(), ESekai.getId("item/penguin_body")).value()
            );
            HEAD_STACK.getOrCreateNbt().putInt(
                    CUSTOM_MODEL_DATA, PolymerResourcePackUtils.requestModel(HEAD_STACK.getItem(), ESekai.getId("item/penguin_head")).value()
            );
            LEFT_WING_STACK.getOrCreateNbt().putInt(
                    CUSTOM_MODEL_DATA, PolymerResourcePackUtils.requestModel(LEFT_WING_STACK.getItem(), ESekai.getId("item/penguin_left_wing")).value()
            );
            RIGHT_WING_STACK.getOrCreateNbt().putInt(
                    CUSTOM_MODEL_DATA, PolymerResourcePackUtils.requestModel(RIGHT_WING_STACK.getItem(), ESekai.getId("item/penguin_right_wing")).value()
            );
            LEFT_FEET_STACK.getOrCreateNbt().putInt(
                    CUSTOM_MODEL_DATA, PolymerResourcePackUtils.requestModel(LEFT_FEET_STACK.getItem(), ESekai.getId("item/penguin_left_feet")).value()
            );
            RIGHT_FEET_STACK.getOrCreateNbt().putInt(
                    CUSTOM_MODEL_DATA, PolymerResourcePackUtils.requestModel(RIGHT_FEET_STACK.getItem(), ESekai.getId("item/penguin_right_feet")).value()
            );

        }
        public final ItemDisplayElement BODY;
        public final ItemDisplayElement HEAD;
        public final ItemDisplayElement LEFT_WING;
        public final ItemDisplayElement RIGHT_WING;
        public final ItemDisplayElement LEFT_FEET;
        public final ItemDisplayElement RIGHT_FEET;
        private final Matrix4x3fStack stack = new Matrix4x3fStack(8);

        public PenguinModel() {
            this.BODY = this.addElement(new ItemDisplayElement(BODY_STACK));
            this.HEAD = this.addElement(new ItemDisplayElement(HEAD_STACK));
            this.LEFT_WING = this.addElement(new ItemDisplayElement(LEFT_WING_STACK));
            this.RIGHT_WING = this.addElement(new ItemDisplayElement(RIGHT_WING_STACK));
            this.LEFT_FEET = this.addElement(new ItemDisplayElement(LEFT_FEET_STACK));
            this.RIGHT_FEET = this.addElement(new ItemDisplayElement(RIGHT_FEET_STACK));

            this.BODY.setModelTransformation(ModelTransformationMode.HEAD);
            this.HEAD.setModelTransformation(ModelTransformationMode.HEAD);
            this.LEFT_FEET.setModelTransformation(ModelTransformationMode.HEAD);
            this.LEFT_WING.setModelTransformation(ModelTransformationMode.HEAD);
            this.RIGHT_FEET.setModelTransformation(ModelTransformationMode.HEAD);
            this.RIGHT_WING.setModelTransformation(ModelTransformationMode.HEAD);

            this.setDefaultTransformation();
        }

        public void setDefaultTransformation() {
            var container = AnimationLoader.getContainer("penguin").get(AnimationLoader.DEFAULT_POSE_KEY);
            this.BODY.setTransformation(container.getContainerTick("body", 0));
            this.HEAD.setTransformation(container.getContainerTick("head", 0));
            this.LEFT_WING.setTransformation(container.getContainerTick("left_wing", 0));
            this.RIGHT_WING.setTransformation(container.getContainerTick("right_wing", 0));
            this.LEFT_FEET.setTransformation(container.getContainerTick("left_foot", 0));
            this.RIGHT_FEET.setTransformation(container.getContainerTick("right_foot", 0));
        }

        @Override
        protected void notifyElementsOfPositionUpdate(Vec3d newPos, Vec3d delta) {
            PenguinPetEntity.this.rideAnchor.notifyMove(this.currentPos, newPos, delta);
        }

        @Override
        public Vec3d getPos() {
            return PenguinPetEntity.this.attachment.getPos();
        }

        @Override
        public void tick() {
            super.tick();

        }

        public void animateModel(PenguinPetEntity entity, int tick) {
            var speed = entity.limbAnimator.getSpeed();
            var limbPos = entity.limbAnimator.getPos();
            var container = AnimationLoader.getContainer("penguin").get("walk");
            float f = ((float) entity.deathTime) / 20F * 1.6F;

            f = MathHelper.sqrt(f);

            if (f > 1.0F) {
                f = 1.0F;
            }

            if (entity.deathAngle == f && entity.previousLimbPos == limbPos && entity.previousSpeed == speed) {
                this.setDefaultTransformation();
                this.syncYaw(entity);
                return;
            }

            entity.deathAngle = f;
            entity.previousSpeed = speed;
            entity.previousLimbPos = limbPos;


            stack.clear();
            stack.translate(0, -0.2f, 0);
            var data = (float) Math.toRadians(-MathHelper.lerpAngleDegrees(0.5f, entity.prevBodyYaw, entity.bodyYaw));
            stack.rotateY((float) Math.toRadians(-MathHelper.lerpAngleDegrees(0.5f, entity.prevBodyYaw, entity.bodyYaw)) + (float) (0.00001f * Math.random()));
            if (entity.deathTime > 0) {
                stack.rotate(RotationAxis.POSITIVE_Z.rotation(f * MathHelper.HALF_PI));
                this.setDefaultTransformation();

                this.BODY.setTransformation(stack);
                this.RIGHT_WING.setTransformation(stack);
                this.LEFT_WING.setTransformation(stack);
                this.HEAD.setTransformation(stack);
                this.LEFT_FEET.setTransformation(stack);
                this.RIGHT_FEET.setTransformation(stack);

                return;
            }

            if (!entity.limbAnimator.isLimbMoving()) {
                if (alreadyStopped) {
                    return;
                }
                alreadyStopped = true;
                this.setDefaultTransformation();
                this.syncYaw(entity);

                this.startAllInterpolation();
                this.setInterpolationDuration(4);
                return;
            }

            this.setInterpolationDuration(1);

            alreadyStopped = false;
            var bodyMatrix = container.getContainerTick("body", tick);
            this.BODY.setTransformation(bodyMatrix);

            var headMatrix = container.getContainerTick("head", tick);
            this.HEAD.setTransformation(headMatrix);

            var leftWingMatrix = container.getContainerTick("left_wing", tick);
            this.LEFT_WING.setTransformation(leftWingMatrix);

            var rightWingMatrix = container.getContainerTick("right_wing", tick);
            this.RIGHT_WING.setTransformation(rightWingMatrix);

            var leftFootMatrix = container.getContainerTick("left_foot", tick);
            this.LEFT_FEET.setTransformation(leftFootMatrix);

            var rightFootMatrix = container.getContainerTick("right_foot", tick);
            this.RIGHT_FEET.setTransformation(rightFootMatrix);

            this.syncYaw(entity);
            this.startAllInterpolation();
        }

        public void syncYaw(PenguinPetEntity entity) {
            this.HEAD.setYaw(entity.getYaw());
            this.BODY.setYaw(entity.getYaw());
            this.LEFT_WING.setYaw(entity.getYaw());
            this.RIGHT_WING.setYaw(entity.getYaw());
            this.LEFT_FEET.setYaw(entity.getYaw());
            this.RIGHT_FEET.setYaw(entity.getYaw());

            hitbox.setYaw(entity.getYaw());
        }

        public void setInterpolationDuration(int time) {
            this.HEAD.setInterpolationDuration(time);
            this.BODY.setInterpolationDuration(time);
            this.LEFT_FEET.setInterpolationDuration(time);
            this.RIGHT_FEET.setInterpolationDuration(time);
            this.LEFT_WING.setInterpolationDuration(time);
            this.RIGHT_WING.setInterpolationDuration(time);
        }

        public void startAllInterpolation() {
            this.BODY.startInterpolation();
            this.HEAD.startInterpolation();
            this.LEFT_FEET.startInterpolation();
            this.RIGHT_FEET.startInterpolation();
            this.LEFT_WING.startInterpolation();
            this.RIGHT_WING.startInterpolation();
        }
    }
}
