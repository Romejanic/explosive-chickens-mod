package com.jackd.exchickens.client.renderer;

import com.jackd.exchickens.entity.EntityLaunchedEgg;

import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class EntityIncubatingLaunchedEggRenderer extends EntityRenderer<EntityLaunchedEgg> {

    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/chicken.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);

    private final ChickenEntityModel<EntityLaunchedEgg> chickenVariantModel;

    public EntityIncubatingLaunchedEggRenderer(Context ctx) {
        super(ctx);
        TexturedModelData chickenModel = ChickenEntityModel.getTexturedModelData();
        this.chickenVariantModel = new ChickenEntityModel<>(chickenModel.createModel());
    }

    @Override
    public void render(EntityLaunchedEgg entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        // align model with entity rotation
        matrices.translate(0f, 0.4f, 0f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch()) + 90.0F));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)(entity.age + tickDelta) * 25.0f));
        matrices.translate(0f, -0.9f, 0f);
        // render entity model
        VertexConsumer consumer = vertexConsumers.getBuffer(LAYER);
        this.chickenVariantModel.child = false;
        this.chickenVariantModel.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV);
        // render chicken dots manually
        consumer = vertexConsumers.getBuffer(ChickenDotsFeatureRenderer.LAYER);
        this.chickenVariantModel.render(matrices, consumer, 0xF00000, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(EntityLaunchedEgg entity) {
        return TEXTURE;
    }

}
