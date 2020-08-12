package powercraft.transport.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresGap;
import powercraft.api.gres.PC_GresImage;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresInventoryPlayer;
import powercraft.api.gres.PC_GresLayoutH;
import powercraft.api.gres.PC_GresLayoutV;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWidget.PC_GresAlign;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSyncInv;
import powercraft.api.network.packet.PC_PacketSyncInvTC;
import powercraft.api.network.packet.PC_PacketSyncPlayerInv;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_VecI;
import powercraft.transport.container.PCtr_ContainerSeparationBelt;
import powercraft.transport.tile.PCtr_TileEntitySeparationBeltBase;

public class PCtr_GuiSeparationBelt extends PCtr_ContainerSeparationBelt implements PC_IGresClient {

	public PCtr_TileEntitySeparationBeltBase te;
	public EntityPlayer player;

	public PCtr_GuiSeparationBelt(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, te, o);
		this.te = (PCtr_TileEntitySeparationBeltBase) te.getWorldObj().getTileEntity(te.xCoord, te.yCoord, te.zCoord);
		this.player = player;
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_PacketHandler.sendToServer(
				new PC_PacketSyncInvTC(null, new Object[] { new PC_VecI(te.xCoord, te.yCoord, te.zCoord) }));

		PC_GresWindow w = new PC_GresWindow(00, 00, "tile.PCtr_BlockBeltSeparator.name");
		w.setWidthForInventory();
		PC_GresLayoutH hg = new PC_GresLayoutH();
		hg.add(new PC_GresImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", 56, 66, 8, 15));

		PC_GresInventory left, right;

		hg.add(left = new PC_GresInventory(3, 3));

		hg.add(right = new PC_GresInventory(3, 3));

		for (int i = 0; i < te.separatorContents.length; i++) {
			if (i % 6 >= 3) {
				left.setSlot(i % 3, (int) Math.floor(i / 6), invSlots[i]);
			} else {
				right.setSlot(i % 3, (int) Math.floor(i / 6), invSlots[i]);
			}
		}
		hg.add(new PC_GresImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", 64, 66, 8, 15));
		w.add(hg);

		PC_GresLayoutV vg = new PC_GresLayoutV();
		vg.setAlignH(PC_GresAlign.LEFT);
		vg.setMinWidth(100);
		// vg.add(new PC_GresLabel("pc.gui.separationBelt.group").setWidgetMargin(0));
		vg.setWidgetMargin(0);

		hg = new PC_GresLayoutH();
		hg.setAlignH(PC_GresAlign.LEFT);
		hg.setWidgetMargin(0);

		vg.add(hg);

		w.add(new PC_GresGap(0, 2));
		w.add(vg);
		w.add(new PC_GresGap(0, 2));

		w.add(new PC_GresInventoryPlayer(true));
		w.add(new PC_GresGap(0, 0));
		gui.add(w);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
	}

	@Override
	public void onKeyPressed(PC_IGresGui gui, char c, int i) {
		if (i == Keyboard.KEY_RETURN || i == Keyboard.KEY_ESCAPE || i == Keyboard.KEY_E) {
			PC_PacketHandler.sendToServer(
					new PC_PacketSyncInv(te, new Object[] { new PC_VecI(te.xCoord, te.yCoord, te.zCoord) }));
			PC_PacketHandler.sendToServer(new PC_PacketSyncPlayerInv(player.inventory));
			gui.close();
		}
	}

	@Override
	public void updateTick(PC_IGresGui gui) {
	}

	@Override
	public void updateScreen(PC_IGresGui gui) {
	}

	@Override
	public boolean drawBackground(PC_IGresGui gui, int par1, int par2, float par3) {
		return false;
	}

	@Override
	public void keyChange(String key, Object value) {
	}

}
