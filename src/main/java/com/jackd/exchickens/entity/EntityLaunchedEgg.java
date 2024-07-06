package com.jackd.exchickens.entity;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.EnumUtils;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.items.ItemChickenLauncher.Variant;
import com.jackd.exchickens.util.ExplosionSizes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class EntityLaunchedEgg extends ThrownItemEntity {

    private static final TrackedData<String> VARIANT;

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
    protected void initDataTracker(Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, Variant.REGULAR.name());
    }

    public void setVariant(Variant variant) {
        this.dataTracker.set(VARIANT, variant.name());
    }

    public Variant getVariant() {
        return Variant.valueOf(this.dataTracker.get(VARIANT));
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

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if(nbt.contains("Variant")) {
            Variant variant = EnumUtils.getEnum(Variant.class, nbt.getString("Variant"));
            if(variant != null) {
                this.setVariant(variant);
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("Variant", this.dataTracker.get(VARIANT));
    }

    static {
        VARIANT = DataTracker.registerData(EntityLaunchedEgg.class, TrackedDataHandlerRegistry.STRING);
    }
    
}
