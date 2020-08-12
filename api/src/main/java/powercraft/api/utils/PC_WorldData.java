package powercraft.api.utils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import powercraft.launcher.mod_PowerCraft;

public class PC_WorldData extends WorldSavedData {

	final static String key = mod_PowerCraft.MODID;

	public static PC_WorldData forWorld(World world) {
		MapStorage storage = world.perWorldStorage;
		PC_WorldData result = (PC_WorldData) storage.loadData(PC_WorldData.class, key);
		if (result == null) {
			result = new PC_WorldData(key);
			storage.setData(key, result);
		}
		return result;
	}

	private NBTTagCompound data = new NBTTagCompound();

	public PC_WorldData(String tagName) {
		super(tagName);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		data = compound.getCompoundTag(key);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setTag(key, data);
	}

	public NBTTagCompound getData() {
		return data;
	}
}
