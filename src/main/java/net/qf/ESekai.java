package net.qf;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;
import net.qf.impl.register.ESekaiCommandRegister;
import net.qf.impl.register.ESekaiItemRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ESekai implements ModInitializer {
	public static final String MOD_ID = "esekai";
    public static final Logger LOGGER = LoggerFactory.getLogger("ESekai-Logger");

	@Override
	public void onInitialize() {
		LOGGER.info("You were run over by dump truck. Welcome to ESekai! :3");
		CommandRegistrationCallback.EVENT.register(ESekaiCommandRegister::register);
		ESekaiItemRegister.init();
	}
	
	public static Identifier getId(String value) {
		return new Identifier(MOD_ID, value);
	}

	public static String getTranslation(String value) {
		return "esekai." + value;
	}
}