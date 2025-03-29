package killercreepr.usurvive.core.world.module;

import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import killercreepr.usurvive.core.world.generation.OreGenerators;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class GenericOverworldModule extends SimpleWorldModule {
    public GenericOverworldModule(@NotNull CruxWorld parent) {
        super(parent);
    }

    @Override
    public void onInitiate() {
        super.onInitiate();
        World world = parent.toBukkitWorld();
        world.getPopulators().add(OreGenerators.ORBIT_ORE);
    }
}
