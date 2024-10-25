package killercreepr.usurvive.menu.module;

import killercreepr.crux.data.communication.Communicator;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.cruxmenus.api.menu.Menu;
import killercreepr.cruxmenus.api.menu.module.ActiveMenuModule;
import killercreepr.cruxmenus.core.menu.module.SimpleMenuModule;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CurrencyDepositModule extends SimpleMenuModule {
    protected final @NotNull String id;
    protected final @NotNull Collection<Key> acceptedCurrencies;
    protected final @NotNull NumberProvider currencyIndexes;
    protected final @Nullable Communicator depositMsg;

    public CurrencyDepositModule(@NotNull Key key, @NotNull String id, @NotNull Collection<Key> acceptedCurrencies, @NotNull NumberProvider currencyIndexes, @Nullable Communicator depositMsg) {
        super(key);
        this.id = id;
        this.acceptedCurrencies = acceptedCurrencies;
        this.currencyIndexes = currencyIndexes;
        this.depositMsg = depositMsg;
    }

    @Override
    public @Nullable ActiveMenuModule build(@NotNull Menu menu) {
        return new ActiveCurrencyDepositModule(id, this);
    }
}
