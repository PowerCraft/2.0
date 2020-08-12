package powercraft.teleport;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresLayoutV;
import powercraft.api.gres.PC_GresScrollArea;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWidget.PC_GresAlign;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.tileentity.PC_TileEntity;

public class PCtp_GuiPlayerTeleport implements PC_IGresClient {

	private EntityPlayer player;
	private List<String> names;
	private PC_GresButton cancel;
	private PC_GresTextEdit search;
	private List<PC_GresButton> targets;
	private PC_GresWidget targetsBox;
	private PC_GresWidget targetsBoxScroll;

	public PCtp_GuiPlayerTeleport(EntityPlayer player, PC_TileEntity te, Object[] o) {
		this.player = player;
		names = (List<String>) o[0];
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWidget vg = new PC_GresLayoutV();
		vg.add(new PC_GresLabel("pc.gui.teleportTo.title").setColor(PC_GresWidget.textColorDisabled, 0xFFFFFF)
				.enable(false));
		vg.add(search = new PC_GresTextEdit("", 10));
		targetsBox = new PC_GresLayoutV();
		targetsBox.setAlignH(PC_GresAlign.LEFT);
		targets = new ArrayList<PC_GresButton>();
		for (String name : names) {
			PC_GresButton but = new PC_GresButton(name);
			targetsBox.add(but);
			targets.add(but);
		}
		vg.add(targetsBoxScroll = new PC_GresScrollArea(0, 120, targetsBox, PC_GresScrollArea.VSCROLL));
		vg.add(cancel = new PC_GresButton("pc.gui.cancel"));
		gui.add(vg);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if (widget == cancel) {
			gui.close();
		} else if (widget == search) {
			String searchFor = widget.getText();
			for (PC_GresButton but : targets) {
				but.setVisible(but.getText().toLowerCase().contains(searchFor.toLowerCase()));
			}
			targetsBox.setSize(targetsBox.getSize().x, 10);
			targetsBox.calcSize();
			targetsBoxScroll.calcChildPositions();
		} else if (widget instanceof PC_GresButton) {
			PCtp_TeleporterData td = PCtp_TeleporterManager.getTargetByName(widget.getText());
			PC_PacketHandler.sendToServer(new PCtp_PacketTeleport(widget.getText()));
			gui.close();
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
