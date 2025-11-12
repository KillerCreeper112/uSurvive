package killercreepr.usurvive.api.story;

import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FileStoryProgressionLoader<T, D> {
    FileObjectHandler<T> tempDataHandler();
    FileObjectHandler<D> permanentDataHandler();

    /*@NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull T e);
    @Nullable T deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e);


    @NotNull FileElement serializeDataToFile(@NotNull FileContext<?> ctx, @NotNull D e);
    @Nullable D deserializeDataFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e);*/
}
