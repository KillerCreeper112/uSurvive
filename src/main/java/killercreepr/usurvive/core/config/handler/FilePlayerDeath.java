package killercreepr.usurvive.core.config.handler;

import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.AutoFileOptions;
import killercreepr.usurvive.core.death.PlayerDeath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FilePlayerDeath extends AutoFileHandler<PlayerDeath> {
    public FilePlayerDeath(@NotNull Class<PlayerDeath> type, @Nullable AutoFileOptions options) {
        super(type, options);
    }

    public FilePlayerDeath(@NotNull Class<PlayerDeath> type) {
        super(type);
    }

    public FilePlayerDeath(){
        this(
            PlayerDeath.class,
            AutoFileOptions.builder()
                .manualHandlers(Map.of(
                    "drops", new StoredItemsConverter()
                ))
                .build()
        );
    }
}
