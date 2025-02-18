package killercreepr.usurvive.core.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessCalculator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FileSleepWeaknessCalculator implements FileObjectHandler<SleepWeaknessCalculator> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> fileContext, @NotNull SleepWeaknessCalculator sleepWeaknessCalculator) {
        throw new UnsupportedOperationException("NNOOOOOO");
    }

    @Nullable
    @Override
    public SleepWeaknessCalculator deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        Map<Integer, NumberProvider> map = ctx.getRegistry().deserializeFromFile(
            new TypeToken<Map<Integer, NumberProvider>>(){}.getType(), e
        );
        if(map == null || map.isEmpty()) return null;
        return new SleepWeaknessCalculator(map);
    }
}
