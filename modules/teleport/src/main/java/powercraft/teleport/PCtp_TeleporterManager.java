package powercraft.teleport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.interfaces.PC_IDataHandler;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

public class PCtp_TeleporterManager implements PC_IDataHandler {

	public static PC_VecI coords[] = { new PC_VecI(0, 0, -1), new PC_VecI(+1, 0, 0), new PC_VecI(0, 0, +1),
			new PC_VecI(-1, 0, 0) };
	private static List<PCtp_TeleporterData> teleporter = new ArrayList<PCtp_TeleporterData>();
	public static boolean needSave;

	@Override
	public void load(NBTTagCompound nbtTag) {
		teleporter.clear();
		int num = nbtTag.getInteger("count");
		for (int i = 0; i < num; i++) {
			PCtp_TeleporterData td = new PCtp_TeleporterData();
			PC_Utils.loadFromNBT(nbtTag, "value[" + i + "]", td);
			teleporter.add(td);
			// PC_PacketHandler.sendToAll(new PC_PacketTeleporterSyncClient(td, ""));
		}
	}

	@Override
	public NBTTagCompound save(NBTTagCompound nbtTag) {

		this.needSave = false;
		nbtTag.setInteger("count", teleporter.size());
		int i = 0;
		for (PCtp_TeleporterData td : teleporter) {
			PC_Utils.saveToNBT(nbtTag, "value[" + i + "]", td);
			i++;
		}
		return nbtTag;
	}

	@Override
	public boolean needSave() {
		return needSave;
	}

	@Override
	public void reset() {
		teleporter.clear();
	}

	public static void openGui(EntityPlayer entityPlayer, int x, int y, int z) {
		int dimension = entityPlayer.dimension;
		PCtp_TeleporterData td = getTeleporterData(dimension, new PC_VecI(x, y, z));
		if (td == null) {
			td = new PCtp_TeleporterData();
			registerTeleporterData(dimension, new PC_VecI(x, y, z), td);
		}
		openGui(entityPlayer, td);
	}

	public static void openGui(EntityPlayer entityPlayer, PCtp_TeleporterData td) {
		List<String> names = getTargetNames();
		for (int i = 0; i < names.size(); i++) {
			PCtp_TeleporterData td1 = getTargetByName(names.get(i));
			if (td1.dimension != entityPlayer.dimension)
				names.remove(td1.name);
		}
		String defaultTarget = null;
		if (td.defaultTarget != null) {
			PCtp_TeleporterData td2 = getTeleporterData(td.defaultTargetDimension, td.defaultTarget);
			if (td2 != null)
				defaultTarget = td2.name;
		}
		PCtp_TileEntityTeleporter tile = (PCtp_TileEntityTeleporter) entityPlayer.getEntityWorld()
				.getTileEntity(td.pos.x, td.pos.y, td.pos.z);
		PC_GresRegistry.openGres("Teleporter", entityPlayer, tile, new Object[] { td, names, defaultTarget });
	}

	public static void registerTeleporterData(int dimension, PC_VecI pos, PCtp_TeleporterData td) {
		releaseTeleporterData(dimension, pos);
		td.dimension = dimension;
		td.pos = pos;
		teleporter.add(td);
		needSave = true;
	}

	public static void releaseTeleporterData(int dimension, PC_VecI pos) {
		teleporter.remove(getTeleporterData(dimension, pos));
		needSave = true;
	}

	public static PCtp_TeleporterData getTeleporterData(int dimension, PC_VecI pos) {
		for (PCtp_TeleporterData td : teleporter) {
			if (td.dimension == dimension && pos.equals(td.pos)) {
				needSave = true;
				return td;
			}
		}
		return null;
	}

	private static PC_Struct2<PC_VecI, Integer> calculatePos(World world, PCtp_TeleporterData to, int rot) {
		int entrot = rot;
		PC_VecI tc = to.pos;
		PC_VecI out = tc.copy();
		PC_VecI tmp = out;

		for (int i = 0; i < 4; i++)
			tmp = tc.offset(coords[i]);

		if (to.direction == 0) {
			entrot = 0;
			if (PC_Utils.getBID(world, tc.offset(coords[0])) == Blocks.air)
				out = tc.offset(coords[0]);
		}
		if (to.direction == 1) {
			entrot = -90;
			if (PC_Utils.getBID(world, tc.offset(coords[1])) == Blocks.air)
				out = tc.offset(coords[1]);
		}
		if (to.direction == 2) {
			entrot = 180;
			if (PC_Utils.getBID(world, tc.offset(coords[2])) == Blocks.air)
				out = tc.offset(coords[2]);
		}
		if (to.direction == 3) {
			entrot = 90;
			if (PC_Utils.getBID(world, tc.offset(coords[3])) == Blocks.air)
				out = tc.offset(coords[3]);
		}

		return new PC_Struct2<PC_VecI, Integer>(out, entrot);
	}

	private static boolean teleportTo(Entity entity, PC_Struct2<PC_VecI, Integer> s) {
		if (!entity.worldObj.isRemote) {
			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				if (player.playerNetServerHandler.netManager.isChannelOpen()) {
					player.setPositionAndUpdate(s.a.x + 0.5, s.a.y + 0.1, s.a.z + 0.5);
					player.fallDistance = 0.0F;
				}
			} else {
				entity.setLocationAndAngles(s.a.x + 0.5, s.a.y + 0.1, s.a.z + 0.5, s.b, 0);
				entity.motionX = 0;
				entity.motionY = 0;
				entity.motionZ = 0;
			}
		}
		return true;
	}

	public static boolean teleportEntityToTarget(Entity entity, PCtp_TeleporterData to) {
		World world = PC_Utils.mcs().worldServerForDimension(to.dimension);
		PC_Struct2<PC_VecI, Integer> s = calculatePos(world, to, (int) entity.prevRotationYaw);
		if (!entity.worldObj.isRemote) {
			if (!(entity instanceof EntityPlayerMP))
				if (!teleportTo(entity, s))
					return false;
			if (entity.dimension != to.dimension)
				entity.travelToDimension(to.dimension);
			if (entity instanceof EntityPlayerMP)
				if (!teleportTo(entity, s))
					return false;
		}
		return true;
	}

	public static boolean teleportEntityTo(Entity entity, PCtp_TeleporterData td) {

		if (td.defaultTarget == null)
			return false;
		PCtp_TeleporterData to = getTeleporterData(td.defaultTargetDimension, td.defaultTarget);
		if (to == null)
			return false;
		return teleportEntityToTarget(entity, to);
	}

	public static List<String> getTargetNames() {
		List<String> names = new ArrayList<String>();
		for (PCtp_TeleporterData td : teleporter) {
			if (td.name != null && !td.name.equals(""))
				names.add(td.name);
		}
		return names;
	}

	public static List<Integer> getTargetDimensions() {
		List<Integer> dimension = new ArrayList<Integer>();
		for (PCtp_TeleporterData td : teleporter) {
			if (td.name != null && !td.name.equals(""))
				dimension.add(td.dimension);
		}
		return dimension;
	}

	public static boolean isNameOk(String text) {
		return !getTargetNames().contains(text);
	}

	public static PCtp_TeleporterData getTargetByName(String name) {
		for (PCtp_TeleporterData td : teleporter) {
			if (td != null)
				if (name.equals(td.name))
					return td;
		}
		return null;
	}

	public static void openTeleportGui(EntityPlayer player, PCtp_TeleporterData td) {
		List<String> names = getTargetNames();
		for (int i = 0; i < names.size(); i++) {
			PCtp_TeleporterData td2 = getTargetByName(names.get(i));
			if (td2.dimension != player.dimension)
				names.remove(td2.name);
		}

		if (td.name != null && !td.equals(""))
			names.remove(td.name);
		PC_GresRegistry.openGres("PlayerTeleport", player, null, names);
	}

	@Override
	public String getName() {
		return "PCtp_TeleporterManager";
	}

}
