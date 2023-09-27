package net.qf.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.qf.api.*;
import net.qf.commands.suggest.ElementSuggestProvider;
import net.qf.impl.ESekaiSkillUser;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.qf.api.ESekaiDamageTag.*;


public class ESekaiDebugDamage {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("testDamage")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("targets", EntityArgumentType.players())
                        .then(argument("element", StringArgumentType.word()).suggests(new ElementSuggestProvider())
                        .executes(ctx -> debugDamage(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "targets"), StringArgumentType.getString(ctx, "element")))))
        );
        dispatcher.register(literal("setTestSkill")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ESekaiDebugDamage::debugSkill)
        );
    }

    private static int debugDamage(ServerCommandSource source, ServerPlayerEntity player, String damageSourceId) {
        var value = ESekaiSchool.valueOf(damageSourceId.toUpperCase());
        var combiner = ESekaiDamageCalculator.getCombiner(source.getPlayer(), value, List.of(ATTACK, PROJECTILE));
        var result = new ESekaiDamageCalculator.Result(value, 10);
        player.damage(ESekaiDamageSource.player(value, source.getPlayer(), List.of(ATTACK, PROJECTILE)), (float)result.randomValue(combiner));

        return 0;
    }

    private static int debugSkill(CommandContext<ServerCommandSource> ctx) {
        var player = ctx.getSource().getPlayer();
        var skillUser = (ESekaiSkillUser) player;

        skillUser.setSkill(new ESekaiCreationSkill(TriggerType.CAST, new SkillInfo(SkillInfo.SkillMechanism.AOE, SkillInfo.TargetType.ENEMIES, 10, 4, true), 10F, ESekaiSchool.FIRE, List.of(ATTACK, SPELL), 10F, 2F));
        return 0;
    }
}
