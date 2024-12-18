package killercreepr.usurvive.core.world;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxgeneration.util.CruxNoise;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.entity.NaturalEntityWorldSpawner;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntityWorldSpawner;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.block.USurviveBlocks;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import killercreepr.usurvive.core.world.generation.OreGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.LimitedRegion;
import org.jetbrains.annotations.NotNull;

public class USurviveNether extends SimpleWorldModule {
    //protected final NaturalEntitySpawnManager entitySpawnManager;

    public USurviveNether(@NotNull CruxWorld world) {
        super(world);
        /*entitySpawnManager = new NaturalEntitySpawnManager(this, createEntitySpawner()){
            @Override
            public boolean onSpawnNavigation() {
                USurvivePlugin.inst().log("Navigating overworld spawns");
                return super.onSpawnNavigation();
            }

            @Override
            public int maxNaturalSpawnTick() {
                return CruxMath.random(1, 4);
            }
        };*/
    }

    public NaturalEntityWorldSpawner createEntitySpawner(){
        return new SimpleNaturalEntityWorldSpawner(USurvivePlugin.inst(), parent.getRandom(),
            USurviveRegistries.OVERWORLD_NATURAL_ENTITY_SPAWN_GROUP,
            200, 34, 10);
    }


    //protected boolean active = false;

    public void tick(){
        //entitySpawnManager.tick();
    }

    public final OreGenerator SPECTRAL_SOIL = new OreGenerator(
        //NumberProvider.uniform(1, 3),
        //NumberProvider.uniform(1, 6),
        NumberProvider.constant(0),
        NumberProvider.constant(0),
        NumberProvider.constant(0),//15 clump chance

        NumberProvider.constant(50),

        CruxNoise.fast().frequency(.13f)
            .noiseType(CruxNoise.NoiseType.OpenSimplex2)
            .fractalType(CruxNoise.FractalType.FBm)
            .fractalOctaves(3),
        null,
        NumberProvider.uniform(18, 36)
    ){
        @Override
        public void place(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
            USurviveBlocks.SPECTRAL_SOIL.getBaseBlock().setBlock(limitedRegion, x, y, z);
        }

        @Override
        public boolean canSpawnClumpedOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
            return false;
        }

        @Override
        public boolean canSpawnOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
            return testBlock(limitedRegion.getBlockData(x, y, z));
        }

        private boolean testBlock(BlockData data){
            Material m = data.getMaterial();
            return m == Material.SOUL_SOIL || m == Material.SOUL_SAND;
            //return MaterialSetTag.STONE_ORE_REPLACEABLES.isTagged(m);
        }
    };

    @Override
    public void onInitiate() {
        super.onInitiate();
        World world = parent.toBukkitWorld();
        world.getPopulators().add(SPECTRAL_SOIL);
    }

    public int tickInterval(){
        return 100;
    }
}
