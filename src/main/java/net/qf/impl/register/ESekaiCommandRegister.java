package net.qf.impl.register;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.qf.commands.ESekaiAddModifierCommand;
import net.qf.commands.ESekaiDebugDamage;
import net.qf.commands.ESekaiTestSkillScrollCommand;

public class ESekaiCommandRegister {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment env) {
        ESekaiDebugDamage.register(dispatcher);
        ESekaiAddModifierCommand.register(dispatcher);
        ESekaiTestSkillScrollCommand.register(dispatcher);
    }
}
