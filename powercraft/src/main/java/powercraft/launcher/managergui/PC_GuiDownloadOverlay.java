package powercraft.launcher.managergui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import powercraft.launcher.manager.PC_ModuleManager;
import powercraft.launcher.manager.PC_ModuleManager.ModuleUpdateInfo;
import powercraft.launcher.manager.PC_UpdateXMLFile.XMLPackTag;

public class PC_GuiDownloadOverlay extends GuiScreen {

	public GuiButton reload;
	public boolean isPack;
	public XMLPackTag pack;
	public ModuleUpdateInfo module;
	private int gsx, gsy, gswidth, gsheight;

	public PC_GuiDownloadOverlay(Object xml) {// TODO: For draw as real overlay, int x, int y, int width, int height){
		// this.gsx = x;
		// this.gsy = y;
		// this.gswidth = width;
		// this.gsheight = height;
		if (xml instanceof String) {
			isPack = false;
		}
		if (xml instanceof XMLPackTag) {
			isPack = true;
			pack = (XMLPackTag) xml;
		}
		if (xml instanceof ModuleUpdateInfo) {
			isPack = false;
			module = (ModuleUpdateInfo) xml;
		}
	}

	@Override
	public void initGui() {
		buttonList.clear();
		if (isPack)
			buttonList.add(reload = new GuiButton(1, this.width / 2 - 100, height - 30, 200, 20,
					EnumChatFormatting.GREEN + StatCollector.translateToLocal("Reload game")));
		else
			buttonList.add(reload = new GuiButton(2, this.width / 2 - 100, height - 30, 200, 20,
					EnumChatFormatting.GREEN + StatCollector.translateToLocal("Back")));
		reload.enabled = false;
		PC_ModuleManager.updatedCount = 0;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		GL11.glEnable(GL11.GL_BLEND);
		this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
		GL11.glDisable(GL11.GL_BLEND);
		super.drawScreen(par1, par2, par3);
		if (!isPack)
			if (PC_ModuleManager.updatedCount > 0) {
				reload.enabled = true;
				if (PC_ModuleManager.errors.isEmpty())
					drawCenteredString(fontRendererObj,
							EnumChatFormatting.GREEN + StatCollector.translateToLocal("Downloaded success"), width / 2,
							height / 2 - 15, 0xffffff);
				else {
					drawCenteredString(fontRendererObj,
							EnumChatFormatting.RED + StatCollector.translateToLocal("Download have fails"), width / 2,
							10, 0xffffff);
					for (int i = 0; i < PC_ModuleManager.errors.size(); i++) {
						drawString(fontRendererObj, PC_ModuleManager.errors.get(i), 20, i * 10 + 20, 0xffffff);
					}
				}
			} else
				drawCenteredString(fontRendererObj,
						EnumChatFormatting.GREEN + StatCollector.translateToLocal("Downloading in progress"), width / 2,
						height / 2 - 15, 0xffffff);
		else if (pack != null) {
			if (PC_ModuleManager.updatedCount == pack.getModules().size() * 2) {
				reload.enabled = true;
				if (PC_ModuleManager.errors.isEmpty())
					drawCenteredString(fontRendererObj,
							EnumChatFormatting.GREEN + StatCollector.translateToLocal("Downloaded success"), width / 2,
							height / 2 - 15, 0xffffff);
				else {
					drawCenteredString(fontRendererObj,
							EnumChatFormatting.RED + StatCollector.translateToLocal("Download have fails"), width / 2,
							10, 0xffffff);
					for (int i = 0; i < PC_ModuleManager.errors.size(); i++) {
						drawString(fontRendererObj, PC_ModuleManager.errors.get(i), 20, i * 10 + 20, 0xffffff);
					}
				}
			} else
				drawCenteredString(fontRendererObj,
						EnumChatFormatting.GREEN + StatCollector.translateToLocal("Downloading in progress"), width / 2,
						height / 2 - 15, 0xffffff);
		}

	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == 1) {
			mc.shutdown();
		}
		if (par1GuiButton.id == 2) {
			mc.displayGuiScreen(new PC_GuiManager());
		}
	}

	@Override
	protected void keyTyped(char par1, int par2) {
	}
}
