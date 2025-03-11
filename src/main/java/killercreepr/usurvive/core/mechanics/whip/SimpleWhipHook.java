package killercreepr.usurvive.core.mechanics.whip;

import killercreepr.usurvive.api.mechanics.whip.WhipHook;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleWhipHook implements WhipHook {
    protected final @NotNull Key key;
    public SimpleWhipHook(@NotNull Key key) {
        this.key = key;
    }

    @Override
    public @NotNull Key key() {
        return key;
    }
}
