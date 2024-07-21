package com.jackd.exchickens.component;

import com.jackd.exchickens.ModContent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public record ArmorCookableComponent(float maxCookTime, ItemStack cookedItem) {

    public static final float DEFAULT_COOK_TIME = 25.0f;
    public static final float SECONDS_PER_TICK = 1f / 20f;

    public static final Codec<ArmorCookableComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
            Codec.FLOAT.fieldOf("max_cook_time").forGetter(ArmorCookableComponent::maxCookTime),
            ItemStack.UNCOUNTED_CODEC.fieldOf("cooked_item").forGetter(ArmorCookableComponent::cookedItem)
        ).apply(builder, ArmorCookableComponent::new);
    });

    public static void inventoryTickItem(ItemStack stack, World world, Entity entity, int slot) {
        if(entity instanceof PlayerEntity living && stack.contains(ModContent.ARMOR_COOKABLE_COMPONENT) && entity.isOnFire()) {
            ArmorCookableComponent comp = stack.get(ModContent.ARMOR_COOKABLE_COMPONENT);
            float cookTime = stack.getOrDefault(ModContent.ARMOR_COOK_TIME_COMPONENT, 0f) + SECONDS_PER_TICK;
            if(cookTime > comp.maxCookTime()) {
                int armorSlot = getInventorySlot(stack, slot, living.getInventory());
                ItemStack cookedStack = comp.cookedItem();
                cookedStack.applyComponentsFrom(stack.getComponents());
                cookedStack.remove(ModContent.ARMOR_COOKABLE_COMPONENT);
                living.getInventory().setStack(armorSlot, cookedStack);
            } else {
                stack.set(ModContent.ARMOR_COOK_TIME_COMPONENT, cookTime);
            }
        }
    }

    private static int getInventorySlot(ItemStack stack, int slot, PlayerInventory inv) {
        int index = inv.getSlotWithStack(stack);
        return index > -1 ? index : PlayerInventory.MAIN_SIZE + slot;
    }

    public static ArmorCookableComponent defaultWithItem(ItemConvertible item) {
        return new ArmorCookableComponent(DEFAULT_COOK_TIME, new ItemStack(item));
    }

}
