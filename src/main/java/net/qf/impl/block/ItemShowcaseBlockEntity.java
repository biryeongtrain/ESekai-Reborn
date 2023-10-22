package net.qf.impl.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.qf.impl.block.interfaces.DefaultSidedInventory;
import net.qf.impl.register.ESekaiBlockregistry;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ItemShowcaseBlockEntity extends BlockEntity implements DefaultSidedInventory {
    private static final int[] SLOT = new int[]{0};
    private final String OWNER_KEY = "owner";
    private final String ITEM_YAW_KEY = "item_yaw";
    public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private final DefaultedList<ItemStack> INVENTORY =  DefaultedList.ofSize(1, ItemStack.EMPTY);
    public UUID owner = EMPTY_UUID;
    public float itemYaw = 0F;
    public ItemShowcaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ItemShowcaseBlockEntity(BlockPos pos, BlockState state) {
        this(ESekaiBlockregistry.ITEM_SHOWCASE_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getStacks() {
        return this.INVENTORY;
    }

    public ItemStack getHoldingItem() {
        return this.INVENTORY.get(0);
    }

    public ItemStack extractHoldingItem() {
        this.itemYaw = 0F;
        return this.removeStack(0);
    }

    public boolean insertHoldingItem(ItemStack stack, float yaw) {
        if (this.hasHoldingItem()) {
            return false;
        }

        this.setStack(0, stack);
        this.itemYaw = -yaw;
        return true;
    }

    public boolean hasHoldingItem() {
        return !this.INVENTORY.get(0).isEmpty();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return SLOT;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.INVENTORY);
        nbt.putUuid(OWNER_KEY, this.owner);
        nbt.putFloat(ITEM_YAW_KEY, this.itemYaw);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, this.INVENTORY);
        this.owner = nbt.getUuid(OWNER_KEY);
        this.itemYaw = nbt.getFloat(ITEM_YAW_KEY);
        super.readNbt(nbt);
    }
}
