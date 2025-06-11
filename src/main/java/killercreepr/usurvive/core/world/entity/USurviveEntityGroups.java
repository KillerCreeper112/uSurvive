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
import killercreepr.usurvive.core.world.BiomeUtil;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class USurviveEntityGroups {
    public static final NaturalEntitySpawnGroup EMPTY = new SimpleNaturalEntitySpawnGroup(80, 0f) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            return true;
        }
    };
    public static final NaturalEntitySpawnGroup SCULK_BLIGHT = new NaturalSpawnPartGroup(10, 0f,
        USurviveEntitySpawns.SCULK_BLIGHT){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 12) return false;
            Block b = ctx.getBlock();
            if(b.getBiome() != Biome.DEEP_DARK) return false;
            return getEntityAmountNearChunk(b.getChunk(), 4) < 3 &&
                getEntityAmountNearChunk(b.getChunk(), 5, e -> CruxMob.is(e, USurviveMobs.SCULK_BLIGHT)) < 1;
        }
    };
    public static final NaturalEntitySpawnGroup STICK_BUG = new NaturalSpawnPartGroup(10, 0f,
        USurviveEntitySpawns.STICK_BUG){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(!CruxMath.testChance(6)) return false;
            Block b = ctx.getBlock();
            World world = ctx.getWorld();
            if(world.isDayTime() && CruxMath.testChance(60)) return false;
            if(b.getBiome() != Biome.FOREST && b.getBiome() != Biome.WINDSWEPT_FOREST) return false;
            if(b.getLightFromBlocks() > 0) return false;
            return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 7, e -> CruxMob.is(e, USurviveMobs.STICK_BUG)) < 6;
        }
    };
    public static final NaturalEntitySpawnGroup ELDRITCH_WASTES_HOSTILE_NIGHT = new NaturalSpawnPartGroup(6, 0f,
        USurviveEntitySpawns.VANE, USurviveEntitySpawns.STARE){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(!CruxMath.testChance(5)) return false;
            Block b = ctx.getBlock();
            if(!BiomeUtil.isEldritchWastes(b.getBiome().key())) return false;
            World world = ctx.getWorld();
            if(world.isDayTime()) return false;
            if(b.getLightFromBlocks() > 0) return false;
            return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e)) < 6;
        }
    };
    public static final NaturalEntitySpawnGroup ELDRITCH_WASTES_HOSTILE = new NaturalSpawnPartGroup(10, 0f,
        USurviveEntitySpawns.SPETTLE, USurviveEntitySpawns.SKELETON_WOLF, USurviveEntitySpawns.SKELETON_CHICKEN,
        USurviveEntitySpawns.SHADE_STAG, USurviveEntitySpawns.LURKSCALE, USurviveEntitySpawns.VOID_LURKER,
        USurviveEntitySpawns.WHISPERSHADE){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.testChance(25)) return false;
            Block b = ctx.getBlock();
            if(!BiomeUtil.isEldritchWastes(b.getBiome().key())) return false;
            if(b.getLightFromBlocks() > 0) return false;
            return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e)) < 15;
        }
    };
    public static final NaturalEntitySpawnGroup ELDRITCH_WASTES_PASSIVE = new NaturalSpawnPartGroup(6, 0f,
        USurviveEntitySpawns.WHIMLET){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.testChance(40)) return false;
            Block b = ctx.getBlock();
            if(!BiomeUtil.isEldritchWastes(b.getBiome().key())) return false;
            if(b.getLightFromBlocks() > 0) return false;
            return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e)) < 6;
        }
    };

    public static void register(@NotNull Registry<NaturalEntitySpawnGroup> registry){
        registry.register(EMPTY);
        registry.register(SCULK_BLIGHT);
        registry.register(STICK_BUG);
        registry.register(ELDRITCH_WASTES_HOSTILE_NIGHT);
        registry.register(ELDRITCH_WASTES_HOSTILE);
        registry.register(ELDRITCH_WASTES_PASSIVE);
    }
}
