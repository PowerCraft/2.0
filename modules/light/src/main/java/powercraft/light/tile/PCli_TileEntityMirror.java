package powercraft.light.tile;

import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_ITileEntityRenderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Color;
import powercraft.light.PCli_App;
import powercraft.light.model.PCli_ModelMirror;

public class PCli_TileEntityMirror extends PC_TileEntity implements PC_ITileEntityRenderer {

	private static PCli_ModelMirror modelMirror = new PCli_ModelMirror();

	// @PC_ClientServerSync???
	private int mirrorColor = -1;

	public void setMirrorColor(int mirrorColor) {
		if (this.mirrorColor != mirrorColor) {
			this.mirrorColor = mirrorColor;
		}
	}

	public int getMirrorColor() {
		return mirrorColor;
	}

	@Override
	public void renderTileEntityAt(double x, double y, double z, float rot) {

		modelMirror.bottomSticks.showModel = false;
		modelMirror.ceilingSticks.showModel = false;
		modelMirror.stickXplus.showModel = false;
		modelMirror.stickXminus.showModel = false;
		modelMirror.stickZplus.showModel = false;
		modelMirror.stickZminus.showModel = false;
		modelMirror.stickZminus.showModel = false;

		modelMirror.signBoard.showModel = true;

		int i, j, k;

		i = xCoord;
		j = yCoord;
		k = zCoord;

		if (worldObj.getBlock(i, j - 1, k).getMaterial().isSolid()) {
			modelMirror.bottomSticks.showModel = true;
		} else if (worldObj.getBlock(i, j + 1, k).getMaterial().isSolid()) {
			modelMirror.ceilingSticks.showModel = true;
		} else if (worldObj.getBlock(i + 1, j, k).getMaterial().isSolid()) {
			modelMirror.stickXplus.showModel = true;
		} else if (worldObj.getBlock(i - 1, j, k).getMaterial().isSolid()) {
			modelMirror.stickXminus.showModel = true;
		} else if (worldObj.getBlock(i, j, k + 1).getMaterial().isSolid()) {
			modelMirror.stickZplus.showModel = true;
		} else if (worldObj.getBlock(i, j, k - 1).getMaterial().isSolid()) {
			modelMirror.stickZminus.showModel = true;
		}
		PC_Renderer.glPushMatrix();
		float f = 0.6666667F;

		float f1 = (getBlockMetadata() * 360) / 16F;

		PC_Renderer.bindTexture(PC_TextureRegistry.getPowerCraftImageDir()
				+ PC_TextureRegistry.getTextureName(PCli_App.instance, "mirror.png"));
		PC_Renderer.glRotatef(-f1 - 90, 0, 1, 0);
		PC_Renderer.glScalef(f, -f, -f);

		int color = getMirrorColor();

		if (color != -1) {

			float red = (float) PC_Color.red(PC_Color.crystal_colors[color]);
			float green = (float) PC_Color.green(PC_Color.crystal_colors[color]);
			float blue = (float) PC_Color.blue(PC_Color.crystal_colors[color]);

			PC_Renderer.glColor4f(red, green, blue, 0.5f);

		}

		modelMirror.renderMirrorNoSideSticks();
		PC_Renderer.glPopMatrix();

		PC_Renderer.glPushMatrix();
		PC_Renderer.glRotatef(90, 0, 1, 0);
		PC_Renderer.glScalef(f, -f, -f);
		modelMirror.renderMirrorSideSticks();
		PC_Renderer.glPopMatrix();

		PC_Renderer.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
