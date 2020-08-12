package powercraft.logic.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.PC_Lang;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresCheckBox;
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
import powercraft.logic.tile.PClo_TileEntityPulsar;

public class PClo_GuiPulsar implements PC_IGresClient {

	private PClo_TileEntityPulsar pulsar;

	private PC_GresWidget buttonOK, buttonCancel;
	private PC_GresWidget editDelay;
	private PC_GresWidget editHold;
	private PC_GresWidget txError;

	private PC_GresCheckBox checkSilent, checkPaused;

	private boolean error = false;

	public PClo_GuiPulsar(EntityPlayer player, PC_TileEntity te, Object[] o) {
		pulsar = (PClo_TileEntityPulsar) te;
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow("tile.PClo_BlockPulsar.name");

		w.setAlignH(PC_GresAlign.STRETCH);
		PC_GresWidget hg, vg;

		// layout with the inputs
		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);

		vg = new PC_GresLayoutV().setAlignH(PC_GresAlign.LEFT);
		vg.add(new PC_GresLabel("pc.gui.pulsar.delay"));
		vg.add(editDelay = new PC_GresTextEdit("" + PC_Utils.ticksToSecs(pulsar.getDelay()), 8,
				PC_GresInputType.UNSIGNED_FLOAT));
		hg.add(vg);

		vg = new PC_GresLayoutV().setAlignH(PC_GresAlign.LEFT);
		vg.add(new PC_GresLabel("pc.gui.pulsar.hold"));
		vg.add(editHold = new PC_GresTextEdit("" + PC_Utils.ticksToSecs(pulsar.getHold()), 8,
				PC_GresInputType.UNSIGNED_FLOAT));
		hg.add(vg);

		w.add(hg);

		w.add(txError = new PC_GresLabel("").setColor(PC_GresWidget.textColorEnabled, 0x990000)
				.setColor(PC_GresWidget.textColorHover, 0x990000));

		/*
		 * w.add(new PC_GresGap(0,3)); w.add(new
		 * PC_GresImage(mod_PClogic.getImgDir()+"pulsar_hint.png", 0, 0, 131, 20));
		 * w.add(new PC_GresGap(0,3));
		 */

		// buttons
		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.setAlignH(PC_GresAlign.CENTER);
		hg.add(checkSilent = new PC_GresCheckBox("pc.gui.pulsar.silent").check(pulsar.isSilent()));
		hg.add(checkPaused = new PC_GresCheckBox("pc.gui.pulsar.paused").check(pulsar.isPaused()));
		w.add(hg);
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

		if (widget == editDelay || widget == editHold) {
			String delay = editDelay.getText();
			String hold = editHold.getText();
			txError.setText("");
			if (!(delay.equals("") || hold.equals(""))) {
				int idelay = PC_Utils.secsToTicks(Double.parseDouble(delay));
				int ihold = PC_Utils.secsToTicks(Double.parseDouble(hold));
				if (idelay < 2) {
					txError.setText(PC_Lang.tr("pc.gui.pulsar.errDelay"));
				} else if (ihold >= idelay || ihold <= 0) {
					txError.setText(PC_Lang.tr("pc.gui.pulsar.errHold"));
				}
			}
		}

		if (widget == buttonCancel) {
			gui.close();
		}

		if (widget == buttonOK) {
			String delay = editDelay.getText();
			String hold = editHold.getText();
			txError.setText("");
			if (!(delay.equals("") || hold.equals(""))) {
				int idelay = PC_Utils.secsToTicks(Double.parseDouble(delay));
				int ihold = PC_Utils.secsToTicks(Double.parseDouble(hold));
				if (idelay < 2) {
					txError.setText(PC_Lang.tr("pc.gui.pulsar.errDelay"));
				} else if (ihold >= idelay || ihold <= 0) {
					txError.setText(PC_Lang.tr("pc.gui.pulsar.errHold"));
				} else {
					pulsar.setSilent(checkSilent.isChecked());
					pulsar.setPaused(checkPaused.isChecked());
					pulsar.setTimes(idelay, ihold);
					PC_PacketHandler.sendToServer(new PC_PacketSyncTEServer(new Object[] { 0, pulsar.getCoord(),
							checkPaused.isChecked(), checkSilent.isChecked(), idelay, ihold }));
					gui.close();
				}
			}
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
		if (key.equals("delay")) {
			editDelay.setText("" + (Integer) value);
		} else if (key.equals("holdtime")) {
			editHold.setText("" + (Integer) value);
		} else if (key.equals("paused")) {
			checkPaused.check((Boolean) value);
		} else if (key.equals("silent")) {
			checkSilent.check((Boolean) value);
		}
	}

}
