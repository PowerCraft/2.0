package powercraft.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.reflect.PC_ReflectHelper;

public class PCco_MobSpawnerSetter {

	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		TileEntityMobSpawner tems = (TileEntityMobSpawner) player.worldObj.getTileEntity((Integer) o[0], (Integer) o[1],
				(Integer) o[2]);
		MobSpawnerBaseLogic msbl = tems.func_145881_a();
		msbl.setEntityName((String) o[3]);
		PC_ReflectHelper.setValue(MobSpawnerBaseLogic.class, msbl, 9, null, Entity.class);
		return true;
	}

}
