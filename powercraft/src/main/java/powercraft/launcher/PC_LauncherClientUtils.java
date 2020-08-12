package powercraft.launcher;

import java.io.File;

import net.minecraft.client.Minecraft;
import powercraft.launcher.manager.PC_ModuleManager;
import powercraft.launcher.managergui.PC_GuiManager;

public class PC_LauncherClientUtils extends PC_LauncherUtils {

	public static Minecraft mc() {
		return Minecraft.getMinecraft();
	}

	@Override
	protected boolean pIsClient() {
		return true;
	}

	@Override
	protected File pGetMCDirectory() {
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	public void pLookForUpdates() {
		PC_ModuleManager.lookForUpdates();
	}

	@Override
	public void pOpenUpdateGui(boolean requestDownloadTarget) {
		Minecraft.getMinecraft().displayGuiScreen(new PC_GuiManager());
	}

}
