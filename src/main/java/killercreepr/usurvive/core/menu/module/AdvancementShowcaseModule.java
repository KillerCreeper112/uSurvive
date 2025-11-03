package killercreepr.usurvive.core.menu.module;

import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxmenus.api.menu.CfgMenu;
import killercreepr.cruxmenus.api.menu.Menu;
import killercreepr.cruxmenus.api.menu.module.ActiveMenuModule;
import killercreepr.cruxmenus.core.menu.module.SimpleMenuModule;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class AdvancementShowcaseModule extends SimpleMenuModule {
    protected final @NotNull String id;
    public final @NotNull List<Key> advancement_managers;
    public final NumberProvider advance_slots;
    public final NumberProvider advance_manager_slots;
    public final NumberProvider general_slot_offset;
    public final NumberProvider center_offset_x;
    public final NumberProvider center_offset_y;
    public final Map<String, String> description_color;
    public final DynamicItem unknown_icon;
    public final DynamicItem empty_manager_icon;
    public final NumberProvider description_length;
    public final List<String> completed_lore;

    public AdvancementShowcaseModule(@NotNull Key key, @NotNull String id, @NotNull List<Key> advancement_managers, NumberProvider advance_slots, NumberProvider advance_manager_slots, NumberProvider general_slot_offset, NumberProvider centerOffsetX, NumberProvider centerOffsetY, Map<String, String> descriptionColor, DynamicItem unknownIcon, DynamicItem emptyManagerIcon, NumberProvider descriptionLength, List<String> completedLore) {
        super(key);
        this.id = id;
        this.advancement_managers = advancement_managers;
        this.advance_slots = advance_slots;
        this.advance_manager_slots = advance_manager_slots;
        this.general_slot_offset = general_slot_offset;
        center_offset_x = centerOffsetX;
        center_offset_y = centerOffsetY;
        description_color = descriptionColor;
        unknown_icon = unknownIcon;
        empty_manager_icon = emptyManagerIcon;
        description_length = descriptionLength;
        completed_lore = completedLore;
    }

    @Override
    public @Nullable ActiveMenuModule build(@NotNull Menu menu) {
        return new ActiveAdvancementShowcaseModule(id, this, (CfgMenu) menu);
    }
}
