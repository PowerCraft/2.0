package powercraft.launcher.manager;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.GuiScreenEvent;
import powercraft.launcher.PC_LauncherClientUtils;

public class PC_UpdateEventHandler {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void config(GuiScreenEvent e) {
		if (e.gui.getClass().getName().equals("net.minecraft.client.gui.GuiMainMenu")) {
			if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
				PC_ModuleManager.onStart = false;
				PC_LauncherClientUtils.lookForUpdates();
			}
		}
	}

}
