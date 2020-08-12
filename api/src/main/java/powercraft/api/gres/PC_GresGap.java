package powercraft.api.gres;

import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * GUI gap with fixed size
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PC_GresGap extends PC_GresWidget {

	/**
	 * gap
	 * 
	 * @param width  min width
	 * @param height min height
	 */
	public PC_GresGap(int width, int height) {
		super(width, height);
		canAddWidget = false;
	}

	@Override
	public PC_VecI calcSize() {
		if (!visible)
			return zerosize;
		return minSize.copy();
	}

	@Override
	protected PC_RectI render(PC_VecI posOffset, PC_RectI scissorOld, double scale) {
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
