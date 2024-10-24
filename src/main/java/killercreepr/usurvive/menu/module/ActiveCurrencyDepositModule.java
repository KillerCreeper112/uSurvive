package killercreepr.usurvive.menu.module;

import killercreepr.crux.Crux;
import killercreepr.cruxitems.item.plugin.PluginItem;
import killercreepr.cruxmenus.api.menu.CfgMenu;
import killercreepr.cruxmenus.api.menu.Menu;
import killercreepr.cruxmenus.api.menu.SlottedMenu;
import killercreepr.cruxmenus.api.menu.slot.Slot;
import killercreepr.cruxmenus.core.menu.module.SimpleActiveMenuModule;
import killercreepr.cruxmenus.core.menu.slot.SimpleSlot;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ActiveCurrencyDepositModule extends SimpleActiveMenuModule {
    protected final @NotNull CurrencyDepositModule module;
    public ActiveCurrencyDepositModule(@NotNull String id, @NotNull CurrencyDepositModule module) {
        super(id, module);
        this.module = module;
    }

    @Override
    public @NotNull CurrencyDepositModule getModule() {
        return module;
    }

    @Override
    public void load(@NotNull Menu menu) {
        if(!(menu instanceof SlottedMenu m) || !(menu instanceof CfgMenu cfg)) return;
        super.load(menu);
        List<Number> currencyIndexes = module.currencyIndexes.sampleList();
        for(Number slot : currencyIndexes){
            Slot menuSlot = buildCurrencySlot(cfg, slot.intValue());
            m.addSlot(menuSlot, true);
        }
    }

    private Slot buildCurrencySlot(CfgMenu cfg, int slot){
        return new SimpleSlot(cfg, slot){
            @Override
            public boolean mayPlace(@NotNull HumanEntity p, @Nullable ItemStack item) {
                if(item == null) return false;
                for(Key k : module.acceptedCurrencies){
                    if(PluginItem.isPluginItem(item, k)) return true;
                }
                return false;//todo make proper
            }
        };
    }
}
