package com.jackd.exchickens.client.renderer;

import com.jackd.exchickens.entity.EntityLaunchedEgg;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class EntityLaunchedEggRenderer extends EntityRenderer<EntityLaunchedEgg> {

    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/chicken.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);

    private final FlyingItemEntityRenderer<EntityLaunchedEgg> eggVariantRenderer;
    private final ChickenEntityModel<EntityLaunchedEgg> chickenVariantModel;
    private final ModelPart chickenModelRoot;

    public EntityLaunchedEggRenderer(Context ctx) {
        super(ctx);
        this.eggVariantRenderer = new FlyingItemEntityRenderer<EntityLaunchedEgg>(ctx);
        TexturedModelData chickenModel = ChickenEntityModel.getTexturedModelData();
        this.chickenModelRoot = chickenModel.createModel();
        this.chickenVariantModel = new ChickenEntityModel<>(this.chickenModelRoot);
    }

    @Override
    public void render(EntityLaunchedEgg entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        switch(entity.getVariant()) {
            case INCUBATING:
                matrices.push();
                // align model with entity rotation
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 90.0F));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch()) + 90.0F));
                VertexConsumer consumer = vertexConsumers.getBuffer(LAYER);
                this.chickenVariantModel.child = false;
                this.chickenVariantModel.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();
                break;
            case REGULAR:
            default:
                this.eggVariantRenderer.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
                break;
        }
    }

    @Override
    public Identifier getTexture(EntityLaunchedEgg entity) {
        return TEXTURE;
    }

}
