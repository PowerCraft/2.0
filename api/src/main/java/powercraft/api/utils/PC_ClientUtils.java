package powercraft.api.utils;

import java.io.File;
import java.util.HashMap;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.discovery.ContainerType;
import cpw.mods.fml.common.discovery.ModCandidate;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.api.registry.PC_RegistryClient;
import powercraft.launcher.mod_PowerCraft;

public class PC_ClientUtils extends PC_Utils {

	private HashMap<String, Class<? extends EntityFX>> entityFX = new HashMap<String, Class<? extends EntityFX>>();

	private PC_ClientUtils() {
	}

	public static boolean create() {
		if (instance == null) {
			instance = new PC_ClientUtils();
			PC_RegistryClient.create();
			return true;
		}

		return false;
	}

	public static Minecraft mc() {
		return Minecraft.getMinecraft();
	}

	@Override
	protected World iGetWorldForDimension(int dimension) {
		IntegratedServer server = mc().getIntegratedServer();
		if (server != null) {
			return server.worldServerForDimension(dimension);
		}
		return mc().theWorld;
	}

	@Override
	protected boolean iIsClient() {
		return true;
	}

	@Override
	protected GameType iGetGameTypeFor(EntityPlayer player) {
		return PC_ReflectHelper.getValue(PlayerControllerMP.class, mc().playerController, 10, GameType.class);
	}

	@Override
	protected File iGetMCDirectory() {
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	protected boolean iIsEntityFX(Entity entity) {
		return entity instanceof EntityFX;
	}

	public static void registerEnitiyFX(Class<? extends EntityFX> fx) {
		registerEnitiyFX(fx.getSimpleName(), fx);
	}

	public static void registerEnitiyFX(String name, Class<? extends EntityFX> fx) {
		((PC_ClientUtils) instance).entityFX.put(name, fx);
	}

	@Override
	protected void iSpawnParticle(String name, Object[] o) {

		if (!entityFX.containsKey(name)) {
			System.err.println("no particle for \"" + name + "\"");
			return;
		}

		EntityFX fx = PC_ReflectHelper.create(entityFX.get(name), o);

		if (fx != null) {
			mc().effectRenderer.addEffect(fx);
		}

	}

	@Override
	protected void iChatMsg(String tr) {
		mc().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(tr));
	}

	public static MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end,
			Block block) {
		block.setBlockBoundsBasedOnState(world, x, y, z);
		start = start.addVector((double) (-x), (double) (-y), (double) (-z));
		end = end.addVector((double) (-x), (double) (-y), (double) (-z));

		Vec3 vec32 = start.getIntermediateWithXValue(end, block.getBlockBoundsMinX());
		Vec3 vec33 = start.getIntermediateWithXValue(end, block.getBlockBoundsMaxX());
		Vec3 vec34 = start.getIntermediateWithYValue(end, block.getBlockBoundsMinY());
		Vec3 vec35 = start.getIntermediateWithYValue(end, block.getBlockBoundsMaxY());
		Vec3 vec36 = start.getIntermediateWithZValue(end, block.getBlockBoundsMinZ());
		Vec3 vec37 = start.getIntermediateWithZValue(end, block.getBlockBoundsMaxZ());
		Vec3 vec38 = null;

		AxisAlignedBB aabb = block.getSelectedBoundingBoxFromPool(world, x, y, z);
		byte b0 = -1;
		if (aabb == null)
			return null;
		if (!PC_Utils.isVecInsideYZBounds(vec32, aabb.getOffsetBoundingBox(-x, -y, -z)))
			vec32 = null;

		if (!PC_Utils.isVecInsideYZBounds(vec33, aabb.getOffsetBoundingBox(-x, -y, -z)))
			vec33 = null;

		if (!PC_Utils.isVecInsideXZBounds(vec34, aabb.getOffsetBoundingBox(-x, -y, -z)))
			vec34 = null;

		if (!PC_Utils.isVecInsideXZBounds(vec35, aabb.getOffsetBoundingBox(-x, -y, -z)))
			vec35 = null;

		if (!PC_Utils.isVecInsideXYBounds(vec36, aabb.getOffsetBoundingBox(-x, -y, -z)))
			vec36 = null;

		if (!PC_Utils.isVecInsideXYBounds(vec37, aabb.getOffsetBoundingBox(-x, -y, -z)))
			vec37 = null;

		if (vec32 != null && (vec38 == null || start.squareDistanceTo(vec32) < start.squareDistanceTo(vec38)))
			vec38 = vec32;

		if (vec33 != null && (vec38 == null || start.squareDistanceTo(vec33) < start.squareDistanceTo(vec38)))
			vec38 = vec33;

		if (vec34 != null && (vec38 == null || start.squareDistanceTo(vec34) < start.squareDistanceTo(vec38)))
			vec38 = vec34;

		if (vec35 != null && (vec38 == null || start.squareDistanceTo(vec35) < start.squareDistanceTo(vec38)))
			vec38 = vec35;

		if (vec36 != null && (vec38 == null || start.squareDistanceTo(vec36) < start.squareDistanceTo(vec38)))
			vec38 = vec36;

		if (vec37 != null && (vec38 == null || start.squareDistanceTo(vec37) < start.squareDistanceTo(vec38)))
			vec38 = vec37;

		if (vec38 == vec32)
			b0 = 4;

		if (vec38 == vec33)
			b0 = 5;

		if (vec38 == vec34)
			b0 = 0;

		if (vec38 == vec35)
			b0 = 1;

		if (vec38 == vec36)
			b0 = 2;

		if (vec38 == vec37)
			b0 = 3;

		if (vec38 == null)
			return null;
		else
			return new MovingObjectPosition(x, y, z, b0, vec38.addVector((double) x, (double) y, (double) z));
	}

}
