package killercreepr.usurvive.core.world.generation;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.api.block.tag.BlockTag;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxgeneration.util.CruxNoise;
import killercreepr.usurvive.core.block.USurviveBlocks;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.generator.LimitedRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class OrbitOreGenerator extends OreGenerator{
    public OrbitOreGenerator(@NotNull Holder<Number> clumpSize, @NotNull Holder<Number> clumpDensity, @NotNull Holder<Number> clumpChance, @NotNull Holder<Number> oreSpawnChance, @NotNull CruxNoise oreSpawnNoise, @Nullable Holder<NumberProvider> betweenYValues, @NotNull Holder<Number> maxSpawnTries) {
        super(clumpSize, clumpDensity, clumpChance, oreSpawnChance, oreSpawnNoise, betweenYValues, maxSpawnTries);
    }

    @Override
    public void place(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        USurviveBlocks.ORBIT_ORE.getBaseBlock().setBlock(limitedRegion, x, y, z);
    }

    @Override
    public boolean canSpawnClumpedOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        return hasSolidNear(limitedRegion, x,y,z);
    }

    public boolean canSpawnOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        BlockState b = limitedRegion.getBlockState(x, y, z);
        if(!testBlock(b)) return false;

        int airAdjacent = 0;
        for (int xAddon = -1; xAddon <= 1; xAddon++) {
            for (int yAddon = -1; yAddon <= 1; yAddon++) {
                for (int zAddon = -1; zAddon <= 1; zAddon++) {
                    if (xAddon == 0 && yAddon == 0 && zAddon == 0) continue;
                    if (!limitedRegion.isInRegion(x + xAddon, y + yAddon, z + zAddon)) continue;
                    var block = limitedRegion.getBlockState(x + xAddon, y + yAddon, z + zAddon).getBlock();
                    if (block.isEmpty() || block.isLiquid() || block.isPassable()) {
                        airAdjacent++;
                    }
                }
            }
        }
        return airAdjacent >= 1 || (CruxMath.testChance(25) && canSpawnOreNonAir(limitedRegion, x, y, z)); // or >= 2 for stronger air bias
    }

    @Override
    public void generateClump(LimitedRegion region, Random random, int centerX, int centerY, int centerZ) {
        super.generateClump(region, random, centerX, centerY, centerZ);
    }

    public boolean canSpawnOreNonAir(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        //if(!canPlaceOre(limitedRegion, x, y, z)) return false;
        //if(!testBlock(limitedRegion.getBlockData(x, y, z))) return false;
        int amount = 0;
        int max = CruxMath.random(4, 17);
        for(int xAddon = -1; xAddon < 2; xAddon++){
            for(int zAddon = -1; zAddon < 2; zAddon++){
                for(int yAddon = -1; yAddon < 2; yAddon++){
                    if(xAddon == 0 && yAddon == 0 && zAddon == 0) continue;
                    if(!limitedRegion.isInRegion(x+xAddon, y+yAddon, z+zAddon)) continue;
                    if(!testBlock(limitedRegion.getBlockState(x+xAddon, y+yAddon, z+zAddon))) continue;
                    amount++;
                    if(amount >= max) return true;
                }
            }
        }
        return false;
    }

    private boolean testBlock(BlockState data){
        Material m = data.getType();
        if(m == Material.WATER || m == Material.LAVA) return false;
        return MaterialSetTag.STONE_ORE_REPLACEABLES.isTagged(m) ||
            MaterialSetTag.DEEPSLATE_ORE_REPLACEABLES.isTagged(m) || isValidReplaceableBlock(data);
    }

    public BlockTag blockTag(){
        return CruxRegistries.BLOCK_TAG.get(Crux.key("generation/orbit_replaceable"));
    }

    public boolean isValidReplaceableBlock(BlockState data){
        var cruxed = Crux.handlers().block().getByBlockState(data);
        Key m = cruxed.getType();
        if(m.equals(Material.NETHERRACK.key())) return true;
        if(m.equals(Material.BASALT.key())) return true;
        if(m.equals(Material.BLACKSTONE.key())) return true;
        BlockTag tag = blockTag();
        if(tag != null && tag.isTagged(cruxed)) return true;
        return false;
    }
}
