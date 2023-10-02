package net.qf.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.qf.api.ESekaiCreationSkill;
import net.qf.api.ESekaiSchool;
import net.qf.api.SkillInfo;
import net.qf.api.TriggerType;
import net.qf.impl.item.SkillScroll;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;
import static net.qf.api.ESekaiDamageTag.ATTACK;
import static net.qf.api.ESekaiDamageTag.SPELL;
import static net.qf.impl.register.ESekaiItemRegister.SKILL_SCROLL;

public class ESekaiTestSkillScrollCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("addTestSkillToItem")
                .requires(source -> source.hasPermissionLevel(2) && source.isExecutedByPlayer())
                .executes(ESekaiTestSkillScrollCommand::setSkillScrollSkill)
        );
        dispatcher.register(literal("getTestSkillInItem")
                .requires(source -> source.hasPermissionLevel(2) && source.isExecutedByPlayer())
                .executes(ESekaiTestSkillScrollCommand::getSkillScrollSkill)
        );
    }

    private static int setSkillScrollSkill(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();

        var stack = player.getMainHandStack();

        if (!(stack.getItem() == SKILL_SCROLL) ||stack.isEmpty()) {
            return 0;
        }

        SkillScroll item = (SkillScroll) stack.getItem();
        item.setSkillDataToStack(player, stack, new ESekaiCreationSkill(TriggerType.CAST,
                new SkillInfo(SkillInfo.SkillMechanism.AOE, SkillInfo.TargetType.ENEMIES, 10F, 5F, true),
                10F, ESekaiSchool.CHAOS, List.of(ATTACK, SPELL), 10F, 1F));
        return 1;
    }

    private static int getSkillScrollSkill(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();

        var stack = player.getMainHandStack();

        if (!(stack.getItem() == SKILL_SCROLL) ||stack.isEmpty()) {
            return 0;
        }

        SkillScroll item = (SkillScroll) stack.getItem();
        var skill = item.getSkillDataInStack(stack);

        player.sendMessage(Text.literal(skill.info().toString()));
        return 1;
    }
}
