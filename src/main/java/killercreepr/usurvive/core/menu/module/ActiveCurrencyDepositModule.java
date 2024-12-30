package killercreepr.usurvive.core.menu.module;

import killercreepr.crux.core.Crux;
import killercreepr.cruxcurrency.api.currency.CruxCurrency;
import killercreepr.cruxcurrency.api.economy.EconomyHandler;
import killercreepr.cruxcurrency.api.item.CruxCurrencyItem;
import killercreepr.cruxcurrency.core.registry.CruxCurrencyRegistries;
import killercreepr.cruxmenus.api.menu.CfgMenu;
import killercreepr.cruxmenus.api.menu.Menu;
import killercreepr.cruxmenus.api.menu.SlottedMenu;
import killercreepr.cruxmenus.api.menu.contex.SlotContext;
import killercreepr.cruxmenus.api.menu.slot.Slot;
import killercreepr.cruxmenus.core.menu.module.SimpleActiveMenuModuled;
import killercreepr.cruxmenus.core.menu.slot.SimpleSlot;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ActiveCurrencyDepositModule extends SimpleActiveMenuModuled {
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
                CruxCurrencyItem currencyItem = CruxCurrencyRegistries.CURRENCY_ITEM.get(Crux.handlers().item().getType(item));
                if(currencyItem == null) return false;
                CruxCurrency currency = currencyItem.getCurrency().value();
                return module.acceptedCurrencies.contains(currency.key());
            }

            @Override
            public void onChanged(@NotNull SlotContext ctx) {
                super.onChanged(ctx);
                if(!(ctx.getWhoClicked() instanceof OfflinePlayer p)) return;
                ItemStack newItem = ctx.getNewItem();
                if(newItem == null) return;
                CruxCurrencyItem currencyItem = CruxCurrencyRegistries.CURRENCY_ITEM.get(Crux.handlers().item().getType(newItem));
                if(currencyItem == null) return;
                CruxCurrency currency = currencyItem.getCurrency().value();
                int amount = newItem.getAmount();
                newItem.setAmount(0);
                EconomyHandler.handler().depositBalance(p, currency, amount);
                Crux.scheduler().runTask(menu::refresh);
                if(module.depositMsg != null && p instanceof Player online){
                    module.depositMsg.use(online);
                }
            }
        };
    }
}
