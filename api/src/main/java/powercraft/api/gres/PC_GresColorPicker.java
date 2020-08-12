package powercraft.api.gres;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import powercraft.api.utils.PC_RectI;
import powercraft.api.utils.PC_VecI;

/**
 * Color picker
 * 
 * @author MightyPork
 *
 */
public class PC_GresColorPicker extends PC_GresWidget {

	private int[][] colorArray = new int[40][20];
	private int px = 1;
	private int color = 0x000000;
	private int lx = -1, ly = -1;

	/**
	 * color picker
	 * 
	 * @param color  initial color
	 * @param width  width px
	 * @param height height px
	 */
	public PC_GresColorPicker(int color, int width, int height) {
		super("");
		canAddWidget = false;
		colorArray = new int[width][height];
		size = calcSize();

		if (parent != null)
			parent.calcChildPositions();

		float[] hsv = { 0, 1, 1 };

		int he = colorArray[0].length;
		int wi = colorArray.length;

		int col = 0;
		for (hsv[0] = 0; hsv[0] <= 1; hsv[0] += 1F / (wi - 2), col++) {
			if (col >= colorArray.length)
				col = colorArray.length - 1;
			int i = 0;
			for (int row = 0; row <= he / 2; row++) {
				float mp = (1F / (colorArray[0].length / 2)) * i++;
				Color cc = new Color();
				cc.fromHSB(hsv[0], mp, hsv[2]);
				colorArray[col][row] = clr(cc.getRed(), cc.getGreen(), cc.getBlue());
			}
			i = 0;
			for (int row = he / 2 + 1; row < he; row++) {
				float mp = 1F - (1F / (colorArray[0].length / 2)) * i++;
				Color cc = new Color();
				cc.fromHSB(hsv[0], hsv[1], mp);
				colorArray[col][row] = clr(cc.getRed(), cc.getGreen(), cc.getBlue());
			}
		}

		for (int row = 0; row < colorArray[0].length; row++) {
			Color cc = new Color();
			cc.fromHSB(0, 0, row * (1F / colorArray[0].length));
			colorArray[colorArray.length - 1][row] = clr(cc.getRed(), cc.getGreen(), cc.getBlue());
			colorArray[colorArray.length - 2][row] = clr(cc.getRed(), cc.getGreen(), cc.getBlue());
		}

		this.setColor(color);
	}

	/**
	 * Set selected color
	 * 
	 * @param color color rgb
	 */
	public void setColor(int color) {
		lx = -1;
		ly = -1;
		for (int x = 0; x < colorArray.length; x++) {
			for (int y = 0; y < colorArray[0].length; y++) {
				if (color == colorArray[x][y]) {
					lx = x;
					ly = y;
					break;
				}

			}
		}
		this.color = color;
	}

	/**
	 * get selected color
	 * 
	 * @return color rgb
	 */
	public int getColor() {
		return color;
	}

	private int clr(float r, float g, float b) {
		return Math.round(Math.min(255, Math.max(0, r))) << 16 | Math.round(Math.min(255, Math.max(0, g))) << 8
				| Math.round(Math.min(255, Math.max(0, b)));
	}

	@Override
	public PC_VecI calcSize() {
		if (!visible)
			return zerosize;
		if (colorArray == null)
			return zerosize;
		else
			return new PC_VecI(Math.round(colorArray.length * px), Math.round(colorArray[0].length * px));
	}

	@Override
	public void calcChildPositions() {
	}

	private boolean dragging = false;

	@Override
	protected PC_RectI render(PC_VecI posOffset, PC_RectI scissorOld, double scale) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		double posX, posY, pixelW, pixelH;
		int color;
		pixelW = 1.0D;
		pixelH = 1.0D;
		if (colorArray != null) {
			for (int x = 0; x < colorArray.length; x++) {
				for (int y = 0; y < colorArray[0].length; y++) {
					color = colorArray[x][y];
					if (color != -1) {

						if (System.currentTimeMillis() % 1000 < 500 && x == lx && y == ly) {
							color = ~color;
						}

						posX = x * px + pos.x + posOffset.x;
						posY = y * px + pos.y + posOffset.y;
						tessellator.setColorRGBA((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, 255);
						tessellator.addVertex(posX, posY, 0.0D);
						tessellator.addVertex(posX + pixelW * px, posY, 0.0D);
						tessellator.addVertex(posX + pixelW * px, posY + pixelH * px, 0.0D);
						tessellator.addVertex(posX, posY + pixelH * px, 0.0D);
						tessellator.addVertex(posX + pixelW * px, posY, 0.0D);
						tessellator.addVertex(posX, posY, 0.0D);
						tessellator.addVertex(posX, posY + pixelH * px, 0.0D);
						tessellator.addVertex(posX + pixelW * px, posY + pixelH * px, 0.0D);
					}
				}
			}
		}
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		return null;
	}

	@Override
	public MouseOver mouseOver(PC_VecI mousePos) {
		return MouseOver.THIS;
	}

	@Override
	public boolean mouseClick(PC_VecI mousePos, int key) {
		dragging = (key != -1);
		lx = mousePos.x / px;
		ly = mousePos.y / px;
		if (lx < 0 || ly < 0 || lx >= colorArray.length || ly >= colorArray[lx].length)
			return false;
		color = colorArray[lx][ly];
		return true;
	}

	@Override
	public void mouseMove(PC_VecI mousePos) {
		if (dragging) {
			mouseClick(mousePos, 0);
			gui.registerAction(this);
		}
	}

	@Override
	public void addedToWidget() {
	}

	@Override
	public void mouseWheel(int i) {
	}

	@Override
	public boolean keyTyped(char c, int key) {
		return false;
	}

}
