package powercraft.launcher.managergui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.util.EnumChatFormatting;
import powercraft.launcher.manager.PC_ModuleManager;
import powercraft.launcher.manager.PC_ModuleManager.ModuleUpdateInfo;

public class PC_GuiScrollUpdate extends PC_GuiScroll {

	Map<String, ArrayList<String>> uText = new TreeMap<String, ArrayList<String>>();

	public PC_GuiScrollUpdate(int x, int y, int width, int height) {
		super(x, y, width, height);
		uText = new TreeMap<String, ArrayList<String>>();
	}

	@Override
	public int getElementCount() {
		return PC_ModuleManager.moduleListToUpdate.size();
	}

	@Override
	public int getElementHeight(int element) {
		List<ModuleUpdateInfo> muiList = PC_ModuleManager.moduleListToUpdate;
		ArrayList<String> lText = new ArrayList<String>();
		lText.addAll(getTextFor(muiList.get(element)));
		int h = lText.size() * 10;
		return h;
	}

	@Override
	public boolean isElementActive(int element) {
		return false;
	}

	public ArrayList<String> getTextFor(ModuleUpdateInfo mui) {
		if (!uText.containsKey(mui.xmlModule.getName())) {
			ArrayList<String> lText = new ArrayList<String>();
			lText.add(EnumChatFormatting.GREEN + mui.xmlModule.getName() + " - v"
					+ mui.newVersion.getVersion().toString());
			String[] stL = StringUtils.split(mui.newVersion.getInfo().trim(), "\\\n");
			for (String st : stL) {
				if (st.equalsIgnoreCase("n"))
					continue;
				st = st.replaceAll("\\s+", " ");
				lText.add(st);
			}
			lText.add("------------------------------------------------");
			uText.put(mui.xmlModule.getName(), lText);
			return lText;
		} else {
			return uText.get(mui.xmlModule.getName());
		}
	}

	@Override
	public void drawElement(int element, int par1, int par2, float par3) {
		List<ModuleUpdateInfo> muiList = PC_ModuleManager.moduleListToUpdate;
		ArrayList<String> lText = new ArrayList<String>();
		lText.addAll(getTextFor(muiList.get(element)));

		for (int i = 0; i < lText.size(); i++) {
			fontRendererObj.drawString(lText.get(i), gsx, i * 10, 0xFFFFFFFF);
		}
	}

	@Override
	public void clickElement(int element, int par1, int par2, int par3) {
	}

}
