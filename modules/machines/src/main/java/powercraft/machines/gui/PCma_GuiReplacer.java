package powercraft.machines.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.PC_Lang;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresButtonImage;
import powercraft.api.gres.PC_GresCheckBox;
import powercraft.api.gres.PC_GresGap;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresInventoryPlayer;
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
import powercraft.api.network.packet.PC_PacketSyncInvTC;
import powercraft.api.network.packet.PC_PacketSyncTEServer;
import powercraft.api.registry.PC_TextureRegistry;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_VecI;
import powercraft.machines.container.PCma_ContainerReplacer;

public class PCma_GuiReplacer extends PCma_ContainerReplacer implements PC_IGresClient {

	private PC_GresWidget textedit[] = new PC_GresTextEdit[3];
	private PC_GresButton button[] = new PC_GresButton[2];
	private PC_GresLabel errorLabel;
	private PC_GresInventory slot;

	private boolean valid;

	private PC_GresCheckBox checkFrame;
	private EntityPlayer player;

	public PCma_GuiReplacer(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, te, o);
		this.player = player;
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_PacketHandler.sendToServer(new PC_PacketSyncInvTC(null,
				new Object[] { new PC_VecI(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) }));

		PC_GresWidget w = new PC_GresWindow("pc.gui.blockReplacer.title").setWidthForInventory()
				.setAlignH(PC_GresAlign.CENTER);

		PC_GresWidget hg;
		PC_GresWidget vg;

		hg = new PC_GresLayoutH().setAlignV(PC_GresAlign.TOP);

		PC_GresWidget hg1;

		hg1 = new PC_GresLayoutH().setWidgetMargin(1).setAlignV(PC_GresAlign.CENTER).setText("X");
		hg1.add(new PC_GresLabel("X"));
		hg1.add(textedit[0] = new PC_GresTextEdit("" + tileEntity.getCoordOffset().x, 3, PC_GresInputType.INT)
				.setWidgetMargin(1));
		vg = new PC_GresLayoutV().setWidgetMargin(1);
		vg.add(new PC_GresButtonImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", new PC_VecI(44, 18),
				new PC_VecI(6, 4)).setButtonPadding(3, 3).setId(102).setWidgetMargin(0));
		vg.add(new PC_GresButtonImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", new PC_VecI(50, 18),
				new PC_VecI(6, 4)).setButtonPadding(3, 3).setId(101).setWidgetMargin(0));
		hg1.add(vg);
		hg.add(hg1);

		hg.add(new PC_GresGap(3, 0));

		hg1 = new PC_GresLayoutH().setWidgetMargin(1).setAlignV(PC_GresAlign.CENTER).setText("Y");
		hg1.add(new PC_GresLabel("Y"));
		hg1.add(textedit[1] = new PC_GresTextEdit("" + tileEntity.getCoordOffset().y, 3, PC_GresInputType.INT)
				.setWidgetMargin(1));
		vg = new PC_GresLayoutV().setWidgetMargin(1);
		vg.add(new PC_GresButtonImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", new PC_VecI(44, 18),
				new PC_VecI(6, 4)).setButtonPadding(3, 3).setId(202).setWidgetMargin(0));
		vg.add(new PC_GresButtonImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", new PC_VecI(50, 18),
				new PC_VecI(6, 4)).setButtonPadding(3, 3).setId(201).setWidgetMargin(0));
		hg1.add(vg);
		hg.add(hg1);

		hg.add(new PC_GresGap(3, 0));

		hg1 = new PC_GresLayoutH().setWidgetMargin(1).setAlignV(PC_GresAlign.CENTER).setText("Z");
		hg1.add(new PC_GresLabel("Z"));
		hg1.add(textedit[2] = new PC_GresTextEdit("" + tileEntity.getCoordOffset().z, 3, PC_GresInputType.INT)
				.setWidgetMargin(1));
		vg = new PC_GresLayoutV().setWidgetMargin(1);
		vg.add(new PC_GresButtonImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", new PC_VecI(44, 18),
				new PC_VecI(6, 4)).setButtonPadding(3, 3).setId(302).setWidgetMargin(0));
		vg.add(new PC_GresButtonImage(PC_TextureRegistry.getGresImgDir() + "widgets.png", new PC_VecI(50, 18),
				new PC_VecI(6, 4)).setButtonPadding(3, 3).setId(301).setWidgetMargin(0));
		hg1.add(vg);
		hg.add(hg1);

		w.add(hg);

		// w.add(new PC_GresGap(0, 6));

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER).setWidgetMargin(1);
		hg.add(errorLabel = (PC_GresLabel) new PC_GresLabel("").setWidgetMargin(1));
		errorLabel.setColor(PC_GresWidget.textColorEnabled, 0x990000);
		w.add(hg);

		w.add(slot = new PC_GresInventory(invSlots[0]));

		w.add(new PC_GresInventoryPlayer(true));

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.add(checkFrame = new PC_GresCheckBox("pc.gui.blockReplacer.particleFrame"));
		checkFrame.check(tileEntity.isAidEnabled());
		hg.add(button[1] = new PC_GresButton("pc.gui.ok"));
		w.add(hg);

		gui.add(w);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
		tileEntity.syncInventory(0, player, 0);
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if (widget == button[0]) {

			gui.close();

		} else if (widget == button[1]) {

			int x = Integer.parseInt(textedit[0].getText());
			int y = Integer.parseInt(textedit[1].getText());
			int z = Integer.parseInt(textedit[2].getText());

			tileEntity.setCoordOffset(new PC_VecI(x, y, z));
			tileEntity.setAidEnabled(checkFrame.isChecked());
			PC_PacketHandler.sendToServer(new PC_PacketSyncTEServer(
					new Object[] { 1, tileEntity.getCoord(), new PC_VecI(x, y, z), checkFrame.isChecked() }));

			gui.close();

		} else if (widget == textedit[0] || widget == textedit[1] || widget == textedit[2]) {
			valid = false;

			try {

				for (int count = 0; count <= 2; count++) {
					if (!textedit[count].getText().equals("") && !textedit[count].getText().equals("-")) {

						if (Math.abs(Integer.valueOf(textedit[count].getText())) > 16) {
							errorLabel.setText(PC_Lang.tr("pc.gui.blockReplacer.errWrongValue"));
							button[1].enable(false);
							return;
						} else {
							if (Integer.valueOf(textedit[count].getText()) != 0) {
								valid = true;
							}
						}

					} else {
						errorLabel.setText(PC_Lang.tr("pc.gui.blockReplacer.errWrongValue"));
						button[1].enable(false);
						return;
					}
				}

			} catch (NumberFormatException nfe) {
				valid = false;
			}

			if (!valid) {
				errorLabel.setText(PC_Lang.tr("pc.gui.blockReplacer.err3zeros"));
			}

			if (valid) {
				errorLabel.setText("");
				int x = Integer.parseInt(textedit[0].getText());
				int y = Integer.parseInt(textedit[1].getText());
				int z = Integer.parseInt(textedit[2].getText());
				tileEntity.setCoordOffset(new PC_VecI(x, y, z));
			}

			button[1].enable(valid);

		} else {

			if (widget instanceof PC_GresButton) {
				int id = widget.getId();
				String number;
				PC_GresWidget edit = null;
				int num;

				if (id == 101 || id == 102) {
					edit = textedit[0];
				} else if (id == 201 || id == 202) {
					edit = textedit[1];
				} else if (id == 301 || id == 302) {
					edit = textedit[2];
				}

				if (edit == null) {
					return;
				}

				number = edit.getText();
				try {
					num = Integer.parseInt(number);
				} catch (NumberFormatException e) {
					return;
				}

				if (id % 100 == 1) {
					num--;
				}
				if (id % 100 == 2) {
					num++;
				}

				if (num < -16) {
					num = -16;
				}
				if (num > 16) {
					num = 16;
				}

				edit.setText(num + "");
				actionPerformed(edit, gui);

			}

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
		if (key.equals("coordOffset")) {
			PC_VecI pos = (PC_VecI) value;
			textedit[0].setText("" + pos.x);
			textedit[1].setText("" + pos.y);
			textedit[2].setText("" + pos.z);
		} else if (key.equals("aidEnabled")) {
			checkFrame.check((Boolean) value);
		}
	}

}
