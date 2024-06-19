package com.jackd.exchickens.entity;

import com.jackd.exchickens.ModContent;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityLaunchedEgg extends ThrownItemEntity {

    public EntityLaunchedEgg(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public EntityLaunchedEgg(World world, LivingEntity owner) {
        super(ModContent.LAUNCHED_EGG_ENTITY, owner, world);
    }

    public EntityLaunchedEgg(World world, double x, double y, double z) {
        super(ModContent.LAUNCHED_EGG_ENTITY, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModContent.TRICK_EGG_ITEM;
    }
    
}
