package powercraft.api.gres;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.FontRenderer;
import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * Resizable GUI button
 * 
 * @author XOR19, Rapus95, MightyPork
 * @copy (c) 2012
 */
public class PC_GresButton extends PC_GresWidget {

	/**
	 * Button inner padding - distance from the borders. Added twice, once on each
	 * side.
	 */
	protected PC_VecI buttonScale = new PC_VecI(6, 6);

	/** Flag that is currently under pressed mouse */
	protected boolean isClicked = false;

	/**
	 * @param label button label
	 */
	public PC_GresButton(String label) {
		super(label);
		canAddWidget = false;
		minSize.setTo(60, 0, 0);
		buttonScale = new PC_VecI(6, 6);
	}

	/**
	 * Set distance from text to borders of the box.
	 * 
	 * @param x distance horizontally
	 * @param y distance vertically
	 * @return this
	 */
	public PC_GresButton setButtonPadding(int x, int y) {
		buttonScale = new PC_VecI(x, y);
		return this;
	}

	@Override
	public PC_VecI calcSize() {
		if (!visible)
			return zerosize;
		FontRenderer fontRenderer = getFontRenderer();

		if (buttonScale == null)
			buttonScale = new PC_VecI(6, 6);

		size.setTo(fontRenderer.getStringWidth(text), fontRenderer.FONT_HEIGHT, 0).add(buttonScale).add(buttonScale);

		if (size.x < minSize.x) {
			size.x = minSize.x;
		}
		if (size.y < minSize.y) {
			size.y = minSize.y;
		}

		return size.copy();
	}

	@Override
	public void calcChildPositions() {

	}

	@Override
	protected PC_RectI render(PC_VecI offsetPos, PC_RectI scissorOld, double scale) {

		int state;
		if (!enabled || !parent.enabled) {
			state = 0; // disabled
		} else if (isClicked) {
			state = 3; // enabled and clicked
		} else if (isMouseOver) {
			state = 2; // enabled and hover
		} else {
			state = 1; // enabled and not hover
		}

		int txC = 0xe0e0e0;

		if (state == 0) {
			txC = 0xa0a0a0; // dark
		}
		if (state == 1) {
			txC = 0xe0e0e0; // light
		}
		if (state > 1) {
			txC = 0xffffa0; // yellow
		}

		renderTextureSliced(offsetPos, imgdir + "button.png", size, new PC_VecI(0, state * 50), new PC_VecI(256, 50),
				new PC_RectI(2, 2, 2, 3));

		drawCenteredString(getFontRenderer(), text, offsetPos.x + pos.x + size.x / 2,
				offsetPos.y + pos.y + (size.y - getFontRenderer().FONT_HEIGHT) / 2, txC);

		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mpos) {
		isMouseOver = true;
		return MouseOver.THIS;
	}

	@Override
	public boolean mouseClick(PC_VecI mpos, int key) {
		if (!enabled || !visible) {
			return false;
		}
		if (!parent.enabled) {
			return false;
		}
		if (isClicked && key == -1) {
			isClicked = false;
			return true;
		}
		isClicked = key == -1 ? false : true;
		// if (key != -1) mc.getSoundHandler().playSound(ISound.AttenuationType.);
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
