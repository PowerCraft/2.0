package powercraft.deco.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class PCde_ModelPlatform extends ModelBase {

	private ModelRenderer ironLedge[];

	/**
	 * Radio block model.
	 */
	public PCde_ModelPlatform() {

		textureWidth = 128;
		textureHeight = 64;

		ironLedge = new ModelRenderer[5];

		// ledges
		ironLedge[0] = new ModelRenderer(this, 64, 0);
		ironLedge[0].addBox(-8F, 7F, -8F, 16, 1, 16, 0.0F);

		ironLedge[1] = new ModelRenderer(this, 0, 37);
		ironLedge[1].addBox(-8F, -4F, -8F, 1, 11, 16, 0.0F);
		ironLedge[1].rotateAngleY = (float) (Math.PI * 1.5F);

		ironLedge[2] = new ModelRenderer(this, 0, 37);
		ironLedge[2].addBox(-8.0002F, -4.00008F, -8.0002F, 1, 11, 16, 0.0F);
		ironLedge[2].rotateAngleY = (float) (Math.PI * 0F);

		ironLedge[3] = new ModelRenderer(this, 0, 37);
		ironLedge[3].addBox(-8.0004F, -4.00005F, -8.0004F, 1, 11, 16, 0.0F);
		ironLedge[3].rotateAngleY = (float) (Math.PI * 0.5F);

		ironLedge[4] = new ModelRenderer(this, 0, 37);
		ironLedge[4].addBox(-8.0006F, -4.0001F, -8.0006F, 1, 11, 16, 0.0F);
		ironLedge[4].rotateAngleY = (float) (Math.PI * 1F);

	}

	/**
	 * Set which fences are shown.
	 * 
	 * @param a     1st
	 * @param b     2nd
	 * @param c     3rd
	 * @param d     4th
	 * @param floor floor piece
	 */
	public void setLedgeFences(boolean a, boolean b, boolean c, boolean d, boolean floor) {
		ironLedge[0].showModel = floor;
		ironLedge[1].showModel = b;
		ironLedge[2].showModel = d;
		ironLedge[3].showModel = a;
		ironLedge[4].showModel = c;
	}

	/**
	 * Do render.
	 * 
	 * @param type device type. Equals to type in tile entity. NonSolid block adds
	 *             100 to it.
	 */
	public void render() {
		// parts[1].render(0.0625F);

		for (ModelRenderer part : ironLedge) {
			if (part == null) {
				break;
			}

			part.render(0.0625F); // length of one size and position unit
		}
	}

}
