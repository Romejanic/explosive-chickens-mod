package com.jackd.exchickens.items;

import com.jackd.exchickens.ModContent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class ItemTrickEgg extends ItemFake {

    public static final float EXPLOSION_STRENGTH = 5.0f;

    public ItemTrickEgg() {
        super(new Item.Settings(), Identifier.of("minecraft:egg"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        stack.decrementUnlessCreative(1, user);
        if(!world.isClient()) {
            world.createExplosion(user, user.getX(), user.getY(), user.getZ(), EXPLOSION_STRENGTH, ExplosionSourceType.MOB);
            user.damage(ModContent.dmgSource(world, ModContent.DAMAGE_TRICK_EGG), Float.MAX_VALUE);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

}
