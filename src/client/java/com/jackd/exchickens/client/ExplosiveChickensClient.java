package com.jackd.exchickens.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.client.renderer.EntityExplosiveChickenRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class ExplosiveChickensClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplosiveChickensClient.class);

    @Override
    public void onInitializeClient() {
        // register entity renderers
        EntityRendererRegistry.register(ModContent.EXPLODING_CHICKEN_ENTITY, (context) -> {
            return new EntityExplosiveChickenRenderer(context);
        });

        LOGGER.info("Registered client renderers for mod");
    }

}
