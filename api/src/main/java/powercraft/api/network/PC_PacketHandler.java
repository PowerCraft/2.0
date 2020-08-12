package powercraft.api.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import powercraft.api.network.packet.PC_PacketOpenGres;
import powercraft.api.network.packet.PC_PacketSyncInv;
import powercraft.api.network.packet.PC_PacketSyncInvTC;
import powercraft.api.network.packet.PC_PacketSyncPlayerInv;
import powercraft.api.network.packet.PC_PacketSyncTEClient;
import powercraft.api.network.packet.PC_PacketSyncTEServer;

/**
 * 
 * This class will house the SimpleNetworkWrapper instance, which I will name
 * 'dispatcher', as well as give us a logical place from which to register our
 * packets. These two things could be done anywhere, however, even in your Main
 * class, but I will be adding other functionality (see below) that gives this
 * class a bit more utility.
 * 
 * While unnecessary, I'm going to turn this class into a 'wrapper' for
 * SimpleNetworkWrapper so that instead of writing
 * "PacketDispatcher.dispatcher.{method}" I can simply write
 * "PacketDispatcher.{method}" All this does is make it quicker to type and
 * slightly shorter; if you do not care about that, then make the 'dispatcher'
 * field public instead of private, or, if you do not want to add a new class
 * just for one field and one static method that you could put anywhere, feel
 * free to put them wherever.
 * 
 * For further convenience, I have also added two extra sendToAllAround methods:
 * one which takes an EntityPlayer and one which takes coordinates.
 *
 */
public class PC_PacketHandler {
	// a simple counter will allow us to get rid of 'magic' numbers used during
	// packet registration
	private static byte packetId = 0;

	/**
	 * The SimpleNetworkWrapper instance is used both to register and send packets.
	 * Since I will be adding wrapper methods, this field is private, but you should
	 * make it public if you plan on using it directly.
	 */
	private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE
			.newSimpleChannel(powercraft.launcher.mod_PowerCraft.MODID);

	/**
	 * Call this during pre-init or loading and register all of your packets
	 * (messages) here
	 */
	public static final void registerPackets() {
		// Packets handled on CLIENT
		registerMessage(PC_PacketOpenGres.class);
		registerMessage(PC_PacketSyncTEClient.class);

		// Packets handled on SERVER
		registerMessage(PC_PacketSyncInv.class);
		registerMessage(PC_PacketSyncPlayerInv.class);
		registerMessage(PC_PacketSyncTEServer.class);

		// Packets Bidirectional
		registerMessage(PC_PacketSyncInvTC.class);
	}

	public static <T extends PC_AbstractMessage<T> & IMessageHandler<T, IMessage>> void registry(Class<T> clazz) {
		registerMessage(clazz);
	}

	/**
	 * Registers an {@link PC_AbstractMessage} to the appropriate side(s)
	 */
	private static final <T extends PC_AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage(
			Class<T> clazz) {
		// We can tell by the message class which side to register it on by using
		// #isAssignableFrom (google it)

		// Also, one can see the convenience of using a static counter 'packetId' to
		// keep
		// track of the current index, rather than hard-coding them all, plus it's one
		// less
		// parameter to pass.
		if (PC_AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
			PC_PacketHandler.dispatcher.registerMessage(clazz, clazz, packetId++, Side.CLIENT);
		} else if (PC_AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
			PC_PacketHandler.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		} else {
			// hopefully you didn't forget to extend the right class, or you will get
			// registered on both sides
			PC_PacketHandler.dispatcher.registerMessage(clazz, clazz, packetId, Side.CLIENT);
			PC_PacketHandler.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		}
	}

	// ========================================================//
	// The following methods are the 'wrapper' methods; again,
	// this just makes sending a message slightly more compact
	// and is purely a matter of stylistic preference
	// ========================================================//

	/**
	 * Send this message to the specified player's client-side counterpart. See
	 * {@link SimpleNetworkWrapper#sendTo(IMessage, EntityPlayerMP)}
	 */
	public static final void sendTo(IMessage message, EntityPlayerMP player) {
		PC_PacketHandler.dispatcher.sendTo(message, player);
	}

	/**
	 * Send this message to everyone. See
	 * {@link SimpleNetworkWrapper#sendToAll(IMessage)}
	 */
	public static void sendToAll(IMessage message) {
		PC_PacketHandler.dispatcher.sendToAll(message);
	}

	/**
	 * Send this message to everyone within a certain range of a point. See
	 * {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		PC_PacketHandler.dispatcher.sendToAllAround(message, point);
	}

	/**
	 * Sends a message to everyone within a certain range of the coordinates in the
	 * same dimension. Shortcut to
	 * {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static final void sendToAllAround(IMessage message, int dimension, double x, double y, double z,
			double range) {
		PC_PacketHandler.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
	}

	/**
	 * Sends a message to everyone within a certain range of the player provided.
	 * Shortcut to
	 * {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
		PC_PacketHandler.sendToAllAround(message, player.worldObj.provider.dimensionId, player.posX, player.posY,
				player.posZ, range);
	}

	/**
	 * Send this message to everyone within the supplied dimension. See
	 * {@link SimpleNetworkWrapper#sendToDimension(IMessage, int)}
	 */
	public static final void sendToDimension(IMessage message, int dimensionId) {
		PC_PacketHandler.dispatcher.sendToDimension(message, dimensionId);
	}

	/**
	 * Send this message to the server. See
	 * {@link SimpleNetworkWrapper#sendToServer(IMessage)}
	 */
	public static final void sendToServer(IMessage message) {
		PC_PacketHandler.dispatcher.sendToServer(message);
	}
}
