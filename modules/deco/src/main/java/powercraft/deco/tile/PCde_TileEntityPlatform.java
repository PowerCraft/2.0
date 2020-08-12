package powercraft.deco.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_ITileEntityRenderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.deco.PCde_App;
import powercraft.deco.block.PCde_BlockPlatform;
import powercraft.deco.model.PCde_ModelPlatform;
import powercraft.launcher.mod_PowerCraft;

public class PCde_TileEntityPlatform extends PC_TileEntity implements PC_ITileEntityRenderer {

	private PCde_ModelPlatform model = new PCde_ModelPlatform();

	@Override
	public void renderTileEntityAt(double x, double y, double z, float f0) {
		float f = 1.0F;

		Minecraft.getMinecraft().getTextureManager()
				.bindTexture(new ResourceLocation(mod_PowerCraft.MODID, PC_TextureRegistry.getPowerCraftImageDir()
						+ PC_TextureRegistry.getTextureName(PCde_App.instance, "block_deco.png")));

		PC_Renderer.glPushMatrix();
		PC_Renderer.glScalef(f, -f, -f);

		boolean[] fences = PCde_BlockPlatform.getFencesShownLedge(worldObj, getCoord());
		model.setLedgeFences(fences[0], fences[1], fences[2], fences[3], fences[4]);

		model.render();

		PC_Renderer.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		PC_Renderer.glPopMatrix();
	}

}
