package killercreepr.usurvive.core.world.generation;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxgeneration.util.CruxNoise;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class OreGenerator extends BlockPopulator {
    public static final BlockFace[] FACES = new BlockFace[]{
        BlockFace.UP,
        BlockFace.DOWN,
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.WEST,
        BlockFace.SOUTH
    };

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

    public NumberProvider generateBetweenYValues(WorldInfo worldInfo){
        return NumberProvider.uniform(worldInfo.getMinHeight(), worldInfo.getMaxHeight()-1);
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
                    if(CruxMath.random(1, 100) <= 40) continue;
                    float noise = oreSpawnNoise.noise(xx, zz);
                    if(noise < .6) continue;
                }
                NumberProvider betweenY = betweenYValues == null ? generateBetweenYValues(worldInfo) : betweenYValues.value();
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

    public static boolean pass(@NotNull Block b){
        return b.isEmpty() || b.isPassable() || b.isReplaceable();
    }

    public boolean canPlaceOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        return hasSolidNear(limitedRegion, x, y, z);
    }

    public boolean isValidSolid(Block b){
        return b.isSolid() && !b.isLiquid();
    }

    public boolean hasSolidNear(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        for(BlockFace face : FACES){
            int xx = x + face.getModX();
            int yy = y + face.getModY();
            int zz = z + face.getModZ();
            if(!limitedRegion.isInRegion(xx, yy, zz)) continue;
            var state = limitedRegion.getBlockState(xx, yy, zz);
            var b = state.getBlock();
            if(isValidSolid(b)) return true;
        }
        return false;
    }

    public boolean canSpawnClumpedOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        return canSpawnOre(limitedRegion, x, y, z);
    }
    public abstract boolean canSpawnOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z);
    public abstract void place(@NotNull LimitedRegion limitedRegion, int x, int y, int z);

    public void generateClump(LimitedRegion region, Random random, int centerX, int centerY, int centerZ) {
        int density = clumpDensity.value().intValue(); // Number of blobs/nodes in the clump
        float blobRadius = clumpSize.value().floatValue(); // Radius per blob

        double directionAngle = random.nextDouble() * Math.PI * 2;
        double branchAngleVariance = Math.toRadians(15 + random.nextInt(30));
        double verticalVariance = 1.5 + random.nextDouble() * 1.5;

        /*double dx = Math.cos(directionAngle);
        double dz = Math.sin(directionAngle);*/

        for (int i = 0; i < density; i++) {
            double branchAngle = directionAngle + (random.nextDouble() - 0.5) * branchAngleVariance;
            double offset = i * 0.75;
            double x = centerX + Math.cos(branchAngle) * offset;
            double z = centerZ + Math.sin(branchAngle) * offset;
            double y = centerY + (random.nextDouble() - 0.5) * verticalVariance * i;

            float radius = (float) (blobRadius * (0.6 + random.nextDouble() * 0.5));
            placeBlob(region, random, x, y, z, radius);
        }
    }


    public void placeBlob(LimitedRegion region, Random random, double cx, double cy, double cz, float radius) {
        int minX = (int)(cx - radius);
        int maxX = (int)(cx + radius);
        int minY = (int)(cy - radius);
        int maxY = (int)(cy + radius);
        int minZ = (int)(cz - radius);
        int maxZ = (int)(cz + radius);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (!region.isInRegion(x, y, z)) continue;
                    if (!canSpawnClumpedOre(region, x, y, z)) continue;

                    double dx = (x + 0.5 - cx) / radius;
                    double dy = (y + 0.5 - cy) / radius;
                    double dz = (z + 0.5 - cz) / radius;
                    double distSq = dx * dx + dy * dy + dz * dz;

                    if (distSq <= 1.0 && random.nextDouble() < (1.0 - distSq)) {
                        place(region, x, y, z); // More likely toward center
                    }
                }
            }
        }
    }

    public void generateVeinPath(LimitedRegion region, Random random, int startX, int startY, int startZ) {
        int steps = clumpDensity.value().intValue() * 2 + random.nextInt(5); // Total path length
        double angle = random.nextDouble() * Math.PI * 2;
        double verticalBias = random.nextDouble() * 0.5;

        double x = startX;
        double y = startY;
        double z = startZ;

        for (int i = 0; i < steps; i++) {
            float radius = clumpSize.value().floatValue() * (0.6f + random.nextFloat() * 0.4f);
            placeBlob(region, random, x, y, z, radius);

            // Slight curve on each step
            angle += (random.nextDouble() - 0.5) * 0.4;

            // Step forward with slight y-variance
            x += Math.cos(angle) * 1.2;
            z += Math.sin(angle) * 1.2;
            y += (random.nextDouble() - 0.5 + verticalBias) * 0.7;

            // Optional: Rare branching (10% chance)
            if (random.nextDouble() < 0.1 && i < steps - 4) {
                int forkSteps = 2 + random.nextInt(3);
                double forkAngle = angle + (random.nextDouble() - 0.5) * Math.PI / 2;
                generateBranch(region, random, x, y, z, forkAngle, forkSteps);
            }
        }
    }

    private void generateBranch(LimitedRegion region, Random random, double x, double y, double z, double angle, int steps) {
        for (int i = 0; i < steps; i++) {
            float radius = clumpSize.value().floatValue() * (0.5f + random.nextFloat() * 0.3f);
            placeBlob(region, random, x, y, z, radius);

            x += Math.cos(angle) * 1.1;
            z += Math.sin(angle) * 1.1;
            y += (random.nextDouble() - 0.5) * 0.6;
        }
    }



    /*private void generateClump(LimitedRegion region, Random random, int centerX, int centerY, int centerZ) {
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
    }*/

    private boolean isWithinClump(int centerX, int centerY, int centerZ, int x, int y, int z, int radius) {
        return Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2)) <= radius;
    }
}
