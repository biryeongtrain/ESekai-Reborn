package net.qf.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.qf.api.ESekaiSchool;
import org.jetbrains.annotations.NotNull;

import static net.qf.ESekai.MOD_ID;


public class QfDamageSource extends DamageSource {

    @NotNull
    private final ESekaiSchool school;

    public QfDamageSource(RegistryEntry<DamageType> type, Entity attacker, @NotNull ESekaiSchool school) {
        super(type, attacker);
        this.school = school;
    }


    public ESekaiSchool getElement() {
        return school;
    }

    @Override
    public Text getDeathMessage(LivingEntity killed) {
        if (this.getSource() == null && this.getAttacker() == null) super.getDeathMessage(killed);
        String string = MOD_ID + ".death." + this.getType().msgId() + ".player";
        Text text = this.getAttacker() == null ? this.getSource().getDisplayName() : this.getAttacker().getDisplayName();
        if (this.getAttacker() == null && this.getSource() == null) {
            LivingEntity livingEntity2 = killed.getPrimeAdversary();
            return livingEntity2 != null ? Text.translatable(string + ".environment", new Object[]{killed.getDisplayName(), livingEntity2.getDisplayName()}) : Text.translatable(string, new Object[]{killed.getDisplayName()});
        }
        Entity killer = this.getAttacker();
        ItemStack killerHeldItem;
        if (killer instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) killer;
            killerHeldItem = livingEntity.getMainHandStack();
        } else {
            killerHeldItem = ItemStack.EMPTY;
        }
        return !killerHeldItem.isEmpty() ?
                Text.translatable(string + ".item", new Object[]{killed.getDisplayName(), text, killerHeldItem, this.toHoverText()}) :
                Text.translatable(string , new Object[]{killed.getDisplayName(), text, toHoverText()});

    }

    private Text toHoverText() {
        MutableText text = (Text.literal(school.getIcon()));
        text.styled(style ->
            style.withColor(school.getColor())
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.empty().append((school.getTranslationText())).styled(style2 -> style.withColor(school.getColor())))));
        return text;
    }
}
