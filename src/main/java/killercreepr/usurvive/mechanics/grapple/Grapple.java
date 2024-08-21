package killercreepr.usurvive.mechanics.grapple;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class Grapple {
    protected final @NotNull Key hook;
    protected final int maxHooks;
    public Grapple(@NotNull Key hook, int maxHooks) {
        this.hook = hook;
        this.maxHooks = maxHooks;
    }

    public int getMaxHooks() {
        return maxHooks;
    }

    @NotNull
    public Key getHook() {
        return hook;
    }
}
