package powercraft.api.gres;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.mod_PowerCraft;

/**
 * Resizable GUI button with image
 * 
 * @author XOR19, Rapus95, MightyPork
 * @copy (c) 2012
 */
public class PC_GresButtonImage extends PC_GresButton {

	private String texture;
	private PC_VecI textureLeftTop, imageSize;

	/**
	 * @param imageFile
	 * @param leftTop
	 * @param imageSize
	 */
	public PC_GresButtonImage(String imageFile, PC_VecI leftTop, PC_VecI imageSize) {
		super("");
		canAddWidget = false;
		minSize.setTo(imageSize);
		this.texture = imageFile;
		buttonScale = new PC_VecI(4, 4);
		this.textureLeftTop = leftTop;
		this.imageSize = imageSize;
	}

	@Override
	public PC_VecI calcSize() {
		if (!visible)
			return zerosize;

		if (buttonScale == null)
			buttonScale = new PC_VecI(4, 4);
		if (size == null)
			size = new PC_VecI();
		if (imageSize == null)
			imageSize = new PC_VecI();

		size.setTo(imageSize).add(buttonScale).add(buttonScale);

		if (size.x < minSize.x) {
			size.x = minSize.x;
		}
		if (size.y < minSize.y) {
			size.y = minSize.y;
		}

		return size.copy();
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

		renderTextureSliced(offsetPos, imgdir + "button.png", size, new PC_VecI(0, state * 50), new PC_VecI(256, 50),
				new PC_RectI(2, 2, 2, 3));

		// and here goes the image
		mc.renderEngine.bindTexture(new ResourceLocation(mod_PowerCraft.MODID, texture));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		drawTexturedModalRect(pos.x + offsetPos.x + (size.x - imageSize.x) / 2,
				pos.y + offsetPos.y + (size.y - imageSize.y) / 2, textureLeftTop.x, textureLeftTop.y, imageSize.x,
				imageSize.y);

		GL11.glDisable(GL11.GL_BLEND);

		return null;
	}

}
