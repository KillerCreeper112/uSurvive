package killercreepr.usurvive.core.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.math.CruxLocation;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.entity.player.data.RandomTPData;
import killercreepr.usurvive.core.entity.player.data.SimpleRandomTPData;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FileRandomTPData implements FileObjectHandler<RandomTPData> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull RandomTPData data) {
        return ctx.getRegistry().serializeToFile(data.asMap());
    }

    @Nullable
    @Override
    public RandomTPData deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        Map<Key, RandomTPData.Pos> map = ctx.getRegistry().deserializeFromFile(
            new TypeToken<Map<Key, CruxLocation>>(){}.getType(), e
        );
        if(map == null) return null;
        RandomTPData data = new SimpleRandomTPData();
        data.setAllLastTeleported(map);
        return data;
    }
}
