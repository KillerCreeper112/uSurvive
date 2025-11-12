package killercreepr.usurvive.core.config.handler;

import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.story.FileStoryProgressionLoader;
import killercreepr.usurvive.api.story.StoryProgressData;
import killercreepr.usurvive.api.story.StoryProgression;
import killercreepr.usurvive.core.guide.progression.GuideProgression;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FileStoryProgression implements FileObjectHandler<StoryProgression> {
    protected final Map<Key, FileStoryProgressionLoader<? extends StoryProgression, ? extends StoryProgressData>> HANDLERS = new HashMap<>();
    public void register(Key key, FileStoryProgressionLoader<? extends StoryProgression, ? extends StoryProgressData> handler){
        HANDLERS.put(key, handler);
    }

    public Map<Key, FileStoryProgressionLoader<? extends StoryProgression, ? extends StoryProgressData>> getHandlers() {
        return HANDLERS;
    }

    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull StoryProgression guide) {
        FileStoryProgressionLoader handler = HANDLERS.get(guide.key());
        FileObject o = new FileObject();
        o.add("type", ctx.getRegistry().serializeToFile(guide.key()));
        o.add("data", handler.serializeToFile(ctx, guide));
        return o;
    }

    @Nullable
    @Override
    public StoryProgression deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        if(!(e instanceof FileObject o)) return null;
        FileStoryProgressionLoader<? extends StoryProgression, ? extends StoryProgressData> handler =
            HANDLERS.get(ctx.getRegistry().deserializeFromFile(Key.class, o.get("type")));
        if(handler == null) throw new IllegalArgumentException("No handler of " + o.get("type") + " " + o);
        return handler.deserializeFromFile(ctx, o.get("data"));
    }
}
