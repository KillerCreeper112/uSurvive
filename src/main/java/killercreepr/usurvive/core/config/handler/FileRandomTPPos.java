package killercreepr.usurvive.core.config.handler;

import killercreepr.crux.api.math.CruxLocation;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.entity.player.data.RandomTPData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileRandomTPPos implements FileObjectHandler<RandomTPData.Pos> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull RandomTPData.Pos data) {
        return new FileObject()
            .add("location", ctx.getRegistry().serializeToFile(data.getLocation()))
            .add("time", ctx.getRegistry().serializeToFile(data.getTime()))
            ;
    }

    @Nullable
    @Override
    public RandomTPData.Pos deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        if(!(e instanceof FileObject o)) return null;
        CruxLocation loc = ctx.getRegistry().deserializeFromFile(CruxLocation.class, o.get("location"));
        Long time = ctx.getRegistry().deserializeFromFile(Long.class, o.get("time"));
        if(time == null || loc == null) return null;
        return new RandomTPData.Pos.Simple(loc, time);
    }
}
