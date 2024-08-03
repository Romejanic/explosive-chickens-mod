package com.jackd.exchickens.client.mixins;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jackd.exchickens.ModContent;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow
    public abstract void loadItemModel(ModelIdentifier modelId);

    @Inject(method="<init>", at=@At(value="INVOKE", target="Lnet/minecraft/client/render/model/ModelLoader;loadItemModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal=1))
    public void addLauncherModels(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.loadItemModel(modelId("launcher_3d"));
        this.loadItemModel(modelId("incendiary_launcher_3d"));
        this.loadItemModel(modelId("incubating_launcher_3d"));
        this.loadItemModel(modelId("incendiary_incubating_launcher_3d"));
    }

    private ModelIdentifier modelId(String name) {
        return new ModelIdentifier(ModContent.id(name), "inventory");
    }

}
