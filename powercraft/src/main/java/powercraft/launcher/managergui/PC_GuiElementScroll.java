package powercraft.launcher.managergui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class PC_GuiElementScroll extends PC_GuiScroll {

	private List<ScrollElement> elements = new ArrayList<ScrollElement>();
	private ScrollElement activeElement;

	public PC_GuiElementScroll(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public ScrollElement getActiveElement() {
		return activeElement;
	}

	public void add(ScrollElement element) {
		ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		element.setWorldAndResolution(mc, resolution.getScaledWidth(), resolution.getScaledHeight());
		element.setElementWidth(gswidth - 8);
		elements.add(element);
	}

	public void remove(ScrollElement element) {
		if (activeElement == element)
			activeElement = null;
		elements.remove(element);
	}

	public List<ScrollElement> getElements() {
		return new ArrayList<ScrollElement>(elements);
	}

	@Override
	public int getElementCount() {
		return elements.size();
	}

	@Override
	public int getElementHeight(int element) {
		return elements.get(element).getHeight();
	}

	@Override
	public boolean isElementActive(int element) {
		return activeElement == elements.get(element) && activeElement.showSelection();
	}

	@Override
	public void drawElement(int element, int par1, int par2, float par3) {
		elements.get(element).drawScreen(par1, par2, par3);
	}

	@Override
	public void clickElement(int element, int par1, int par2, int par3) {
		if (element == -1) {
			activeElement = null;
		} else {
			activeElement = elements.get(element);
			activeElement.mouseClicked(par1, par2, par3);
		}
	}

	@Override
	public void keyTyped(char par1, int par2) {
		if (activeElement != null) {
			activeElement.keyTyped(par1, par2);
		}
		super.keyTyped(par1, par2);
	}

	@Override
	public void mouseMovedOrUp(int par1, int par2, int par3) {
		super.mouseMovedOrUp(par1, par2, par3);
		if (activeElement != null) {
			activeElement.mouseMovedOrUp(par1, par2, par3);
		}
	}

	@Override
	public void updateScreen() {
		for (ScrollElement scrollElement : elements) {
			scrollElement.updateScreen();
		}
		super.updateScreen();
	}

	public static abstract class ScrollElement extends GuiScreen {

		protected int elementWidth;

		public void setElementWidth(int elementWidth) {
			this.elementWidth = elementWidth;
		}

		public void keyTyped(char par1, int par2) {
			super.keyTyped(par1, par2);
		}

		public void mouseClicked(int par1, int par2, int par3) {
			super.mouseClicked(par1, par2, par3);
		}

		public void mouseMovedOrUp(int par1, int par2, int par3) {
			super.mouseMovedOrUp(par1, par2, par3);
		}

		public abstract boolean showSelection();

		public abstract int getHeight();

	}

}
