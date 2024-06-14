package com.jackd.exchickens;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ExplosiveChickens implements ModInitializer {

    // private static final Logger LOGGER = LoggerFactory.getLogger(ExplosiveChickens.class);

    public static final String MODID = "exchickens";

    @Override
    public void onInitialize() {
        ModContent.registerContent();
    }

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }

}
