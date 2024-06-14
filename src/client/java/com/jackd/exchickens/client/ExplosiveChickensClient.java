package com.jackd.exchickens.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;

public class ExplosiveChickensClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplosiveChickensClient.class);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Hello from Explosive Chickens client");
    }

}
