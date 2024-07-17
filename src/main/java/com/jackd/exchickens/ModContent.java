package com.jackd.exchickens;

import com.google.common.collect.Lists;
import com.jackd.exchickens.block.BlockChickenTrap;
import com.jackd.exchickens.entity.EntityExplodingChicken;
import com.jackd.exchickens.entity.EntityLaunchedEgg;
import com.jackd.exchickens.items.ItemArmorCookable;
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

    // ============ BLOCKS ============ //
    public static final Block CHICKEN_TRAP_BLOCK = new BlockChickenTrap(AbstractBlock.Settings.create().nonOpaque().noCollision().sounds(BlockSoundGroup.STONE).breakInstantly().pistonBehavior(PistonBehavior.DESTROY));

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

    // ============ COMPONENT TYPES ============ //
    public static final ComponentType<Float> ARMOR_COOK_TIME_COMPONENT = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        id("armor_cook_time"),
        ComponentType.<Float>builder().codec(Codec.FLOAT).build()
    );

    // ============ ITEMS ============ //
    public static final Item TRICK_EGG_ITEM = new ItemTrickEgg(Identifier.ofVanilla("egg"));
    public static final Item TRICK_RAW_CHICKEN_ITEM = new ItemTrickFood(FoodComponents.CHICKEN, Identifier.ofVanilla("chicken"));
    public static final Item TRICK_COOKED_CHICKEN_ITEM = new ItemTrickFood(FoodComponents.COOKED_CHICKEN, Identifier.ofVanilla("cooked_chicken"));
    public static final Item CHICKEN_LAUNCHER_ITEM = new ItemChickenLauncher(Variant.REGULAR, new Item.Settings().maxCount(1).maxDamage(150));
    public static final Item INCUBATING_CHICKEN_LAUNCHER_ITEM = new ItemChickenLauncher(Variant.INCUBATING, new Item.Settings().maxCount(1).maxDamage(150).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));
    public static final Item INCENDIARY_CHICKEN_LAUNCHER_ITEM = new ItemChickenLauncher(Variant.REGULAR, true, new Item.Settings().maxCount(1).maxDamage(150));
    public static final Item INCENDIARY_INCUBATING_CHICKEN_LAUNCHER_ITEM = new ItemChickenLauncher(Variant.INCUBATING, true, new Item.Settings().maxCount(1).maxDamage(150).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

    public static final Item CHICKEN_HELMET = new ItemArmorCookable(CHICKEN_ARMOR, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(4)));
    public static final Item CHICKEN_CHESTPLATE = new ItemArmorCookable(CHICKEN_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(4)));
    public static final Item CHICKEN_LEGGINGS = new ItemArmorCookable(CHICKEN_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(4)));
    public static final Item CHICKEN_BOOTS = new ItemArmorCookable(CHICKEN_ARMOR, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(4)));

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
            entries.addAll(Lists.newArrayList(
                CHICKEN_SPAWN_EGG,
                TRICK_EGG_ITEM,
                TRICK_RAW_CHICKEN_ITEM,
                TRICK_COOKED_CHICKEN_ITEM,
                CHICKEN_LAUNCHER_ITEM,
                INCUBATING_CHICKEN_LAUNCHER_ITEM,
                INCENDIARY_CHICKEN_LAUNCHER_ITEM,
                INCENDIARY_INCUBATING_CHICKEN_LAUNCHER_ITEM,
                CHICKEN_HELMET,
                CHICKEN_CHESTPLATE,
                CHICKEN_LEGGINGS,
                CHICKEN_BOOTS
            ).stream().map(i -> new ItemStack(i)).toList());
        })
        .build();

    // ============ TAGS ============ //
    public static final TagKey<Biome> TAG_CHICKEN_BIOMES = TagKey.of(RegistryKeys.BIOME, id("chicken_spawn_biomes"));


    protected static void registerContent() {
        // register all blocks
        registerBlock(id("chicken_trap"), CHICKEN_TRAP_BLOCK, ItemGroups.FUNCTIONAL);

        // register all items
        Registry.register(Registries.ITEM, id("egg"), TRICK_EGG_ITEM);
        Registry.register(Registries.ITEM, id("chicken"), TRICK_RAW_CHICKEN_ITEM);
        Registry.register(Registries.ITEM, id("cooked_chicken"), TRICK_COOKED_CHICKEN_ITEM);
        Registry.register(Registries.ITEM, id("exploding_chicken_spawn_egg"), CHICKEN_SPAWN_EGG);
        Registry.register(Registries.ITEM, id("launcher"), CHICKEN_LAUNCHER_ITEM);
        Registry.register(Registries.ITEM, id("incubating_launcher"), INCUBATING_CHICKEN_LAUNCHER_ITEM);
        Registry.register(Registries.ITEM, id("incendiary_launcher"), INCENDIARY_CHICKEN_LAUNCHER_ITEM);
        Registry.register(Registries.ITEM, id("incendiary_incubating_launcher"), INCENDIARY_INCUBATING_CHICKEN_LAUNCHER_ITEM);
        Registry.register(Registries.ITEM, id("chicken_helmet"), CHICKEN_HELMET);
        Registry.register(Registries.ITEM, id("chicken_chestplate"), CHICKEN_CHESTPLATE);
        Registry.register(Registries.ITEM, id("chicken_leggings"), CHICKEN_LEGGINGS);
        Registry.register(Registries.ITEM, id("chicken_boots"), CHICKEN_BOOTS);

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
            content.add(CHICKEN_HELMET);
            content.add(CHICKEN_CHESTPLATE);
            content.add(CHICKEN_LEGGINGS);
            content.add(CHICKEN_BOOTS);
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

        // register dispenser behaviours
        DispenserBlock.registerProjectileBehavior(TRICK_EGG_ITEM);
    }

    private static void registerBlock(Identifier id, Block block, RegistryKey<ItemGroup> creativeTab) {
        // register the block itself
        Registry.register(Registries.BLOCK, id, block);
        
        // register the block item
        Item blockItem = new BlockItem(block, new Item.Settings());
        Registry.register(Registries.ITEM, id, blockItem);

        // add to creative tabs
        RegistryKey<ItemGroup> modTabKey = RegistryKey.of(Registries.ITEM_GROUP.getKey(), id("tab"));
        ItemGroupEvents.modifyEntriesEvent(modTabKey).register(content -> {
            content.add(blockItem);
        });

        // add to vanilla tab (optional)
        if(creativeTab != null) {
            ItemGroupEvents.modifyEntriesEvent(creativeTab).register(content -> {
                content.add(blockItem);
            });
        }
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

}
