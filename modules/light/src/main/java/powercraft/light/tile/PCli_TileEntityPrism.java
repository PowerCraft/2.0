package powercraft.light.tile;

import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_ITileEntityAABB;
import powercraft.api.tileentity.PC_ITileEntityRenderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.light.PCli_App;
import powercraft.light.model.PCli_ModelPrism;

public class PCli_TileEntityPrism extends PC_TileEntity implements PC_ITileEntityRenderer, PC_ITileEntityAABB {

	private static PCli_ModelPrism modelPrism = new PCli_ModelPrism();

	/**
	 * List of prism's sides, flags whether there are attached glass panels. starts
	 * with up and down, but the order does not really matter here.
	 */
	@PC_ClientServerSync(clientChangeAble = false)
	private boolean[] prismSides = { false, false, false, false, false, false, false, false, false, false };

	public boolean getPrismSide(int side) {
		if (side < 0 || side > 9) {
			return false;
		}
		if (prismSides != null) {
			return prismSides[side];
		}
		return false;
	}

	public void setPrismSide(int side, boolean state) {
		if (side < 0 || side > 9) {
			return;
		}
		if (prismSides[side] != state) {
			prismSides[side] = state;
			notifyChanges("prismSides");
		}
	}

	@Override
	public void renderTileEntityAt(double x, double y, double z, float rot) {
		modelPrism.mainCrystal.showModel = true;

		for (int a = 0; a <= 9; a++) {
			modelPrism.sides[a].showModel = getPrismSide(a);
		}

		float f = 1.0F;

		PC_Renderer.bindTexture(PC_TextureRegistry.getPowerCraftImageDir()
				+ PC_TextureRegistry.getTextureName(PCli_App.instance, "prism.png"));

		PC_Renderer.glPushMatrix();
		PC_Renderer.glRotatef(90, 0, 1, 0);
		PC_Renderer.glScalef(f, -f, -f);

		PC_Renderer.glEnable(3042 /* GL_BLEND */);
		PC_Renderer.glDisable(3008 /* GL_ALPHA_TEST */);
		PC_Renderer.glEnable(2977 /* GL_NORMALIZE */);
		PC_Renderer.glBlendFunc(770 /* GL_SRC_ALPHA */, 771 /* GL_ONE_MINUS_SRC_ALPHA */);

		modelPrism.renderPrism();

		PC_Renderer.glDisable(2977 /* GL_NORMALIZE */);
		PC_Renderer.glDisable(3042 /* GL_BLEND */);
		PC_Renderer.glEnable(3008 /* GL_ALPHA_TEST */);

		PC_Renderer.glPopMatrix();

		PC_Renderer.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
