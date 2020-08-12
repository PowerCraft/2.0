package powercraft.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.interfaces.PC_IDataHandler;
import powercraft.api.interfaces.PC_IMSG;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.utils.PC_Struct3;

public class PCnt_RadioManager implements PC_IDataHandler, PC_IMSG {

	public static final String default_radio_channel = "default";

	private static HashMap<String, Integer> channels = new HashMap<String, Integer>();
	private static HashMap<String, Integer> remoteChannels = new HashMap<String, Integer>();
	private static List<String> weaselChannels = new ArrayList<String>();
	private static boolean needSave = false;

	public static void transmitterOn(String channel) {
		needSave = true;
		int num = 1;
		if (channels.containsKey(channel)) {
			num += channels.get(channel);
		}
		channels.put(channel, num);
	}

	public static void transmitterOff(String channel) {
		needSave = true;
		if (channels.containsKey(channel)) {
			int num = channels.get(channel) - 1;
			if (num <= 0) {
				channels.remove(channel);
			} else {
				channels.put(channel, num);
			}
		}
	}

	public static void remoteOn(String channel) {
		int num = 1;
		if (remoteChannels.containsKey(channel)) {
			num += remoteChannels.get(channel);
		}
		remoteChannels.put(channel, num);
	}

	public static void remoteOff(String channel) {
		if (remoteChannels.containsKey(channel)) {
			int num = remoteChannels.get(channel) - 1;
			if (num <= 0) {
				remoteChannels.remove(channel);
			} else {
				remoteChannels.put(channel, num);
			}
		}
	}

	public static void weaselOn(String channel) {
		if (!weaselChannels.contains(channel)) {
			weaselChannels.add(channel);
		}
	}

	public static void weaselOff(String channel) {
		if (weaselChannels.contains(channel)) {
			weaselChannels.remove(channel);
		}
	}

	public static boolean getChannelState(String channel) {
		return weaselChannels.contains(channel) || channels.containsKey(channel) || remoteChannels.containsKey(channel);
	}

	@Override
	public void load(NBTTagCompound nbtTag) {
		channels.clear();
		weaselChannels.clear();
		int num = nbtTag.getInteger("count");
		for (int i = 0; i < num; i++) {
			String key = nbtTag.getString("key[" + i + "]");
			int value = nbtTag.getInteger("value[" + i + "]");
			channels.put(key, value);
		}
		num = nbtTag.getInteger("wcount");
		for (int i = 0; i < num; i++) {
			String value = nbtTag.getString("wvalue[" + i + "]");
			weaselChannels.add(value);
		}
	}

	@Override
	public NBTTagCompound save(NBTTagCompound nbtTag) {
		nbtTag.setInteger("count", channels.size());
		int i = 0;
		for (Entry<String, Integer> e : channels.entrySet()) {
			nbtTag.setString("key[" + i + "]", e.getKey());
			nbtTag.setInteger("value[" + i + "]", e.getValue());
			i++;
		}
		nbtTag.setInteger("wcount", weaselChannels.size());
		i = 0;
		for (String channel : weaselChannels) {
			nbtTag.setString("wvalue[" + i + "]", channel);
			i++;
		}
		return nbtTag;
	}

	@Override
	public boolean needSave() {
		boolean ret = needSave;
		needSave = false;
		return ret;
	}

	@Override
	public void reset() {
		channels.clear();
		remoteChannels.clear();
		weaselChannels.clear();
	}

	public static class FunctionProvider {

		public void tx(String channel, boolean state) {
			if (state) {
				weaselOn(channel);
			} else {
				weaselOff(channel);
			}
		}

		public boolean rx(String channel) {
			return getChannelState(channel);
		}

	}

	@Override
	public Object msg(int msg, Object... obj) {
		switch (msg) {
		case PC_MSGRegistry.MSG_GET_PROVIDET_GLOBAL_FUNCTIONS:
			List<PC_Struct3<String, String, Object>> l = new ArrayList<PC_Struct3<String, String, Object>>();
			FunctionProvider fp = new FunctionProvider();
			l.add(new PC_Struct3<String, String, Object>("tx", "tx", fp));
			l.add(new PC_Struct3<String, String, Object>("rx", "rx", fp));
			return l;
		}
		return null;
	}

	@Override
	public String getName() {
		return "PC_RadioManager";
	}

}
