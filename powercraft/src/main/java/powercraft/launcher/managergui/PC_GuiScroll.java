package powercraft.launcher.managergui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import powercraft.launcher.PC_LauncherClientUtils;

public abstract class PC_GuiScroll extends GuiScreen {

	protected int gsx;
	protected int gsy;
	protected int gswidth;
	protected int gsheight;
	protected float scroll;
	protected int my = -1;
	protected boolean bar;

	public PC_GuiScroll(int x, int y, int width, int height) {
		this.gsx = x;
		this.gsy = y;
		this.gswidth = width;
		this.gsheight = height;
		mc = PC_LauncherClientUtils.mc();
		ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		setWorldAndResolution(mc, resolution.getScaledWidth(), resolution.getScaledHeight());
	}

	public abstract int getElementCount();

	public abstract int getElementHeight(int element);

	public abstract boolean isElementActive(int element);

	public abstract void drawElement(int element, int par1, int par2, float par3);

	public abstract void clickElement(int element, int par1, int par2, int par3);

	public int getTotalHeight() {
		int height = 0;
		int count = getElementCount();
		for (int i = 0; i < count; i++) {
			height += getElementHeight(i) + 4;
		}
		return height;
	}

	public boolean displayScrollBar() {
		return getTotalHeight() > gsheight - 4;
	}

	private void bindAmountScrolled() {
		int var1 = this.getTotalHeight() - gsheight + 4;

		if (var1 < 0) {
			var1 = 0;
		}

		if (this.scroll < 0.0F) {
			this.scroll = 0.0F;
		}

		if (this.scroll > (float) var1) {
			this.scroll = (float) var1;
		}
	}

	public void drawScreen(int par1, int par2, float par3) {
		ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int scale = resolution.getScaleFactor();
		if (my != -1) {
			int nmy = resolution.getScaledHeight() - Mouse.getY() * resolution.getScaledHeight() / this.mc.displayHeight
					- 1;
			float scrollMultiplier = 1;
			if (bar) {
				int h = getTotalHeight() - gsheight + 4;

				if (h < 1) {
					h = 1;
				}

				int a = gsheight * gsheight / getTotalHeight();

				if (a < 32) {
					a = 32;
				}

				if (a > gsheight - 8) {
					a = gsheight - 8;
				}

				scrollMultiplier /= (float) (gsheight - a) / (float) h;
			} else {
				scrollMultiplier = -1;
			}
			scroll += (nmy - my) * scrollMultiplier;
			my = nmy;
		}
		bindAmountScrolled();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		Tessellator tessellator = Tessellator.instance;
		mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/options_background.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float var17 = 32.0F;
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_I(2105376);
		tessellator.addVertexWithUV(gsx, gsy + gsheight, 0.0D, (float) gsx / var17,
				(float) (gsy + gsheight + (int) this.scroll) / var17);
		tessellator.addVertexWithUV(gsx + gswidth, gsy + gsheight, 0.0D, (float) (gsx + gswidth) / var17,
				(float) (gsy + gsheight + (int) this.scroll) / var17);
		tessellator.addVertexWithUV(gsx + gswidth, gsy, 0.0D, (float) (gsx + gswidth) / var17,
				(float) (gsy + (int) this.scroll) / var17);
		tessellator.addVertexWithUV(gsx, gsy, 0.0D, (float) gsx / var17, (float) (gsy + (int) this.scroll) / var17);
		tessellator.draw();

		int drawY = -(int) scroll;
		int elementWidth = gswidth - 8;

		int count = getElementCount();
		for (int i = 0; i < count; i++) {
			int elementHeight = getElementHeight(i);
			if (drawY + elementHeight > 0) {
				GL11.glPushMatrix();
				GL11.glTranslatef(gsx + 4, gsy + drawY + 4, 0);
				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				GL11.glScissor(gsx * scale, mc.displayHeight - (gsy + gsheight) * scale, gswidth * scale,
						gsheight * scale);
				if (isElementActive(i)) {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					tessellator.startDrawingQuads();
					tessellator.setColorOpaque_I(8421504);
					tessellator.addVertexWithUV(-2, 2 + elementHeight, 0.0D, 0.0D, 1.0D);
					tessellator.addVertexWithUV(elementWidth - 6, 2 + elementHeight, 0.0D, 1.0D, 1.0D);
					tessellator.addVertexWithUV(elementWidth - 6, -2, 0.0D, 1.0D, 0.0D);
					tessellator.addVertexWithUV(-2, -2, 0.0D, 0.0D, 0.0D);
					tessellator.setColorOpaque_I(0);
					tessellator.addVertexWithUV(-1, 1 + elementHeight, 0.0D, 0.0D, 1.0D);
					tessellator.addVertexWithUV(elementWidth - 7, 1 + elementHeight, 0.0D, 1.0D, 1.0D);
					tessellator.addVertexWithUV(elementWidth - 7, -1, 0.0D, 1.0D, 0.0D);
					tessellator.addVertexWithUV(-1, -1, 0.0D, 0.0D, 0.0D);
					tessellator.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}
				int w = elementHeight * scale;
				int bottom = mc.displayHeight - (Math.max(gsy + drawY + 4, gsy) + elementHeight) * scale;
				if (gsheight - drawY - 4 < elementHeight) {
					w = (gsheight - drawY - 4) * scale;
					bottom += (elementHeight - (gsheight - drawY - 4)) * scale;
				}
				if (elementWidth > 8 && w > 0) {
					GL11.glScissor((gsx + 4) * scale, bottom, (elementWidth - 8) * scale, w);
					drawElement(i, par1 - gsx, par2 - gsy - drawY, par3);
				}
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				GL11.glPopMatrix();
			}
			drawY += elementHeight + 4;
		}

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		byte var20 = 4;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_I(0, 0);
		tessellator.addVertexWithUV(gsx, gsy + var20, 0.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(gsx + gswidth, gsy + var20, 0.0D, 1.0D, 1.0D);
		tessellator.setColorRGBA_I(0, 255);
		tessellator.addVertexWithUV(gsx + gswidth, gsy, 0.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(gsx, gsy, 0.0D, 0.0D, 0.0D);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_I(0, 255);
		tessellator.addVertexWithUV(gsx, gsy + gsheight, 0.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(gsx + gswidth, gsy + gsheight, 0.0D, 1.0D, 1.0D);
		tessellator.setColorRGBA_I(0, 0);
		tessellator.addVertexWithUV(gsx + gswidth, gsy + gsheight - var20, 0.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(gsx, gsy + gsheight - var20, 0.0D, 0.0D, 0.0D);
		tessellator.draw();

		if (displayScrollBar()) {
			int var13 = gsheight * gsheight / getTotalHeight();

			if (var13 < 32) {
				var13 = 32;
			}

			if (var13 > gsheight - 8) {
				var13 = gsheight - 8;
			}

			int var14 = (int) this.scroll * (gsheight - var13) / (getTotalHeight() - gsheight + 4) + gsy;

			if (var14 < gsy) {
				var14 = gsy;
			}

			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0, 255);
			tessellator.addVertexWithUV(gsx + gswidth - 7, gsy + gsheight, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(gsx + gswidth, gsy + gsheight, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(gsx + gswidth, gsy, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(gsx + gswidth - 7, gsy, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(8421504, 255);
			tessellator.addVertexWithUV(gsx + gswidth - 7, (double) (var14 + var13), 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(gsx + gswidth, (double) (var14 + var13), 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(gsx + gswidth, (double) var14, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(gsx + gswidth - 7, (double) var14, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(12632256, 255);
			tessellator.addVertexWithUV(gsx + gswidth - 7, (double) (var14 + var13 - 1), 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(gsx + gswidth - 1, (double) (var14 + var13 - 1), 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(gsx + gswidth - 1, (double) var14, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(gsx + gswidth - 7, (double) var14, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
		} else {
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(8421504, 255);
			tessellator.addVertexWithUV(gsx + gswidth - 7, gsy + gsheight, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(gsx + gswidth, gsy + gsheight, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(gsx + gswidth, gsy, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(gsx + gswidth - 7, gsy, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(12632256, 255);
			tessellator.addVertexWithUV(gsx + gswidth - 7, gsy + gsheight - 1, 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(gsx + gswidth - 1, gsy + gsheight - 1, 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(gsx + gswidth - 1, gsy, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(gsx + gswidth - 7, gsy, 0.0D, 0.0D, 0.0D);
			tessellator.draw();
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);

	}

	public void keyTyped(char par1, int par2) {

	}

	public void mouseClicked(int par1, int par2, int par3) {

		par1 -= gsx;
		par2 -= gsy;
		if (!(par1 > 0 && par1 < gswidth && par2 > 0 && par2 < gsheight)) {
			return;
		}
		if (displayScrollBar()) {
			bar = par1 > gswidth - 7;
			my = par2 + gsy;
			if (bar) {
				return;
			}
		}
		par1 -= 4;
		par2 -= 2;
		int elementWidth = gswidth - 8;
		if (par1 < 0 && par1 > elementWidth - 8) {
			clickElement(-1, -1, -1, -1);
			return;
		}
		int cY = -(int) scroll;
		int count = getElementCount();
		for (int i = 0; i < count; i++) {
			int height = getElementHeight(i);
			if (cY < par2 && cY + height + 4 > par2) {
				clickElement(i, par1, par2 - cY, par3);
				return;
			}
			cY += height + 4;
		}
		clickElement(-1, -1, -1, -1);
		return;
	}

	public void mouseMovedOrUp(int par1, int par2, int par3) {
		if (my != -1) {
			my = -1;
		}
	}

	public void updateScreen() {

	}

}
