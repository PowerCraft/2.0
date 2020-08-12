package powercraft.api.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import powercraft.api.block.PC_Block;
import powercraft.api.tileentity.PC_ITileEntityRenderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;

public class PC_TileEntitySpecialRenderer extends TileEntitySpecialRenderer {

	private static final int rotationMap[] = { 90, 180, 0, -90, 0, 0 };
	private static PC_TileEntitySpecialRenderer instance = null;

	public static TileEntitySpecialRenderer getInstance() {
		if (instance == null)
			instance = new PC_TileEntitySpecialRenderer();
		return instance;
	}

	private PC_TileEntitySpecialRenderer() {

	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float timeStamp) {
		if (tileEntity instanceof PC_ITileEntityRenderer && tileEntity instanceof PC_TileEntity) {
			PC_TileEntity te = (PC_TileEntity) tileEntity;
			Block block = PC_Utils.getBlock(te.getWorld(), te.getCoord());
			PC_Direction rot = PC_Direction.FRONT;
			if (block instanceof PC_Block) {
				rot = ((PC_Block) block).getRotation(PC_Utils.getMD(te.getWorld(), te.getCoord()));
			}
			PC_Renderer.glPushMatrix();
			PC_Renderer.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
			PC_Renderer.glRotatef(rotationMap[rot.getMCSide()], 0, 1, 0);
			((PC_ITileEntityRenderer) te).renderTileEntityAt(x, y, z, timeStamp);
			PC_Renderer.glPopMatrix();
		}
	}

}
