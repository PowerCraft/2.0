package powercraft.api.renderer;

public interface PC_IOverlayRenderer {

	public void preOverlayRendering(PC_OverlayRenderer overlayRenderer, float timeStamp, boolean screen, int mx,
			int my);

	public void postOverlayRendering(PC_OverlayRenderer overlayRenderer, float timeStamp, boolean screen, int mx,
			int my);

}
