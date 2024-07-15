package com.jackd.exchickens.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.util.Averager;
import com.jackd.exchickens.util.ExplosionSizes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    
    protected LivingEntityMixin() {
        super(null, null);
    }

    @Inject(method="dropInventory", at=@At("HEAD"))
    private void dropInventory(CallbackInfo info) {
        World world = this.getWorld();
        if(!world.isClient) {
            Averager explosionPowerAvg = new Averager();
            for(ItemStack armorStack : this.getArmorItems()) {
                if(armorStack.getItem() instanceof ArmorItem armorItem && this.isChickenArmor(armorItem.getMaterial())) {
                    explosionPowerAvg.add(ExplosionSizes.chickenArmor(armorItem.getType()));
                }
            }
            float explosionPower = explosionPowerAvg.getAverage();
            if(explosionPower > 0f) {
                world.createExplosion(this, this.getX(), this.getY(), this.getZ(), explosionPower, ExplosionSourceType.MOB);
            }
        }
    }

    private boolean isChickenArmor(RegistryEntry<ArmorMaterial> material) {
        return material == ModContent.CHICKEN_ARMOR;
    }

    @Shadow
    public abstract Iterable<ItemStack> getArmorItems();

}