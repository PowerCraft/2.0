package powercraft.storage;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWidget.PC_GresAlign;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.tileentity.PC_TileEntity;

public class PCs_GuiCompressor implements PC_IGresClient {

	public PC_GresTextEdit name;
	public EntityPlayer player;

	private PC_GresWidget btnOK;
	private PC_GresWidget btnCANCEL;

	public PCs_GuiCompressor(EntityPlayer player, PC_TileEntity te, Object[] o) {
		this.player = player;
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow(PCs_App.compressor.getUnlocalizedName() + ".normal.name");
		NBTTagCompound nbt = player.getCurrentEquippedItem().getTagCompound();
		int[] pos = { 0, 0, 0 };
		if (nbt != null)
			pos = nbt.getIntArray("posChest");
		w.add(new PC_GresLabel(StatCollector.translateToLocal("container.chest") + " (" + "x:" + pos[0] + " y:" + pos[1]
				+ " z:" + pos[2] + ")"));
		w.add(new PC_GresLabel("pc.gui.compressor.name"));
		w.setAlignH(PC_GresAlign.LEFT);
		name = new PC_GresTextEdit(PCs_ItemCompressor.getName(player.getCurrentEquippedItem()), 20);
		w.add(name);
		w.add(btnCANCEL = new PC_GresButton("pc.gui.cancel").setId(1));
		w.add(btnOK = new PC_GresButton("pc.gui.ok").setId(0));
		gui.add(w);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if (widget == btnCANCEL) {
			gui.close();
		} else if (widget == btnOK) {
			PCs_ItemCompressor.setName(player, name.getText());

		}
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
