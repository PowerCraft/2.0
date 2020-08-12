package powercraft.mobile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class PCmo_ModelMiner extends ModelBase {

	public ModelRenderer bodyTop, bodyBottom, bodyChest, trackLeft, trackRight, spadeBase, spadeTop, spadeBottom,
			spadeConnection, button;

	/**
	 * miner model
	 */
	public PCmo_ModelMiner() {
		textureWidth = 134;
		textureHeight = 62;

		bodyTop = new ModelRenderer(this, 55, 31);
		bodyBottom = new ModelRenderer(this, 0, 0);
		bodyChest = new ModelRenderer(this, 0, 31);
		button = new ModelRenderer(this, 28, 44);

		trackLeft = new ModelRenderer(this, 28, 31);
		trackRight = new ModelRenderer(this, 28, 31);

		spadeConnection = new ModelRenderer(this, 0, 0);
		spadeBase = new ModelRenderer(this, 61, -22);
		spadeTop = new ModelRenderer(this, 87, 0);
		spadeBottom = new ModelRenderer(this, 87, 25);

		bodyTop.addBox(-10, -22, -10, 10, 10, 20, 0.0F);
		bodyChest.addBox(0, -21, -9, 9, 9, 18, 0.0F);
		button.addBox(9, -19, -1, 1, 2, 2, 0.0F);
		bodyBottom.addBox(-10, -12, -10, 20, 10, 20, 0.0F);

		trackLeft.addBox(-9, -6, -12, 18, 6, 4, 0.0F);
		trackRight.addBox(-9, -6, 8, 18, 6, 4, 0.0F);

		spadeConnection.addBox(-11, -14, -3, 1, 6, 6, 0.0F);
		spadeBase.addBox(-12, -17, -11, 1, 12, 22, 0.0F);
		spadeTop.addBox(0, 0, 0, 1, 3, 22, 0.0F);
		spadeBottom.addBox(0, 0, 0, 1, 3, 22, 0.0F);

		bodyTop.setRotationPoint(0.0F, 0.0F, 0.0F);
		bodyChest.setRotationPoint(0.0F, 0.0F, 0.0F);
		button.setRotationPoint(0.0F, 0.0F, 0.0F);
		bodyBottom.setRotationPoint(0.0F, 0.0F, 0.0F);
		trackLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
		trackRight.setRotationPoint(0.0F, 0.0F, 0.0F);
		spadeConnection.setRotationPoint(0.0F, 0.0F, 0.0F);
		spadeBase.setRotationPoint(0.0F, 0.0F, 0.0F);
		spadeTop.setRotationPoint(-13.819F, -18.42F, -11F);
		spadeTop.rotateAngleZ = (float) (-Math.PI / 4D);
		spadeBottom.setRotationPoint(-13.12F, -2.9F, -11F);
		spadeBottom.rotateAngleZ = (float) (-Math.PI * 0.75D);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bodyTop.render(f5);
		bodyChest.render(f5);
		bodyBottom.render(f5);
		trackLeft.render(f5);
		trackRight.render(f5);
		spadeConnection.render(f5);
		spadeBase.render(f5);
		spadeTop.render(f5);
		spadeBottom.render(f5);
		button.render(f5);
	}

}
