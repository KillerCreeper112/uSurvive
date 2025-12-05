package killercreepr.usurvive.core.config.handler;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.rank.Rank;
import killercreepr.usurvive.core.rank.SimpleRank;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileRank implements FileObjectHandler<Rank> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull Rank rank) {
        return null;
    }

    @Nullable
    @Override
    public Rank deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ele) {
        if(!(ele instanceof FileObject o)) return null;
        return null;
    }

    @Nullable
    public Rank deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ele, Key key) {
        if(!(ele instanceof FileObject o)) return null;
        var r = ctx.getRegistry();
        return new SimpleRank(
            key,
            r.deserializeFromFileOrDefault(DataExchange.class, o.get("data"), DataExchange.empty())
        );
    }
}
