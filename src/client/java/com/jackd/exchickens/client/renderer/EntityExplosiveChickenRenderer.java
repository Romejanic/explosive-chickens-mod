package com.jackd.exchickens.client.renderer;

import com.jackd.exchickens.entity.EntityExplodingChicken;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EntityExplosiveChickenRenderer extends MobEntityRenderer<EntityExplodingChicken, ChickenEntityModel<EntityExplodingChicken>> {

    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/chicken.png");

    public EntityExplosiveChickenRenderer(Context context) {
        super(context, new ChickenEntityModel<EntityExplodingChicken>(context.getPart(EntityModelLayers.CHICKEN)), 0.3f);
        this.addFeature(new ChickenDotsFeatureRenderer<EntityExplodingChicken>(this));
    }

    @Override
    public Identifier getTexture(EntityExplodingChicken entity) {
        return TEXTURE;
    }

    @Override
    protected float getAnimationProgress(EntityExplodingChicken chickenEntity, float tickDelta) {
        float g = MathHelper.lerp(tickDelta, chickenEntity.prevFlapProgress, chickenEntity.flapProgress);
        float h = MathHelper.lerp(tickDelta, chickenEntity.prevMaxWingDeviation, chickenEntity.maxWingDeviation);
        return (MathHelper.sin(g) + 1.0F) * h;
    }

}
