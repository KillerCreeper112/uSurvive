package killercreepr.usurvive.mechanics.grapple.standard;

import killercreepr.crux.Crux;
import killercreepr.crux.registry.KeyedRegistry;
import killercreepr.usurvive.mechanics.grapple.GrappleHook;
import killercreepr.usurvive.mechanics.grapple.impl.PufferFishGrappleHook;
import org.jetbrains.annotations.NotNull;

public class StandardGrappleHooks {
    public static PufferFishGrappleHook PUFFERFISH = new PufferFishGrappleHook(Crux.key("pufferfish"));
    public static void register(@NotNull KeyedRegistry<GrappleHook> registry){
        registry.register(PUFFERFISH);
    }
}
