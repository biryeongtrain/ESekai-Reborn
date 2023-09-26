package net.qf.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.qf.api.ESekaiSchool;
import net.qf.commands.suggest.ElementSuggestProvider;

import java.util.Collection;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ESekaiAddModifierCommand {
    public static UUID uuid = UUID.fromString("45f646c0-9fb6-435e-b019-b5eaeed9133e");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("addModifier")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("targets", EntityArgumentType.players())
                        .then(argument("element", StringArgumentType.word()).suggests(new ElementSuggestProvider())
                                .then(argument("value", FloatArgumentType.floatArg())
                                        .executes(ctx -> addModifier(ctx, EntityArgumentType.getPlayers(ctx, "targets"), StringArgumentType.getString(ctx, "element"),FloatArgumentType.getFloat(ctx, "value")))))
                ));
    }

    private static int addModifier(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> players, String schoolId,float value) {
        var school = ESekaiSchool.valueOf(schoolId.toUpperCase());
        players.forEach(player -> {
            var instance = player.getAttributes().getCustomInstance(school.getScalingAttribute());
            assert instance != null;
            instance.addPersistentModifier(new EntityAttributeModifier(uuid, "esekai_debug_stat", value / 100, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        });
        return 0;
    }
}
