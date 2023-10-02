package net.qf.impl.register;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.qf.impl.item.SkillScroll;

import static net.qf.ESekai.getId;

public class ESekaiItemRegister {
    public static Item SKILL_SCROLL = Registry.register(Registries.ITEM, getId("skill_scroll"), new SkillScroll(new FabricItemSettings().maxCount(16)));

    public static void init() {

    }
}
