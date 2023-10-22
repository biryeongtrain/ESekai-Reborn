package net.qf.impl.register;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Rarity;
import net.qf.impl.block.ChairBlock;
import net.qf.impl.block.ItemShowcaseBlock;
import net.qf.impl.item.SkillScroll;
import org.jetbrains.annotations.Nullable;

import static net.qf.ESekai.getId;

public class ESekaiItemRegistry {
    public static Item SKILL_SCROLL = Registry.register(
            Registries.ITEM, getId("skill_scroll"), new SkillScroll(new FabricItemSettings().maxCount(16)));
    public static PolymerBlockItem TEST_FURNITURE_BlOCK_ITEM = new PolymerBlockItem(ESekaiBlockregistry.TEST_FURNITURE, new Item.Settings().rarity(Rarity.RARE), Items.PAPER) {
        @Override
        public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
            return ChairBlock.Model.MODEL_ITEM.getNbt().getInt("CustomModelData");
        }
    };
    public static PolymerBlockItem ITEM_SHOWCASE_ITEM = new PolymerBlockItem(ESekaiBlockregistry.ITEM_SHOWCASE_BLOCK, new Item.Settings().rarity(Rarity.EPIC), Items.LEATHER_HORSE_ARMOR) {
        @Override
        public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
            return ItemShowcaseBlock.Model.FRAME.getNbt().getInt("CustomModelData");
        }

        @Override
        public int getPolymerArmorColor(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
            return ItemShowcaseBlock.Model.FRAME.getNbt().getCompound("display").getInt("color");
        }
    };


    public static void init() {
        Registry.register(Registries.ITEM, getId("test_furniture_block"), TEST_FURNITURE_BlOCK_ITEM);
        Registry.register(Registries.ITEM, getId("item_showcase_block"), ITEM_SHOWCASE_ITEM);
    }
}
