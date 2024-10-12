package killercreepr.usurvive.registries;

import killercreepr.crux.registry.KeyedRegistry;
import killercreepr.crux.registry.Registry;
import killercreepr.crux.registry.SimpleKeyedRegistry;
import killercreepr.crux.registry.SimpleRegistry;
import killercreepr.cruxworlds.world.entity.NaturalEntitySpawnGroup;
import killercreepr.usurvive.mechanics.grapple.GrappleHook;

public class USurviveRegistries {
    public static final KeyedRegistry<GrappleHook> GRAPPLE_HOOK = new SimpleKeyedRegistry<>();
    public static final Registry<NaturalEntitySpawnGroup> OVERWORLD_NATURAL_ENTITY_SPAWN_GROUP = SimpleRegistry.fromSet();
}
