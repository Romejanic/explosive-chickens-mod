package com.jackd.exchickens.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.entity.ai.GoalAttackTamed;
import com.jackd.exchickens.util.ExplosionSizes;
import com.jackd.exchickens.util.ItemUtils;
import com.jackd.exchickens.util.MathUtils;
import com.jackd.exchickens.util.NbtUtils;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.Goal.Control;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EggItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class EntityExplodingChicken extends ChickenEntity implements Tameable {

    private static final TrackedData<ItemStack> FIREWORK_STACK;
    private static final TrackedData<Integer> FIREWORK_FUSE;
    private static final TrackedData<Vector3f> LAUNCH_DIRECTION;
    private static final TrackedData<Optional<UUID>> OWNER_UUID;
    private static final TrackedData<Optional<BlockPos>> TRAPPED_POS;
    private static final byte STATUS_FIREWORK_EXPLODE = 17;
    private static final byte STATUS_TAME_SUCCESS = 18;
    private static final byte STATUS_TAME_FAILURE = 19;

    private boolean willExplode = false;
    private boolean didExplode = false;

    public EntityExplodingChicken(EntityType<? extends ChickenEntity> entityType, World world) {
        super(entityType, world);
        this.targetSelector.setControlEnabled(Control.TARGET, true);
    }

    @Override
    protected void initDataTracker(Builder builder) {
        super.initDataTracker(builder);
        builder.add(FIREWORK_STACK, getDefaultFireworkStack());
        builder.add(FIREWORK_FUSE, -1);
        builder.add(LAUNCH_DIRECTION, MathUtils.ZERO);
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(TRAPPED_POS, Optional.empty());
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new MeleeAttackGoal(this, this.getMovementSpeed() * 2f, false));
        this.targetSelector.add(0, new GoalAttackTamed(this));
    }

    public boolean hasFireworkAttached() {
        return this.getFireworkStack().isOf(Items.FIREWORK_ROCKET) && !this.isBaby();
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
        // check if chicken is trapped
        if(this.isTrapped()) {
            this.setAiDisabled(true);
            this.setOnGround(true);
            this.flapSpeed = 0f;
            BlockPos pos = this.getTrappedBlockPos().get();
            BlockState trapState = this.getWorld().getBlockState(pos);
            if(trapState.getBlock() == ModContent.CHICKEN_TRAP_BLOCK) {
                // align chicken with block
                Direction direction = trapState.get(HorizontalFacingBlock.FACING);
                this.setPosition(pos.getX() + 0.5d, pos.getY() - 0.25d, pos.getZ() + 0.5d);
                this.setBodyYaw(direction.asRotation());
                this.setHeadYaw(direction.asRotation());
            } else {
                // trap was broken/removed, untrap the chicken
                this.setPosition(this.getX(), this.getY() + 0.4d, this.getZ());
                this.setTrappedBlockPos(null);
                this.setAiDisabled(false);
            }
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
        // ignite fireworks if lit on fire
        if(this.isOnFire() && this.hasFireworkAttached() && !this.isFireworkIgnited()) {
            this.igniteFirework();
        }
    }

    public void explode() {
        if(this.didExplode) return;
        this.didExplode = true;

        World world = this.getWorld();
        if(!world.isClient()) {
            world.createExplosion(this, this.getX(), this.getY(), this.getZ(), ExplosionSizes.chickenExplosion(), this.isOnFire(), ExplosionSourceType.MOB);
            this.getWorld().sendEntityStatus(this, STATUS_FIREWORK_EXPLODE);
            if(this.isFireworkIgnited()) {
                this.dropItem(ModContent.TRICK_COOKED_CHICKEN_ITEM);
            }
            this.discard();
        }
    }

    private void explodeFireworks() {
        if(!this.hasFireworkAttached()) return;
        ItemStack itemStack = this.getFireworkStack();
        FireworksComponent fireworksComponent = itemStack.get(DataComponentTypes.FIREWORKS);
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
        // if tamed don't allow other players to interact
        if(this.isTamed() && !this.isOwner(player)) return super.interactMob(player, hand);

        // test which item player is holding (if any)
        World world = this.getWorld();
        ItemStack heldItem = player.getStackInHand(hand);
        if(heldItem != null) {
            // is the player using a firework rocket?
            if(heldItem.isOf(Items.FIREWORK_ROCKET) && !this.isBaby()) {
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
            }
            // is the player using a flint and steel?
            else if(heldItem.isOf(Items.FLINT_AND_STEEL) && this.hasFireworkAttached()) {
                this.igniteFirework();
                heldItem.damage(1, player, getSlotForHand(hand));
                return ActionResult.SUCCESS;
            }
            // is the player using gunpowder?
            // else if(heldItem.isOf(Items.GUNPOWDER) && !this.isTamed() && !this.isTrapped()) {
            //     if(!world.isClient) {
            //         this.tryTameChicken(player, world);
            //     }
            //     heldItem.decrementUnlessCreative(1, player);
            //     return ActionResult.SUCCESS;
            // }
        }
        // otherwise is there a rocket attached?
        else if(this.hasFireworkAttached()) {
            // give rocket to player and remove from chicken
            player.giveItemStack(this.getFireworkStack());
            this.removeFirework();
            return ActionResult.SUCCESS_NO_ITEM_USED;
        }
        // otherwise use vanilla behaviour
        return super.interactMob(player, hand);
    }

    @Override
    public void onAttacking(Entity target) {
        super.onAttacking(target);
        if(target instanceof LivingEntity ent && this.isTamed() && !this.isOwner(ent)) {
            this.explode();
        }
    }

    private void tryTameChicken(PlayerEntity player, World world) {
        if(MathUtils.randomChance(8)) {
            // tame successful
            this.setOwnerUUID(player.getUuid());
            world.sendEntityStatus(this, STATUS_TAME_SUCCESS);
        } else {
            // tame failed
            world.sendEntityStatus(this, STATUS_TAME_FAILURE);
        }
    }

    @Override
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.of(uuid));
    }

    public boolean isTamed() {
        return this.getOwnerUuid() != null;
    }

    public boolean isOwner(LivingEntity other) {
        return this.isTamed() && other.getUuid().equals(this.getOwnerUuid());
    }

    public Optional<BlockPos> getTrappedBlockPos() {
        return this.dataTracker.get(TRAPPED_POS);
    }

    public boolean isTrapped() {
        return this.getTrappedBlockPos().isPresent();
    }

    public boolean isTrappedAt(BlockPos pos) {
        if(!this.isTrapped()) return false;
        return this.getTrappedBlockPos().get().equals(pos);
    }

    public void setTrappedBlockPos(BlockPos pos) {
        if(pos != null) {
            this.dataTracker.set(TRAPPED_POS, Optional.of(pos));
        } else {
            this.dataTracker.set(TRAPPED_POS, Optional.empty());
        }
    }

    @Override
    public boolean collidesWith(Entity other) {
        if(other instanceof PlayerEntity) {
            this.willExplode = !this.isTamed() && !this.isTrapped();
        }
        return super.collidesWith(other);
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        return this.isTamed() && this.isOwner(target) ? false : super.canTarget(target);
    }

    @Override
    public ItemEntity dropItem(ItemConvertible item) {
        if(item.asItem() instanceof EggItem) {
            return super.dropItem(ModContent.TRICK_EGG_ITEM);
        }
        return super.dropItem(item);
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
        // try to get owner
        UUID uuid = NbtUtils.readUUID(nbt, this, "Owner");
        if(uuid != null) {
            this.setOwnerUUID(uuid);
        }
        // set trapped block pos (if present)
        this.dataTracker.set(TRAPPED_POS, NbtUtils.readBlockPos(nbt, "TrappedPos"));
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
        if(this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        if(this.isTrapped()) {
            NbtUtils.writeBlockPos(nbt, "TrappedPos", this.getTrappedBlockPos().get());
        }
    }

    @Override
    public void handleStatus(byte status) {
        World world = this.getWorld();
        switch(status) {
            case STATUS_FIREWORK_EXPLODE:
                if(world.isClient) {
                    this.explodeFireworks();
                }
                break;
            case STATUS_TAME_SUCCESS:
            case STATUS_TAME_FAILURE:
                this.spawnTameParticles(status == STATUS_TAME_SUCCESS, world);
                break;
            default:
                break;
        }
        super.handleStatus(status);
    }

    private void spawnTameParticles(boolean success, World world) {
        ParticleEffect particleEffect = success ? ParticleTypes.HEART : ParticleTypes.SMOKE;
        for(int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            world.addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public ChickenEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return ModContent.EXPLODING_CHICKEN_ENTITY.create(serverWorld);
    }

    @Override
    public EntityView method_48926() {
        return this.getEntityWorld();
    }

    private static ItemStack getDefaultFireworkStack() {
        return new ItemStack(Items.AIR);
    }

    public static DefaultAttributeContainer.Builder createExplodingChickenAttributes() {
        return ChickenEntity.createChickenAttributes()
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0F);
    }

    static {
        FIREWORK_STACK = DataTracker.registerData(EntityExplodingChicken.class, TrackedDataHandlerRegistry.ITEM_STACK);
        FIREWORK_FUSE = DataTracker.registerData(EntityExplodingChicken.class, TrackedDataHandlerRegistry.INTEGER);
        LAUNCH_DIRECTION = DataTracker.registerData(EntityExplodingChicken.class, TrackedDataHandlerRegistry.VECTOR3F);
        OWNER_UUID = DataTracker.registerData(EntityExplodingChicken.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        TRAPPED_POS = DataTracker.registerData(EntityExplodingChicken.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
    }

}
