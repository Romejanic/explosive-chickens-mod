package com.jackd.exchickens.items;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.entity.EntityLaunchedEgg;
import com.jackd.exchickens.items.ItemChickenLauncher.Variant;
import com.jackd.exchickens.util.ExplosionSizes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class ItemTrickEgg extends ItemFake implements ProjectileItem {

    public ItemTrickEgg(Identifier originalItem) {
        super(new Item.Settings(), originalItem);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        stack.decrementUnlessCreative(1, user);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if(!world.isClient()) {
            world.createExplosion(user, user.getX(), user.getY(), user.getZ(), ExplosionSizes.ITEM_EXPLOSION, ExplosionSourceType.MOB);
            user.damage(ModContent.dmgSource(world, ModContent.DAMAGE_TRICK_EGG), Float.MAX_VALUE);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        EntityLaunchedEgg egg = EntityLaunchedEgg.createEntity(world, pos, Variant.REGULAR);
        ItemStack fired = new ItemStack(ModContent.TRICK_EGG_ITEM);
        egg.setItem(fired);
        return egg;
    }

}
