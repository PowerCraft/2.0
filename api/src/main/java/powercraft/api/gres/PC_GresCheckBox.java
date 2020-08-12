package powercraft.api.gres;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.mod_PowerCraft;

/**
 * Resizable GUI checkbox
 * 
 * @author XOR19, MightyPork
 * @copy (c) 2012
 */
public class PC_GresCheckBox extends PC_GresWidget {

	private static final int WIDTH = 11;
	private boolean checked = false;

	/**
	 * Resizable GUI checkbox
	 * 
	 * @param label checkbox label
	 */
	public PC_GresCheckBox(String label) {
		super(label);
		canAddWidget = false;
		color[textColorEnabled] = 0x000000;
		color[textColorShadowEnabled] = 0xAAAAAA;
		color[textColorDisabled] = 0x707070;
		color[textColorShadowDisabled] = 0xAAAAAA;
	}

	/**
	 * @return is checkbox checked?
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * Set checked state
	 * 
	 * @param state checked
	 * @return this
	 */
	public PC_GresCheckBox check(boolean state) {
		checked = state;
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

		drawTexturedModalRect(pos.x + offsetPos.x, pos.y + offsetPos.y, WIDTH * state, 0, WIDTH, WIDTH);

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
			checked ^= true;
		}

		// if (key != -1) mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		return true;
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
