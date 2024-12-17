package killercreepr.usurvive.world.entity;

import killercreepr.crux.api.registry.Registry;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import killercreepr.cruxworlds.core.world.entity.NaturalSpawnPartGroup;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntitySpawnGroup;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
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
            return getEntityAmountNearChunk(b.getChunk(), 6) < 3;
        }
    };

    public static void register(@NotNull Registry<NaturalEntitySpawnGroup> registry){
        registry.register(EMPTY);
        registry.register(SCULK_BLIGHT);
    }
}
