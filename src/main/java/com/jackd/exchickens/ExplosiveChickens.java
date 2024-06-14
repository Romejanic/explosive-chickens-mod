package com.jackd.exchickens;

import net.fabricmc.api.ModInitializer;

public class ExplosiveChickens implements ModInitializer {

    // private static final Logger LOGGER = LoggerFactory.getLogger(ExplosiveChickens.class);

    public static final String MODID = "exchickens";

    @Override
    public void onInitialize() {
        ModContent.registerContent();
    }

}
