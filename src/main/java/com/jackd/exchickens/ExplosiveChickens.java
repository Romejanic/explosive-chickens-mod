package com.jackd.exchickens;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

public class ExplosiveChickens implements ModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplosiveChickens.class);

    public static final String MODID = "exchickens";

    @Override
    public void onInitialize() {
        ModContent.registerMiscContent();
        LOGGER.info("Finished registering mod content");
    }

}
