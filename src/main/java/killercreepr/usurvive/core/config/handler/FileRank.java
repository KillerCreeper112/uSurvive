package killercreepr.usurvive.core.config.handler;

import com.google.common.reflect.TypeToken;
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

import java.util.ArrayList;
import java.util.List;

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
    public Rank deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ele, Key key, FileObject defaultData) {
        if(!(ele instanceof FileObject o)) return null;
        var r = ctx.getRegistry();
        List<String> rankUpCommands = new ArrayList<>();
        List<String> got = r.deserializeFromFile(new TypeToken<List<String>>(){}.getType(), o.get("rank_up_commands"));
        if(got != null) rankUpCommands.addAll(got);

        if(!r.deserializeFromFileOrDefault(Boolean.class, o.get("override_default_rank_up_commands"), false)){
            rankUpCommands.addAll(
                r.deserializeFromFileOrDefault(new TypeToken<List<String>>(){}.getType(), defaultData.get("default_rank_up_commands"), List.of())
            );
        }

        DataExchange generalInfo = DataExchange.builder()
            .put("rank", key.value())
            .build();

        DataExchange info = generalInfo.append(r.deserializeFromFileOrDefault(DataExchange.class, o.get("data"), DataExchange.empty()));

        if(!r.deserializeFromFileOrDefault(Boolean.class, o.get("override_default_data"), false)){
            info = r.deserializeFromFileOrDefault(DataExchange.class, defaultData.get("default_data"), DataExchange.empty())
                .append(info);
        }

        return new SimpleRank(key, info, rankUpCommands);
    }
}
