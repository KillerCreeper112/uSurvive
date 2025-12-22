package killercreepr.usurvive.core.enchant;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.WritableRegistry;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import io.papermc.paper.tag.PreFlattenTagRegistrar;
import io.papermc.paper.tag.TagEntry;
import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.core.Crux;
import killercreepr.cruxenchants.api.enchant.ApplicableItemGroup;
import killercreepr.cruxenchants.api.enchant.ApplicableItemType;
import killercreepr.cruxenchants.api.enchant.CruxEnchant;
import killercreepr.cruxenchants.core.util.EnchantRegistererUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class USurviveEnchantRegisterer extends EnchantRegistererUtil {
    public USurviveEnchantRegisterer(WritableRegistry<Enchantment, EnchantmentRegistryEntry.Builder> registry,
                                     @Nullable KeyedRegistry<CruxEnchant> wrapperRegistry,
                                     RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder> event) {
        super(registry, wrapperRegistry, event);
    }
    public static final TagKey<ItemType> SHARP_WEAPON_PICKAXE = TagKey.create(RegistryKey.ITEM, Key.key("crux", "sharp_weapon_pickaxe"));
    public static final TagKey<ItemType> SHARP_WEAPON_PICKAXE_HOE = TagKey.create(RegistryKey.ITEM, Key.key("crux", "sharp_weapon_pickaxe_hoe"));
    public static final TagKey<ItemType> BOW_CROSSBOW = TagKey.create(RegistryKey.ITEM, Key.key("crux", "bow_crossbow"));
    public static final TagKey<ItemType> BOW_CROSSBOW_TRIDENT = TagKey.create(RegistryKey.ITEM, Key.key("crux", "bow_crossbow_trident"));
    public static final TagKey<ItemType> CHESTPLATE_LEGGINGS = TagKey.create(RegistryKey.ITEM, Key.key("crux", "chestplate_leggings"));
    public static final TagKey<ItemType> WEAPONS_TOOLS_ARMOR = TagKey.create(RegistryKey.ITEM, Key.key("crux", "weapons_tools_armor"));

    public static void register(LifecycleEventManager<BootstrapContext> manager){
        manager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM), event -> {
            final PreFlattenTagRegistrar<ItemType> registrar = event.registrar();
            registrar.setTag(SHARP_WEAPON_PICKAXE, Set.of(
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_SHARP_WEAPON, true),
                TagEntry.tagEntry(ItemTypeTagKeys.PICKAXES, true)
            ));

            registrar.setTag(SHARP_WEAPON_PICKAXE_HOE, Set.of(
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_SHARP_WEAPON, true),
                TagEntry.tagEntry(ItemTypeTagKeys.PICKAXES, true),
                TagEntry.tagEntry(ItemTypeTagKeys.HOES, true)
            ));

            registrar.setTag(BOW_CROSSBOW, Set.of(
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_BOW, true),
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_CROSSBOW, true)
            ));

            registrar.setTag(BOW_CROSSBOW_TRIDENT, Set.of(
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_BOW, true),
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_CROSSBOW, true),
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_TRIDENT, true)
            ));

            registrar.setTag(CHESTPLATE_LEGGINGS, Set.of(
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR, true),
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_LEG_ARMOR, true)
            ));
            registrar.setTag(WEAPONS_TOOLS_ARMOR, Set.of(
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_ARMOR, true),
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_WEAPON, true),
                TagEntry.tagEntry(ItemTypeTagKeys.PICKAXES, true),
                TagEntry.tagEntry(ItemTypeTagKeys.AXES, true),
                TagEntry.tagEntry(ItemTypeTagKeys.SHOVELS, true),
                TagEntry.tagEntry(ItemTypeTagKeys.HOES, true)
            ));
        });

        manager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT), event -> {
            final PostFlattenTagRegistrar<Enchantment> registrar = event.registrar();
            registrar.addToTag(
                EnchantmentTagKeys.IN_ENCHANTING_TABLE,
                Set.of(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("trample")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("joy_whirl")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("burning_thorns")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("backstab")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("withstand")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("second_wind")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("boomshot")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("velocity")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("frostbite")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("insight")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("penetrate")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("trueshot")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("braced"))
                )
            );
        });
    }

    @Override
    public void register() {
        register(
            "trample", b -> b.description(Component.text("Trample"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.FEET)
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("feather_falling")))),

            crux -> crux.description("Take less fall damage when landing on an entity and damage them in the process")
                .applicableItemGroup(ApplicableItemGroup.itemGroup(ApplicableItemType.BOOTS))
        );
        register(
            "joy_whirl", b -> b.description(Component.text("Joy Whirl"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.SWORDS))
                .anvilCost(1)
                .maxLevel(3)
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("Chance to launch hit targets in the air and spin them around")
                .applicableItemGroup(ApplicableItemGroup.itemGroup(ApplicableItemType.SWORD))
                //.exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("feather_falling"))))
        );
        register(
            "burning_thorns", b -> b.description(Component.text("Burning Thorns"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.ARMOR)
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("thorns")))),

            crux -> crux.description("Chance to damage and set entities who hit the wearer on fire")
                .applicableItemGroup(ApplicableItemGroup.ARMOR)
        );
        register(
            "backstab", b -> b.description(Component.text("Backstab"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(3)
                .weight(4)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("Deal more damage when striking a target from behind")
                .applicableItemGroup(ApplicableItemGroup.WEAPONS)
        );
        register(
            "withstand", b -> b.description(Component.text("Withstand"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR))
                .anvilCost(1)
                .maxLevel(4)
                .weight(4)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.CHEST),

            crux -> crux.description("Briefly gain Resistance after taking a big hit")
                .applicableItemGroup(ApplicableItemGroup.itemGroup(ApplicableItemType.CHESTPLATE))
        );
        register(
            "second_wind", b -> b.description(Component.text("Second Wind"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR))
                .anvilCost(1)
                .maxLevel(3)
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.FEET),

            crux -> crux.description("Sprint faster when on low health")
                .applicableItemGroup(ApplicableItemGroup.itemGroup(ApplicableItemType.BOOTS))
        );
        register(
            "boomshot", b -> b.description(Component.text("Boomshot"))
                .supportedItems(RegistrySet.keySet(RegistryKey.ITEM,
                    TypedKey.create(RegistryKey.ITEM, Key.key("bow")),
                    TypedKey.create(RegistryKey.ITEM, Key.key("crossbow"))
                ))
                .anvilCost(1)
                .maxLevel(5)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.HAND),

            crux -> crux.description("Chance to launch an arrow that explodes upon impact")
                .applicableItemGroup(ApplicableItemGroup.BOWS_N_CROSSBOWS)
        );
        register(
            "velocity", b -> b.description(Component.text("Velocity"))
                .supportedItems(RegistrySet.keySet(RegistryKey.ITEM,
                    TypedKey.create(RegistryKey.ITEM, Key.key("bow")),
                    TypedKey.create(RegistryKey.ITEM, Key.key("crossbow"))
                ))
                .anvilCost(1)
                .maxLevel(2)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.HAND),

            crux -> crux.description("Increases projectile speed")
                .applicableItemGroup(ApplicableItemGroup.BOWS_N_CROSSBOWS)
        );
        register(
            "frostbite", b -> b.description(Component.text("Frostbite"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(2)
                .weight(2)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.HAND)
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("fire_aspect")))),

            crux -> crux.description("Chance to freeze and slow down the target upon hit")
                .applicableItemGroup(ApplicableItemGroup.WEAPONS)
        );
        register(
            "insight", b -> b.description(Component.text("Insight"))
                .supportedItems(event.getOrCreateTag(SHARP_WEAPON_PICKAXE_HOE))
                .anvilCost(1)
                .maxLevel(3)
                .weight(2)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND)
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("mending")))),

            crux -> crux.description("Chance to receive more exp from mining or killing mobs")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemType.PICKAXE)
                    .add(ApplicableItemGroup.SHARP_WEAPONS)
                    .add(ApplicableItemType.HOE)
                    .build())
        );
        register(
            "penetrate", b -> b.description(Component.text("Penetrate"))
                .supportedItems(event.getOrCreateTag(BOW_CROSSBOW))
                .anvilCost(1)
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, RegistryKey.ENCHANTMENT.typedKey(Key.key("piercing"))))
                .maxLevel(4)
                .weight(2)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("Projectiles ignore a portion of the target's armor")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemType.BOW)
                    .add(ApplicableItemType.CROSSBOW)
                    .build())
        );
        register(
            "braced", b -> b.description(Component.text("Braced"))
                .supportedItems(event.getOrCreateTag(CHESTPLATE_LEGGINGS))
                .anvilCost(1)
                .maxLevel(4)
                .weight(2)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("Reduces knockback taken")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemType.CHESTPLATE)
                    .add(ApplicableItemType.LEGGINGS)
                    .build())
        );
        register(
            "trueshot", b -> b.description(Component.text("Trueshot"))
                .supportedItems(event.getOrCreateTag(BOW_CROSSBOW))
                .anvilCost(1)
                .maxLevel(1)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("Projectiles ignore friendly entities (friended players or players within party and your tamed animals)")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.BOWS_N_CROSSBOWS)
                    .add(ApplicableItemType.TRIDENT)
                    .build())
        );
        register(
            "sporeburst", b -> b.description(Component.text("Sporeburst"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(5)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("Killing an entity causes it to explode in a toxic cloud, poisoning nearby enemies")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.WEAPONS)
                    .build())
        );
        register(
            "mirrorstrike", b -> b.description(Component.text("Mirror Strike"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("On hit, creates a faint strike after some time, that repeats part of the damage")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.WEAPONS)
                    .build())
        );
        register(
            "snare", b -> b.description(Component.text("Snare"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(4)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("Occasionally roots attackers in place")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.WEAPONS)
                    .build())
        );
        register(
            "blooming_curse", b -> b.description(Component.text("Curse of Blooming", NamedTextColor.RED))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR))
                .anvilCost(1)
                .maxLevel(1)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.ARMOR),
            crux -> crux.description("Gain strength and speed when below 50% HP. However, gradually takes max HP away each time the effect is triggered")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.ARMOR)
                    .build())
        );
        register(
            "stagger", b -> b.description(Component.text("Stagger"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(2)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("Critical hits inflict weakness and slowness")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.WEAPONS)
                    .build())
        );
        register(
            "hollowing_curse", b -> b.description(Component.text("Curse of Hollowing", NamedTextColor.RED))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR))
                .anvilCost(1)
                .maxLevel(1)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.ARMOR),
            crux -> crux.description("User gains max health while in the abyss. Loses max health while anywhere else")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.ARMOR)
                    .build())
        );
        register(
            "tether", b -> b.description(Component.text("Tether"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(4)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),
            crux -> crux.description("Attacking an entity briefly links the user to the victim. Breaking the tether by moving far away will cause damage to the victim")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.WEAPONS)
                    .build())
        );
        register(
            "frenzy", b -> b.description(Component.text("Frenzy"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),
            crux -> crux.description("After each hit, gain extra attack speed for a short period of time. Stacks based on level")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.WEAPONS)
                    .build())
        );
        register(
            "shatter", b -> b.description(Component.text("Shatter"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND)
                .exclusiveWith(RegistrySet.keySet(
                    RegistryKey.ENCHANTMENT,
                    minecraftTyped("smite"),
                    minecraftTyped("bane_of_arthropods"),
                    minecraftTyped("sharpness")
                )),
            crux -> crux.description("Deals bonus damage to entities with shields, armor, or absorption")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.WEAPONS)
                    .build())
        );
        register(
            "reap", b -> b.description(Component.text("Reap"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),
            crux -> crux.description("Killing an entity releases a damaging shockwave if other entities are nearby")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.WEAPONS)
                    .build())
        );
        register(
            "ricochet", b -> b.description(Component.text("Ricochet"))
                .supportedItems(event.getOrCreateTag(BOW_CROSSBOW_TRIDENT))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),
            crux -> crux.description("Projectiles bounce to multiple targets")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.BOWS_N_CROSSBOWS)
                    .add(ApplicableItemType.TRIDENT)
                    .build())
        );
        register(
            "cling", b -> b.description(Component.text("Cling"))
                .supportedItems(event.getOrCreateTag(BOW_CROSSBOW_TRIDENT))
                .anvilCost(1)
                .maxLevel(4)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),
            crux -> crux.description("Projectiles do damage over time while also preventing players from swapping their hands")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.BOWS_N_CROSSBOWS)
                    .build())
        );
        register(
            "focus", b -> b.description(Component.text("Focus"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_BOW))
                .anvilCost(1)
                .maxLevel(4)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),
            crux -> crux.description("Fully drawn bow shots deal more damage based on time held")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemType.BOW)
                    .build())
        );
        register(
            "remnant", b -> b.description(Component.text("Remnant"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR))
                .anvilCost(1)
                .maxLevel(1)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.ARMOR),
            crux -> crux.description("When the user dies, a ghostly version of them remains and fights nearby enemies (One time use enchant)")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.ARMOR)
                    .build())
        );
        register(
            "cleansing", b -> b.description(Component.text("Cleansing"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.ARMOR),
            crux -> crux.description("Inflicted weakness, slowness, and poison have shorter durations at the cost of item durability")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.ARMOR)
                    .build())
        );
        register(
            "resentment_curse", b -> b.description(Component.text("Curse of Resentment", NamedTextColor.RED))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR))
                .anvilCost(1)
                .maxLevel(1)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.ARMOR),
            crux -> crux.description("Normally passive mobs will target the user if they get too close")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.ARMOR)
                    .build())
        );
        register(
            "spoilage_curse", b -> b.description(Component.text("Curse of Spoilage", NamedTextColor.RED))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR))
                .anvilCost(1)
                .maxLevel(1)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.ARMOR),
            crux -> crux.description("Food restores less hunger")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.ARMOR)
                    .build())
        );
        register(
            "breaking_curse", b -> b.description(Component.text("Curse of Breaking", NamedTextColor.RED))
                .supportedItems(event.getOrCreateTag(WEAPONS_TOOLS_ARMOR))
                .anvilCost(1)
                .maxLevel(1)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.ANY),
            crux -> crux.description("Item takes more durability damage")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.ARMOR)
                    .add(ApplicableItemGroup.WEAPONS)
                    .add(ApplicableItemGroup.TOOLS)
                    .build())
        );
        register(
            "chainshock", b -> b.description(Component.text("Chainshock"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_MACE))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.HAND),
            crux -> crux.description("CHAIN WOOOSH")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemType.MACE)
                    .build())
        );
        register(
            "soulbound", b -> b.description(Component.text("Soulbound"))
                .supportedItems(event.getOrCreateTag(WEAPONS_TOOLS_ARMOR))
                .anvilCost(1)
                .maxLevel(1)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.HAND),
            crux -> crux.description("Prevents the item from being dropped upon death (One time use enchant)")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemGroup.TOOLS)
                    .add(ApplicableItemGroup.ARMOR)
                    .build())
        );
        register(
            "emotive", b -> b.description(Component.text("Emotive"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.HEAD_ARMOR))
                .anvilCost(1)
                .maxLevel(1)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.HEAD),
            crux -> crux.description("Displays an emoji above the user's head depending on their health and hunger")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemType.HELMET)
                    .build())
        );
        register(
            "yoink", b -> b.description(Component.text("Yoink"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_FISHING))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),
            crux -> crux.description("Allows the fishing hook to fling solid blocks")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemType.FISHING_ROD)
                    .build())
        );
    }

    private TypedKey<Enchantment> minecraftTyped(String key){
        return RegistryKey.ENCHANTMENT.typedKey(Key.key(key));
    }
}
