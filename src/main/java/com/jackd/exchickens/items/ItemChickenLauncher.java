package com.jackd.exchickens.items;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.entity.EntityLaunchedEgg;

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

    public ItemChickenLauncher(Item.Settings settings) {
        super(settings);
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
                egg.setVelocity(user, user.getPitch(), user.getYaw(), 0f, 3.5f, 1f);
                world.spawnEntity(egg);
            }
            spawnSmokeParticles(user, world);
            consumeAmmo(user, ModContent.TRICK_EGG_ITEM, 1);
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
        Vec3d eyePos = player.getEyePos();
        float f = -MathHelper.sin(player.getYaw() * 0.017453292F) * MathHelper.cos(player.getPitch() * 0.017453292F);
        float g = -MathHelper.sin(player.getPitch() * 0.017453292F);
        float h = MathHelper.cos(player.getYaw() * 0.017453292F) * MathHelper.cos(player.getPitch() * 0.017453292F);
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
        return egg;
    }

    public static boolean canFire(PlayerEntity player) {
        return player.isCreative() || player.getInventory().contains(stack -> stack.isOf(ModContent.TRICK_EGG_ITEM));
    }

}
