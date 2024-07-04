package com.jackd.exchickens.client.renderer;

import net.minecraft.util.math.Vec3d;

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
                // orient model upright
                matrices.translate(0f, 1.3f, 0f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
                // align model with entity rotation
                Vec3d vec3d = entity.getVelocity();
                double d = vec3d.horizontalLength();
                float pitch = (float)(MathHelper.atan2(vec3d.y, d) * MathHelper.DEGREES_PER_RADIAN);
                float yyaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * MathHelper.DEGREES_PER_RADIAN);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yyaw));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
                VertexConsumer consumer = vertexConsumers.getBuffer(LAYER);
                this.chickenVariantModel.child = false;
                this.chickenVariantModel.setAngles(entity, 0f, 0f, MathHelper.sin((float)entity.age) * 0.5f + 0.5f, 0f, 0f);
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
