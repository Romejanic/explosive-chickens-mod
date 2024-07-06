package com.jackd.exchickens;

import com.jackd.exchickens.entity.EntityExplodingChicken;
import com.jackd.exchickens.entity.EntityLaunchedEgg;
import com.jackd.exchickens.items.ItemChickenLauncher;
import com.jackd.exchickens.items.ItemTrickEgg;
import com.jackd.exchickens.items.ItemTrickFood;
import com.jackd.exchickens.items.ItemChickenLauncher.Variant;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ModContent {

    // ============ ENTITIES ============ //
    public static final EntityType<EntityExplodingChicken> EXPLODING_CHICKEN_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        id("exploding_chicken"),
        EntityType.Builder.<EntityExplodingChicken>create(EntityExplodingChicken::new, SpawnGroup.CREATURE)
            .dimensions(0.4F, 0.7F).eyeHeight(0.644F)
            .build()
    );
    
    public static final EntityType<EntityLaunchedEgg> LAUNCHED_EGG_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        id("launched_egg"),
        EntityType.Builder.<EntityLaunchedEgg>create((type, world) -> new EntityLaunchedEgg(type, world, Variant.REGULAR), SpawnGroup.MISC)
            .dimensions(0.25F, 0.25F)
            .maxTrackingRange(4).trackingTickInterval(10)
            .build()
    );

    public static final EntityType<EntityLaunchedEgg> INCUBATING_LAUNCHED_EGG_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        id("incubating_launched_egg"),
        EntityType.Builder.<EntityLaunchedEgg>create((type, world) -> new EntityLaunchedEgg(type, world, Variant.INCUBATING), SpawnGroup.MISC)
            .dimensions(0.4F, 0.7F)
            .maxTrackingRange(4).trackingTickInterval(10)
            .build()
    );

    // ============ ITEMS ============ //
    public static final Item TRICK_EGG_ITEM = new ItemTrickEgg(Identifier.ofVanilla("egg"));
    public static final Item TRICK_RAW_CHICKEN_ITEM = new ItemTrickFood(FoodComponents.CHICKEN, Identifier.ofVanilla("chicken"));
    public static final Item TRICK_COOKED_CHICKEN_ITEM = new ItemTrickFood(FoodComponents.COOKED_CHICKEN, Identifier.ofVanilla("cooked_chicken"));
    public static final Item CHICKEN_LAUNCHER_ITEM = new ItemChickenLauncher(Variant.REGULAR, new Item.Settings().maxCount(1).maxDamage(150));
    public static final Item INCUBATING_CHICKEN_LAUNCHER_ITEM = new ItemChickenLauncher(Variant.INCUBATING, new Item.Settings().maxCount(1).maxDamage(150).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item INCENDIARY_CHICKEN_LAUNCHER_ITEM = new ItemChickenLauncher(Variant.REGULAR, true, new Item.Settings().maxCount(1).maxDamage(150));
    public static final Item INCENDIARY_INCUBATING_CHICKEN_LAUNCHER_ITEM = new ItemChickenLauncher(Variant.INCUBATING, true, new Item.Settings().maxCount(1).maxDamage(150).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    public static final Item CHICKEN_SPAWN_EGG = new SpawnEggItem(EXPLODING_CHICKEN_ENTITY, 0xcccccc, 0xff3300, new Item.Settings());

    // ============ DAMAGE ============ //
    public static final RegistryKey<DamageType> DAMAGE_TRICK_EGG = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("trick_egg"));
    public static final RegistryKey<DamageType> DAMAGE_FOOD = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("exploding_food"));
    public static final RegistryKey<DamageType> LAUNCHER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("launcher"));

    // ============ ITEM GROUP ============ //
    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
        .icon(() -> new ItemStack(CHICKEN_SPAWN_EGG))
        .displayName(Text.translatable("itemGroup.exchickens.tab"))
        .entries((ctx, entries) -> {
            entries.add(CHICKEN_SPAWN_EGG);
            entries.add(TRICK_EGG_ITEM);
            entries.add(TRICK_RAW_CHICKEN_ITEM);
            entries.add(TRICK_COOKED_CHICKEN_ITEM);
            entries.add(CHICKEN_LAUNCHER_ITEM);
            entries.add(INCUBATING_CHICKEN_LAUNCHER_ITEM);
            entries.add(INCENDIARY_CHICKEN_LAUNCHER_ITEM);
            entries.add(INCENDIARY_INCUBATING_CHICKEN_LAUNCHER_ITEM);
        })
        .build();

    // ============ TAGS ============ //
    public static final TagKey<Biome> TAG_CHICKEN_BIOMES = TagKey.of(RegistryKeys.BIOME, id("chicken_spawn_biomes"));


    protected static void registerContent() {
        // register all items
        Registry.register(Registries.ITEM, id("egg"), TRICK_EGG_ITEM);
        Registry.register(Registries.ITEM, id("chicken"), TRICK_RAW_CHICKEN_ITEM);
        Registry.register(Registries.ITEM, id("cooked_chicken"), TRICK_COOKED_CHICKEN_ITEM);
        Registry.register(Registries.ITEM, id("exploding_chicken_spawn_egg"), CHICKEN_SPAWN_EGG);
        Registry.register(Registries.ITEM, id("launcher"), CHICKEN_LAUNCHER_ITEM);
        Registry.register(Registries.ITEM, id("incubating_launcher"), INCUBATING_CHICKEN_LAUNCHER_ITEM);
        Registry.register(Registries.ITEM, id("incendiary_launcher"), INCENDIARY_CHICKEN_LAUNCHER_ITEM);
        Registry.register(Registries.ITEM, id("incendiary_incubating_launcher"), INCENDIARY_INCUBATING_CHICKEN_LAUNCHER_ITEM);

        // add items to vanilla groups
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
            content.add(TRICK_EGG_ITEM);
            content.add(TRICK_RAW_CHICKEN_ITEM);
            content.add(TRICK_COOKED_CHICKEN_ITEM);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(CHICKEN_LAUNCHER_ITEM);
            content.add(INCUBATING_CHICKEN_LAUNCHER_ITEM);
            content.add(INCENDIARY_CHICKEN_LAUNCHER_ITEM);
            content.add(INCENDIARY_INCUBATING_CHICKEN_LAUNCHER_ITEM);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.add(CHICKEN_SPAWN_EGG);
        });

        // register entity attributes
        FabricDefaultAttributeRegistry.register(EXPLODING_CHICKEN_ENTITY, EntityExplodingChicken.createExplodingChickenAttributes());

        // register item group
        Registry.register(Registries.ITEM_GROUP, id("tab"), ITEM_GROUP);

        // add natural spawns for entities
        BiomeModifications.addSpawn(BiomeSelectors.tag(TAG_CHICKEN_BIOMES), SpawnGroup.CREATURE, EXPLODING_CHICKEN_ENTITY, 5, 1, 32);
    }

    public static DamageSource dmgSource(World world, RegistryKey<DamageType> key) {
        return new DamageSource(dmgTypeEntry(world, key));
    }

    public static DamageSource dmgSource(World world, RegistryKey<DamageType> key, Entity attacker) {
        return new DamageSource(dmgTypeEntry(world, key), attacker);
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
