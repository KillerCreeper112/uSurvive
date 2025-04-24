package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.api.component.parser.InputDecodeContext;
import killercreepr.crux.api.component.parser.hybrid.PersistParser;
import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.api.component.parser.hybrid.TextInputField;
import killercreepr.crux.core.Crux;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.ActiveEntityTickable;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickableModifier;
import killercreepr.cruxtickables.core.entity.tickable.SimpleDataEntityTickable;
import killercreepr.cruxtickables.core.registries.CruxTickableRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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

    private static EntityTickable register(EntityTickable tickable){
        return CruxTickableRegistries.ENTITY_TICKABLE.register(tickable);
    }
}
