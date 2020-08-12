package powercraft.teleport;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.PC_Lang;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresCheckBox;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresLayoutH;
import powercraft.api.gres.PC_GresLayoutV;
import powercraft.api.gres.PC_GresRadioButton;
import powercraft.api.gres.PC_GresRadioButton.PC_GresRadioGroup;
import powercraft.api.gres.PC_GresScrollArea;
import powercraft.api.gres.PC_GresTab;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWidget.PC_GresAlign;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.tileentity.PC_TileEntity;

public class PCtp_GuiTeleporter implements PC_IGresClient {

	private EntityPlayer player;
	private List<String> names;
	private String defaultTarget;
	private List<Integer> dimension;
	private PCtp_TeleporterData td;
	private PC_GresButton ok;
	private PC_GresTextEdit name;
	private PC_GresTextEdit search;

	private PC_GresRadioGroup rg;

	private PC_GresCheckBox animals;
	private PC_GresCheckBox monsters;
	private PC_GresCheckBox items;
	private PC_GresCheckBox players;
	private PC_GresCheckBox lasers;
	private PC_GresCheckBox sneakTrigger;
	private PC_GresCheckBox playerChoose;
	private PC_GresCheckBox soundEnabled;

	private PC_GresRadioGroup dir;

	private PC_GresWidget radioBox;
	private PC_GresWidget radioBoxScroll;

	private PC_TileEntity te;

	public PCtp_GuiTeleporter(EntityPlayer player, PC_TileEntity te, Object[] o) {
		this.player = player;
		this.te = te;
		td = (PCtp_TeleporterData) o[0];
		names = (List<String>) o[1];
		defaultTarget = (String) o[2];
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = (new PC_GresWindow("tile.PCtp_BlockTeleporter.name"));

		PC_GresTab t = new PC_GresTab();

		PC_GresWidget vg = new PC_GresLayoutV();
		PC_GresWidget hg = new PC_GresLayoutH();
		hg.add(new PC_GresLabel("pc.gui.teleporter.name"));
		hg.add(name = new PC_GresTextEdit(td.name, 10));
		vg.add(hg);

		rg = new PC_GresRadioGroup();

		hg = new PC_GresLayoutH();
		hg.add(new PC_GresLabel("pc.gui.teleporter.target"));
		hg.add(search = new PC_GresTextEdit("", 10));
		vg.add(hg);
		radioBox = new PC_GresLayoutV();
		radioBox.setAlignH(PC_GresAlign.LEFT);
		PCtp_TeleporterManager tm = new PCtp_TeleporterManager();
		for (String name : names) {
			if (!name.equals(td.name)) {
				PC_GresRadioButton rb = new PC_GresRadioButton(name, rg);
				if (name.equals(defaultTarget))
					rb.check(true);
				radioBox.add(rb);
			}
		}
		PC_GresRadioButton rb = new PC_GresRadioButton("pc.gui.teleporter.nothing", rg);
		if (defaultTarget == null || defaultTarget.equals(""))
			rb.check(true);
		radioBox.add(rb);
		vg.add(radioBoxScroll = new PC_GresScrollArea(0, 100, radioBox, PC_GresScrollArea.VSCROLL));
		t.addTab(vg, new PC_GresLabel("pc.gui.teleporter.page1"));
		PC_GresWidget tab1 = vg;

		vg = new PC_GresLayoutV();
		vg.setAlignH(PC_GresAlign.LEFT);
		vg.add(animals = new PC_GresCheckBox("pc.gui.teleporter.animals"));
		animals.check(td.animals);
		vg.add(monsters = new PC_GresCheckBox("pc.gui.teleporter.monsters"));
		monsters.check(td.monsters);
		vg.add(items = new PC_GresCheckBox("pc.gui.teleporter.items"));
		items.check(td.items);
		vg.add(players = new PC_GresCheckBox("pc.gui.teleporter.players"));
		players.check(td.players);
		vg.add(lasers = new PC_GresCheckBox("pc.gui.teleporter.lasers"));
		lasers.check(td.lasers);
		vg.add(sneakTrigger = new PC_GresCheckBox("pc.gui.teleporter.sneakTrigger"));
		sneakTrigger.check(td.sneakTrigger);
		vg.add(playerChoose = new PC_GresCheckBox("pc.gui.teleporter.playerChoose"));
		playerChoose.check(td.playerChoose);
		vg.add(soundEnabled = new PC_GresCheckBox("pc.gui.teleporter.soundEnabled"));
		soundEnabled.check(td.soundEnabled);

		dir = new PC_GresRadioGroup();
		rb = new PC_GresRadioButton("pc.gui.teleporter.north", dir);
		rb.check(td.direction == PCtp_TeleporterData.N);
		rb.setId(PCtp_TeleporterData.N);
		vg.add(rb);
		rb = new PC_GresRadioButton("pc.gui.teleporter.east", dir);
		rb.check(td.direction == PCtp_TeleporterData.E);
		rb.setId(PCtp_TeleporterData.E);
		vg.add(rb);
		rb = new PC_GresRadioButton("pc.gui.teleporter.south", dir);
		rb.check(td.direction == PCtp_TeleporterData.S);
		rb.setId(PCtp_TeleporterData.S);
		vg.add(rb);
		rb = new PC_GresRadioButton("pc.gui.teleporter.west", dir);
		rb.check(td.direction == PCtp_TeleporterData.W);
		rb.setId(PCtp_TeleporterData.W);
		vg.add(rb);

		t.addTab(vg, new PC_GresLabel("pc.gui.teleporter.page2"));

		t.makeTabVisible(tab1);

		w.add(t);
		w.add(ok = new PC_GresButton("pc.gui.ok"));
		gui.add(w);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if (widget == name) {
			ok.enable(!names.contains(name.getText()) || name.getText().equals(td.name));
		} else if (widget == search) {
			String searchFor = widget.getText();
			PC_GresRadioButton sel = null;
			int num = 0;
			for (PC_GresRadioButton rb : rg) {
				if (rb.getText().toLowerCase().contains(searchFor.toLowerCase())) {
					rb.setVisible(true);
					sel = rb;
					num++;
				} else {
					rb.setVisible(false);
				}
			}
			if (num == 1)
				sel.check(true);
			radioBox.setSize(radioBox.getSize().x, 10);
			radioBox.calcSize();
			radioBoxScroll.calcChildPositions();
		} else if (widget == ok) {
			if (ok.isEnabled()) {
				PC_GresRadioButton rb = rg.getChecked();
				String target = "";
				if (rb != null)
					target = rb.getText();
				if (target.equals(PC_Lang.tr("pc.gui.teleporter.nothing")))
					target = "";
				td.name = name.getText();
				td.animals = animals.isChecked();
				td.monsters = monsters.isChecked();
				td.items = items.isChecked();
				td.players = players.isChecked();
				td.lasers = lasers.isChecked();
				td.sneakTrigger = sneakTrigger.isChecked();
				td.playerChoose = playerChoose.isChecked();
				td.soundEnabled = soundEnabled.isChecked();
				td.direction = this.dir.getChecked().getId();
				PC_PacketHandler.sendToServer(new PCtp_PacketTeleporterSync(td, target, 0));
				gui.close();
			}
		}
	}

	@Override
	public void onKeyPressed(PC_IGresGui gui, char c, int i) {
		if (i == Keyboard.KEY_RETURN) {
			if (ok.isEnabled()) {
				actionPerformed(ok, gui);
			}
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
	}

}
