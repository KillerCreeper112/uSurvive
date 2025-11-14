package killercreepr.usurvive.core.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileNull;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.AutoFileOptions;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.story.StoryState;
import killercreepr.usurvive.core.story.SimpleStoryState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class FileStoryState implements FileObjectHandler<StoryState> {
    protected final AutoFileHandler<SimpleStoryState> auto = new AutoFileHandler<>(SimpleStoryState.class,
        AutoFileOptions.builder()
            .addTypeToken("completedFlags", new TypeToken<Set<String>>() {})
            .addTypeToken("intFlags", new TypeToken<Map<String, Integer>>() {})
            .build()
    );
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull StoryState state) {
        if(state.getCompletedFlags().isEmpty() && state.getIntFlags().isEmpty()) return FileNull.INSTANCE;
        return auto.serializeToFile(ctx, (SimpleStoryState) state);
    }

    @Nullable
    @Override
    public StoryState deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        if(e instanceof FileNull) return new SimpleStoryState();
        return auto.deserializeFromFile(ctx, e);
    }
}
