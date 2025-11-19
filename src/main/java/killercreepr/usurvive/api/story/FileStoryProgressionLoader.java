package killercreepr.usurvive.api.story;

import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;

public interface FileStoryProgressionLoader<T, D> {
    FileObjectHandler<T> tempDataHandler();
    FileObjectHandler<D> permanentDataHandler();

    /*@NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull T e);
    @Nullable T deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e);


    @NotNull FileElement serializeDataToFile(@NotNull FileContext<?> ctx, @NotNull D e);
    @Nullable D deserializeDataFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e);*/
}
