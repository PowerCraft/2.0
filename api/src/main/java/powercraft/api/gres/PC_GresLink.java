package powercraft.api.gres;

import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * Resizable GUI hypertext link-like widget
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PC_GresLink extends PC_GresWidget {

	private boolean isClicked = false;
	private boolean underline;

	/**
	 * Link button
	 * 
	 * @param label label
	 */
	public PC_GresLink(String label) {
		super(label);
		canAddWidget = false;
		minSize.setTo(10, 0, 0);
		setColor(textColorEnabled, 0x000000);
		setColor(textColorDisabled, 0xa0a0a0);
		setColor(textColorHover, 0x0000ff);
		setColor(textColorClicked, 0xff0000);
	}

	@Override
	public PC_VecI calcSize() {

		size.setTo(getStringWidth(text), getLineHeight(), 0).add(2, 0, 0);

		if (size.x < minSize.x) {
			size.x = minSize.x;
		}

		return size.copy();
	}

	/**
	 * Set underline
	 * 
	 * @param underline
	 * @return this
	 */
	public PC_GresLink setUnderline(boolean underline) {
		this.underline = underline;
		return this;
	}

	@Override
	public void calcChildPositions() {

	}

	@Override
	protected PC_RectI render(PC_VecI offsetPos, PC_RectI scissorOld, double scale) {

		int state;
		if (!enabled) {
			state = 0; // disabled
		} else if (isClicked) {
			state = 3; // enabled and clicked
		} else if (isMouseOver) {
			state = 2; // enabled and hover
		} else {
			state = 1; // enabled and not hover
		}

		int textColor = getColor(textColorEnabled);

		if (state == 0) {
			textColor = getColor(textColorDisabled); // gray
		}
		if (state == 1) {
			textColor = getColor(textColorEnabled); // black
		}
		if (state == 2) {
			textColor = getColor(textColorHover); // blue, hover
		}
		if (state == 3) {
			textColor = getColor(textColorClicked); // red, activated
		}

		drawStringColor(text, offsetPos.x + pos.x, offsetPos.y + pos.y, textColor);

		int yy = offsetPos.y + pos.y + getFontRenderer().FONT_HEIGHT;

		if (underline)
			drawRect(offsetPos.x + pos.x, yy, offsetPos.x + size.x + pos.x + 1, yy + 1, textColor);

		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mpos) {
		isMouseOver = true;
		return MouseOver.THIS;
	}

	@Override
	public boolean mouseClick(PC_VecI mpos, int key) {
		if (!enabled) {
			return false;
		}
		if (isClicked && key == -1) {
			isClicked = false;
			return true;
		}
		isClicked = key == -1 ? false : true;

		// if (key != -1) mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		return false;
	}

	@Override
	public boolean keyTyped(char c, int key) {
		return false;
	}

	@Override
	public void mouseMove(PC_VecI mpos) {
		if (mpos.x < 0 || mpos.x >= size.x || mpos.y < 0 || mpos.y >= size.y || mouseOver(mpos) == MouseOver.NON) {
			isClicked = false;
		}
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
