package com.jackd.exchickens.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class EntityExplodingChicken extends ChickenEntity {

    private static final float MIN_EXPLODE_RANGE = 2.0F;
    private static final float MAX_EXPLODE_RANGE = 6.0F;

    private boolean willExplode = false;
    private boolean didExplode = false;

    public EntityExplodingChicken(EntityType<? extends ChickenEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.willExplode) {
            this.explode();
        }
    }

    public void explode() {
        if(this.didExplode) return;
        this.didExplode = true;

        World world = this.getWorld();
        if(!world.isClient()) {
            float range = MIN_EXPLODE_RANGE + (MAX_EXPLODE_RANGE - MIN_EXPLODE_RANGE) * (float)Math.random();
            world.createExplosion(this, this.getX(), this.getY(), this.getZ(), range, ExplosionSourceType.MOB);
            this.kill();
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(!super.damage(source, amount)) return false;

        // explode on 40% of hits or if hit by an arrow
        if(source.isOf(DamageTypes.ARROW) || Math.random() < 0.4d) {
            this.explode();
        }

        return true;
    }

    @Override
    public boolean collidesWith(Entity other) {
        if(other instanceof PlayerEntity) {
            this.willExplode = true;
        }
        return super.collidesWith(other);
    }

}
