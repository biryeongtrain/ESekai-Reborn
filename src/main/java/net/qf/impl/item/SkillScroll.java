package net.qf.impl.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.qf.api.ESekaiCreationSkill;
import net.qf.impl.ESekaiSkillUser;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;
import static net.qf.ESekai.getId;
import static net.qf.ESekai.getTranslation;

public class SkillScroll extends Item implements PolymerItem {
    public static final String SKILL_DATA_TAG_NAME = "skillData";
    public static final String LEVEL_DATA_TAG_NAME = "level";
    private final PolymerModelData modelData;

    public SkillScroll(Settings settings) {
        super(settings);
        this.modelData = PolymerResourcePackUtils.requestModel(Items.PAPER, getId("item/skill_scroll"));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.modelData.item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.modelData.value();
    }

    @Override
    public void modifyClientTooltip(List<Text> tooltip, ItemStack stack, @Nullable ServerPlayerEntity player) {
        if (stack.getSubNbt(SKILL_DATA_TAG_NAME) == null) {
            tooltip.add(translatable(getTranslation("item.skill_scroll.empty")));
        } else {
            int level = stack.getOrCreateNbt().getInt(LEVEL_DATA_TAG_NAME);
            var skill = ESekaiCreationSkill.CODEC.decode(NbtOps.INSTANCE, stack.getSubNbt(SKILL_DATA_TAG_NAME)).getOrThrow(false, x -> {}).getFirst();
            var info = skill.info();
            var user = (ESekaiSkillUser) player;

            if (user == null) {
                return;
            }
            tooltip.add(translatable(getTranslation("item.skill_scroll.level"))
                    .append(literal(String.valueOf(level)).formatted(user.getLevel() >= level ? Formatting.GREEN : Formatting.RED))
                    .append(literal(" | "))
                    .append("⏳ ").append(literal(String.valueOf(skill.cooldown())).formatted(Formatting.DARK_AQUA).append(translatable(getTranslation("skill.second")).formatted(Formatting.DARK_AQUA)))
                    .append(literal(" | "))
                    .append("\uD83E\uDDEA ").formatted(Formatting.BLUE).append(literal(String.valueOf(skill.cost())).formatted(Formatting.BLUE)).formatted(Formatting.RESET)
            );
            tooltip.add(literal("🫐  ")
                    .append(literal(skill.school().getIcon()).styled(style -> style.withColor(skill.school().getColor())))
                    .append(" | ").append("✔ ").append(info.targetType().getTranslation().styled(style -> style.withColor(info.targetType().getColor())))
                    .append(" | ").append("\uD83D\uDDE1 ").append(literal(String.valueOf(skill.baseDamage())).formatted(Formatting.RED))
            );
            tooltip.add(literal("⓪ ")
                    .append(info.mechanism().getTranslation())
                    .append(literal("(" + info.radius() + " X " + info.vertical() + " X " + info.radius() + ")").formatted(Formatting.GRAY))
            );
            var tags = literal("\uD83D\uDD6E ");
            skill.tags().forEach(tag -> {
                tags.append(tag.getTranslationText());
                tags.append(" ");
            });
            tooltip.add(tags);

        }
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        NbtCompound compound = stack.getOrCreateNbt();
        ESekaiSkillUser user = (ESekaiSkillUser) player;

        compound.putInt(LEVEL_DATA_TAG_NAME, user.getLevel());
        stack.setNbt(compound);
    }

    public void setSkillDataToStack(ServerPlayerEntity player, ItemStack stack, ESekaiCreationSkill skill) {
        var compound = stack.getOrCreateNbt();
        if (!compound.contains(LEVEL_DATA_TAG_NAME)) {
            ESekaiSkillUser user = (ESekaiSkillUser) player;

            compound.putInt(LEVEL_DATA_TAG_NAME, user.getLevel());
        }
        var skillComponent = ESekaiCreationSkill.CODEC.encodeStart(NbtOps.INSTANCE, skill).getOrThrow(false, (x) -> {});
        compound.put(SKILL_DATA_TAG_NAME, skillComponent);

        stack.setNbt(compound);
    }

    @Nullable
    public ESekaiCreationSkill getSkillDataInStack(ItemStack stack) {
        if (!stack.hasNbt() || (stack.getSubNbt(SKILL_DATA_TAG_NAME) == null)) {
            return null;
        }

        var skill = ESekaiCreationSkill.CODEC.decode(NbtOps.INSTANCE, stack.getSubNbt(SKILL_DATA_TAG_NAME)).getOrThrow(false, (x) -> {});

        return skill.getFirst();
    }
}
