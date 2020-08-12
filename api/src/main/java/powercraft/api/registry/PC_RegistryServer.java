package powercraft.api.registry;

import java.lang.reflect.Field;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import powercraft.api.block.PC_Block;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketOpenGres;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.launcher.loader.PC_ModuleObject;

public class PC_RegistryServer implements PC_IPacketHandler {

	protected static final int KEYEVENT = 0;

	protected static PC_RegistryServer instance;

	protected PC_RegistryServer() {
		PC_PacketHandler.registerPackets();
	}

	public static boolean create() {
		if (instance == null) {
			instance = new PC_RegistryServer();
			return true;
		}
		return false;
	}

	public static PC_RegistryServer getInstance() {
		return instance;
	}

	protected void loadLanguage(PC_ModuleObject module) {
	}

	protected void saveLanguage(PC_ModuleObject module) {
	}

	protected void tileEntitySpecialRenderer(Class<? extends TileEntity> tileEntityClass) {
		GameRegistry.registerTileEntity(tileEntityClass, tileEntityClass.getName());
	}

	protected void openGres(String name, EntityPlayer player, TileEntity te, Object... o) {
		if (!(player instanceof EntityPlayerMP)) {
			return;
		}

		int guiID = 0;

		try {
			Field var6 = EntityPlayerMP.class.getDeclaredFields()[15];
			var6.setAccessible(true);
			guiID = var6.getInt(player);
			guiID = guiID % 100 + 1;
			var6.setInt(player, guiID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PC_PacketHandler.sendTo(new PC_PacketOpenGres(name, te, o), (EntityPlayerMP) player);

		Class<? extends PC_GresBaseWithInventory> c = PC_GresRegistry.getContainer(name);

		if (c != null) {

			if (PC_GresBaseWithInventory.class.isAssignableFrom(c)) {
				try {
					PC_GresBaseWithInventory bwi = PC_ReflectHelper.create(c, player, te, o);
					player.openContainer = bwi;
					player.openContainer.windowId = guiID;
					player.openContainer.addCraftingToCrafters((EntityPlayerMP) player);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void registerTexture(String texture) {
	}

	protected void playSound(double x, double y, double z, String sound, float soundVolume, float pitch) {
	}

	protected void watchForKey(String name, int key) {
	}

	protected void onIconLoading(PC_Block block, Object iconRegister) {

	}

	protected IIcon registerIcon(String texture) {
		return null;
	}

	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		return false;
	}

	public String getUsedLang() {
		return null;
	}

}
