package powercraft.api.gres;

import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * Resizable GUI plain text label
 * 
 * @author XOR19, Rapus95, MightyPork
 * @copy (c) 2012
 */
public class PC_GresLabel extends PC_GresWidget {

	/**
	 * Text label
	 * 
	 * @param label text
	 */
	public PC_GresLabel(String label) {
		super(label);
		canAddWidget = false;

		alignH = PC_GresAlign.LEFT;
		widgetMargin = 4;
	}

	@Override
	public PC_VecI calcSize() {
		size.setTo(getStringWidth(text), getLineHeight(), 0);
		if (size.x < minSize.x) {
			size.x = minSize.x;
		}
		return size.copy();
	}

	@Override
	protected PC_RectI render(PC_VecI offsetPos, PC_RectI scissorOld, double scale) {
		int wid = getStringWidth(text);
		int xstart = offsetPos.x + pos.x;

		switch (alignH) {
		case CENTER:
			xstart = xstart + size.x / 2 - wid / 2;
			break;
		case RIGHT:
			xstart = xstart + size.x - wid;
		default:
		case LEFT:
			break;
		}

		drawString(text, xstart, offsetPos.y + pos.y);

		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mpos) {
		return MouseOver.THIS;
	}

	@Override
	public boolean mouseClick(PC_VecI mpos, int key) {
		return false;
	}

	@Override
	public boolean keyTyped(char c, int key) {
		return false;
	}

	@Override
	public void calcChildPositions() {

	}

	@Override
	public void mouseMove(PC_VecI mpos) {

	}

	@Override
	public PC_VecI getMinSize() {
		return calcSize();
	}

	@Override
	public void mouseWheel(int i) {
	}

	@Override
	public void addedToWidget() {
	}
}
