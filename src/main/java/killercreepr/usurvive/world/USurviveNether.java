package killercreepr.usurvive.world;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxgeneration.util.CruxNoise;
import killercreepr.cruxworlds.world.NaturalEntitySpawnManager;
import killercreepr.cruxworlds.world.SimpleWorld;
import killercreepr.cruxworlds.world.creator.CruxWorldModuleCreator;
import killercreepr.cruxworlds.world.entity.NaturalEntityWorldSpawner;
import killercreepr.cruxworlds.world.entity.impl.SimpleNaturalEntityWorldSpawner;
import killercreepr.usurvive.USurvivePlugin;
import killercreepr.usurvive.block.USurviveBlocks;
import killercreepr.usurvive.registries.USurviveRegistries;
import killercreepr.usurvive.world.generation.OreGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Random;

public class USurviveNether extends SimpleWorld {
    //protected final NaturalEntitySpawnManager entitySpawnManager;

    public USurviveNether(@NotNull World world, @NotNull Collection<CruxWorldModuleCreator> modules) {
        this(world, new Random(world.getSeed()), modules);
    }

    public USurviveNether(@NotNull World world, @NotNull Random random, @NotNull Collection<CruxWorldModuleCreator> modules) {
        super(world, random, modules);
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
        return new SimpleNaturalEntityWorldSpawner(USurvivePlugin.inst(), random,
            USurviveRegistries.OVERWORLD_NATURAL_ENTITY_SPAWN_GROUP,
            200, 34, 10);
    }


    protected boolean active = false;

    public BukkitRunnable buildRunnable(){
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
    }

    public boolean isActive(){
        return active;
    }

    public void tick(){
        //entitySpawnManager.tick();
    }

    public final OreGenerator SPECTRAL_SOIL = new OreGenerator(
        NumberProvider.uniform(1, 3), NumberProvider.uniform(1, 6),
        NumberProvider.constant(15),

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
            return m == Material.SOUL_SOIL || m == Material.SOUL_SAND;
            //return MaterialSetTag.STONE_ORE_REPLACEABLES.isTagged(m);
        }
    };

    @Override
    public void onInitiate() {
        super.onInitiate();
        world.getPopulators().add(SPECTRAL_SOIL);
    }

    @Override
    public void onLoad() {
        active = true;
        //buildRunnable().runTaskTimerAsynchronously(USurvivePlugin.inst(), tickInterval(), tickInterval());
    }

    public int tickInterval(){
        return 100;
    }

    @Override
    public void onUnload() {
        active = false;
    }
}
