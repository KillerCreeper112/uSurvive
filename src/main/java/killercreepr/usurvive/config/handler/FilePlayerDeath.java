package killercreepr.usurvive.config.handler;

import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.usurvive.death.PlayerDeath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class FilePlayerDeath extends AutoFileHandler<PlayerDeath> {
    public FilePlayerDeath(@NotNull Class<PlayerDeath> type, @Nullable BiPredicate<String, Object> isValid, @Nullable Predicate<Field> disabledFields) {
        super(type, isValid, disabledFields);
    }

    public FilePlayerDeath(@NotNull Class<PlayerDeath> type, @Nullable BiPredicate<String, Object> isValid) {
        super(type, isValid);
    }

    public FilePlayerDeath(@NotNull Class<PlayerDeath> type) {
        super(type);
    }

    public FilePlayerDeath(){
        this(PlayerDeath.class);
    }
}
