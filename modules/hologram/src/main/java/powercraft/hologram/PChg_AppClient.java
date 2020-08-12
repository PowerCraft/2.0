package powercraft.hologram;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCache;
import powercraft.api.annotation.PC_FieldObject;
import powercraft.api.renderer.PC_RenderBlocks;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.renderer.PC_TileEntitySpecialRenderer;
import powercraft.api.tileentity.PC_ITileEntityRenderer;
import powercraft.api.utils.PC_ClientUtils;
import powercraft.api.utils.PC_VecI;
import powercraft.hologram.render.PChg_HologramGlassesOverlay;
import powercraft.hologram.render.PChg_HologramRenderBlocks;
import powercraft.hologram.tile.PChg_TileEntityHologramField;
import powercraft.launcher.loader.PC_ClientModule;
import powercraft.launcher.loader.PC_ClientModule.PC_LoadTextureFiles;

@PC_ClientModule
public class PChg_AppClient extends PChg_App {

	@PC_FieldObject(clazz = PChg_HologramGlassesOverlay.class)
	public static PChg_HologramGlassesOverlay hologramGlassesOverlay;

	@PC_LoadTextureFiles
	public List<String> loadTextureFiles(List<String> textures) {
		textures.add("glasses.png");
		return textures;
	}

	@Override
	public void renderHologramField(PChg_TileEntityHologramField te, double x, double y, double z) {
		RenderHelper.disableStandardItemLighting();
		PC_VecI offset = te.getOffset();
		if (offset == null)
			offset = new PC_VecI();
		offset = offset.offset(te.getCoord());
		Minecraft mc = PC_ClientUtils.mc();
		ChunkCache cc = new ChunkCache(te.getWorldObj(), offset.x - 18, offset.y - 18, offset.z - 18, offset.x + 17,
				offset.y + 17, offset.z + 17, 18);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		PC_Renderer.glPushMatrix();
		PC_Renderer.glRotatef(90, 0, 1, 0);
		PC_Renderer.glTranslatef(0, 1, 0);
		PC_Renderer.glScalef(1 / 16.0f, 1 / 16.0f, 1 / 16.0f);
		PC_Renderer.glTranslatef(-offset.x, -offset.y, -offset.z);
		PC_RenderBlocks renderer = new PChg_HologramRenderBlocks(cc);
		mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		if (te == PChg_HologramGlassesOverlay.fieldToUpdate || te.glList == 0 || te.mapToUpdate.contains(te)) {
			te.mapToUpdate.clear();
			if (te.glList == 0) {
				te.glList = GL11.glGenLists(1);
			}
			GL11.glNewList(te.glList, GL11.GL_COMPILE_AND_EXECUTE);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			PC_Renderer.tessellatorStartDrawingQuads();
			for (int yy = -16; yy < 16; yy++) {
				for (int xx = -16; xx < 16; xx++) {
					for (int zz = -16; zz < 16; zz++) {
						Block block = te.getWorld().getBlock(offset.x + xx, offset.y + yy, offset.z + zz);
						if (block != null && block != Blocks.air) {
							renderer.setMeta(
									te.getWorld().getBlockMetadata(offset.x + xx, offset.y + yy, offset.z + zz));
							PC_Renderer.renderBlockByRenderType(renderer, block, offset.x + xx, offset.y + yy,
									offset.z + zz);
						}
					}
				}
			}
			PC_Renderer.tessellatorDraw();
			GL11.glEndList();
			PChg_HologramGlassesOverlay.fieldToUpdate = null;
		} else {
			GL11.glCallList(te.glList);
		}

		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		for (int yy = -16; yy < 16; yy++) {
			for (int xx = -16; xx < 16; xx++) {
				for (int zz = -16; zz < 16; zz++) {
					Block block = te.getWorld().getBlock(offset.x + xx, offset.y + yy, offset.z + zz);
					if (block != null) {
						TileEntity tileEntity = cc.getTileEntity(offset.x + xx, offset.y + yy, offset.z + zz);
						if (tileEntity != null && !(tileEntity instanceof PChg_TileEntityHologramField)
								&& tileEntity instanceof PC_ITileEntityRenderer) {
							GL11.glPushAttrib(-1);
							PC_TileEntitySpecialRenderer.getInstance().renderTileEntityAt(tileEntity, offset.x + xx,
									offset.y + yy, offset.z + zz, 1);
							GL11.glPopAttrib();
						}
					}
				}
			}
		}

		double rpx = RenderManager.renderPosX;
		double rpy = RenderManager.renderPosY;
		double rpz = RenderManager.renderPosZ;

		RenderManager.renderPosX = 0;
		RenderManager.renderPosY = 0;
		RenderManager.renderPosZ = 0;

		List var5 = te.getWorldObj().getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(offset.x - 15,
				offset.y - 15, offset.z - 15, offset.x + 16, offset.y + 16, offset.z + 16));
		int var6;
		Entity var7;

		for (var6 = 0; var6 < var5.size(); ++var6) {
			GL11.glPushAttrib(-1);
			var7 = (Entity) var5.get(var6);
			RenderManager.instance.renderEntitySimple(var7, 1);
			GL11.glPopAttrib();
		}
		RenderHelper.enableStandardItemLighting();
		GL11.glDisable(GL11.GL_BLEND);
		RenderManager.renderPosX = rpx;
		RenderManager.renderPosY = rpy;
		RenderManager.renderPosZ = rpz;

		PC_Renderer.glPopMatrix();
	}

}
