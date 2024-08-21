package killercreepr.usurvive.mechanics.grapple.standard;

import killercreepr.crux.Crux;
import killercreepr.crux.registry.KeyedRegistry;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.usurvive.mechanics.grapple.GrappleHook;
import killercreepr.usurvive.mechanics.grapple.impl.PufferFishGrappleHook;
import org.jetbrains.annotations.NotNull;

public class StandardGrappleHooks {
    public static PufferFishGrappleHook PUFFERFISH = new PufferFishGrappleHook(
        Crux.key("pufferfish"),
        NumberProvider.constant(2.25D),
        NumberProvider.constant(.02D)
    );
    public static void register(@NotNull KeyedRegistry<GrappleHook> registry){
        registry.register(PUFFERFISH);
    }
}
