package com.jackd.exchickens;

import com.jackd.exchickens.entity.EntityExplodingChicken;
import com.jackd.exchickens.items.ItemTrickEgg;
import com.jackd.exchickens.items.ItemTrickFood;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModContent {
    
    // ============ ITEMS ============ //
    public static final Item TRICK_EGG_ITEM = new ItemTrickEgg();
    public static final Item TRICK_RAW_CHICKEN_ITEM = new ItemTrickFood(FoodComponents.CHICKEN, Identifier.of("minecraft:chicken"));
    public static final Item TRICK_COOKED_CHICKEN_ITEM = new ItemTrickFood(FoodComponents.COOKED_CHICKEN, Identifier.of("minecraft:cooked_chicken"));

    // ============ ENTITIES ============ //
    public static final EntityType<EntityExplodingChicken> EXPLODING_CHICKEN_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        id("exploding_chicken"),
        EntityType.Builder.create(EntityExplodingChicken::new, SpawnGroup.CREATURE).build());

    // ============ DAMAGE ============ //
    public static final RegistryKey<DamageType> DAMAGE_TRICK_EGG = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("trick_egg"));
    public static final RegistryKey<DamageType> DAMAGE_FOOD = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("exploding_food"));

    protected static void registerContent() {
        // register all items
        Registry.register(Registries.ITEM, id("egg"), TRICK_EGG_ITEM);
        Registry.register(Registries.ITEM, id("chicken"), TRICK_RAW_CHICKEN_ITEM);
        Registry.register(Registries.ITEM, id("cooked_chicken"), TRICK_COOKED_CHICKEN_ITEM);

        // add items to groups
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
            content.add(TRICK_EGG_ITEM);
            content.add(TRICK_RAW_CHICKEN_ITEM);
            content.add(TRICK_COOKED_CHICKEN_ITEM);
        });

        // register entity attributes
        FabricDefaultAttributeRegistry.register(EXPLODING_CHICKEN_ENTITY, EntityExplodingChicken.createChickenAttributes());
    }

    public static DamageSource dmgSource(World world, RegistryKey<DamageType> key) {
        return new DamageSource(dmgTypeEntry(world, key));
    }

    public static DamageType dmgType(World world, RegistryKey<DamageType> key) {
        return dmgTypeEntry(world, key).value();
    }

    private static RegistryEntry<DamageType> dmgTypeEntry(World world, RegistryKey<DamageType> key) {
        return world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key);
    }

    public static Identifier id(String path) {
        return Identifier.of(ExplosiveChickens.MODID, path);
    }

}
