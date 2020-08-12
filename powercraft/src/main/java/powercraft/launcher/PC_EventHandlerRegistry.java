package powercraft.launcher;

import java.util.ArrayList;

public class PC_EventHandlerRegistry {

	private static ArrayList<String> eventHandlers = new ArrayList<String>();

	public static void registry(String eventHandler) {
		eventHandlers.add(eventHandler);
	}

	public static ArrayList<String> getEventHandlers() {
		return eventHandlers;
	}

}
