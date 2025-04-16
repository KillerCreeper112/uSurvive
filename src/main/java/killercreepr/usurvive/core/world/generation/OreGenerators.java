package killercreepr.usurvive.core.world.generation;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxgeneration.util.CruxNoise;
import killercreepr.usurvive.core.block.USurviveBlocks;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.LimitedRegion;
import org.jetbrains.annotations.NotNull;

public class OreGenerators {
    public static final OreGenerator ORBIT_ORE = new OreGenerator(
        NumberProvider.uniform(1, 3), NumberProvider.uniform(1, 6),
        NumberProvider.constant(30), NumberProvider.constant(45), //was 50%

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
            //if(!testBlock(limitedRegion.getBlockData(x, y, z))) return false;
            int amount = 0;
            int max = CruxMath.random(7, 20);
            for(int xAddon = -1; xAddon < 2; xAddon++){
                for(int zAddon = -1; zAddon < 2; zAddon++){
                    for(int yAddon = -1; yAddon < 2; yAddon++){
                        if(xAddon == 0 && yAddon == 0 && zAddon == 0) continue;
                        if(!limitedRegion.isInRegion(x+xAddon, y+yAddon, z+zAddon)) continue;
                        if(!testBlock(limitedRegion.getBlockData(x+xAddon, y+yAddon, z+zAddon))) continue;
                        amount++;
                        if(amount >= max) return true;
                    }
                }
            }
            return false;
        }

        private boolean testBlock(BlockData data){
            Material m = data.getMaterial();
            return MaterialSetTag.STONE_ORE_REPLACEABLES.isTagged(m) ||
                MaterialSetTag.DEEPSLATE_ORE_REPLACEABLES.isTagged(m);
        }
    };
    /*public static final OreGenerator ORBIT_ORE = new OreGenerator(
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
        public boolean canSpawnOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
            if(!testBlock(limitedRegion.getBlockData(x, y, z))) return false;
            int amount = 0;
            int max = CruxMath.random(10, 27);
            for(int xAddon = -1; xAddon < 2; xAddon++){
                for(int zAddon = -1; zAddon < 2; zAddon++){
                    for(int yAddon = -1; yAddon < 2; yAddon++){
                        if(xAddon == 0 && yAddon == 0 && zAddon == 0) continue;
                        if(!limitedRegion.isInRegion(x+xAddon, y+yAddon, z+zAddon)) continue;
                        if(!testBlock(limitedRegion.getBlockData(x+xAddon, y+yAddon, z+zAddon))) continue;
                        amount++;
                        if(amount >= max) return true;
                    }
                }
            }
            return false;
        }

        private boolean testBlock(BlockData data){
            Material m = data.getMaterial();
            return MaterialSetTag.STONE_ORE_REPLACEABLES.isTagged(m);
        }
    };*/
}
