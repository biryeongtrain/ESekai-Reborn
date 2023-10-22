package net.qf;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;
import net.qf.impl.entity.anim.AnimationLoader;
import net.qf.impl.register.ESekaiBlockregistry;
import net.qf.impl.register.ESekaiCommandRegistry;
import net.qf.impl.register.ESekaiEntityRegistry;
import net.qf.impl.register.ESekaiItemRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ESekai implements ModInitializer {
	public static final String MOD_ID = "esekai";
    public static final Logger LOGGER = LoggerFactory.getLogger("ESekai-Logger");

	@Override
	public void onInitialize() {
		LOGGER.info("You were run over by dump truck. Welcome to ESekai! :3");
		PolymerResourcePackUtils.addModAssets(MOD_ID);
		CommandRegistrationCallback.EVENT.register(ESekaiCommandRegistry::register);
		ESekaiItemRegistry.init();
		ESekaiBlockregistry.init();
		ESekaiEntityRegistry.init();

		AnimationLoader.registerMod(MOD_ID);
		AnimationLoader.build();
	}
	
	public static Identifier getId(String value) {
		return new Identifier(MOD_ID, value);
	}

	public static String getTranslation(String value) {
		return "esekai." + value;
	}
}