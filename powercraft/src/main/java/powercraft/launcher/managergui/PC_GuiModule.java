package powercraft.launcher.managergui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import powercraft.launcher.loader.PC_ModuleObject;
import powercraft.launcher.loader.PC_ModuleVersion;
import powercraft.launcher.manager.PC_ModuleManager;
import powercraft.launcher.manager.PC_ModuleManager.ModuleUpdateInfo;
import powercraft.launcher.manager.PC_UpdateXMLFile.XMLModuleTag;
import powercraft.launcher.manager.PC_UpdateXMLFile.XMLPackTag;
import powercraft.launcher.manager.PC_UpdateXMLFile.XMLVersionTag;

public class PC_GuiModule extends GuiScreen {

	private PC_GuiScrollModuleAndPackScroll moduleAndPack;
	private PC_GuiManager guiUpdate;
	private int activeElement = -1;
	private List<String> lText;
	protected int gsx;
	protected int gsy;
	protected int gswidth;
	protected int gsheight;
	protected float scroll;
	protected int my = -1;
	protected boolean bar;

	public PC_GuiModule(int x, int y, int width, int height, PC_GuiScrollModuleAndPackScroll moduleAndPack,
			PC_GuiManager guiUpdate) {
		this.moduleAndPack = moduleAndPack;
		this.gsx = x;
		this.gsy = y;
		this.gswidth = width;
		this.gsheight = height;
		this.guiUpdate = guiUpdate;
		this.fontRendererObj = guiUpdate.mc.fontRenderer;
		if (this.gswidth - 12 > 10) {
			lText = fontRendererObj.listFormattedStringToWidth(StatCollector.translateToLocal("Select module or pack"),
					this.gswidth - 12);
		}
	}

	public void draw(int par1, int par2, float par3, FontRenderer fr) {
		Object obj = moduleAndPack.getSelection();
		String disp = getObjectString(obj);
		if (disp != null) {
			fr.drawString(disp, gsx, gsy, 0xffffff);
		} else {
			for (int i = 0; i < lText.size(); i++) {
				fr.drawString(lText.get(i), gsx, i * 10 + gsy, 0xFFFFFFFF);
			}
		}
	}

	public String getObjectString(Object obj) {
		String ret = null;
		if (obj instanceof XMLPackTag) {
			XMLPackTag pt = (XMLPackTag) obj;
			lText = new ArrayList<String>();
			lText.add(EnumChatFormatting.GREEN + "v" + pt.getVersions().get(0).getVersion().toString());
			List<String> stL = fontRendererObj.listFormattedStringToWidth(pt.getVersions().get(0).getInfo(),
					this.gsx - 12);
			for (String st : stL)
				lText.add(st);
			lText.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("Modules in pack:"));
			for (XMLModuleTag m : pt.getModules()) {
				if (m != null)
					lText.add(m.getName());
			}
			guiUpdate.install.enabled = true;
			guiUpdate.install.visible = true;
			guiUpdate.enabled.visible = false;
			// guiUpdate.config.visible = false;
			guiUpdate.delete.visible = false;
			guiUpdate.update.visible = false;
			return ret;
		} else if (obj instanceof ModuleUpdateInfo) {
			ModuleUpdateInfo ui = (ModuleUpdateInfo) obj;
			lText = new ArrayList<String>();
			lText.add(EnumChatFormatting.GREEN + "v" + ui.newVersion.getVersion().toString());
			List<String> stL = fontRendererObj.listFormattedStringToWidth(ui.newVersion.getInfo(), this.gsx - 12);
			for (String st : stL)
				lText.add(st);
			guiUpdate.install.enabled = true;
			guiUpdate.install.visible = true;
			guiUpdate.enabled.visible = false;
			// guiUpdate.config.visible = false;
			guiUpdate.update.visible = false;
			guiUpdate.delete.visible = false;
			return ret;
		} else if (obj instanceof PC_ModuleObject) {
			PC_ModuleObject mod = (PC_ModuleObject) obj;
			ret = EnumChatFormatting.GREEN + "v" + mod.getStandartVersion().getVersion().toString();
			if (!mod.getConfig().getBoolean("loader.enabled"))
				guiUpdate.enabled.displayString = EnumChatFormatting.RED + StatCollector.translateToLocal("Disabled");
			else
				guiUpdate.enabled.displayString = EnumChatFormatting.GREEN + StatCollector.translateToLocal("Enabled");
			if (mod.getModuleName().equalsIgnoreCase("api") || mod.getModuleName().equalsIgnoreCase("core")) {
				guiUpdate.enabled.enabled = false;
				guiUpdate.delete.enabled = false;
				guiUpdate.enabled.displayString = EnumChatFormatting.GREEN + StatCollector.translateToLocal("Enabled");
			} else {
				guiUpdate.enabled.enabled = true;
				guiUpdate.delete.enabled = true;
			}
			guiUpdate.install.visible = false;
			guiUpdate.enabled.visible = true;
			// guiUpdate.config.visible = true;
			guiUpdate.delete.visible = true;
			if (!PC_ModuleManager.moduleListToUpdate.isEmpty()) {
				guiUpdate.update.visible = true;
				guiUpdate.update.enabled = true;
			}
			return ret;
		} else {
			guiUpdate.install.visible = false;
			guiUpdate.enabled.visible = false;
			// guiUpdate.config.visible = false;
			guiUpdate.delete.visible = false;
			guiUpdate.update.enabled = false;
			guiUpdate.update.visible = false;
		}
		return ret;
	}
}
