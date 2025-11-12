package killercreepr.usurvive.core.config.handler;

import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.story.FileStoryProgressionLoader;
import killercreepr.usurvive.api.story.StoryProgressData;
import killercreepr.usurvive.api.story.StoryProgression;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FileStoryProgressData implements FileObjectHandler<StoryProgressData> {
    protected final FileStoryProgression fileStoryProgression;

    public FileStoryProgressData(FileStoryProgression fileStoryProgression) {
        this.fileStoryProgression = fileStoryProgression;
    }

    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull StoryProgressData guide) {
        FileStoryProgressionLoader handler = fileStoryProgression.HANDLERS.get(guide.key());
        FileObject o = new FileObject();
        o.add("type", ctx.getRegistry().serializeToFile(guide.key()));
        o.add("data", handler.permanentDataHandler().serializeToFile(ctx, guide));
        return o;
    }

    @Nullable
    @Override
    public StoryProgressData deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        if(!(e instanceof FileObject o)) return null;
        FileStoryProgressionLoader<? extends StoryProgression, ? extends StoryProgressData> handler =
            fileStoryProgression.HANDLERS.get(ctx.getRegistry().deserializeFromFile(Key.class, o.get("type")));
        if(handler == null) throw new IllegalArgumentException("No handler of " + o.get("type") + " " + o);
        return handler.permanentDataHandler().deserializeFromFile(ctx, o.get("data"));
    }
}
