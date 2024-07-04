package com.jackd.exchickens.items;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.entity.EntityLaunchedEgg;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemChickenLauncher extends Item implements ProjectileItem {

    public enum Variant {
        REGULAR,
        INCUBATING
    }

    private final Variant variant;

    public ItemChickenLauncher(Variant variant, Item.Settings settings) {
        super(settings);
        this.variant = variant;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(canFire(user)) {
            world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.PLAYERS);
            if(!world.isClient) {
                ItemStack fired = new ItemStack(ModContent.TRICK_EGG_ITEM);
                EntityLaunchedEgg egg = new EntityLaunchedEgg(world, user);
                egg.setItem(fired);
                egg.setVariant(this.variant);
                egg.setVelocity(user, user.getPitch(), user.getYaw(), 0f, 3.5f, 1f);
                world.spawnEntity(egg);
            }
            spawnSmokeParticles(user, world);
            consumeAmmo(user, ModContent.TRICK_EGG_ITEM, 1);
            stack.damage(this.getDamageAmount(), user, LivingEntity.getSlotForHand(hand));
            return TypedActionResult.success(stack, false);
        } else {
            world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS);
            return TypedActionResult.fail(stack);
        }
    }

    private void consumeAmmo(PlayerEntity player, Item ammoItem, int quantity) {
        if(player.isCreative()) return;
        for(int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if(stack.isOf(ammoItem)) {
                stack.decrementUnlessCreative(quantity, player);
                return;
            }
        }
    }

    private void spawnSmokeParticles(PlayerEntity player, World world) {
        final float d2r = MathHelper.RADIANS_PER_DEGREE;
        Vec3d eyePos = player.getEyePos();
        float f = -MathHelper.sin(player.getYaw() * d2r) * MathHelper.cos(player.getPitch() * d2r);
        float g = -MathHelper.sin(player.getPitch() * d2r);
        float h = MathHelper.cos(player.getYaw() * d2r) * MathHelper.cos(player.getPitch() * d2r);
        for(int i = 0; i < 16; i++) {
            float s = (float)(i + 1) / 8f;
            world.addParticle(ParticleTypes.SMOKE, eyePos.x, eyePos.y, eyePos.z, f * s, g * s, h * s);
        }
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        EntityLaunchedEgg egg = new EntityLaunchedEgg(world, pos.getX(), pos.getY(), pos.getZ());
        ItemStack fired = new ItemStack(ModContent.TRICK_EGG_ITEM);
        egg.setItem(fired);
        egg.setVariant(this.variant);
        return egg;
    }

    public Variant getVariant() {
        return this.variant;
    }

    public int getDamageAmount() {
        return this.variant == Variant.INCUBATING ? 5 : 2;
    }

    public static boolean canFire(PlayerEntity player) {
        return player.isCreative() || player.getInventory().contains(stack -> stack.isOf(ModContent.TRICK_EGG_ITEM));
    }

}
