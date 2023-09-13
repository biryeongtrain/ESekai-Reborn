package net.qf.impl.register;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.qf.commands.ESekaiAddModifierCommand;
import net.qf.commands.ESekaiDebugDamage;

public class ESekaiCommandRegister {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        ESekaiDebugDamage.register(dispatcher);
        ESekaiAddModifierCommand.register(dispatcher);
    }
}
