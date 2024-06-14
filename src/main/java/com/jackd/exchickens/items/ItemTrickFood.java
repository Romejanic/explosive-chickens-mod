package com.jackd.exchickens.items;

import com.jackd.exchickens.ModContent;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class ItemTrickFood extends ItemFake {

    public static final float EXPLOSION_STRENGTH = 5.0f;

    public ItemTrickFood(FoodComponent food, Identifier fakingId) {
        super(new Item.Settings().food(food), fakingId);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if(!world.isClient()) {
            world.createExplosion(user, user.getX(), user.getY(), user.getZ(), EXPLOSION_STRENGTH, ExplosionSourceType.MOB);
            user.damage(ModContent.dmgSource(world, ModContent.DAMAGE_FOOD), Float.MAX_VALUE);
        }
        return super.finishUsing(stack, world, user);
    }
    
}
