package killercreepr.usurvive.core.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.guide.tips.Tip;
import killercreepr.usurvive.core.guide.tips.SimpleTip;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FileTip implements FileObjectHandler<Tip> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull Tip tip) {
        return ctx.getRegistry().serializeToFile(tip.message());
    }

    @Nullable
    @Override
    public Tip deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        List<String> message = ctx.getRegistry().deserializeFromFile(
            new TypeToken<List<String>>(){}.getType(), e
        );
        return new SimpleTip(message);
    }
}
