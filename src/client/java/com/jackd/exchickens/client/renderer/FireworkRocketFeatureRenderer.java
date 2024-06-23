package com.jackd.exchickens.client.renderer;

import org.joml.Quaternionf;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.client.model.ModelFireworkRocket;
import com.jackd.exchickens.entity.EntityExplodingChicken;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class FireworkRocketFeatureRenderer<T extends EntityExplodingChicken> extends FeatureRenderer<T, ChickenEntityModel<T>> {

    private static final Identifier TEXTURE = ModContent.id("textures/entity/firework_rocket_model.png");
    private static final RenderLayer LAYER = RenderLayer.getEntitySolid(TEXTURE);
    private static final Quaternionf ROTATE_QUAT = new Quaternionf().rotateY((float)Math.PI);

    private final ModelFireworkRocket fireworkRocketModel;

    public FireworkRocketFeatureRenderer(FeatureRendererContext<T, ChickenEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
        this.fireworkRocketModel = ModelFireworkRocket.get();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityExplodingChicken entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer consumer = vertexConsumers.getBuffer(LAYER);
        this.fireworkRocketModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

        // move model to appear on top of chicken
        matrices.push();
        matrices.translate(0.05f, 0.2f, 0f);
        matrices.multiply(ROTATE_QUAT);
        matrices.scale(0.4f, 0.4f, 0.4f);
        this.fireworkRocketModel.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }

    @Override
    protected Identifier getTexture(EntityExplodingChicken entity) {
        return TEXTURE;
    }
    
}
