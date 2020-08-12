package powercraft.light.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_ITileEntityRenderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_Utils;
import powercraft.light.PCli_App;
import powercraft.light.block.PCli_BlockLight;
import powercraft.light.model.PCli_ModelLight;

public class PCli_TileEntityLight extends PC_TileEntity implements PC_ITileEntityRenderer, PC_IPacketHandler {

	private static PCli_ModelLight model = new PCli_ModelLight();

	private PC_Color color = new PC_Color(1.0f, 1.0f, 1.0f);
	private boolean isStable;
	private boolean isHuge;

	public void setColor(PC_Color c) {
		color = c;
	}

	public PC_Color getColor() {
		return color;
	}

	public void setStable(boolean stable) {
		isStable = stable;
	}

	public boolean isStable() {
		return isStable;
	}

	public void setHuge(boolean huge) {
		isHuge = huge;
	}

	public boolean isHuge() {
		return isHuge;
	}

	public boolean isActive() {
		return PC_Utils.getBID(worldObj, xCoord, yCoord, zCoord) == PCli_BlockLight.on;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagCompound c = nbttagcompound.getCompoundTag("color");
		color.readFromNBT(c);
		isStable = nbttagcompound.getBoolean("isStable");
		isHuge = nbttagcompound.getBoolean("isHuge");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagCompound c = new NBTTagCompound();
		color.writeToNBT(c);
		nbttagcompound.setTag("color", c);
		nbttagcompound.setBoolean("isStable", isStable);
		nbttagcompound.setBoolean("isHuge", isHuge);
	}

	@Override
	public void renderTileEntityAt(double x, double y, double z, float rot) {

		PC_Renderer.glPushMatrix();
		float f = 1.0F;

		PC_Renderer.glRotatef(90, 0, 1, 0);

		PC_Renderer.bindTexture(PC_TextureRegistry.getPowerCraftImageDir()
				+ PC_TextureRegistry.getTextureName(PCli_App.instance, "block_light.png"));

		PC_Renderer.glScalef(f, -f, -f);

		PC_Color clr = getColor();
		if (clr != null)
			PC_Renderer.glColor4f(clr.x, clr.y, clr.z, 1.0f);
		else
			PC_Renderer.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		int meta = PC_Utils.getMD(worldObj, getCoord());
		switch (meta) {
		case 0:
			break;
		case 1:
			PC_Renderer.glRotatef(-90, 1, 0, 0);
			break;
		case 2:
			PC_Renderer.glRotatef(90, 1, 0, 0);
			break;
		case 3:
			PC_Renderer.glRotatef(-90, 0, 0, 1);
			break;
		case 4:
			PC_Renderer.glRotatef(90, 0, 0, 1);
			break;
		case 5:
			PC_Renderer.glRotatef(180, 1, 0, 0);
			break;
		}

		if (isHuge()) {
			model.renderHuge();
		} else {
			model.renderNormal();
		}

		PC_Renderer.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		PC_Renderer.glPopMatrix();

	}

	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		this.setColor((PC_Color) o[2]);
		this.setHuge((Boolean) o[3]);
		this.setStable((Boolean) o[4]);
		return false;
	}
}
