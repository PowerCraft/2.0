package powercraft.machines.tile;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.block.PC_Block;
import powercraft.api.registry.PC_RecipeRegistry;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.tileentity.PC_ITileEntityRenderer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.machines.PCma_App;
import powercraft.machines.block.PCma_BlockFishingMachine;
import powercraft.machines.model.PCma_ModelFishingMachine;

public class PCma_TileEntityFishingMachine extends PC_TileEntity implements PC_ITileEntityRenderer {

	private static PCma_ModelFishingMachine model = new PCma_ModelFishingMachine();
	private static Random rand = new Random();

	private long lastTime = System.currentTimeMillis();
	private int fishTimer = 250 + rand.nextInt(350);
	private int burningFuel;
	@PC_ClientServerSync(clientChangeAble = false)
	public boolean running;
	public int rotation;

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean state) {
		if (running != state) {
			running = state;
			notifyChanges("running");
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		fishTimer = nbtTagCompound.getInteger("fishTimer");
		burningFuel = nbtTagCompound.getInteger("burningFuel");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setInteger("fishTimer", fishTimer);
		nbtTagCompound.setInteger("burningFuel", burningFuel);
	}

	private IInventory getChestInventory() {
		TileEntity te = PC_Utils.getTE(worldObj, getCoord().offset(0, 1, 0));

		if (te instanceof IInventory) {
			return (IInventory) te;
		}
		return null;
	}

	private boolean checkFuel() {

		if (burningFuel > 11) {
			burningFuel -= 3;
			return true;
		} else {

			IInventory inv = getChestInventory();
			if (inv == null) {
				turnIntoBlocks();
				return false;
			}

			for (int s = 0; s < inv.getSizeInventory(); s++) {
				ItemStack stack = inv.getStackInSlot(s);
				int cost = PC_RecipeRegistry.getFuelValue(stack);
				if (cost > 0) {
					burningFuel += cost;
					if (stack.getItem().hasContainerItem()) {
						inv.setInventorySlotContents(s, new ItemStack(stack.getItem().getContainerItem(), 1, 0));
					} else {
						inv.decrStackSize(s, 1);
					}

					return true;
				}
			}

			return false;
		}
	}

	private void turnIntoBlocks() {
		PC_VecI pos = getCoord();
		int meta = PC_Utils.getMD(worldObj, pos);
		PC_Utils.setBID(worldObj, pos, Blocks.iron_block, meta);
	}

	/**
	 * Catch and eject a fish to nearby covneyor or air block.
	 */
	private void catchFish() {

		PC_VecI[] outputs = { new PC_VecI(1, 1, 0), new PC_VecI(0, 1, 1), new PC_VecI(-1, 1, 0), new PC_VecI(0, 1, -1),
				new PC_VecI(2, 0, 0), new PC_VecI(0, 0, 2), new PC_VecI(-2, 0, 0), new PC_VecI(0, 0, -2) };

		for (int i = 0; i < outputs.length; i++) {
			Block b = PC_Utils.getBlock(worldObj, getCoord().offset(outputs[i]));
			if (b instanceof PC_Block && ((PC_Block) b).getModule().getModuleName().equals("Transport")) {
				ejectFish_do(getCoord().offset(outputs[i]), false);
				return;
			}
		}

		for (int i = 0; i < outputs.length; i++) {
			if (PC_Utils.getBID(worldObj, getCoord().offset(outputs[i])) == Blocks.air) {
				ejectFish_do(getCoord().offset(outputs[i]), true);
				return;
			}
		}

	}

	/**
	 * Create and eject fish or ink item
	 * 
	 * @param out  pos of the output block
	 * @param fast set false if there's a conveyor.
	 */
	private void ejectFish_do(PC_VecI out, boolean fast) {
		if (worldObj.isRemote)
			return;
		ItemStack caught = new ItemStack(rand.nextInt(6) == 0 ? Items.dye : Items.cooked_fished, 1, 0);
		EntityItem entityitem = new EntityItem(worldObj, out.x + 0.5D, out.y + 0.5D, out.z + 0.5D, caught);

		if (!fast) {
			entityitem.motionX = 0;
			entityitem.motionY = 0;
			entityitem.motionZ = 0;
		}

		entityitem.delayBeforeCanPickup = 10;
		worldObj.spawnEntityInWorld(entityitem);
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;
		if (!PCma_BlockFishingMachine.isStructOK(worldObj, getCoord())) {
			turnIntoBlocks();
		}
		boolean oldRunning = isRunning();
		setRunning(checkFuel());
		if (isRunning()) {
			if (--fishTimer <= 0) {
				fishTimer = 250 + rand.nextInt(350);
				catchFish();
			}
		}
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void renderTileEntityAt(double x, double y, double z, float rot) {
		long currentTime = System.currentTimeMillis();
		if (isRunning())
			rotation += (int) ((currentTime - lastTime) / 1000.0f * 360);
		lastTime = currentTime;
		PC_Renderer.glTranslatef(0, -0.5f, 0);
		PC_Renderer.glPushMatrix();
		float f4 = 0.75F;
		PC_Renderer.bindTexture(PC_TextureRegistry.getPowerCraftImageDir()
				+ PC_TextureRegistry.getTextureName(PCma_App.instance, "fisher.png"));
		int rota = PC_Utils.getMD(worldObj, getCoord().offset(0, 1, 0));
		if (rota == 2) {
			rota = 270;
		} else if (rota == 3) {
			rota = 90;
		} else if (rota == 4) {
			rota = 0;
		} else if (rota == 5) {
			rota = 180;
		}
		PC_Renderer.glRotatef(rota, 0.0F, 1.0F, 0.0F);
		PC_Renderer.glScalef(-1F, -1F, 1.0F);
		model.renderModel();

		PC_Renderer.glRotatef(180F - rotation, 0.0F, 1.0F, 0.0F);
		PC_Renderer.glScalef(-1F, -1F, 1.0F);
		model.renderScrew();

		PC_Renderer.glPopMatrix();

		PC_Renderer.glPushMatrix();
		PC_Renderer.glDisable(3553 /* GL_TEXTURE_2D */);
		PC_Renderer.glDisable(2896 /* GL_LIGHTING */);

		double diameter = 0.6D;

		for (int q = 0; q < 24; q++) {
			PC_Renderer.tessellatorStartDrawing(2);
			PC_Renderer.tessellatorSetColorOpaque_I(0x000022);

			for (double k = 0; k <= 3.1415D * 2; k += (3.1415D * 2) / 20D) {
				PC_Renderer.tessellatorAddVertex(Math.sin(k) * diameter, -q * 0.2D, Math.cos(k) * diameter);
			}

			PC_Renderer.tessellatorDraw();
		}

		PC_Renderer.tessellatorStartDrawing(1);
		for (double k = 0; k <= 3.1415 * 2; k += (3.1415D * 2) / 20D) {

			PC_Renderer.tessellatorSetColorOpaque_I(0x000022);
			PC_Renderer.tessellatorAddVertex(Math.sin(k) * diameter, 0D, Math.cos(k) * diameter);
			PC_Renderer.tessellatorAddVertex(Math.sin(k) * diameter, -23 * 0.2D, Math.cos(k) * diameter);

		}
		PC_Renderer.tessellatorDraw();

		PC_Renderer.glEnable(2896 /* GL_LIGHTING */);
		PC_Renderer.glEnable(3553 /* GL_TEXTURE_2D */);
		PC_Renderer.glPopMatrix();
	}

}
