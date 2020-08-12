package powercraft.light.tile;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_BeamTracer;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.block.PC_Block;
import powercraft.api.interfaces.PC_IBeamHandler;
import powercraft.api.item.PC_ItemStack;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_ITileEntityAABB;
import powercraft.api.tileentity.PC_ITileEntityRenderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.light.PCli_App;
import powercraft.light.item.PCli_ItemLaserComposition;
import powercraft.light.model.PCli_ModelLaser;

public class PCli_TileEntityLaser extends PC_TileEntity
		implements PC_IBeamHandler, PC_ITileEntityRenderer, PC_ITileEntityAABB {

	private static PCli_ModelLaser modelLaser = new PCli_ModelLaser();
	private PC_BeamTracer laser;
	@PC_ClientServerSync(clientChangeAble = false)
	private boolean active = false;
	@PC_ClientServerSync(clientChangeAble = false)
	private PC_ItemStack itemstack;
	@PC_ClientServerSync(clientChangeAble = false)
	private boolean isKiller = false;
	@PC_ClientServerSync(clientChangeAble = false)
	private boolean powered = false;
	Random rnd = new Random(10);
	int r = rnd.nextInt(6);

	private int send = 0;

	public boolean isKiller() {
		return isKiller;
	}

	public void setKiller(boolean b) {
		if (isKiller != b) {
			isKiller = b;
			notifyChanges("isKiller");
		}
	}

	public boolean isPowered() {
		return powered;
	}

	public void setPowered(boolean powered) {
		if (this.powered != powered) {
			this.powered = powered;
			notifyChanges("powered");
		}
	}

	public boolean isActive() {
		if (PCli_ItemLaserComposition.isSensor(getItemStack()))
			return active;
		return false;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (!PCli_ItemLaserComposition.isSensor(getItemStack()) && !isPowered() && !isRoasterBurning())
			return;
		if (laser == null) {
			laser = new PC_BeamTracer(worldObj, this);
			laser.setStartCoord(getCoord());
			int metadata = PC_Utils.getMD(worldObj, xCoord, yCoord, zCoord);
			PC_Block block = PC_Utils.getBlock(worldObj, getCoord());
			laser.setStartMove(block.getRotation(PC_Utils.getMD(worldObj, getCoord())).getOffset());
			laser.setColor(PCli_ItemLaserComposition.getColorForItemStack(getItemStack()));
			laser.setDetectEntities(true);
			laser.setCanChangeColor(true);
		}
		laser.setStartLength(PCli_ItemLaserComposition.getLengthLimit(getItemStack(), isRoasterBurning()));
		boolean oldActive = isActive();
		active = false;
		laser.flash();
		if (PCli_ItemLaserComposition.isSensor(getItemStack())) {
			if (oldActive != active) {
				notifyChanges("active");
				PC_Utils.hugeUpdate(worldObj, xCoord, yCoord, zCoord);
			}
		}
	}

	private boolean isRoasterBurning() {
		boolean isBurning = false;
		if (isKiller()) {
			Block b = PC_Utils.getBlock(worldObj, xCoord, yCoord - 1, zCoord);
			if (b != null && b == PC_BlockRegistry.getPCBlockByName("PCma_BlockRoaster")) {
				Object o = PC_MSGRegistry.callBlockMSG(worldObj, getCoord().offset(0, -1, 0),
						PC_MSGRegistry.MSG_DOES_SMOKE);
				if (o instanceof Boolean)
					isBurning = (Boolean) o;
			}
		}
		return isBurning;
	}

	@Override
	public boolean onBlockHit(PC_BeamTracer beamTracer, Block block, PC_VecI coord) {
		return PCli_ItemLaserComposition.onBlockHit(beamTracer, block, coord, getItemStack(), isRoasterBurning());
	}

	@Override
	public boolean onEntityHit(PC_BeamTracer beamTracer, Entity entity, PC_VecI coord) {
		active = true;
		return PCli_ItemLaserComposition.onEntityHit(beamTracer, entity, coord, getItemStack(), isRoasterBurning());
	}

	public PC_ItemStack getItemStack() {
		return itemstack;
	}

	public void setItemStack(PC_ItemStack itemstack) {
		this.itemstack = itemstack;
		notifyChanges("itemstack");
		laser = null;
	}

	@Override
	protected void dataChanged(String key, Object value) {
		if (key.equals("itemstack")) {
			laser = null;
		}
	}

	@Override
	public void renderTileEntityAt(double x, double y, double z, float rot) {
		modelLaser.laserParts[0].showModel = modelLaser.laserParts[1].showModel = modelLaser.laserParts[2].showModel = modelLaser.laserParts[3].showModel = isKiller();

		modelLaser.laserParts[7].showModel = getItemStack() != null;

		PC_Renderer.glPushMatrix();
		float f = 1.0F;

		PC_Renderer.bindTexture(PC_TextureRegistry.getPowerCraftImageDir()
				+ PC_TextureRegistry.getTextureName(PCli_App.instance, "laser.png"));

		PC_Renderer.glScalef(f, -f, -f);
		modelLaser.renderLaser();
		PC_Color color = PCli_ItemLaserComposition.getColorForItemStack(getItemStack());
		PC_Renderer.glColor4f((float) color.x, (float) color.y, (float) color.z, 1.0F);
		modelLaser.renderLens();
		PC_Renderer.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		PC_Renderer.glPopMatrix();
	}

	@Override
	public int getProvidingStrongRedstonePowerValue(PC_Direction dir) {
		return isActive() ? 15 : 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		if (nbttagcompound.getBoolean("item")) {
			itemstack = new PC_ItemStack(PCli_App.laserComposition, 1);
			NBTTagCompound t = new NBTTagCompound();
			t.setInteger("level.kill", nbttagcompound.getInteger("kill"));
			t.setInteger("level.distance", nbttagcompound.getInteger("distance"));
			t.setInteger("level.sensor", nbttagcompound.getInteger("sensor"));
			itemstack.setNBTTag(t);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		if (itemstack != null) {
			nbttagcompound.setBoolean("item", true);
			nbttagcompound.setInteger("kill", itemstack.getNBTTag().getInteger("level.kill"));
			nbttagcompound.setInteger("distance", itemstack.getNBTTag().getInteger("level.distance"));
			nbttagcompound.setInteger("sensor", itemstack.getNBTTag().getInteger("level.sensor"));
		} else
			nbttagcompound.setBoolean("item", false);
	}

}
