package powercraft.logic.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresLayoutH;
import powercraft.api.gres.PC_GresLayoutV;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresTextEdit.PC_GresInputType;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWidget.PC_GresAlign;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSyncTEServer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.logic.tile.PClo_TileEntityDelayer;

public class PClo_GuiDelayer implements PC_IGresClient {

	private PClo_TileEntityDelayer delayer;

	private PC_GresWidget buttonOK, buttonCancel;
	private PC_GresWidget editDelay;
	private PC_GresWidget txError;

	public PClo_GuiDelayer(EntityPlayer player, PC_TileEntity te, Object[] o) {
		delayer = (PClo_TileEntityDelayer) te;
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow("tile.PClo_BlockDelayer.delayer" + delayer.getType() + ".name");

		w.setAlignH(PC_GresAlign.STRETCH);
		PC_GresWidget hg, vg;

		vg = new PC_GresLayoutV().setAlignH(PC_GresAlign.LEFT);
		vg.add(new PC_GresLabel("pc.gui.delayer.delay"));
		vg.add(editDelay = new PC_GresTextEdit("" + PC_Utils.ticksToSecs(delayer.getDelay()), 8,
				PC_GresInputType.UNSIGNED_FLOAT));
		w.add(vg);

		w.add(txError = new PC_GresLabel("").setColor(PC_GresWidget.textColorEnabled, 0x990000)
				.setColor(PC_GresWidget.textColorHover, 0x990000));

		// buttons
		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.setAlignH(PC_GresAlign.JUSTIFIED);
		hg.add(buttonCancel = new PC_GresButton("pc.gui.cancel").setId(1));
		hg.add(buttonOK = new PC_GresButton("pc.gui.ok").setId(0));
		w.add(hg);

		gui.add(w);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if (widget == buttonOK) {
			String sDelay = editDelay.getText();
			if (sDelay != null && !sDelay.equals("")) {
				int delay = PC_Utils.secsToTicks(Double.valueOf(sDelay));
				if (delay > 0) {
					delayer.setDelay(delay);
					PC_PacketHandler
							.sendToServer(new PC_PacketSyncTEServer(new Object[] { 0, delayer.getCoord(), delay }));
					gui.close();
				} else {
					txError.setText("pc.gui.pulsar.errintputzero");
				}
			} else {
				txError.setText("pc.gui.delayer.errnoinput");
			}
		} else if (widget == buttonCancel) {
			gui.close();
		}
	}

	@Override
	public void onKeyPressed(PC_IGresGui gui, char c, int i) {
		if (i == Keyboard.KEY_RETURN) {
			actionPerformed(buttonOK, gui);
		} else if (i == Keyboard.KEY_ESCAPE || i == Keyboard.KEY_E) {
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
		if (key.equals("stateBuffer")) {
			editDelay.setText("" + ((boolean[]) value).length);
		}
	}

}
