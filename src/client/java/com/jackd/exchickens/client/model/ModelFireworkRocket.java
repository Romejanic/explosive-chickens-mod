package com.jackd.exchickens.client.model;

import com.jackd.exchickens.entity.EntityExplodingChicken;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class ModelFireworkRocket extends EntityModel<EntityExplodingChicken> {

    private final ModelPart bb_main;

    public ModelFireworkRocket(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        bb_main.render(matrices, vertices, light, overlay, color);
    }

    @Override
    public void setAngles(EntityExplodingChicken entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // this.bb_main.setAngles(0f, 180f, 0f);
    }

    private static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("bb_main", ModelPartBuilder.create()
        .uv(0, 0).cuboid(-1.0F, -6.0F, -9.0F, 6.0F, 6.0F, 12.0F, new Dilation(0.0F))
        .uv(0, 20).cuboid(-2.0F, -7.0F, 3.0F, 8.0F, 8.0F, 2.0F, new Dilation(0.0F))
        .uv(20, 20).cuboid(-1.0F, -6.0F, 5.0F, 6.0F, 6.0F, 2.0F, new Dilation(0.0F))
        .uv(0, 0).cuboid(0.0F, -5.0F, 7.0F, 4.0F, 4.0F, 2.0F, new Dilation(0.0F))
        .uv(0, 6).cuboid(1.0F, -4.0F, 9.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
        .uv(26, 2).cuboid(1.5F, -3.5F, -15.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 32);
    }

    public static ModelFireworkRocket get() {
        TexturedModelData modelData = getTexturedModelData();
        return new ModelFireworkRocket(modelData.createModel());
    }

}
