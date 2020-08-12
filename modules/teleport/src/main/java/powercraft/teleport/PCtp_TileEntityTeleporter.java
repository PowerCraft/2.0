package powercraft.teleport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import powercraft.api.tileentity.PC_ITileEntityAABB;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_VecI;

public class PCtp_TileEntityTeleporter extends PC_TileEntity implements PC_ITileEntityAABB {

	public int direction = 0, defaultTargetDirection = 0;
	public List<EntityPlayer> playersForTeleport = new ArrayList<EntityPlayer>();
	public boolean soundEnabled, laserDivert = true;
	public PC_VecI defaultTarget;

	@Override
	public Packet getDescriptionPacket() {
		return new S35PacketUpdateTileEntity(0, 0, 0, 3, new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager networkManager, S35PacketUpdateTileEntity packet) {
	}

	public void readFromNBT(NBTTagCompound nbt) {
		PCtp_TeleporterManager tm = new PCtp_TeleporterManager();
		tm.load(nbt);
		super.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		PCtp_TeleporterManager tm = new PCtp_TeleporterManager();
		tm.save(nbt);
		super.writeToNBT(nbt);
	}

	@Override
	public void create(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX,
			float hitY, float hitZ) {
		if (!world.isRemote) {
			PCtp_TeleporterData tpData = PCtp_TeleporterManager
					.getTeleporterData(world.getWorldInfo().getVanillaDimension(), getCoord());
			direction = tpData.direction;
			if (world.getWorldInfo().getVanillaDimension() != tpData.defaultTargetDimension) {
				defaultTarget = null;
			} else {
				defaultTarget = tpData.defaultTarget;
				if (defaultTarget != null) {
					PCtp_TeleporterData otherTPData = PCtp_TeleporterManager
							.getTeleporterData(tpData.defaultTargetDimension, defaultTarget);
					if (otherTPData != null) {
						defaultTargetDirection = otherTPData.direction;
					} else {
						defaultTarget = null;
					}
				}
			}
		}
	}

	@Override
	public void setWorldObj(World par1World) {
		super.setWorldObj(par1World);
		if (!worldObj.isRemote) {
			PCtp_TeleporterData tpData = PCtp_TeleporterManager
					.getTeleporterData(worldObj.getWorldInfo().getVanillaDimension(), getCoord());
			if (tpData != null) {
				direction = tpData.direction;
				if (worldObj.getWorldInfo().getVanillaDimension() != tpData.defaultTargetDimension) {
					defaultTarget = null;
				} else {
					defaultTarget = tpData.defaultTarget;
					if (defaultTarget != null) {
						PCtp_TeleporterData otherTPData = PCtp_TeleporterManager
								.getTeleporterData(tpData.defaultTargetDimension, defaultTarget);
						if (otherTPData != null) {
							defaultTargetDirection = otherTPData.direction;
						} else {
							defaultTarget = null;
						}
					}
				}
			}
		}
	}

	@Override
	public void updateEntity() {
		PCtp_TeleporterManager tm = new PCtp_TeleporterManager();
		PCtp_TeleporterData td = tm.getTeleporterData(this.getWorldObj().getWorldInfo().getVanillaDimension(),
				new PC_VecI(xCoord, yCoord, zCoord));

		List<EntityPlayer> toRemove = new ArrayList<EntityPlayer>();

		for (EntityPlayer player : playersForTeleport) {

			if (player == null) {
				toRemove.add(player);
			} else {
				if (player.posX < xCoord - 1F || player.posY < yCoord - 1F || player.posZ < zCoord - 1F
						|| player.posX > xCoord + 2F || player.posY > yCoord + 3F || player.posZ > zCoord + 2F) {
					toRemove.add(player);
				}
			}
		}

		for (EntityPlayer player : toRemove) {
			playersForTeleport.remove(player);
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		markDirty();

	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void setData(EntityPlayer player, PC_Struct2<String, Object>[] o) {
		for (PC_Struct2<String, Object> s : o) {
			String var = s.a;
			if (var.equals("direction")) {
				direction = (Integer) s.b;
			} else if (var.equals("soundEnabled")) {
				soundEnabled = (Boolean) s.b;
			} else if (var.equals("laserDivert")) {
				laserDivert = (Boolean) s.b;
			} else if (var.equals("defaultTarget")) {
				defaultTarget = (PC_VecI) s.b;
			} else if (var.equals("defaultTargetDirection")) {
				defaultTargetDirection = (Integer) s.b;
			}
		}
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}
}
