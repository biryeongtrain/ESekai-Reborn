package net.qf.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetOwnerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("setOwner")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(argument("target", EntityArgumentType.entity())
                                .executes(ctx -> runCommand(ctx, EntityArgumentType.getEntity(ctx, "target"))))
        );
    }

    private static int runCommand(CommandContext<ServerCommandSource> ctx, Entity selector) {

        if (!(selector instanceof TameableEntity tameableEntity)) {
            return 0;
        }

        if (tameableEntity.getOwner() == null) {
            tameableEntity.setOwner(ctx.getSource().getPlayer());
        }
        return 1;
    }
}
