package killercreepr.usurvive.core.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.guide.tips.Tip;
import killercreepr.usurvive.api.guide.tips.TipCategory;
import killercreepr.usurvive.core.guide.tips.SimpleTipCategory;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FileTipCategory implements FileObjectHandler<TipCategory> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull TipCategory cat) {
        return ctx.getRegistry().serializeToFile(cat.tips());
    }

    public TipCategory deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ee){
        if(!(ee instanceof FileObject o)) return null;
        return deserializeFromFile(ctx, ee, ctx.getRegistry().deserializeFromFile(Key.class, o.get("key")));
    }

    @Nullable
    public TipCategory deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ee, Key key) {
        List<Tip> tips = ctx.getRegistry().deserializeFromFile(
            new TypeToken<List<Tip>>(){}.getType(), ee
        );
        return new SimpleTipCategory(key, tips);
    }
}
