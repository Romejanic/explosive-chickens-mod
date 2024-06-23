package com.jackd.exchickens.entity;

import org.jetbrains.annotations.Nullable;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.util.ItemUtils;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EggItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class EntityExplodingChicken extends ChickenEntity {

    private static final TrackedData<ItemStack> FIREWORK_STACK;

    private static final float MIN_EXPLODE_RANGE = 2.0F;
    private static final float MAX_EXPLODE_RANGE = 6.0F;

    private boolean willExplode = false;
    private boolean didExplode = false;

    public EntityExplodingChicken(EntityType<? extends ChickenEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(Builder builder) {
        super.initDataTracker(builder);
        builder.add(FIREWORK_STACK, getDefaultFireworkStack());
    }

    public boolean hasFireworkAttached() {
        return this.getFireworkStack().isOf(Items.FIREWORK_ROCKET);
    }

    @Nullable
    public ItemStack getFireworkStack() {
        return this.dataTracker.get(FIREWORK_STACK);
    }

    public boolean setFireworkStack(ItemStack stack) {
        if(!stack.isOf(Items.FIREWORK_ROCKET)) return false;
        this.dataTracker.set(FIREWORK_STACK, stack);
        return true;
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
            world.createExplosion(this, this.getX(), this.getY(), this.getZ(), randomExplosionRange(), ExplosionSourceType.MOB);
            this.discard();
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(!super.damage(source, amount)) return false;

        // don't explode if the attacker has silk touch
        ItemStack weaponStack = source.getWeaponStack();
        if(weaponStack != null && ItemUtils.hasEnchantment(weaponStack, Enchantments.SILK_TOUCH, this.getWorld())) {
            return true;
        }

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

    @Override
    public ItemEntity dropItem(ItemConvertible item) {
        if(item.asItem() instanceof EggItem) {
            return super.dropItem(ModContent.TRICK_EGG_ITEM);
        }
        return super.dropItem(item);
    }

    public static float randomExplosionRange() {
        return MIN_EXPLODE_RANGE + (MAX_EXPLODE_RANGE - MIN_EXPLODE_RANGE) * (float)Math.random();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if(nbt.contains("Firework", 10)) {
            ItemStack stack = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("Firework")).orElse(getDefaultFireworkStack());
            this.dataTracker.set(FIREWORK_STACK, stack);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if(this.hasFireworkAttached()) {
            nbt.put("Firework", this.getFireworkStack().encode(getRegistryManager()));
        }
    }

    private static ItemStack getDefaultFireworkStack() {
        return new ItemStack(Items.AIR);
    }

    static {
        FIREWORK_STACK = DataTracker.registerData(EntityExplodingChicken.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }

}
