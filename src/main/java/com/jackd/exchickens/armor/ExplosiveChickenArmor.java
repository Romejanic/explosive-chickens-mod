package com.jackd.exchickens.armor;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.util.ExplosionSizes;

import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class ExplosiveChickenArmor {

    public static void handleEntityDeath(Entity entity, World world, Iterable<ItemStack> armorItems) {
        if(!world.isClient) {
            float multiplier = 0f;
            for(ItemStack armorStack : armorItems) {
                if(armorStack.getItem() instanceof ArmorItem armorItem && isChickenArmor(armorItem.getMaterial())) {
                    multiplier += armorItem.getType().getMaxDamage(1) / 10.0f;
                }
            }
            if(multiplier > 0f) {
                float explosionPower = ExplosionSizes.chickenArmor() * multiplier;
                world.createExplosion(entity, entity.getX(), entity.getY(), entity.getZ(), explosionPower, entity.isOnFire(), ExplosionSourceType.MOB);
            }
        }
    }

    private static boolean isChickenArmor(RegistryEntry<ArmorMaterial> material) {
        return material == ModContent.CHICKEN_ARMOR || material == ModContent.COOKED_CHICKEN_ARMOR;
    }

}
