package powercraft.api.gres;

import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * Resizable GUI horizontal separation line
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PC_GresSeparatorH extends PC_GresWidget {

	private int lineColor = 0x555555;

	/**
	 * horizontal line
	 * 
	 * @param width  min width
	 * @param height height
	 */
	public PC_GresSeparatorH(int width, int height) {
		super(width, height);
		canAddWidget = false;
		setMinHeight(3);
	}

	/**
	 * @return the line color
	 */
	public int getLineColor() {
		return lineColor;
	}

	/**
	 * Set line color.
	 * 
	 * @param lineColor the line color to set
	 * @return this;
	 */
	public PC_GresWidget setLineColor(int lineColor) {
		this.lineColor = lineColor;
		return this;
	}

	@Override
	public PC_VecI calcSize() {
		return minSize.copy();
	}

	@Override
	protected PC_RectI render(PC_VecI off, PC_RectI scissorOld, double scale) {
		drawRect(off.x + pos.x, off.y + size.y / 2 + pos.y, off.x + size.x + pos.x + 1, off.y + size.y / 2 + pos.y + 1,
				lineColor | 0xff000000);
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
