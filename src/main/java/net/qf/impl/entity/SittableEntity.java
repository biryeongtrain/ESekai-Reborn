package net.qf.impl.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.qf.impl.register.ESekaiEntityRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.minecraft.entity.decoration.ArmorStandEntity.ARMOR_STAND_FLAGS;

public class SittableEntity extends Entity implements PolymerEntity {
    private static final EntityAttributeInstance MAX_HEALTH_NULL = new EntityAttributeInstance(
            EntityAttributes.GENERIC_MAX_HEALTH, discard -> {});
    private static final Collection<EntityAttributeInstance> MAX_HEALTH_NULL_SINGLE = Collections.singleton(MAX_HEALTH_NULL);

    static {
        MAX_HEALTH_NULL.setBaseValue(0D);
    }

    public SittableEntity(EntityType<? extends SittableEntity> type, World world) {
        super(type, world);
        this.setInvisible(true);
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    public SittableEntity(World world, double x, double y, double z) {
        this(ESekaiEntityRegistry.SITTABLE_ENTITY, world);
        this.setPosition(x, y, z);

        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        data.add(new DataTracker.Entry<>(ARMOR_STAND_FLAGS, (byte) 16).toSerialized());

        if (player != null) {
            player.networkHandler.sendPacket(new EntityAttributesS2CPacket(getId(), MAX_HEALTH_NULL_SINGLE));
        }
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public boolean shouldSave() {
        var reason = getRemovalReason();
        if (reason != null && !reason.shouldSave()) {
            return false;
        }
        return hasPassengers();
    }

    @Override
    public void removeAllPassengers() {
        super.removeAllPassengers();
        discard();
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        discard();
    }

    @Override
    public void tick() {
        super.tick();
        var passenger = getFirstPassenger();
        if (passenger == null || (((this.getWorld().getTime() + hashCode() & 31) == 0 && getBlockStateAtPos().isAir()))) {
            discard();
            return;
        }
        setYaw(passenger.getYaw());
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
