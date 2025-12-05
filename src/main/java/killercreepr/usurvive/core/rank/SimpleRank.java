package killercreepr.usurvive.core.rank;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.core.data.SimpleKeyed;
import killercreepr.usurvive.api.rank.Rank;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class SimpleRank extends SimpleKeyed implements Rank {
    protected final DataExchange info;

    public SimpleRank(Key key, DataExchange info) {
        super(key);
        this.info = info;
    }

    @NotNull
    @Override
    public DataExchange info() {
        return info;
    }
}
