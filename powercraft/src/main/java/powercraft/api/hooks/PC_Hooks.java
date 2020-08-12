package powercraft.api.hooks;

import powercraft.api.hooklib.asjasm.ASJASM;
import powercraft.api.hooklib.minecraft.HookLoader;
import powercraft.api.hooklib.minecraft.PrimaryClassTransformer;
import powercraft.launcher.PC_Launcher;
import powercraft.launcher.PC_LauncherUtils;
import powercraft.launcher.mod_PowerCraft;

public class PC_Hooks extends HookLoader {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { PrimaryClassTransformer.class.getName(), ASJASM.class.getName() };
	}

	@Override
	public void registerHooks() {
		PC_LauncherUtils.isPreStart = true;
		new PC_LauncherUtils();
		PC_Launcher.loadModules();
		PC_LauncherUtils.isPreStart = false;
		// TODO: move in module
		if(PC_Launcher.getModules().containsKey("Storage"))
			registerHookContainer("powercraft.storage.PCs_TileEntityChest");
		ASJASM.registerFieldHookContainer("powercraft.api.hooks.PC_TEHook");
	}
}