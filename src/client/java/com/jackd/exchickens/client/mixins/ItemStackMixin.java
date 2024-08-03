package com.jackd.exchickens.client.mixins;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.items.ItemChickenLauncher;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    
    @Inject(method="getTooltip", at=@At("RETURN"), cancellable=true)
    private void getTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> info) {
        if(player == null) return;
        if(!(this.getItem() instanceof ItemChickenLauncher)) return;

        Text ammoText = null;
        
        if(player.isCreative())
            ammoText = Text.translatable("exchickens.launcher.ammo.infinite").formatted(Formatting.YELLOW, Formatting.ITALIC);
        else if(ItemChickenLauncher.canFire(player))
            ammoText = Text.translatable("exchickens.launcher.ammo.loaded").formatted(Formatting.GREEN);
        else
            ammoText = Text.translatable("exchickens.launcher.ammo.empty").formatted(Formatting.RED);

        if(ammoText == null) return;
        List<Text> tooltip = info.getReturnValue();
        tooltip.add(Text.translatable("exchickens.launcher.ammo", ammoText).formatted(Formatting.GRAY));
        info.setReturnValue(tooltip);
    }

    @Inject(method="appendAttributeModifiersTooltip", at=@At("TAIL"))
    private void appendAttributeModifiersTooltip(Consumer<Text> textConsumer, @Nullable PlayerEntity player, CallbackInfo info) {
        if(this.getItem() instanceof ArmorItem armorItem && isChickenArmor(armorItem.getMaterial())) {
            textConsumer.accept(Text.translatable("attribute.modifier.chicken_blast").formatted(EntityAttribute.Category.POSITIVE.getFormatting(true)));
        }
    }

    private boolean isChickenArmor(RegistryEntry<ArmorMaterial> material) {
        return material == ModContent.CHICKEN_ARMOR || material == ModContent.COOKED_CHICKEN_ARMOR;
    }

    @Shadow
    public abstract Item getItem();

}
