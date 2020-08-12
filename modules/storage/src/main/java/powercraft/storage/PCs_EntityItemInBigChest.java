package powercraft.storage;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;
import powercraft.api.utils.PC_VecI;

public class PCs_EntityItemInBigChest extends EntityItem {

	private PC_VecI mid;
	private int slot;
	private PC_VecF move = new PC_VecF();

	public PCs_EntityItemInBigChest(World world, PC_VecF pos, PC_VecI mid, PC_VecF move, int slot) {
		super(world, pos.x, pos.y, pos.z);
		this.slot = slot;
		this.mid = mid.copy();
		this.move = move.copy();
	}

	@Override
	public void onUpdate() {
		move.add(new PC_VecF(mid).sub((float) posX, (float) posY, (float) posZ));
		posX += move.x / 100.0;
		posY += move.y / 100.0;
		posZ += move.z / 100.0;
		setVelocity(move.x / 100.0, move.y / 100.0, move.z / 100.0);
		super.onUpdate();
		isDead = false;
	}

	@Override
	public float getCollisionBorderSize() {
		return 0.3f;
	}

	@Override
	public boolean combineItems(EntityItem entityItem) {
		return false;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
	}

	// @Override
	// public boolean interact(EntityPlayer entityPlayer) {
	/// PC_PacketHandler.setTileEntity(getChest(), new PC_Entry("interact", slot));
	// getInv().interact(entityPlayer, slot);
	// return true;
	// }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public ItemStack getEntityItem() {
		if (getInv() == null) {
			setDead();
			return new ItemStack(Blocks.air);
		}
		ItemStack is = getInv().getStackInSlot(slot);
		if (is == null) {
			setDead();
			return new ItemStack(Blocks.air);
		}
		return is;
	}

	private PCs_BigChestInventory getInv() {
		PCs_TileEntityBigChest chest = getChest();
		if (chest == null)
			return null;
		return chest.getInventory();
	}

	private PCs_TileEntityBigChest getChest() {
		return PC_Utils.getTE(worldObj, mid.offset(-2));
	}

}
