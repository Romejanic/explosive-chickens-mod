package com.jackd.exchickens.entity;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.util.ItemUtils;
import com.jackd.exchickens.util.MathUtils;
import com.jackd.exchickens.util.NbtUtils;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class EntityExplodingChicken extends ChickenEntity {

    private static final TrackedData<ItemStack> FIREWORK_STACK;
    private static final TrackedData<Integer> FIREWORK_FUSE;
    private static final TrackedData<Vector3f> LAUNCH_DIRECTION;
    private static final byte STATUS_FIREWORK_EXPLODE = 17;

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
        builder.add(FIREWORK_FUSE, -1);
        builder.add(LAUNCH_DIRECTION, MathUtils.ZERO);
    }

    public boolean hasFireworkAttached() {
        return this.getFireworkStack().isOf(Items.FIREWORK_ROCKET);
    }

    public boolean isFireworkIgnited() {
        return this.dataTracker.get(FIREWORK_FUSE) > -1;
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

    public void removeFirework() {
        this.dataTracker.set(FIREWORK_STACK, getDefaultFireworkStack());
    }

    public void igniteFirework() {
        if(this.hasFireworkAttached() && !this.getWorld().isClient) {
            this.getWorld().playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
            Vector3f launchDir = MathUtils.randomLaunchDirection(this);
            this.dataTracker.set(FIREWORK_FUSE, 60);
            this.dataTracker.set(LAUNCH_DIRECTION, launchDir);
            this.setVelocity(MathUtils.dirToVelocity(launchDir, 1.5));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.willExplode) {
            this.explode();
        }
        // is the firework on the chicken currently lit?
        if(this.hasFireworkAttached() && this.isFireworkIgnited()) {
            Vector3f dir = this.dataTracker.get(LAUNCH_DIRECTION);
            this.dataTracker.set(LAUNCH_DIRECTION, MathUtils.addJitter(dir, 0.8f, 0.3f));

            // spawn particles behind chicken
            this.getWorld().addParticle(ParticleTypes.FIREWORK, this.getX() - dir.x, this.getY(), this.getZ() - dir.z, 0d, 0d, 0d);

            // add velocity in same direction
            this.setYaw(MathUtils.xzAngle(dir));
            this.addVelocity(MathUtils.dirToVelocity(dir, MathHelper.nextDouble(MathUtils.RANDOM, 0.01, 0.15)));

            // update firework fuse time
            int fuse = this.dataTracker.get(FIREWORK_FUSE) - 1;
            this.dataTracker.set(FIREWORK_FUSE, fuse);
            if(fuse == 0) {
                this.explode();
            }
        }
    }

    public void explode() {
        if(this.didExplode) return;
        this.didExplode = true;

        World world = this.getWorld();
        if(!world.isClient()) {
            world.createExplosion(this, this.getX(), this.getY(), this.getZ(), randomExplosionRange(), ExplosionSourceType.MOB);
            this.getWorld().sendEntityStatus(this, STATUS_FIREWORK_EXPLODE);
            this.discard();
        }
    }

    private void explodeFireworks() {
        if(!this.hasFireworkAttached()) return;
        ItemStack itemStack = this.getFireworkStack();
        FireworksComponent fireworksComponent = (FireworksComponent)itemStack.get(DataComponentTypes.FIREWORKS);
        if(fireworksComponent != null) {
            List<FireworkExplosionComponent> explosions = fireworksComponent.explosions();
            Vec3d velocity = this.getVelocity();
            this.getWorld().addFireworkParticle(this.getX(), this.getY(), this.getZ(), velocity.x, velocity.y, velocity.z, explosions);
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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        // is the player holding a firework rocket?
        ItemStack heldItem = player.getStackInHand(hand);
        if(heldItem != null && heldItem.isOf(Items.FIREWORK_ROCKET)) {
            // check if chicken already has a rocket attached
            ItemStack oldStack = null;
            if(this.hasFireworkAttached()) {
                oldStack = this.getFireworkStack();
            }
            // set new firework rocket, decrease stack size
            this.setFireworkStack(heldItem.copyWithCount(1));
            heldItem.decrementUnlessCreative(1, player);
            // give old rocket back to player
            if(oldStack != null) {
                player.giveItemStack(oldStack);
            }
            return ActionResult.SUCCESS;
        // otherwise is there a rocket attached?
        } else if(this.hasFireworkAttached()) {
            // if clicking with a flint and steel, ignite rocket
            if(heldItem.isOf(Items.FLINT_AND_STEEL)) {
                this.igniteFirework();
                heldItem.damage(1, player, getSlotForHand(hand));
                return ActionResult.SUCCESS;
            }
            
            // give rocket to player and remove from chicken
            player.giveItemStack(this.getFireworkStack());
            this.removeFirework();
            return ActionResult.SUCCESS_NO_ITEM_USED;
        }
        // otherwise use vanilla behaviour
        return super.interactMob(player, hand);
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
            if(nbt.contains("FireworkFuseTime")) {
                this.dataTracker.set(FIREWORK_FUSE, nbt.getInt("FireworkFuseTime"));
                this.dataTracker.set(LAUNCH_DIRECTION, NbtUtils.readVector3f(nbt, "LaunchDirection"));
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if(this.hasFireworkAttached()) {
            nbt.put("Firework", this.getFireworkStack().encode(getRegistryManager()));
            if(this.isFireworkIgnited()) {
                nbt.putInt("FireworkFuseTime", this.dataTracker.get(FIREWORK_FUSE));
                NbtUtils.writeVector3f(nbt, "LaunchDirection", this.dataTracker.get(LAUNCH_DIRECTION));
            }
        }
    }

    @Override
    public void handleStatus(byte status) {
        if(status == STATUS_FIREWORK_EXPLODE && this.getWorld().isClient) {
            this.explodeFireworks();
        }
        super.handleStatus(status);
    }

    private static ItemStack getDefaultFireworkStack() {
        return new ItemStack(Items.AIR);
    }

    static {
        FIREWORK_STACK = DataTracker.registerData(EntityExplodingChicken.class, TrackedDataHandlerRegistry.ITEM_STACK);
        FIREWORK_FUSE = DataTracker.registerData(EntityExplodingChicken.class, TrackedDataHandlerRegistry.INTEGER);
        LAUNCH_DIRECTION = DataTracker.registerData(EntityExplodingChicken.class, TrackedDataHandlerRegistry.VECTOR3F);
    }

}
