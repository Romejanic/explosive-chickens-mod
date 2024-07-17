package com.jackd.exchickens.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jackd.exchickens.armor.ExplosiveChickenArmor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    
    protected LivingEntityMixin() {
        super(null, null);
    }

    @Inject(method="dropInventory", at=@At("HEAD"))
    private void dropInventory(CallbackInfo info) {
        World world = this.getWorld();
        ExplosiveChickenArmor.handleEntityDeath(this, world, this.getArmorItems());
    }

    @Shadow
    public abstract Iterable<ItemStack> getArmorItems();

}
