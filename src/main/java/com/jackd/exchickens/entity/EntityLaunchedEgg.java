package com.jackd.exchickens.entity;

import com.jackd.exchickens.ModContent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

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

    private void explode() {
        World world = this.getEntityWorld();
        if(!world.isClient) {
            Entity owner = this.getOwner();
            world.createExplosion(owner, this.getX(), this.getY(), this.getZ(), EntityExplodingChicken.randomExplosionRange(), ExplosionSourceType.MOB);
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        this.explode();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.explode();
    }

    @Override
    protected Item getDefaultItem() {
        return ModContent.TRICK_EGG_ITEM;
    }
    
}
