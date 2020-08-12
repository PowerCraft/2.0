package powercraft.launcher.managergui;

import java.util.ArrayList;
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

public class PC_GuiManager extends GuiScreen {

	private static String launcherDownload = "https://www.curseforge.com/minecraft/mc-mods/powercraft-2/files";

	private static Minecraft smc = PC_LauncherClientUtils.mc();
	private static boolean stop;
	private static PC_GuiManager gui;

	private PC_GuiScrollModuleAndPackScroll scroll;
	private PC_GuiModule moduleGui;
	public GuiButton enabled;
	public GuiButton install;
	// public GuiButton config;
	public GuiButton delete;
	public GuiButton update;

	@Override
	public void initGui() {
		scroll = new PC_GuiScrollModuleAndPackScroll(this.width / 2 - 200, 20, 200, height - 28);
		moduleGui = new PC_GuiModule(this.width / 2 + 5, 20, 200, 20, scroll, this);
		buttonList.clear();
		buttonList.add(
				new GuiButton(1, this.width / 2 + 5, height - 28, 95, 20, StatCollector.translateToLocal("gui.done")));
		buttonList.add(new GuiButton(7, this.width / 2 + 105, height - 28, 95, 20,
				StatCollector.translateToLocal("menu.quit")));
		buttonList.add(enabled = new GuiButton(2, this.width / 2 + 5, 40, 195, 20,
				EnumChatFormatting.GREEN + StatCollector.translateToLocal("Enabled")));
		buttonList.add(install = new GuiButton(3, this.width / 2 + 5, height - 53, 195, 20,
				StatCollector.translateToLocal("Install")));
		// buttonList.add(config = new GuiButton(4, this.width / 2 + 105, 40, 95, 20,
		// StatCollector.translateToLocal("Configuration")));
		buttonList.add(delete = new GuiButton(5, this.width / 2 + 5, 65, 195, 20,
				EnumChatFormatting.RED + StatCollector.translateToLocal("Delete")));
		buttonList.add(update = new GuiButton(6, this.width / 2 + 5, height - 53, 195, 20,
				EnumChatFormatting.GREEN + StatCollector.translateToLocal("Update")));
		enabled.visible = false;
		install.visible = false;
		// config.visible = false;
		delete.visible = false;
		update.visible = false;
		update.enabled = false;
		PC_ModuleManager.checkForUpdate();
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		scroll.drawScreen(par1, par2, par3);
		drawCenteredString(fontRendererObj, StatCollector.translateToLocal("PowerCraft Manager"), width / 2, 4,
				0xffffff);
		if (delete.visible)
			drawString(fontRendererObj,
					EnumChatFormatting.RED + StatCollector.translateToLocal("For apply changes need restart game!"),
					this.width / 2 + 7, 90, 0xffffff);
		super.drawScreen(par1, par2, par3);
		moduleGui.draw(par1, par2, par3, fontRendererObj);
		if (PC_ModuleManager.newLauncher) {
			drawInfo(StatCollector.translateToLocal("There is a new Launcher update"), isMouseOverInfo(par1, par2));
		} else if (!hasAPI()) {
			drawInfo(StatCollector.translateToLocal("Please download the API"), false);
		} else if (PC_LauncherUtils.isDeveloperVersion()) {
			drawInfo(StatCollector.translateToLocal("You are using the Developer Version"), false);
		}
		drawTooltip(par1, par2, par3);
	}

	private boolean hasAPI() {
		for (ModuleUpdateInfo mui : PC_ModuleManager.moduleList) {
			if (mui.module == null) {
				if (mui.xmlModule.getName().equals("Api")) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isMouseOverInfo(int par1, int par2) {
		return par1 > width - 120 && par2 < 32;
	}

	private void drawInfo(String info, boolean highLite) {
		if (highLite) {
			GL11.glColor4f(1.0F, 1.0F, 0.4F, 1.0F);
		} else {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
		smc.renderEngine.bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
		drawTexturedModalRect(width - 120, 0, 0, 166, 120, 32);
		List<String> lines = fontRendererObj.listFormattedStringToWidth(info, 100);
		if (lines.size() == 1) {
			fontRendererObj.drawStringWithShadow(lines.get(0), width - 110, 11, highLite ? 0xFF4433 : 0xFFFFFF);
		} else if (lines.size() > 1) {
			fontRendererObj.drawStringWithShadow(lines.get(0), width - 110, 6, highLite ? 0xFF4433 : 0xFFFFFF);
			fontRendererObj.drawStringWithShadow(lines.get(1), width - 110, 16, highLite ? 0xFF4433 : 0xFFFFFF);
		}
	}

	private void drawTooltip(int par1, int par2, float par3) {
		List list = null;

		if (isMouseOverInfo(par1, par2)) {
			if (PC_ModuleManager.newLauncher) {
				list = Arrays.asList(StatCollector.translateToLocal("Click to Download"));
			}
		}

		if (list != null && list.size() > 0) {
			int l1 = 0;

			for (int i2 = 0; i2 < list.size(); i2++) {
				int k2 = fontRendererObj.getStringWidth((String) list.get(i2));

				if (k2 > l1) {
					l1 = k2;
				}
			}

			int j2 = (par1) + 12;
			int l2 = par2 - 12;
			int i3 = l1;
			int j3 = 8;

			if (list.size() > 1) {
				j3 += 2 + (list.size() - 1) * 10;
			}

			zLevel = 300F;
			int k3 = 0xf0100010;
			drawGradientRect(j2 - 3, l2 - 4, j2 + i3 + 3, l2 - 3, k3, k3);
			drawGradientRect(j2 - 3, l2 + j3 + 3, j2 + i3 + 3, l2 + j3 + 4, k3, k3);
			drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, l2 + j3 + 3, k3, k3);
			drawGradientRect(j2 - 4, l2 - 3, j2 - 3, l2 + j3 + 3, k3, k3);
			drawGradientRect(j2 + i3 + 3, l2 - 3, j2 + i3 + 4, l2 + j3 + 3, k3, k3);
			int l3 = 0x505000ff;
			int i4 = (l3 & 0xfefefe) >> 1 | l3 & 0xff000000;
			drawGradientRect(j2 - 3, (l2 - 3) + 1, (j2 - 3) + 1, (l2 + j3 + 3) - 1, l3, i4);
			drawGradientRect(j2 + i3 + 2, (l2 - 3) + 1, j2 + i3 + 3, (l2 + j3 + 3) - 1, l3, i4);
			drawGradientRect(j2 - 3, l2 - 3, j2 + i3 + 3, (l2 - 3) + 1, l3, l3);
			drawGradientRect(j2 - 3, l2 + j3 + 2, j2 + i3 + 3, l2 + j3 + 3, i4, i4);

			for (int j4 = 0; j4 < list.size(); j4++) {
				String s = (String) list.get(j4);

				fontRendererObj.drawStringWithShadow(s, j2, l2, -1);

				if (j4 == 0) {
					l2 += 2;
				}

				l2 += 10;
			}

			zLevel = 0.0F;
		}
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == 1) {
			mc.displayGuiScreen(new GuiMainMenu());
		} else if (par1GuiButton.id == 2) {
			PC_ModuleManager.activateModule((PC_ModuleObject) scroll.getSelection(),
					!((PC_ModuleObject) scroll.getSelection()).getConfig().getBoolean("loader.enabled"));
			if (!((PC_ModuleObject) scroll.getSelection()).getConfig().getBoolean("loader.enabled"))
				par1GuiButton.displayString = EnumChatFormatting.RED + StatCollector.translateToLocal("Disabled");
			else
				par1GuiButton.displayString = EnumChatFormatting.GREEN + StatCollector.translateToLocal("Enabled");
		} else if (par1GuiButton.id == 3) {
			mc.displayGuiScreen(new PC_GuiDownloadOverlay((Object) scroll.getSelection()));
			PC_ModuleManager.install((Object) scroll.getSelection(), true);
			par1GuiButton.enabled = false;
		} else if (par1GuiButton.id == 4) {
			// Reserverd for configuration... TODO: config gui;
		} else if (par1GuiButton.id == 5) {
			PC_ModuleObject module = (PC_ModuleObject) scroll.getSelection();
			PC_ModuleManager.delete(module.getModuleName());
		} else if (par1GuiButton.id == 6) {
			mc.displayGuiScreen(new PC_GuiUpdate());
		} else if (par1GuiButton.id == 7) {
			mc.shutdown();
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		scroll.keyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		if (isMouseOverInfo(par1, par2)) {
			if (PC_ModuleManager.newLauncher) {
				PC_ModuleManager.openURL(launcherDownload);
			}
		}
		scroll.mouseClicked(par1, par2, par3);
		super.mouseClicked(par1, par2, par3);
	}

	@Override
	protected void mouseMovedOrUp(int par1, int par2, int par3) {
		scroll.mouseMovedOrUp(par1, par2, par3);
		super.mouseMovedOrUp(par1, par2, par3);
	}

}
