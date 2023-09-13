package net.qf.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.qf.api.ESekaiDamageCalculator;
import net.qf.api.ESekaiDamageSource;
import net.qf.api.ESekaiSchool;
import net.qf.commands.suggest.ElementSuggestProvider;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.qf.api.ESekaiDamageTag.ATTACK;
import static net.qf.api.ESekaiDamageTag.PROJECTILE;


public class ESekaiDebugDamage {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("testDamage")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("targets", EntityArgumentType.players())
                        .then(argument("element", StringArgumentType.word()).suggests(new ElementSuggestProvider())
                        .executes(ctx -> debugDamage(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "targets"), StringArgumentType.getString(ctx, "element")))))
        );
    }

    private static int debugDamage(ServerCommandSource source, ServerPlayerEntity player, String damageSourceId) {
        var value = ESekaiSchool.valueOf(damageSourceId.toUpperCase());
        var combiner = ESekaiDamageCalculator.getCombiner(source.getPlayer(), value, List.of(ATTACK, PROJECTILE));
        var result = new ESekaiDamageCalculator.Result(value, 10);
        player.damage(ESekaiDamageSource.player(value, source.getPlayer()), (float)result.randomValue(combiner));

        return 0;
    }
}
