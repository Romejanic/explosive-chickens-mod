package com.jackd.exchickens.client.mixins;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import com.google.common.collect.Maps;
import com.jackd.exchickens.ModContent;
import com.jackd.exchickens.items.ItemChickenLauncher;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    private final Map<Identifier, ModelIdentifier> modelIdMap = Maps.newHashMap();

    @ModifyVariable(method="renderItem", at=@At("HEAD"), argsOnly=true)
    public BakedModel useLauncherModel(BakedModel value, ItemStack stack, ModelTransformationMode mode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay) {
        if(stack.getItem() instanceof ItemChickenLauncher && shouldUseLauncher3D(mode)) {
            ItemModels models = ((ItemRendererAccessor)this).exchickens$getModels();
            ModelIdentifier id = getLauncherModelId(stack);
            return models.getModelManager().getModel(id);
        }
        return value;
    }

    private ModelIdentifier getLauncherModelId(ItemStack stack) {
        Identifier itemId = ModContent.idOf(stack.getItem());
        if(itemId == null) return null;
        if(modelIdMap.containsKey(itemId)) return modelIdMap.get(itemId);
        ModelIdentifier modelId = new ModelIdentifier(itemId.withSuffixedPath("_3d"), "inventory");
        modelIdMap.put(itemId, modelId);
        return modelId;
    }

    private boolean shouldUseLauncher3D(ModelTransformationMode mode) {
        switch(mode) {
            case GUI:
            case GROUND:
                return false;
            default:
                return true;
        }
    }

}
