package powercraft.light.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresCheckBox;
import powercraft.api.gres.PC_GresColor;
import powercraft.api.gres.PC_GresColorPicker;
import powercraft.api.gres.PC_GresLayoutH;
import powercraft.api.gres.PC_GresLayoutV;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWidget.PC_GresAlign;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSyncTEServer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Color;
import powercraft.light.tile.PCli_TileEntityLight;

public class PCli_GuiLight implements PC_IGresClient {

	private PCli_TileEntityLight light;

	private PC_GresCheckBox checkHuge, checkStable;
	private PC_GresColor colorWidget;
	private PC_GresColorPicker colorPicker;
	private PC_GresButton accept, cancel;
	private Object[] o;

	public PCli_GuiLight(EntityPlayer player, PC_TileEntity te, Object[] o) {
		light = (PCli_TileEntityLight) te;
		this.o = o;
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow("tile.PCli_BlockLight.name");

		PC_GresWidget v = new PC_GresLayoutV().setAlignH(PC_GresAlign.STRETCH);

		PC_GresWidget h = new PC_GresLayoutH().setAlignH(PC_GresAlign.JUSTIFIED);
		h.add(checkHuge = new PC_GresCheckBox("pc.gui.light.isHuge").check(light.isHuge()));
		h.add(checkStable = new PC_GresCheckBox("pc.gui.light.isStable").check(light.isStable()));
		v.add(h);

		h = new PC_GresLayoutH().setAlignH(PC_GresAlign.STRETCH);
		h.add(colorWidget = new PC_GresColor(light.getColor()));
		h.add(colorPicker = new PC_GresColorPicker(light.getColor().getHex(), 100, 20));
		v.add(h);

		h = new PC_GresLayoutH().setAlignH(PC_GresAlign.STRETCH);
		;
		h.add(accept = new PC_GresButton("pc.gui.ok"));
		h.add(cancel = new PC_GresButton("pc.gui.cancel"));
		v.add(h);
		w.add(v);
		gui.add(w);
		checkHuge.check((Boolean) o[0]);
		checkStable.check((Boolean) o[1]);
		colorPicker.setColor(((PC_Color) o[2]).getHex());
		colorWidget.setColor((PC_Color) o[2]);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if (widget == colorPicker) {
			colorWidget.setColor(colorPicker.getColor());
		} else if (widget == accept) {
			light.setColor(PC_Color.fromHex(colorPicker.getColor()));
			light.setHuge(checkHuge.isChecked());
			light.setStable(checkStable.isChecked());
			PC_PacketHandler.sendToServer(new PC_PacketSyncTEServer(new Object[] { 0, light.getCoord(),
					PC_Color.fromHex(colorPicker.getColor()), checkHuge.isChecked(), checkStable.isChecked() }));
			gui.close();
		} else if (widget == cancel) {
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
		if (key.equals("color")) {
			colorPicker.setColor(((PC_Color) value).getHex());
			colorWidget.setColor(((PC_Color) value).copy());
		} else if (key.equals("isStable")) {
			checkStable.check((Boolean) value);
		} else if (key.equals("isHuge")) {
			checkHuge.check((Boolean) value);
		}
	}

	@Override
	public void onKeyPressed(PC_IGresGui gui, char c, int i) {
		if (i == Keyboard.KEY_RETURN) {
			actionPerformed(accept, gui);
		} else if (i == Keyboard.KEY_ESCAPE || i == Keyboard.KEY_E) {
			gui.close();
		}
	}

}
