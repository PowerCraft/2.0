package powercraft.machines.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class PCma_ModelFishingMachine extends ModelBase {

	public ModelRenderer model[];
	public ModelRenderer screw[];

	public PCma_ModelFishingMachine() {
		textureWidth = 105;
		textureHeight = 53;

		model = new ModelRenderer[4];
		screw = new ModelRenderer[6];

		model[0] = new ModelRenderer(this, 0, 0);
		model[0].addBox(-9, -22.9F, -9, 18, 7, 18, 0.0F);

		model[1] = new ModelRenderer(this, 0, 26);
		model[1].addBox(-7, -16, -7, 14, 14, 14, 0.0F);

		model[2] = new ModelRenderer(this, 56, 33);
		model[2].addBox(-2, -32, -12, 4, 16, 4, 0.0F);

		model[3] = new ModelRenderer(this, 56, 25);
		model[3].addBox(-3, -33, -13, 6, 2, 6, 0.0F);

		screw[0] = new ModelRenderer(this, 42, 26);
		screw[0].addBox(-0.5F, -10, -0.5F, 1, 10, 1, 0.0F);

		screw[1] = new ModelRenderer(this, 75, 0);
		screw[1].addBox(-2, -11, -2, 4, 6, 4, 0.0F);

		screw[2] = new ModelRenderer(this, 75, 0);
		screw[2].addBox(-2, -1, -8, 4, 1, 6, 0.0F);
		screw[2].setRotationPoint(0.0F, -9.0F, 0.0F);
		screw[2].rotateAngleZ = ((float) Math.PI) / 4F;

		screw[3] = new ModelRenderer(this, 75, 0);
		screw[3].addBox(-2, -1, -8, 4, 1, 6, 0.0F);
		screw[3].setRotationPoint(0.0F, -9.0F, 0.0F);
		screw[3].rotateAngleZ = -((float) Math.PI) / 4F;
		screw[3].rotateAngleY = (float) Math.PI;

		screw[4] = new ModelRenderer(this, 75, 0);
		screw[4].addBox(-8, -1, -2, 6, 1, 4, 0.0F);
		screw[4].setRotationPoint(0.0F, -9.0F, 0.0F);
		screw[4].rotateAngleX = ((float) Math.PI) / 4F;

		screw[5] = new ModelRenderer(this, 75, 0);
		screw[5].addBox(-8, -1, -2, 6, 1, 4, 0.0F);
		screw[5].setRotationPoint(0.0F, -9.0F, 0.0F);
		screw[5].rotateAngleX = ((float) Math.PI) / 4F;
		screw[5].rotateAngleY = (float) Math.PI;

	}

	public void renderModel() {
		for (int i = 0; i < model.length; i++) {
			model[i].render(0.0625F);
		}
	}

	public void renderScrew() {
		for (int i = 0; i < screw.length; i++) {
			screw[i].render(0.0625F);
		}
	}

}
