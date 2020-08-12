package powercraft.storage;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import powercraft.api.inventory.PC_IInventoryWrapper;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.recipes.PC_3DRecipe;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Color;
import powercraft.api.utils.PC_Entry;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecF;
import powercraft.api.utils.PC_VecI;

public class PCs_TileEntityBigChest extends PC_TileEntity implements PC_IInventoryWrapper {

	public static final int TOPBACKLEFT = 0, TOPBACKRIGHT = 1, TOPFRONTLEFT = 2, TOPFRONTRIGHT = 3, BOTTOMBACKLEFT = 4,
			BOTTOMBACKRIGHT = 5, BOTTOMFRONTLEFT = 6, BOTTOMFRONTRIGHT = 7;

	private static PC_3DRecipe recipe = new PC_3DRecipe(null, new String[] { "g  g", "    ", "    ", "g  g" },
			new String[] { "f  f", "    ", "    ", "f  f" }, new String[] { "f  f", "    ", "    ", "f  f" },
			new String[] { "g  g", "    ", "    ", "g  g" }, 'g', PCs_App.bigChest, 'f', Blocks.fence, ' ', null);

	private int pos;
	private PCs_BigChestInventory inv;

	@Override
	public PCs_BigChestInventory getInventory() {
		if (pos == BOTTOMBACKLEFT)
			return inv;
		else {
			PCs_TileEntityBigChest master = getMaster();
			if (master == null)
				return null;
			return master.getInventory();
		}
	}

	public PCs_TileEntityBigChest getMaster() {
		PC_VecI p = getCoord().copy();
		if (pos == TOPBACKLEFT || pos == TOPBACKRIGHT || pos == TOPFRONTLEFT || pos == TOPFRONTRIGHT) {
			p.sub(0, 3, 0);
		}
		if (pos == TOPFRONTLEFT || pos == TOPFRONTRIGHT || pos == BOTTOMFRONTLEFT || pos == BOTTOMFRONTRIGHT) {
			p.sub(0, 0, 3);
		}
		if (pos == TOPBACKRIGHT || pos == TOPFRONTRIGHT || pos == BOTTOMBACKRIGHT || pos == BOTTOMFRONTRIGHT) {
			p.sub(3, 0, 0);
		}
		return PC_Utils.getTE(worldObj, p);
	}

	public void setPos(int pos) {
		this.pos = pos;
		if (pos == BOTTOMBACKLEFT) {
			inv = new PCs_BigChestInventory(worldObj, getCoord().offset(2), this);
		}
	}

	@Override
	public void setWorldObj(World world) {
		super.setWorldObj(world);
		if (inv != null) {
			inv.setPos(worldObj, getCoord().offset(2));
		}
	}

	@Override
	public void updateEntity() {
		if (pos == BOTTOMBACKLEFT && !worldObj.isRemote) {
			AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 4, yCoord + 4, zCoord + 4);
			List<EntityItem> list = worldObj.getEntitiesWithinAABB(EntityItem.class, bb);
			for (EntityItem entity : list) {
				inv.collectItem(entity);
			}
			if (!recipe.getStructRotation(worldObj, getCoord(), 0)) {
				breakStruct();
			}
		}
		Random rand = new Random();
		PC_Color color = new PC_Color(0.7f + rand.nextFloat() * 0.3f, rand.nextFloat() * 0.3f,
				0.2f + rand.nextFloat() * 0.3f);
		PC_Utils.spawnParticle("PC_EntityFanFX", worldObj, new PC_VecF(getCoord()),
				new PC_VecF(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f),
				new PC_VecF(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f).div(10.0f),
				0.05f + rand.nextFloat() * 0.1f, color);

	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		pos = nbtTagCompound.getInteger("pos");
		if (pos == BOTTOMBACKLEFT) {
			inv = new PCs_BigChestInventory(this);
			PC_InventoryUtils.loadInventoryFromNBT(nbtTagCompound, "inv", inv);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setInteger("pos", pos);
		if (pos == BOTTOMBACKLEFT) {
			PC_InventoryUtils.saveInventoryToNBT(nbtTagCompound, "inv", inv);
		}
	}

	public void breakStruct() {
		PCs_TileEntityBigChest master = getMaster();
		// master.removeAllWithout();
	}

	private void removeAllWithout() {
		PC_VecI pos = getCoord();
		for (int x = 0; x <= 1; x++) {
			for (int y = 0; y <= 1; y++) {
				for (int z = 0; z <= 1; z++) {
					if (PC_Utils.getBID(worldObj, pos.offset(x * 3, y * 3, z * 3)) == PCs_App.bigChest) {
						PC_Utils.setBID(worldObj, pos.offset(x * 3, y * 3, z * 3), Blocks.glass, 0);
					}
				}
			}
		}

	}

	@Override
	public void setData(EntityPlayer player, PC_Struct2<String, Object>[] data) {
		for (PC_Struct2<String, Object> d : data) {
			if (d.a.equals("slotChange")) {
				PC_Struct2<Integer, byte[]> s = (PC_Struct2<Integer, byte[]>) d.b;
				ItemStack is = null;
				if (s.b != null) {
					// try {
					// is = ItemStack.loadItemStackFromNBT(CompressedStreamTools.decompress(s.b));
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
				}
				inv.setInventorySlotContents(s.a, is);
			} else if (d.a.equals("pos")) {
				setPos((Integer) d.b);
			} else if (d.a.equals("inv")) {
				byte b[] = (byte[]) d.b;
				// try {
				// NBTTagCompound nbtTag = CompressedStreamTools.decompress(b);
				// PC_InventoryUtils.loadInventoryFromNBT(nbtTag, "inv", inv);
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
			} else if (d.a.equals("interact")) {
				inv.interact(player, (Integer) d.b);
			}
		}
	}

	// @Override
	public PC_Struct2<String, Object>[] getData() {
		if (inv == null) {
			return new PC_Struct2[] { new PC_Entry("pos", pos) };
		}
		NBTTagCompound nbtTag = new NBTTagCompound();
		PC_InventoryUtils.saveInventoryToNBT(nbtTag, "inv", inv);
		try {
			byte b[] = CompressedStreamTools.compress(nbtTag);
			return new PC_Struct2[] { new PC_Entry("pos", pos), new PC_Entry("inv", b) };
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new PC_Struct2[] { new PC_Entry("pos", pos) };
	}

}
