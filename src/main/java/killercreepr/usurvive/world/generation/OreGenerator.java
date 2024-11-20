package killercreepr.usurvive.world.generation;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxgeneration.util.CruxNoise;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class OreGenerator extends BlockPopulator {
    protected final @NotNull Holder<Number> clumpSize;
    protected final @NotNull Holder<Number> clumpDensity;
    protected final @NotNull Holder<Number> clumpChance;
    protected final @NotNull Holder<Number> oreSpawnChance;
    protected final @NotNull CruxNoise oreSpawnNoise;

    protected final @Nullable Holder<NumberProvider> betweenYValues;

    protected final @NotNull Holder<Number> maxSpawnTries;

    public OreGenerator(@NotNull Holder<Number> clumpSize,
                        @NotNull Holder<Number> clumpDensity,
                        @NotNull Holder<Number> clumpChance,
                        @NotNull Holder<Number> oreSpawnChance,
                        @NotNull CruxNoise oreSpawnNoise,
                        @Nullable Holder<NumberProvider> betweenYValues, @NotNull Holder<Number> maxSpawnTries) {
        this.clumpSize = clumpSize;
        this.clumpDensity = clumpDensity;
        this.clumpChance = clumpChance;
        this.oreSpawnChance = oreSpawnChance;
        this.oreSpawnNoise = oreSpawnNoise;
        this.betweenYValues = betweenYValues;
        this.maxSpawnTries = maxSpawnTries;
    }

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if(!CruxMath.testChance(random, oreSpawnChance.value().doubleValue())) return;

        int spawned = 0;
        int max = CruxMath.random(1, 6);
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                int xx = x + (chunkX * 16);
                int zz = z + (chunkZ * 16);
                if(spawned > 0){
                    if(CruxMath.random(1, 100) <= 70) continue;
                    float noise = oreSpawnNoise.noise(xx, zz);
                    if(noise < .6) continue;
                }
                NumberProvider betweenY = betweenYValues == null ? NumberProvider.uniform(worldInfo.getMinHeight(), worldInfo.getMaxHeight()-1) : betweenYValues.value();
                int tries = maxSpawnTries.value().intValue();
                while(tries > 0){
                    tries--;
                    int y = betweenY.value().intValue();
                    if(!canSpawnOre(limitedRegion, xx, y, zz)) continue;

                    if(CruxMath.testChance(random, clumpChance.value().doubleValue())){
                        generateClump(limitedRegion, random, xx, y+CruxMath.random(0,1), zz);
                        break;
                    }
                    place(limitedRegion, xx, y, zz);

                    spawned++;
                    if(spawned >= max) return;
                    break;
                }
                /*for(int y = maxY; y > minY; y--){
                    if(!canSpawnOre(limitedRegion, xx, y, zz)) continue;

                    if(CruxMath.testChance(random, clumpChance.value().doubleValue())){
                        generateClump(limitedRegion, random, xx, y+CruxMath.random(0,1), zz);
                        break;
                    }
                    place(limitedRegion, xx, y, zz);

                    spawned++;
                    if(spawned >= max) return;
                    break;
                }*/
            }
        }
    }

    public static  boolean pass(@NotNull Block b){
        return b.isEmpty() || b.isPassable() || b.isReplaceable();
    }

    public boolean canSpawnClumpedOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        return canSpawnOre(limitedRegion, x, y, z);
    }
    public abstract boolean canSpawnOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z);
    public abstract void place(@NotNull LimitedRegion limitedRegion, int x, int y, int z);

    private void generateClump(LimitedRegion region, Random random, int centerX, int centerY, int centerZ) {
        int radius = clumpSize.value().intValue(); // Radius of the clump
        int density = clumpDensity.value().intValue(); // Number of blocks to place in the clump

        for (int i = 0; i < density; i++) {
            int x = centerX + random.nextInt(radius * 2) - radius;
            int y = centerY + random.nextInt(radius * 2) - radius;
            int z = centerZ + random.nextInt(radius * 2) - radius;

            if(!region.isInRegion(x, y, z)) continue;
            if(!isWithinClump(centerX, centerY, centerZ, x, y, z, radius)) continue;
            if(!canSpawnClumpedOre(region, x, y, z)) continue;
            place(region, x, y, z);
        }
    }

    private boolean isWithinClump(int centerX, int centerY, int centerZ, int x, int y, int z, int radius) {
        return Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2)) <= radius;
    }
}
