package powercraft.launcher.managergui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;
import powercraft.launcher.PC_Launcher;
import powercraft.launcher.loader.PC_ModuleObject;
import powercraft.launcher.manager.PC_ModuleManager;
import powercraft.launcher.manager.PC_ModuleManager.ModuleUpdateInfo;
import powercraft.launcher.manager.PC_UpdateXMLFile.XMLPackTag;

public class PC_GuiScrollModuleAndPackScroll extends PC_GuiScroll {

	private int activeElement = -1;
	private List<ModuleUpdateInfo> moduleList = new ArrayList<ModuleUpdateInfo>();
	private List<XMLPackTag> packList = new ArrayList<XMLPackTag>();

	public PC_GuiScrollModuleAndPackScroll(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public int getElementCount() {
		moduleList.clear();
		for (ModuleUpdateInfo m : PC_ModuleManager.moduleList) {
			moduleList.add(m);
		}
		for (ModuleUpdateInfo m : PC_ModuleManager.moduleList) {
			if (m != null)
				for (PC_ModuleObject m2 : PC_Launcher.getModules().values()) {
					if (m2 != null)
						if (m.xmlModule.getName().equals(m2.getModuleName())) {
							moduleList.remove(m);
						}
				}
		}
		return PC_ModuleManager.packList.size() + moduleList.size() + PC_Launcher.getModules().size()+3;
	}

	@Override
	public int getElementHeight(int element) {
		return 12;
	}

	@Override
	public boolean isElementActive(int element) {
		return activeElement == element;
	}

	@Override
	public void drawElement(int element, int par1, int par2, float par3) {
		String displayText = null;
		if (element == 1) {
			displayText = "\u2193" + StatCollector.translateToLocal("Installed modules") + "\u2193";
		} else if (element == PC_Launcher.getModules().size() + 2) {
			displayText = "\u2193" + StatCollector.translateToLocal("Packs") + "\u2193";
		} else if (element == PC_Launcher.getModules().size() + PC_ModuleManager.packList.size() + 3)
			displayText = "\u2193" + StatCollector.translateToLocal("Modules") + "\u2193";
		if (displayText != null) {
			int width = fontRendererObj.getStringWidth(displayText);
			drawHorizontalLine(0, (this.gswidth - 8 - width) / 2 - 2, 6, 0xFFFFFFFF);
			drawHorizontalLine((this.gswidth - 8 + width) / 2 + 2, this.gswidth - 8, 6, 0xFFFFFFFF);
			drawCenteredString(fontRendererObj, displayText, (this.gswidth - 8) / 2, 1, 0xFFFFFFFF);
		} else if (element == 0) {
			drawString(fontRendererObj, "API", 2, 2, 0xffffff);
		} else if (element < PC_Launcher.getModules().size() + 2) {
			PC_ModuleObject module = (PC_ModuleObject) PC_Launcher.getModules().values().toArray()[element - 2];
			if(module != null)
				drawString(fontRendererObj, module.getModuleName(), 2, 2, 0xffffff);
		} else if (element < PC_Launcher.getModules().size() + PC_ModuleManager.packList.size() + 3) {
			XMLPackTag pack = PC_ModuleManager.packList.get(element - PC_Launcher.getModules().size() - 3);
			if(pack != null)
				drawString(fontRendererObj, pack.getName(), 2, 2, 0xffffff);
		} else {
			ModuleUpdateInfo module = moduleList
					.get(element - PC_Launcher.getModules().size() - PC_ModuleManager.packList.size() - 3);
			drawString(fontRendererObj, module.xmlModule.getName(), 2, 2, 0xffffff);

		}
	}

	@Override
	public void clickElement(int element, int par1, int par2, int par3) {
		if (!(element == 1 || element == PC_Launcher.getModules().size() + 2
				|| element == PC_Launcher.getModules().size() + PC_ModuleManager.packList.size() + 3)) {
			activeElement = element;
		}
		if (element == -1)
			activeElement = -1;
	}

	public Object getSelection() {
		if (activeElement == 0) {
			PC_ModuleObject module = PC_Launcher.getAPI();
			if (module != null) {
				return module;
			}
			return null;
		}
		if (activeElement < 0) {
			return null;
		}
		if (activeElement > 1 && activeElement < PC_Launcher.getModules().size() + 2) {
			PC_ModuleObject module = (PC_ModuleObject) PC_Launcher.getModules().values().toArray()[activeElement - 2];
			return module;
		} else if (activeElement > PC_Launcher.getModules().size() + PC_ModuleManager.packList.size() + 3) {
			ModuleUpdateInfo module = moduleList
					.get(activeElement - (PC_Launcher.getModules().size() + PC_ModuleManager.packList.size() + 3));
			return module;
		} else if (activeElement < PC_Launcher.getModules().size() + PC_ModuleManager.packList.size() + 3) {
			if (activeElement - PC_Launcher.getModules().size() - 3 >= 0
					&& activeElement - PC_Launcher.getModules().size() - 3 <= PC_ModuleManager.packList.size()) {
				XMLPackTag pack = PC_ModuleManager.packList.get(activeElement - PC_Launcher.getModules().size() - 3);
				return pack;
			} else
				return null;
		}
		return null;
	}

}
