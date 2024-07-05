package com.jackd.exchickens.client.renderer;

import com.jackd.exchickens.ModContent;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class ChickenDotsFeatureRenderer<T extends LivingEntity> extends EyesFeatureRenderer<T, ChickenEntityModel<T>>  {

    private static final Identifier TEXTURE = ModContent.id("textures/entity/chicken_dots.png");
    public static final RenderLayer LAYER = RenderLayer.getEntityDecal(TEXTURE);

    public ChickenDotsFeatureRenderer(FeatureRendererContext<T, ChickenEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return LAYER;
    }

}
