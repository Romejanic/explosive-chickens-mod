package com.jackd.exchickens.items;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.entity.EntityLaunchedEgg;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class ItemChickenLauncher extends Item implements ProjectileItem {

    public ItemChickenLauncher(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.PLAYERS);

        if(!world.isClient) {
            ItemStack fired = new ItemStack(ModContent.TRICK_EGG_ITEM);
            EntityLaunchedEgg egg = new EntityLaunchedEgg(world, user);
            egg.setItem(fired);
            egg.setVelocity(user, user.getPitch(), user.getYaw(), 0f, 3.5f, 1f);
            world.spawnEntity(egg);
        }

        return TypedActionResult.success(stack, false);
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        EntityLaunchedEgg egg = new EntityLaunchedEgg(world, pos.getX(), pos.getY(), pos.getZ());
        ItemStack fired = new ItemStack(ModContent.TRICK_EGG_ITEM);
        egg.setItem(fired);
        return egg;
    }

}
