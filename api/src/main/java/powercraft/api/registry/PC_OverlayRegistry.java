package powercraft.api.registry;

import java.util.ArrayList;
import java.util.List;

import powercraft.api.renderer.PC_IOverlayRenderer;
import powercraft.api.renderer.PC_OverlayRenderer;

public class PC_OverlayRegistry {

	private static List<PC_IOverlayRenderer> overlayRenderers = new ArrayList<PC_IOverlayRenderer>();

	public static void register(PC_IOverlayRenderer overlayRenderer) {
		if (!overlayRenderers.contains(overlayRenderer)) {
			overlayRenderers.add(overlayRenderer);
		}
	}

	public static void preOverlayRendering(PC_OverlayRenderer baseOverlayRenderer, float timeStamp, boolean screen,
			int mx, int my) {
		for (PC_IOverlayRenderer overlayRenderer : overlayRenderers) {
			overlayRenderer.preOverlayRendering(baseOverlayRenderer, timeStamp, screen, mx, my);
		}
	}

	public static void postOverlayRendering(PC_OverlayRenderer baseOverlayRenderer, float timeStamp, boolean screen,
			int mx, int my) {
		for (PC_IOverlayRenderer overlayRenderer : overlayRenderers) {
			overlayRenderer.postOverlayRendering(baseOverlayRenderer, timeStamp, screen, mx, my);
		}
	}

}
