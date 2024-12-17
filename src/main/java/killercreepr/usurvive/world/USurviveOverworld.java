package killercreepr.usurvive.world;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.api.data.tick.Ticked;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxgeneration.util.CruxNoise;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.entity.NaturalEntityWorldSpawner;
import killercreepr.cruxworlds.core.world.NaturalEntitySpawnManager;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntityWorldSpawner;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import killercreepr.usurvive.USurvivePlugin;
import killercreepr.usurvive.block.USurviveBlocks;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import killercreepr.usurvive.world.generation.OreGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.LimitedRegion;
import org.jetbrains.annotations.NotNull;

public class USurviveOverworld extends SimpleWorldModule implements Ticked {
    protected final NaturalEntitySpawnManager entitySpawnManager;

    public USurviveOverworld(@NotNull CruxWorld world) {
        super(world);
        entitySpawnManager = new NaturalEntitySpawnManager(world, createEntitySpawner()){
            @Override
            public boolean onSpawnNavigation() {
                USurvivePlugin.inst().log("Navigating overworld spawns");
                return super.onSpawnNavigation();
            }

            @Override
            public int maxNaturalSpawnTick() {
                return CruxMath.random(1, 4);
            }
        };
    }

    public NaturalEntityWorldSpawner createEntitySpawner(){
        return new SimpleNaturalEntityWorldSpawner(USurvivePlugin.inst(), parent.getRandom(),
            USurviveRegistries.OVERWORLD_NATURAL_ENTITY_SPAWN_GROUP,
            200, 34, 10);
    }


    //protected boolean active = false;

    /*public BukkitRunnable buildRunnable(){
        return new BukkitRunnable() {
            @Override
            public void run() {
                if(!isActive()){
                    cancel();
                    return;
                }
                tick();
            }
        };
    }*/

    /*public boolean isActive(){
        return active;
    }*/

    @Override
    public void tick(){
        entitySpawnManager.tick();
    }

    public final OreGenerator ORBIT_ORE = new OreGenerator(
        NumberProvider.uniform(1, 3), NumberProvider.uniform(1, 6),
        NumberProvider.constant(15), NumberProvider.constant(50),

        CruxNoise.fast().frequency(.09f)
            .noiseType(CruxNoise.NoiseType.OpenSimplex2)
            .fractalType(CruxNoise.FractalType.FBm)
            .fractalOctaves(3),
        null,
        NumberProvider.uniform(16, 32)
    ){
        @Override
        public void place(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
            USurviveBlocks.ORBIT_ORE.getBaseBlock().setBlock(limitedRegion, x, y, z);
        }

        @Override
        public boolean canSpawnClumpedOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
            return true;
        }

        @Override
        public boolean canSpawnOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
            for(int xAddon = -1; xAddon < 2; xAddon++){
                for(int zAddon = -1; zAddon < 2; zAddon++){
                    for(int yAddon = -1; yAddon < 2; yAddon++){
                        if(xAddon == 0 && yAddon == 0 && zAddon == 0) continue;
                        if(!limitedRegion.isInRegion(x+xAddon, y+yAddon, z+zAddon)) continue;
                        if(!testBlock(limitedRegion.getBlockData(x+xAddon, y+yAddon, z+zAddon))) return false;
                    }
                }
            }
            return true;
        }

        private boolean testBlock(BlockData data){
            Material m = data.getMaterial();
            return MaterialSetTag.STONE_ORE_REPLACEABLES.isTagged(m);
        }
    };

    @Override
    public void onInitiate() {
        super.onInitiate();
        World world = parent.toBukkitWorld();
        world.getPopulators().add(ORBIT_ORE);
    }

    public int tickInterval(){
        return 100;
    }
}
