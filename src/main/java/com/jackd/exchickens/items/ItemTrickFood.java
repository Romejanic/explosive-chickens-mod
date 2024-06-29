package com.jackd.exchickens.items;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.util.ExplosionSizes;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class ItemTrickFood extends ItemFake {

    public ItemTrickFood(FoodComponent food, Identifier fakingId) {
        super(new Item.Settings().food(food), fakingId);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(!world.isClient()) {
            world.createExplosion(user, user.getX(), user.getY(), user.getZ(), ExplosionSizes.ITEM_EXPLOSION, ExplosionSourceType.MOB);
            user.damage(ModContent.dmgSource(world, ModContent.DAMAGE_FOOD), Float.MAX_VALUE);
        }
        return super.finishUsing(stack, world, user);
    }
    
}
