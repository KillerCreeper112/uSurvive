package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.api.component.parser.InputDecodeContext;
import killercreepr.crux.api.component.parser.hybrid.PersistParser;
import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.api.component.parser.hybrid.TextInputField;
import killercreepr.crux.api.data.ParticleBuilderSupplier;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.parser.type.ComponentInputParsers;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.ActiveEntityTickable;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickableModifier;
import killercreepr.cruxtickables.core.entity.tickable.SimpleDataEntityTickable;
import killercreepr.cruxtickables.core.registries.CruxTickableRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class USurviveTickables {
    public static void register(){}

    public static final EntityTickable ABSORB_DAMAGE = register(new SimpleDataEntityTickable(Crux.key("absorb_damage")) {
        public static final PersistParser<?> DATA_PARSER = PersistTextParser.mapBuilder(Map.class)
            .field("chance", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("chance")))
            .field("dmg_reduction_percent", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("dmg_reduction_percent")))
            .field("max_dmg_reduction", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("max_dmg_reduction")))
            .field("item_dmg", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("item_dmg")))
            .field("damage_types", TextInputField.field(PersistTextParser.LIST.KEY, e -> (List<Key>) e.get("damage_types")))
            .field("damage_causes", TextInputField.field(PersistTextParser.LIST.STRING, e -> (List<String>) e.get("damage_causes")))
            .apply(InputDecodeContext::get)
            .createInput(Crux.key("data"));

        @Override
        public PersistParser<?> getDataParser() {
            return DATA_PARSER;
        }

        @Override
        public @Nullable ActiveEntityTickable buildActive(@NotNull Entity entity, @Nullable CruxSlot slot, @NotNull EntityTickableModifier mod) {
            Map data = (Map) mod.getData();
            return new ActiveAbsorbDamageTickable(
                entity, this, slot,
                (float) data.getOrDefault("chance", 100f),
                (float) data.getOrDefault("dmg_reduction_percent", 1f),
                (float) data.getOrDefault("max_dmg_reduction", 999f),
                (float) data.getOrDefault("item_dmg", 1f),
                (Collection<String>) data.get("damage_causes"),
                (Collection<Key>) data.get("damage_types")
            );
        }
    });

    public static final EntityTickable ABSORB_NEARBY_FRIENDLY_DAMAGE = register(new SimpleDataEntityTickable(Crux.key("absorb_nearby_friendly_damage")) {
        public static final PersistParser<?> DATA_PARSER = PersistTextParser.mapBuilder(Map.class)
            .field("chance", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("chance")))
            .field("dmg_reduction_percent", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("dmg_reduction_percent")))
            .field("max_dmg_reduction", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("max_dmg_reduction")))
            .field("item_dmg", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("item_dmg")))
            .field("damage_types", TextInputField.field(PersistTextParser.LIST.KEY, e -> (List<Key>) e.get("damage_types")))
            .field("damage_causes", TextInputField.field(PersistTextParser.LIST.STRING, e -> (List<String>) e.get("damage_causes")))
            .apply(InputDecodeContext::get)
            .createInput(Crux.key("data"));

        @Override
        public PersistParser<?> getDataParser() {
            return DATA_PARSER;
        }

        @Override
        public @Nullable ActiveEntityTickable buildActive(@NotNull Entity entity, @Nullable CruxSlot slot, @NotNull EntityTickableModifier mod) {
            Map data = (Map) mod.getData();
            return new ActiveAbsorbNearbyFriendlyDamageTickable(
                entity, this, slot,
                (float) data.getOrDefault("chance", 100f),
                (float) data.getOrDefault("dmg_reduction_percent", .7f),
                (float) data.getOrDefault("max_dmg_reduction", 999f),
                (float) data.getOrDefault("item_dmg", 0f),
                (Collection<String>) data.get("damage_causes"),
                (Collection<Key>) data.get("damage_types"),
                (float) data.getOrDefault("range", 8f)
            );
        }
    });

    public static final EntityTickable ROSE_GOLD = register(new SimpleDataEntityTickable(Crux.key("rose_gold")) {
        public static final PersistParser<?> DATA_PARSER = PersistTextParser.mapBuilder(Map.class)
            .field("speed_multiplier", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("speed_multiplier")))
            .field("safe_fall_distance", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("safe_fall_distance")))
            .apply(InputDecodeContext::get)
            .createInput(Crux.key("data"));

        @Override
        public PersistParser<?> getDataParser() {
            return DATA_PARSER;
        }

        @Override
        public @Nullable ActiveEntityTickable buildActive(@NotNull Entity entity, @Nullable CruxSlot slot, @NotNull EntityTickableModifier mod) {
            Map data = (Map) mod.getData();
            return new RoseGoldTickable(
                entity, this, slot,
                (float) data.getOrDefault("speed_multiplier", .1f),
                (float) data.getOrDefault("safe_fall_distance", 2.5f)
            );
        }
    });

    public static final EntityTickable NO_MOVE_NO_TARGET = register(new SimpleDataEntityTickable(Crux.key("no_move_no_target")) {
        public static final PersistParser<?> DATA_PARSER = PersistTextParser.mapBuilder(Map.class)
            .field("dont_move_ticks", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("dont_move_ticks")))
            .field("particle_rate", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("particle_rate")))
            .field("active_particle", TextInputField.field(ComponentInputParsers.PARTICLE_BUILDER_SUPPLIER, e -> (ParticleBuilderSupplier) e.get("active_particle")))
            .apply(InputDecodeContext::get)
            .createInput(Crux.key("data"));

        @Override
        public PersistParser<?> getDataParser() {
            return DATA_PARSER;
        }

        @Override
        public @Nullable ActiveEntityTickable buildActive(@NotNull Entity entity, @Nullable CruxSlot slot, @NotNull EntityTickableModifier mod) {
            Map data = (Map) mod.getData();

            ParticleBuilderSupplier particleSupplier = (ParticleBuilderSupplier) data.get("active_particle");
            int particleRate = (int) data.getOrDefault("particle_rate", 20);

            return new ActiveNoMoveNoTargetTickable(
                entity, this, slot,
                (int) data.getOrDefault("dont_move_ticks", 60),
                particleSupplier,
                particleRate
            );
        }
    });

    public static final EntityTickable MOULDITE_DIAMOND = register(new SimpleDataEntityTickable(Crux.key("mouldite_diamond")) {
        public static final PersistParser<?> DATA_PARSER = PersistTextParser.mapBuilder(Map.class)
            .apply(InputDecodeContext::get)
            .createInput(Crux.key("data"));

        @Override
        public PersistParser<?> getDataParser() {
            return DATA_PARSER;
        }

        @Override
        public @Nullable ActiveEntityTickable buildActive(@NotNull Entity entity, @Nullable CruxSlot slot, @NotNull EntityTickableModifier mod) {
            return new AttributesTickable(
                entity, this, slot,
                Map.of(
                    Attribute.ARMOR_TOUGHNESS,
                    new AttributeModifier(Crux.key("item/set_bonus/mouldite_diamond"), 0.15D, AttributeModifier.Operation.MULTIPLY_SCALAR_1),

                    Attribute.ATTACK_DAMAGE,
                    new AttributeModifier(Crux.key("item/set_bonus/mouldite_diamond"), 0.05D, AttributeModifier.Operation.MULTIPLY_SCALAR_1),

                    Attribute.MAX_HEALTH,
                    new AttributeModifier(Crux.key("item/set_bonus/mouldite_diamond"), 4D, AttributeModifier.Operation.ADD_NUMBER)
                )
            );
        }
    });

    public static final EntityTickable MOULDITE_ROSE_GOLD = register(new SimpleDataEntityTickable(Crux.key("mouldite_rose_gold")) {
        public static final PersistParser<?> DATA_PARSER = PersistTextParser.mapBuilder(Map.class)
            .apply(InputDecodeContext::get)
            .createInput(Crux.key("data"));

        @Override
        public PersistParser<?> getDataParser() {
            return DATA_PARSER;
        }

        @Override
        public @Nullable ActiveEntityTickable buildActive(@NotNull Entity entity, @Nullable CruxSlot slot, @NotNull EntityTickableModifier mod) {
            return new AttributesTickable(
                entity, this, slot,
                Map.of(
                    Attribute.BLOCK_BREAK_SPEED,
                    new AttributeModifier(Crux.key("item/set_bonus/mouldite_rose_gold"), 0.1D, AttributeModifier.Operation.MULTIPLY_SCALAR_1),

                    Attribute.MOVEMENT_SPEED,
                    new AttributeModifier(Crux.key("item/set_bonus/mouldite_rose_gold"), 0.15D, AttributeModifier.Operation.MULTIPLY_SCALAR_1),

                    Attribute.SAFE_FALL_DISTANCE,
                    new AttributeModifier(Crux.key("item/set_bonus/mouldite_rose_gold"), 3.5D, AttributeModifier.Operation.ADD_NUMBER)
                )
            );
        }
    });

    private static EntityTickable register(EntityTickable tickable){
        return CruxTickableRegistries.ENTITY_TICKABLE.register(tickable);
    }
}
