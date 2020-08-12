package powercraft.machines.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresCheckBox;
import powercraft.api.gres.PC_GresFrame;
import powercraft.api.gres.PC_GresGap;
import powercraft.api.gres.PC_GresImage;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresInventoryPlayer;
import powercraft.api.gres.PC_GresLayoutH;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSyncInvTC;
import powercraft.api.network.packet.PC_PacketSyncTEServer;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_VecI;
import powercraft.machines.container.PCma_ContainerAutomaticWorkbench;

public class PCma_GuiAutomaticWorkbench extends PCma_ContainerAutomaticWorkbench implements PC_IGresClient {

	private PC_GresCheckBox checkRedstone;

	private EntityPlayer player;

	public PCma_GuiAutomaticWorkbench(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, te, o);
		this.player = player;
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_PacketHandler.sendToServer(new PC_PacketSyncInvTC(null,
				new Object[] { new PC_VecI(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) }));

		PC_GresWindow w = new PC_GresWindow(50, 50, "tile.PCma_BlockAutomaticWorkbench.name");

		PC_GresWidget hg = new PC_GresLayoutH();
		PC_GresInventory inv = new PC_GresInventory(3, 3);

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				inv.setSlot(x, y, invSlots[x + y * 3 + 1]);
			}
		}

		hg.add(inv);

		PC_GresWidget hg1 = new PC_GresFrame();
		inv = new PC_GresInventory(3, 3);
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				inv.setSlot(x, y, invSlots[x + y * 3 + 10]);
			}
		}

		hg1.add(inv);

		hg1.add(new PC_GresImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", 44, 66, 12, 11));

		hg1.add(new PC_GresInventory(invSlots[0]));

		hg.add(hg1);

		w.add(hg);

		w.add(checkRedstone = new PC_GresCheckBox("pc.gui.automaticWorkbench.redstoneActivated"));
		checkRedstone.check(tileEntity.isRedstoneActivated());
		w.add(new PC_GresGap(0, 3));

		w.add(new PC_GresInventoryPlayer(true));
		w.add(new PC_GresGap(0, 0));

		gui.add(w);

		onCraftMatrixChanged(tileEntity);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
		tileEntity.call("orderAndCraft");
		tileEntity.syncInventory(0, player, 0);
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		tileEntity.setRedstoneActivated(checkRedstone.isChecked());
		PC_PacketHandler.sendToServer(
				new PC_PacketSyncTEServer(new Object[] { 0, tileEntity.getCoord(), checkRedstone.isChecked() }));
	}

	@Override
	public void onKeyPressed(PC_IGresGui gui, char c, int i) {
		if (i == Keyboard.KEY_RETURN || i == Keyboard.KEY_ESCAPE || i == Keyboard.KEY_E) {
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
		if (key.equals("redstoneActivated")) {
			checkRedstone.check((Boolean) value);
		}
	}

}
