package killercreepr.usurvive.core.config.handler;

import killercreepr.crux.api.data.WorldPosition;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.guide.GuidePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileGuidePoint implements FileObjectHandler<GuidePoint> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull GuidePoint guidePoint) {
        return null;
    }

    @Nullable
    @Override
    public GuidePoint deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ele) {
        var r = ctx.getRegistry();
        if(!(ele instanceof FileObject o)){
            return null;
        }

        if(!o.has("position")){
            WorldPosition pos = r.deserializeFromFile(WorldPosition.class, o);
            if(pos == null) return null;
            return GuidePoint.guidePoint(pos);
        }
        WorldPosition pos = r.deserializeFromFile(WorldPosition.class, o.get("position"));
        if(pos == null) return null;
        return GuidePoint.guidePoint(
            pos,
            r.deserializeFromFile(DialogContent.class, o.get("content"))
        );
    }
}
