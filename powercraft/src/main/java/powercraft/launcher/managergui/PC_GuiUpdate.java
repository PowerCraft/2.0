package powercraft.launcher.managergui;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import powercraft.launcher.PC_LauncherClientUtils;
import powercraft.launcher.PC_LauncherUtils;
import powercraft.launcher.loader.PC_ModuleObject;
import powercraft.launcher.manager.PC_ModuleManager;
import powercraft.launcher.manager.PC_ModuleManager.ModuleUpdateInfo;

public class PC_GuiUpdate extends GuiScreen {

	private static Minecraft smc = PC_LauncherClientUtils.mc();
	private static boolean stop;
	private static PC_GuiManager gui;

	private PC_GuiScrollUpdate scroll;
	public GuiButton reload;
	public GuiButton update;
	public GuiButton back;

	@Override
	public void initGui() {
		scroll = new PC_GuiScrollUpdate(10, 20, this.width - 20, height - 58);
		buttonList.clear();
		buttonList.add(reload = new GuiButton(1, this.width - ((this.width / 3 - 50) * 2 + 15), height - 30,
				this.width / 3 - 50, 20, EnumChatFormatting.GREEN + StatCollector.translateToLocal("Reload game")));
		buttonList.add(update = new GuiButton(2, 10, height - 30, this.width / 3 - 50, 20,
				EnumChatFormatting.GREEN + StatCollector.translateToLocal("Install all")));
		buttonList.add(back = new GuiButton(3, this.width - ((this.width / 3 - 50) + 10), height - 30,
				this.width / 3 - 50, 20, StatCollector.translateToLocal("Back")));
		reload.visible = false;
		reload.enabled = false;
		PC_ModuleManager.checkForUpdate();
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		scroll.drawScreen(par1, par2, par3);
		drawCenteredString(fontRendererObj, StatCollector.translateToLocal("PowerCraft Updates"), width / 2, 4,
				0xffffff);
		super.drawScreen(par1, par2, par3);
		if (PC_ModuleManager.updatedCount == PC_ModuleManager.moduleListToUpdate.size() * 2)
			reload.enabled = true;
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == 1) {
			mc.shutdown();
		} else if (par1GuiButton.id == 2) {
			back.enabled = false;
			update.enabled = false;
			reload.visible = true;
			reload.enabled = false;
			PC_ModuleManager.updateAll();
		} else if (par1GuiButton.id == 3) {
			mc.displayGuiScreen(new PC_GuiManager());
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		scroll.keyTyped(par1, par2);
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		scroll.mouseClicked(par1, par2, par3);
		super.mouseClicked(par1, par2, par3);
	}

	@Override
	protected void mouseMovedOrUp(int par1, int par2, int par3) {
		scroll.mouseMovedOrUp(par1, par2, par3);
		super.mouseMovedOrUp(par1, par2, par3);
	}

}
