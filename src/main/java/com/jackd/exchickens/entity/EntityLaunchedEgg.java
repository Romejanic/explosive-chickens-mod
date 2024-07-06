package com.jackd.exchickens.entity;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.items.ItemChickenLauncher.Variant;
import com.jackd.exchickens.util.ExplosionSizes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class EntityLaunchedEgg extends ThrownItemEntity {

    private final Variant variant;

    public EntityLaunchedEgg(EntityType<? extends ThrownItemEntity> entityType, World world, Variant variant) {
        super(entityType, world);
        this.variant = variant;
    }

    public EntityLaunchedEgg(EntityType<? extends ThrownItemEntity> entityType, World world, LivingEntity owner, Variant variant) {
        super(entityType, owner, world);
        this.variant = variant;
    }

    public EntityLaunchedEgg(EntityType<? extends ThrownItemEntity> entityType,World world, double x, double y, double z, Variant variant) {
        super(entityType, x, y, z, world);
        this.variant = variant;
    }

    public Variant getVariant() {
        return this.variant;
    }

    private void explode() {
        World world = this.getEntityWorld();
        if(!world.isClient) {
            Entity owner = this.getOwner();
            world.createExplosion(owner, this.getX(), this.getY(), this.getZ(), this.getExplosionRange(), this.isOnFire(), ExplosionSourceType.MOB);
            this.discard();
        }
    }

    private float getExplosionRange() {
        return this.getVariant() == Variant.INCUBATING ? ExplosionSizes.chickenExplosion() : ExplosionSizes.launcherExplosion();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        DamageSource dmg = ModContent.dmgSource(this.getWorld(), ModContent.LAUNCHER, this.getOwner());
        entityHitResult.getEntity().damage(dmg, Float.MAX_VALUE);
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

    @Override
    public void tick() {
        super.tick();

        // update rotation manually
        World world = this.getWorld();
        if(world.isClient) {
            Vec3d vel = this.getVelocity();
            double d = vel.horizontalLength();
            this.setPitch((float)(MathHelper.atan2(vel.y, d) * MathHelper.DEGREES_PER_RADIAN));
            this.setYaw((float)(MathHelper.atan2(vel.x, vel.z) * MathHelper.DEGREES_PER_RADIAN));
        }
    }

    public static final EntityLaunchedEgg createEntity(World world, LivingEntity owner, Variant variant) {
        switch(variant) {
            case INCUBATING:
                return new EntityLaunchedEgg(ModContent.INCUBATING_LAUNCHED_EGG_ENTITY, world, owner, Variant.INCUBATING);
            case REGULAR:
            default:
                return new EntityLaunchedEgg(ModContent.LAUNCHED_EGG_ENTITY, world, owner, Variant.REGULAR);
        }
    }

    public static final EntityLaunchedEgg createEntity(World world, Position pos, Variant variant) {
        switch(variant) {
            case INCUBATING:
                return new EntityLaunchedEgg(ModContent.INCUBATING_LAUNCHED_EGG_ENTITY, world, pos.getX(), pos.getY(), pos.getZ(), Variant.INCUBATING);
            case REGULAR:
            default:
                return new EntityLaunchedEgg(ModContent.LAUNCHED_EGG_ENTITY, world, pos.getX(), pos.getY(), pos.getZ(), Variant.REGULAR);
        }
    }

}
