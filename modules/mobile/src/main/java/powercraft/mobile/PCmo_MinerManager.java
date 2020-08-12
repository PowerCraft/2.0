package powercraft.mobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import powercraft.api.PC_Lang;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.recipes.PC_I3DRecipeHandler;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

public class PCmo_MinerManager implements PC_I3DRecipeHandler, PC_IPacketHandler {

	public static int countPowerCrystals(IInventory inv) {
		if (inv != null) {
			int cnt = 0;
			ArrayList<Integer> xtals = new ArrayList<Integer>();
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				if (inv.getStackInSlot(i) != null)
					if (inv.getStackInSlot(i).getItem() == Item
							.getItemFromBlock(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal")))
						if (!xtals.contains(inv.getStackInSlot(i).getItemDamage()))
							xtals.add(inv.getStackInSlot(i).getItemDamage());
			}
			cnt = xtals.size();
			if (cnt > 8)
				cnt = 8;
			return cnt;
		}
		return 0;
	}

	@Override
	public boolean foundStructAt(EntityPlayer entityplayer, World world, PC_Struct2<PC_VecI, Integer> structStart) {
		if (PC_Utils.getBID(world, structStart.a) == Blocks.obsidian) {
			List<PCmo_EntityMiner> miner = world.getEntitiesWithinAABB(PCmo_EntityMiner.class,
					AxisAlignedBB.getBoundingBox(structStart.a.x + 1, structStart.a.y + 1, structStart.a.z + 1,
							structStart.a.x + 3, structStart.a.y + 3, structStart.a.z + 3));
			if (miner.size() != 1)
				return false;
			PCmo_EntityMiner m = miner.get(0);
			if (m.st.isExplosionResistent)
				return false;
			m.st.isExplosionResistent = true;
			for (int x = 0; x < 4; x++) {
				for (int y = 0; y < 4; y++) {
					for (int z = 0; z < 4; z++) {
						PC_Utils.setBID(world, structStart.a.offset(x, y, z), Blocks.air);
					}
				}
			}

			return true;
		}
		String eMinerStructure = PC_Lang.tr("pc.miner.build.errInvalidStructure");
		String eMinerCrystals = PC_Lang.tr("pc.miner.build.errMissingCrystals");

		PCmo_EntityMiner miner = new PCmo_EntityMiner(world, structStart.a.x + 1, structStart.a.y, structStart.a.z + 1);
		miner.rotationYaw = (structStart.b + 1) * 90;

		IInventory inv = null;

		for (int x = structStart.a.x; x <= structStart.a.x + 1 && inv == null; x++) {
			for (int z = structStart.a.z; z <= structStart.a.z + 1 && inv == null; z++) {
				inv = PC_InventoryUtils.getInventoryAt(world, x, structStart.a.y + 1, z);
			}
		}

		if (inv == null) {
			PC_Utils.chatMsg(eMinerStructure);
			return false;
		}

		PC_InventoryUtils.moveStacks(inv, miner.cargo);

		int cnt = countPowerCrystals(miner.cargo);

		if (cnt == 0) {
			PC_Utils.chatMsg(eMinerCrystals);
			return false;
		}

		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					PC_Utils.setBID(world, structStart.a.offset(x, y, z), Blocks.air);
				}
			}
		}

		world.spawnEntityInWorld(miner);
		miner.updateLevel();

		return true;
	}

	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		Entity e = player.worldObj.getEntityByID((Integer) o[1]);
		if (e instanceof PCmo_EntityMiner) {
			PCmo_EntityMiner miner = (PCmo_EntityMiner) e;
			String func = (String) o[2];
			if (func.equals("set")) {
				double x = (Double) o[3];
				double y = (Double) o[4];
				double z = (Double) o[5];
				if (Math.abs(e.posX - x) > 1)
					e.posX = (Double) o[3];
				if (Math.abs(e.posY - y) > 1)
					e.posY = (Double) o[4];
				if (Math.abs(e.posZ - z) > 1)
					e.posZ = (Double) o[5];
				e.motionX = (Double) o[6];
				e.motionY = (Double) o[7];
				e.motionZ = (Double) o[8];
				e.rotationYaw = (Float) o[9];
				byte[] b = (byte[]) o[10];
				try {
					NBTTagCompound tag = CompressedStreamTools.func_152457_a(b, NBTSizeTracker.field_152451_a);
					miner.st.readFromNBT(tag);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				b = (byte[]) o[11];
				try {
					NBTTagCompound tag = CompressedStreamTools.func_152457_a(b, NBTSizeTracker.field_152451_a);
					miner.readEntityFromNBT(tag);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				miner.updateLevel();
				e.setLocationAndAngles(e.posX, e.posY, e.posZ, e.rotationYaw, 0);

			} else if (func.equals("command")) {
				miner.receiveKeyboardCommand((Integer) o[3]);
			} else if (func.equals("setLevel")) {
				miner.st.level = (Integer) o[3];
				miner.updateLevel();
			} else if (func.equals("setInfo")) {
				miner.setInfo((String) o[3], o[4]);
			} else if (func.equals("doInfoSet")) {
				miner.doInfoSet((String) o[3], (Object[]) o[4]);
			}
		}
		return false;
	}

	@Override
	public boolean canBeCrafted() {
		return true;
	}

}
