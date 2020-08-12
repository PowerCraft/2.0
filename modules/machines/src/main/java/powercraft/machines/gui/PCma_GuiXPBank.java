package powercraft.machines.gui;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresLayoutH;
import powercraft.api.gres.PC_GresSeparatorH;
import powercraft.api.gres.PC_GresWidget;
import powercraft.api.gres.PC_GresWidget.PC_GresAlign;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresClient;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSyncTEServer;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_ClientUtils;
import powercraft.machines.PCma_App;
import powercraft.machines.tile.PCma_TileEntityXPBank;

public class PCma_GuiXPBank implements PC_IGresClient {

	private PCma_TileEntityXPBank xpbank;
	private PC_GresWidget buttonClose;
	private PC_GresWidget txStoragePoints;
	private PC_GresWidget txPlayerLevels;
	private EntityPlayer player;

	public PCma_GuiXPBank(EntityPlayer player, PC_TileEntity te, Object[] o) {
		xpbank = (PCma_TileEntityXPBank) te;
		this.player = player;
	}

	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWindow w = new PC_GresWindow(PCma_App.xpBank.getUnlocalizedName() + ".name");
		w.setAlignH(PC_GresAlign.CENTER);

		if (xpbank.getXP() < 0)
			xpbank.setXP(0);

		PC_GresWidget hg;

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.LEFT);
		hg.add(new PC_GresLabel("pc.gui.xpbank.storagePoints").setAlignH(PC_GresAlign.RIGHT));
		hg.add(txStoragePoints = new PC_GresLabel(xpbank.getXP() + "").setColor(PC_GresWidget.textColorEnabled,
				0x009900));
		hg.add(new PC_GresLabel("pc.gui.xpbank.xpUnit"));
		w.add(hg);

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.LEFT);
		hg.add(new PC_GresLabel("pc.gui.xpbank.withdraw").setAlignH(PC_GresAlign.RIGHT));

		hg.add(new PC_GresButton("pc.gui.xpbank.oneLevel").setId(10).setMinWidth(50).setWidgetMargin(2));
		hg.add(new PC_GresButton("pc.gui.xpbank.all").setId(11).setMinWidth(50).setWidgetMargin(2));
		w.add(hg);

		w.add(new PC_GresSeparatorH(0, 5).setLineColor(0x999999));

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.LEFT);
		hg.add(new PC_GresLabel("pc.gui.xpbank.currentPlayerLevel").setAlignH(PC_GresAlign.RIGHT));
		hg.add(txPlayerLevels = new PC_GresLabel(xpbank.getXP() + "").setColor(PC_GresWidget.textColorEnabled,
				0x990099));
		hg.add(new PC_GresLabel("pc.gui.xpbank.xpLevels"));
		w.add(hg);

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.LEFT);
		hg.add(new PC_GresLabel("pc.gui.xpbank.deposit").setAlignH(PC_GresAlign.RIGHT));

		hg.add(new PC_GresButton("pc.gui.xpbank.oneLevel").setId(20).setMinWidth(50).setWidgetMargin(2));
		hg.add(new PC_GresButton("pc.gui.xpbank.all").setId(21).setMinWidth(50).setWidgetMargin(2));
		w.add(hg);

		hg = new PC_GresLayoutH().setAlignH(PC_GresAlign.CENTER);
		hg.add(buttonClose = new PC_GresButton("pc.gui.ok").setId(0));
		w.add(hg);

		updateCounters();

		gui.add(w);
	}

	private void updateCounters() {
		txStoragePoints.setText(xpbank.getXP() + "").setMinWidth(0);
		txPlayerLevels.setText(player.experienceLevel + "").setMinWidth(0);
		txPlayerLevels.getParent().calcChildPositions();
		txStoragePoints.getParent().calcChildPositions();
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
		xpbank.getWorldObj().markBlockForUpdate(xpbank.xCoord, xpbank.yCoord, xpbank.zCoord);
	}

	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		Random rand = new Random();

		switch (widget.getId()) {
		case 0:
			gui.close();
			PC_PacketHandler
					.sendToServer(new PC_PacketSyncTEServer(new Object[] { 1, xpbank.getCoord(), xpbank.getXP(), 0 }));
			break;

		case 10: // withdraw one level

			// xpbank.givePlayerXP(player, 1);

			xpbank.givePlayerLevel(player, 1);

			// withdrawOneLevel();

			PC_ClientUtils.mc().theWorld.playSoundAtEntity(player, "random.orb", 0.3F,
					0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
			break;

		case 11: // withdraw all

			// withdrawOneLevel();
			xpbank.givePlayerXP(player, xpbank.getXP());

			PC_ClientUtils.mc().theWorld.playSoundAtEntity(player, "random.orb", 0.3F,
					0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));

			break;

		case 20: // deposit one level

			// depositOneLevel();
			// xpbank.givePlayerXP(player, -1);

			xpbank.givePlayerLevel(player, -1);

			PC_ClientUtils.mc().theWorld.playSoundAtEntity(player, "random.orb", 0.3F,
					0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
			break;

		case 21: // deposit all

			// depositOneLevel();
			xpbank.givePlayerXP(player, -player.experienceTotal);

			PC_ClientUtils.mc().theWorld.playSoundAtEntity(player, "random.orb", 0.3F,
					0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
			break;
		}
		updateCounters();
	}

	@Override
	public void onKeyPressed(PC_IGresGui gui, char c, int i) {
		if (i == Keyboard.KEY_RETURN || i == Keyboard.KEY_ESCAPE || i == Keyboard.KEY_E) {
			gui.close();
		}
	}

	@Override
	public void updateTick(PC_IGresGui gui) {
		updateCounters();
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
