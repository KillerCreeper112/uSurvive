package killercreepr.usurvive.core.world.entity;

import killercreepr.crux.api.registry.Registry;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import killercreepr.cruxworlds.core.world.entity.NaturalSpawnPartGroup;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntitySpawnGroup;
import killercreepr.usurvive.core.entity.USurviveMobs;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.jetbrains.annotations.NotNull;

public class StandardOverworldGroups {
    public static final NaturalEntitySpawnGroup EMPTY = new SimpleNaturalEntitySpawnGroup(50, 0f) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            return true;
        }
    };
    public static final NaturalEntitySpawnGroup SCULK_BLIGHT = new NaturalSpawnPartGroup(10, 0f,
        StandardOverworldSpawns.SCULK_BLIGHT){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 12) return false;
            Block b = ctx.getBlock();
            if(b.getBiome() != Biome.DEEP_DARK) return false;
            return getEntityAmountNearChunk(b.getChunk(), 4) < 3 &&
                getEntityAmountNearChunk(b.getChunk(), 2, e -> CruxMob.is(e, USurviveMobs.SCULK_BLIGHT)) < 1;
        }
    };
    public static final NaturalEntitySpawnGroup STICK_BUG = new NaturalSpawnPartGroup(10, 0f,
        StandardOverworldSpawns.STICK_BUG){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(!CruxMath.testChance(6)) return false;
            Block b = ctx.getBlock();
            World world = ctx.getWorld();
            if(world.isDayTime() && CruxMath.testChance(60)) return false;
            if(b.getBiome() != Biome.FOREST && b.getBiome() != Biome.WINDSWEPT_FOREST) return false;
            if(b.getLightFromBlocks() > 0) return false;
            return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 4, e -> CruxMob.is(e, USurviveMobs.STICK_BUG)) < 6;
        }
    };

    public static void register(@NotNull Registry<NaturalEntitySpawnGroup> registry){
        registry.register(EMPTY);
        registry.register(SCULK_BLIGHT);
        registry.register(STICK_BUG);
    }
}
