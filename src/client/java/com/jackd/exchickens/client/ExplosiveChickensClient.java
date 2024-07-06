package com.jackd.exchickens.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.client.renderer.EntityExplosiveChickenRenderer;
import com.jackd.exchickens.client.renderer.EntityIncubatingLaunchedEggRenderer;
import com.jackd.exchickens.entity.EntityLaunchedEgg;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

@Environment(EnvType.CLIENT)
public class ExplosiveChickensClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplosiveChickensClient.class);

    @Override
    public void onInitializeClient() {
        // register entity renderers
        EntityRendererRegistry.register(ModContent.EXPLODING_CHICKEN_ENTITY, (context) -> {
            return new EntityExplosiveChickenRenderer(context);
        });
        EntityRendererRegistry.register(ModContent.LAUNCHED_EGG_ENTITY, (context) -> {
            return new FlyingItemEntityRenderer<EntityLaunchedEgg>(context);
        });
        EntityRendererRegistry.register(ModContent.INCUBATING_LAUNCHED_EGG_ENTITY, (context) -> {
            return new EntityIncubatingLaunchedEggRenderer(context);
        });

        LOGGER.info("Registered client renderers for mod");
    }

}
