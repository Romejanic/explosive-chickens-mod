package com.jackd.exchickens.items;

import com.jackd.exchickens.ModContent;

import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

public class ItemArmorCookable extends ArmorItem {

    // private RegistryEntry<ArmorMaterial> cookedMaterial;

    public ItemArmorCookable(RegistryEntry<ArmorMaterial> rawMaterial, Type type, Settings settings) {
        super(rawMaterial, type, settings);
        // this.cookedMaterial = cookedMaterial;
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if(entity.isOnFire()) {
            float cookTime = stack.getOrDefault(ModContent.ARMOR_COOK_TIME_COMPONENT, 0f);
            if(cookTime >= 2.5f) return;
            cookTime = Math.min(2.5f, cookTime + 1f/20f);
            stack.set(ModContent.ARMOR_COOK_TIME_COMPONENT, cookTime);
        }
    }

}
