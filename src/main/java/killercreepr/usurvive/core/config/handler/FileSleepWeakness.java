package killercreepr.usurvive.core.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeakness;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class FileSleepWeakness implements FileObjectHandler<SleepWeakness> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> fileContext, @NotNull SleepWeakness sleepWeaknessCalculator) {
        throw new UnsupportedOperationException("NNOOOOOO");
    }

    @Nullable
    @Override
    public SleepWeakness deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        if(!(e instanceof FileObject o)) return null;
        FileRegistry reg = ctx.getRegistry();
        Set<PotionEffect> potions = reg.deserializeFromFile(
            new TypeToken<Set<PotionEffect>>(){}.getType(), o.get("potions")
        );
        return new SleepWeakness(potions);
    }
}
