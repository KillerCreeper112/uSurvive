package killercreepr.usurvive.api.mechanics.whip;

import killercreepr.usurvive.api.mechanics.whip.context.WhipContext;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface WhipHook extends Keyed {
    @NotNull ActiveWhip throwNew(@NotNull WhipContext ctx);
}
