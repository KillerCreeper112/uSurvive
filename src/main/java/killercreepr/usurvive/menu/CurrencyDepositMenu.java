package killercreepr.usurvive.menu;

import killercreepr.crux.Crux;
import killercreepr.crux.data.DataExchange;
import killercreepr.crux.tags.container.MergedTagContainer;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.cruxitems.item.plugin.PluginItem;
import killercreepr.cruxmenus.api.menu.holder.MenuHolder;
import killercreepr.cruxmenus.api.menu.slot.Slot;
import killercreepr.cruxmenus.core.menu.ConfigMenu;
import killercreepr.cruxmenus.core.menu.slot.SimpleSlot;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CurrencyDepositMenu extends ConfigMenu {
    public CurrencyDepositMenu(@NotNull MenuHolder holder, @NotNull DataExchange info) {
        super(holder, info);
    }

    public CurrencyDepositMenu(@NotNull MenuHolder holder, @NotNull DataExchange info, @Nullable MergedTagContainer tags) {
        super(holder, info, tags);
    }

    private Slot buildCurrencySlot(int slot){
        return new SimpleSlot(this, slot){
            @Override
            public boolean mayPlace(@NotNull HumanEntity p, @Nullable ItemStack item) {
                if(item == null) return false;
                return PluginItem.isPluginItem(item, getCurrencyKey());//todo make proper
            }
        };
    }

    public Key getCurrencyKey(){
        return Crux.key(info().get("currency", String.class));
    }

    @Override
    public void load() {
        super.load();
        List<Integer> currencyIndexes = info().get("currency_indexes", List.class);
        for(int slot : currencyIndexes){
            Slot menuSlot = buildCurrencySlot(slot);
            addSlot(menuSlot, true);
        }
    }
}
