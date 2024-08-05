package com.jackd.exchickens;

import com.google.common.collect.Maps;
import com.jackd.exchickens.block.BlockChickenTrap;
import com.jackd.exchickens.component.ArmorCookableComponent;
import com.jackd.exchickens.entity.EntityExplodingChicken;
import com.jackd.exchickens.entity.EntityLaunchedEgg;
import com.jackd.exchickens.items.ItemChickenLauncher;
import com.jackd.exchickens.items.ItemTrickEgg;
import com.jackd.exchickens.items.ItemTrickFood;
import com.jackd.exchickens.items.ItemChickenLauncher.Variant;
import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

public class ModContent {

    private static final Map<Item, Identifier> itemToIdMap = Maps.newHashMap();

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

    // ============ BLOCKS ============ //
    public static final Block CHICKEN_TRAP_BLOCK = registerBlock(
        "chicken_trap",
        new BlockChickenTrap(AbstractBlock.Settings.create().nonOpaque().noCollision().sounds(BlockSoundGroup.STONE).breakInstantly().pistonBehavior(PistonBehavior.DESTROY)),
        ItemGroups.FUNCTIONAL
    );

    // ============ ARMOR MATERIALS ============ //
    public static final RegistryEntry<ArmorMaterial> CHICKEN_ARMOR = registerArmorMaterial("chicken",
        Map.of(
            ArmorItem.Type.HELMET, 2,
            ArmorItem.Type.CHESTPLATE, 3,
            ArmorItem.Type.LEGGINGS, 2,
            ArmorItem.Type.BOOTS, 1
        ), 
        5,
        RegistryEntry.of(SoundEvents.ENTITY_SLIME_SQUISH),
        () -> Ingredient.ofItems(ModContent.TRICK_RAW_CHICKEN_ITEM),
        1.0F,
        0.0F
    );
    public static final RegistryEntry<ArmorMaterial> COOKED_CHICKEN_ARMOR = registerArmorMaterial("cooked_chicken",
        Map.of(
            ArmorItem.Type.HELMET, 2,
            ArmorItem.Type.CHESTPLATE, 3,
            ArmorItem.Type.LEGGINGS, 2,
            ArmorItem.Type.BOOTS, 1
        ), 
        5,
        RegistryEntry.of(SoundEvents.ENTITY_SLIME_SQUISH),
        () -> Ingredient.ofItems(ModContent.TRICK_COOKED_CHICKEN_ITEM),
        1.2F,
        0.1F
    );

    // ============ COMPONENT TYPES ============ //
    public static final ComponentType<Float> ARMOR_COOK_TIME_COMPONENT = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        id("armor_cook_time"),
        ComponentType.<Float>builder().codec(Codec.FLOAT).build()
    );
    public static final ComponentType<ArmorCookableComponent> ARMOR_COOKABLE_COMPONENT = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        id("armor_cookable"),
        ComponentType.<ArmorCookableComponent>builder().codec(ArmorCookableComponent.CODEC).build()
    );

    // ============ ITEMS ============ //
    public static final Item CHICKEN_SPAWN_EGG = registerItem("exploding_chicken_spawn_egg", new SpawnEggItem(EXPLODING_CHICKEN_ENTITY, 0xcccccc, 0xff3300, new Item.Settings()), ItemGroups.SPAWN_EGGS);

    public static final Item TRICK_EGG_ITEM = registerItem("egg", new ItemTrickEgg(Identifier.ofVanilla("egg")), ItemGroups.FOOD_AND_DRINK);
    public static final Item TRICK_RAW_CHICKEN_ITEM = registerItem("chicken", new ItemTrickFood(FoodComponents.CHICKEN, Identifier.ofVanilla("chicken")), ItemGroups.FOOD_AND_DRINK);
    public static final Item TRICK_COOKED_CHICKEN_ITEM = registerItem("cooked_chicken", new ItemTrickFood(FoodComponents.COOKED_CHICKEN, Identifier.ofVanilla("cooked_chicken")), ItemGroups.FOOD_AND_DRINK);
    public static final Item CHICKEN_LAUNCHER_ITEM = registerItem("launcher", new ItemChickenLauncher(Variant.REGULAR, new Item.Settings().maxCount(1).maxDamage(150)), ItemGroups.COMBAT);
    public static final Item INCUBATING_CHICKEN_LAUNCHER_ITEM = registerItem("incubating_launcher", new ItemChickenLauncher(Variant.INCUBATING, new Item.Settings().maxCount(1).maxDamage(150).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)), ItemGroups.COMBAT);
    public static final Item INCENDIARY_CHICKEN_LAUNCHER_ITEM = registerItem("incendiary_launcher", new ItemChickenLauncher(Variant.REGULAR, true, new Item.Settings().maxCount(1).maxDamage(150)), ItemGroups.COMBAT);
    public static final Item INCENDIARY_INCUBATING_CHICKEN_LAUNCHER_ITEM = registerItem("incendiary_incubating_launcher", new ItemChickenLauncher(Variant.INCUBATING, true, new Item.Settings().maxCount(1).maxDamage(150).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)), ItemGroups.COMBAT);

    public static final Item COOKED_CHICKEN_HELMET = registerItem("cooked_chicken_helmet", new ArmorItem(COOKED_CHICKEN_ARMOR, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(4))), ItemGroups.COMBAT);
    public static final Item COOKED_CHICKEN_CHESTPLATE = registerItem("cooked_chicken_chestplate", new ArmorItem(COOKED_CHICKEN_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(4))), ItemGroups.COMBAT);
    public static final Item COOKED_CHICKEN_LEGGINGS = registerItem("cooked_chicken_leggings", new ArmorItem(COOKED_CHICKEN_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(4))), ItemGroups.COMBAT);
    public static final Item COOKED_CHICKEN_BOOTS = registerItem("cooked_chicken_boots", new ArmorItem(COOKED_CHICKEN_ARMOR, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(4))), ItemGroups.COMBAT);
    public static final Item CHICKEN_HELMET = registerItem("chicken_helmet", new ArmorItem(CHICKEN_ARMOR, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(4)).component(ARMOR_COOKABLE_COMPONENT, ArmorCookableComponent.defaultWithItem(COOKED_CHICKEN_HELMET))), ItemGroups.COMBAT);
    public static final Item CHICKEN_CHESTPLATE = registerItem("chicken_chestplate", new ArmorItem(CHICKEN_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(4)).component(ARMOR_COOKABLE_COMPONENT, ArmorCookableComponent.defaultWithItem(COOKED_CHICKEN_CHESTPLATE))), ItemGroups.COMBAT);
    public static final Item CHICKEN_LEGGINGS = registerItem("chicken_leggings", new ArmorItem(CHICKEN_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(4)).component(ARMOR_COOKABLE_COMPONENT, ArmorCookableComponent.defaultWithItem(COOKED_CHICKEN_LEGGINGS))), ItemGroups.COMBAT);
    public static final Item CHICKEN_BOOTS = registerItem("chicken_boots", new ArmorItem(CHICKEN_ARMOR, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(4)).component(ARMOR_COOKABLE_COMPONENT, ArmorCookableComponent.defaultWithItem(COOKED_CHICKEN_BOOTS))), ItemGroups.COMBAT);

    // ============ DAMAGE ============ //
    public static final RegistryKey<DamageType> DAMAGE_TRICK_EGG = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("trick_egg"));
    public static final RegistryKey<DamageType> DAMAGE_FOOD = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("exploding_food"));
    public static final RegistryKey<DamageType> LAUNCHER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("launcher"));

    // ============ ITEM GROUP ============ //
    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
        .icon(() -> new ItemStack(CHICKEN_SPAWN_EGG))
        .displayName(Text.translatable("itemGroup.exchickens.tab"))
        .build();

    // ============ TAGS ============ //
    public static final TagKey<Biome> TAG_CHICKEN_BIOMES = TagKey.of(RegistryKeys.BIOME, id("chicken_spawn_biomes"));


    protected static void registerMiscContent() {
        // register entity attributes
        FabricDefaultAttributeRegistry.register(EXPLODING_CHICKEN_ENTITY, EntityExplodingChicken.createExplodingChickenAttributes());

        // register item group
        Registry.register(Registries.ITEM_GROUP, id("tab"), ITEM_GROUP);

        // add natural spawns for entities
        BiomeModifications.addSpawn(BiomeSelectors.tag(TAG_CHICKEN_BIOMES), SpawnGroup.CREATURE, EXPLODING_CHICKEN_ENTITY, 5, 1, 32);

        // register dispenser behaviours
        DispenserBlock.registerProjectileBehavior(TRICK_EGG_ITEM);
    }

    private static Block registerBlock(String name, Block block, @Nullable RegistryKey<ItemGroup> creativeTab) {
        // register the block itself
        Identifier id = id(name);
        Registry.register(Registries.BLOCK, id, block);
        
        // register the block item
        Item blockItem = new BlockItem(block, new Item.Settings());
        registerItem(name, blockItem, creativeTab);

        return block;
    }

    private static Item registerItem(String name, Item item, @Nullable RegistryKey<ItemGroup> creativeTab) {
        // register the item itself
        Identifier id = id(name);
        Registry.register(Registries.ITEM, id, item);
        itemToIdMap.put(item, id);

        // add to creative tabs
        RegistryKey<ItemGroup> modTabKey = RegistryKey.of(Registries.ITEM_GROUP.getKey(), id("tab"));
        ItemGroupEvents.modifyEntriesEvent(modTabKey).register(content -> {
            content.add(item);
        });

        // add to vanilla tab (optional)
        if(creativeTab != null) {
            ItemGroupEvents.modifyEntriesEvent(creativeTab).register(content -> {
                content.add(item);
            });
        }

        return item;
    }

    private static RegistryEntry<ArmorMaterial> registerArmorMaterial(String name, Map<ArmorItem.Type, Integer> defense, int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient, float toughness, float knockbackResist) {
        Identifier id = id(name);
        List<ArmorMaterial.Layer> layers = List.of(
            new ArmorMaterial.Layer(id, "", false)
        );
        ArmorMaterial material = Registry.register(
            Registries.ARMOR_MATERIAL,
            id,
            new ArmorMaterial(defense, enchantability, equipSound, repairIngredient, layers, toughness, knockbackResist)
        );
        return RegistryEntry.of(material);
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

    @Nullable
    public static Identifier idOf(Item item) {
        return itemToIdMap.get(item);
    }

}
