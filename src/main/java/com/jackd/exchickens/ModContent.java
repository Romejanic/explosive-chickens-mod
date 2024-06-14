package com.jackd.exchickens;

import com.jackd.exchickens.items.ItemTrickEgg;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModContent {
    
    public static final Item TRICK_EGG_ITEM = new ItemTrickEgg();

    protected static void registerContent() {
        // register all items
        Registry.register(Registries.ITEM, ExplosiveChickens.id("egg"), TRICK_EGG_ITEM);
    }

}
