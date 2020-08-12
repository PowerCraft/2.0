package powercraft.logic.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresInventoryPlayer;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresLayoutH;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.logic.container.PClo_ContainerSpecial;
import powercraft.logic.type.PClo_SpecialType;

public class PClo_GuiSpecial extends PClo_ContainerSpecial implements PC_IGresClient {

	private String addString;

	public PClo_GuiSpecial(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, te, o);
		addString = PClo_SpecialType.names[tileEntity.getType()];
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow("pc.gui.special." + addString + ".name");

		PC_GresWidget lh = new PC_GresLayoutH();

		lh.add(new PC_GresLabel("pc.gui.special." + addString + ".inv"));
		PC_GresInventory inv = new PC_GresInventory(1, 1);
		inv.setSlot(0, 0, invSlots[0]);
		lh.add(inv);

		w.add(lh);

		w.add(new PC_GresInventoryPlayer(true));

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
