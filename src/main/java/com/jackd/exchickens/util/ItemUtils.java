package com.jackd.exchickens.util;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

public class ItemUtils {

    public static boolean hasEnchantment(ItemStack stack, RegistryKey<Enchantment> key, World world) {
        RegistryEntry<Enchantment> entry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).entryOf(key);
        return EnchantmentHelper.getLevel(entry, stack) > 0;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void mergeComponentsFromStack(ItemStack srcStack, ItemStack dstStack) {
        ComponentMap defaultMap = dstStack.getDefaultComponents();
        for(ComponentType type : srcStack.getComponents().getTypes()) {
            if(dstStack.contains(type) && !defaultMap.contains(type)) continue;
            dstStack.set(type, srcStack.get(type));
        }
    }

}
