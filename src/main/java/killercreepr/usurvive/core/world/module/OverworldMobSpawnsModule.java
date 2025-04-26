package killercreepr.usurvive.core.world.module;

import killercreepr.crux.api.data.tick.Ticked;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.entity.NaturalEntityWorldSpawner;
import killercreepr.cruxworlds.core.world.NaturalEntitySpawnManager;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntityWorldSpawner;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import killercreepr.usurvive.core.world.generation.OreGenerators;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class OverworldMobSpawnsModule extends SimpleWorldModule implements Ticked {
    protected final NaturalEntitySpawnManager entitySpawnManager;

    public OverworldMobSpawnsModule(@NotNull CruxWorld world) {
        super(world);
        entitySpawnManager = new NaturalEntitySpawnManager(world, createEntitySpawner()){
            @Override
            public boolean onSpawnNavigation() {
                return super.onSpawnNavigation();
            }

            @Override
            public int maxNaturalSpawnTick() {
                return CruxMath.random(100, 400);
            }
        };
    }

    public NaturalEntityWorldSpawner createEntitySpawner(){
        return new SimpleNaturalEntityWorldSpawner(USurvivePlugin.inst(), parent.getRandom(),
            USurviveRegistries.OVERWORLD_NATURAL_ENTITY_SPAWN_GROUP,
            200, 34, 10);
    }


    @Override
    public void tick(){
        entitySpawnManager.tick();
    }
}
