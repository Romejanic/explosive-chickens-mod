package com.jackd.exchickens.client.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.jackd.exchickens.items.ItemChickenLauncher;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Redirect(method="setModelPose", at = 
        @At(value="INVOKE", target="Lnet/minecraft/client/render/entity/PlayerEntityRenderer;getArmPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;")
    )
    private BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getStackInHand(hand);
        if(heldStack != null && heldStack.getItem() instanceof ItemChickenLauncher) {
            return ArmPose.BOW_AND_ARROW;
        }
        return PlayerEntityRenderer.getArmPose(player, hand);
    }

}
