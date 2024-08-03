package com.jackd.exchickens.client.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {

    @Accessor("models")
    ItemModels exchickens$getModels();

}
