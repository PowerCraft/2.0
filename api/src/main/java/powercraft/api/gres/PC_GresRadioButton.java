package powercraft.api.gres;

import java.util.HashSet;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.mod_PowerCraft;

/**
 * Resizable GUI radio button
 * 
 * @author XOR19, MightyPork
 * @copy (c) 2012
 */
public class PC_GresRadioButton extends PC_GresWidget {

	/**
	 * Radio Group for Radio Button
	 * 
	 * @author MightyPork
	 * @copy (c) 2012
	 */
	public static class PC_GresRadioGroup extends HashSet<PC_GresRadioButton> {
		public PC_GresRadioButton getChecked() {
			Iterator<PC_GresRadioButton> i = iterator();
			while (i.hasNext()) {
				PC_GresRadioButton rb = i.next();
				if (rb.isChecked())
					return rb;
			}
			return null;
		}
	}

	private static final int WIDTH = 11;
	private boolean checked = false;
	private PC_GresRadioGroup radioGroup;

	/**
	 * Radio btn
	 * 
	 * @param title label
	 * @param group radio group
	 */
	public PC_GresRadioButton(String title, PC_GresRadioGroup group) {
		super(title);
		canAddWidget = false;
		color[textColorEnabled] = 0x000000;
		color[textColorShadowEnabled] = 0xAAAAAA;
		color[textColorDisabled] = 0x707070;
		color[textColorShadowDisabled] = 0xAAAAAA;

		radioGroup = group;
		radioGroup.add(this);
	}

	/**
	 * @return is button selected
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * Set selected state, if "true", clear all others.
	 * 
	 * @param state on/off
	 * @return this
	 */
	public PC_GresRadioButton check(boolean state) {
		checked = state;

		if (checked) {
			for (PC_GresRadioButton btn : radioGroup) {
				if (btn != this) {
					btn.check(false);
				}
			}
		}

		return this;
	}

	@Override
	public PC_VecI calcSize() {

		size.setTo(getStringWidth(text), getLineHeight(), 0).add(WIDTH + 3, 0, 0);

		if (size.y < WIDTH) {
			size.y = WIDTH;
		}

		if (size.x < minSize.x) {
			size.x = minSize.x;
		}

		return size.copy();
	}

	@Override
	public PC_VecI getMinSize() {
		return calcSize();
	}

	@Override
	public void calcChildPositions() {

	}

	@Override
	protected PC_RectI render(PC_VecI offsetPos, PC_RectI scissorOld, double scale) {
		String texture = imgdir + "widgets.png";
		mc.renderEngine.bindTexture(new ResourceLocation(mod_PowerCraft.MODID, texture));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int state = 0;
		if (isChecked()) {
			state = 1;
		}
		if (!isEnabled()) {
			state += 2;
		}

		drawTexturedModalRect(pos.x + offsetPos.x, pos.y + offsetPos.y, WIDTH * state, WIDTH, WIDTH, WIDTH);

		drawString(text, offsetPos.x + pos.x + WIDTH + 3, offsetPos.y + pos.y + 2);

		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mpos) {
		return MouseOver.THIS;
	}

	@Override
	public boolean mouseClick(PC_VecI mpos, int key) {
		if (!enabled) {
			return false;
		}
		if (key != -1) {
			check(true);
			return true;
		}

		// if (key != -1) mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		return false;
	}

	@Override
	public void mouseMove(PC_VecI mpos) {

	}

	@Override
	public boolean keyTyped(char c, int key) {
		return false;
	}

	@Override
	public void mouseWheel(int i) {
	}

	@Override
	public void addedToWidget() {
	}
}
