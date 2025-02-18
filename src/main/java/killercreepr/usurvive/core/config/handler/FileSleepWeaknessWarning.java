package killercreepr.usurvive.core.config.handler;

import killercreepr.crux.core.communication.MsgContainer;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileArray;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessWarning;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessWarningData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public class FileSleepWeaknessWarning implements FileObjectHandler<SleepWeaknessWarning> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> fileContext, @NotNull SleepWeaknessWarning sleepWeaknessCalculator) {
        throw new UnsupportedOperationException("NNOOOOOO");
    }

    @Nullable
    @Override
    public SleepWeaknessWarning deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        if(!(e instanceof FileArray a)) return null;
        FileRegistry reg = ctx.getRegistry();
        Collection<SleepWeaknessWarningData> list = new HashSet<>();
        a.forEach(ele ->{
            if(!(ele instanceof FileObject oo)) return;

            SleepWeaknessWarningData warn = new SleepWeaknessWarningData(
                reg.deserializeFromFile(MsgContainer.class, oo.get("msg")),
                oo.getObject(Number.class, "min_slumber_ticks").intValue(),
                oo.getObject(Number.class, "max_slumber_ticks").intValue()
            );
            list.add(warn);
        });
        if(list.isEmpty()) return null;

        return new SleepWeaknessWarning(list);
    }
}
