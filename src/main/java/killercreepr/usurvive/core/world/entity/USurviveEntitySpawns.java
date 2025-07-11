package killercreepr.usurvive.core.world.entity;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.core.data.util.CollectionBuilder;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.world.entity.NaturalCruxMobSpawn;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawn;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntitySpawn;
import killercreepr.usurvive.core.entity.USurviveMobs;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class USurviveEntitySpawns {
    public static final Collection<Material> SPAWNABLE_ON_NOT = new CollectionBuilder<Material>()
        .apply(builder ->{
            builder.addAll(MaterialSetTag.LEAVES.getValues());
            builder.addAll(MaterialSetTag.LOGS.getValues());
            builder.addAll(MaterialSetTag.WART_BLOCKS.getValues());
            builder.addAll(MaterialSetTag.WARPED_STEMS.getValues());
            builder.addAll(MaterialSetTag.CRIMSON_STEMS.getValues());
        })
        .build();

    public static final Map<Integer, NaturalEntitySpawn> EMPTY = new HashMap<>();
    public static NaturalEntitySpawn empty(int weight){
        return EMPTY.computeIfAbsent(weight, (e) -> NaturalEntitySpawn.empty(weight));
    }

    public static final NaturalEntitySpawn SCULK_BLIGHT = new NaturalCruxMobSpawn(4, 0, USurviveMobs.SCULK_BLIGHT) {
        public final Collection<Material> SPAWN_INSIDE = new CollectionBuilder<Material>()
            .add(Material.SCULK)
            .add(Material.DEEPSLATE)
            .add(Material.TUFF)
            .add(Material.GRAVEL)
            .build();
        public final Collection<Material> MUST_BE_ABOVE = new CollectionBuilder<Material>()
            .add(Material.SCULK)
            .build();
        public final int CHECK_ABOVE_MAX = 5;

        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();
            if(!SPAWN_INSIDE.contains(b.getType())) return false;

            //must have solid blocks all around the spawn
            for(Block loc : CruxLoc.getAdjBlocks(b, false, true)){
                if(!SPAWN_INSIDE.contains(loc.getType())) return false;
            }

            //must have a sculk block at least 5 blocks above the spawn
            boolean foundAbove = false;
            for(int i = 1; i <= CHECK_ABOVE_MAX; i++){
                Block check = b.getRelative(0, i, 0);
                if(MUST_BE_ABOVE.contains(check.getType())){
                    foundAbove = true;
                    break;
                }
            }
            return foundAbove;
        }
    };
    public static final NaturalEntitySpawn STICK_BUG = new NaturalCruxMobSpawn(4, 0, USurviveMobs.STICK_BUG) {
        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();

            if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                Block down = b.getRelative(BlockFace.DOWN);
                if(!down.isSolid() || SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                for(BlockFace f : BlockFace.values()){
                    if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                    if(!down.getRelative(f).isSolid()) return false;
                }
                return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 6;
            }
            return false;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return CruxMath.random(1, 3);
        }
    };
    public static final NaturalEntitySpawn VANE = new NaturalCruxMobSpawn(2, 0, USurviveMobs.VANE) {
        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 4) return false;
            Block b = ctx.getBlock();
            if(!b.isEmpty()) return false;
            for(int x = 0; x < 4; x++){
                for(int z = 0;z < 4; z++){
                    for(int y = 0;y < 4; y++){
                        Block check = b.getRelative(x, y, z);
                        if(!check.isEmpty()) return false;
                    }
                }
            }
            boolean solid = false;
            for(int y = 1; y < 24; y++){
                Block check = b.getRelative(0, -y, 0);
                if(check.isSolid()){
                    solid = true;
                    break;
                }
            }
            if(!solid) return false;
            return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 8, e -> CruxMob.is(e, mob, USurviveMobs.STARE)) < 2;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return 1;
        }
    };
    public static final NaturalEntitySpawn STARE = new NaturalCruxMobSpawn(2, 0, USurviveMobs.STARE) {
        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 4) return false;
            Block b = ctx.getBlock();
            if(!b.isEmpty()) return false;
            for(int x = 0; x < 4; x++){
                for(int z = 0;z < 4; z++){
                    for(int y = 0;y < 4; y++){
                        Block check = b.getRelative(x, y, z);
                        if(!check.isEmpty()) return false;
                    }
                }
            }
            boolean solid = false;
            for(int y = 1; y < 24; y++){
                Block check = b.getRelative(0, -y, 0);
                if(check.isSolid()){
                    solid = true;
                    break;
                }
            }
            if(!solid) return false;
            return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 8, e -> CruxMob.is(e, mob, USurviveMobs.VANE)) < 2;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return 1;
        }
    };
    public static final NaturalEntitySpawn WHISPERSHADE = new NaturalCruxMobSpawn(4, 0, USurviveMobs.WHISPERSHADE) {
        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 4) return false;
            Block b = ctx.getBlock();
            if(!b.isEmpty()) return false;
            for(int x = 0; x < 4; x++){
                for(int z = 0;z < 4; z++){
                    for(int y = 0;y < 4; y++){
                        Block check = b.getRelative(x, y, z);
                        if(!check.isEmpty()) return false;
                    }
                }
            }
            boolean solid = false;
            for(int y = 1; y < 16; y++){
                Block check = b.getRelative(0, -y, 0);
                if(check.isSolid()){
                    solid = true;
                    break;
                }
            }
            if(!solid) return false;
            return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 8, e -> CruxMob.is(e, mob)) < 2;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return 1;
        }
    };
    public static final NaturalEntitySpawn SKELETON_WOLF = new NaturalCruxMobSpawn(20, 0, USurviveMobs.SKELETON_WOLF) {
        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();

            if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                Block down = b.getRelative(BlockFace.DOWN);
                if(!down.isSolid() || SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                for(BlockFace f : BlockFace.values()){
                    if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                    if(!down.getRelative(f).isSolid()) return false;
                }
                return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 7, e -> CruxMob.is(e, mob)) < 6;
            }
            return false;
            //return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 6;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return CruxMath.random(1, 3);
        }
    };
    public static final NaturalEntitySpawn SKELETON_CHICKEN = new NaturalCruxMobSpawn(30, 0, USurviveMobs.SKELETON_CHICKEN) {
        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();

            if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                Block down = b.getRelative(BlockFace.DOWN);
                if(!down.isSolid() || SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                for(BlockFace f : BlockFace.values()){
                    if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                    if(!down.getRelative(f).isSolid()) return false;
                }
                return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 7, e -> CruxMob.is(e, mob)) < 5;
            }
            return false;
            //return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 6;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return CruxMath.random(1, 2);
        }
    };
    public static final NaturalEntitySpawn SHADE_STAG = new NaturalCruxMobSpawn(7, 0, USurviveMobs.SHADE_STAG) {
        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();

            if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                Block down = b.getRelative(BlockFace.DOWN);
                if(!down.isSolid() || SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                for(BlockFace f : BlockFace.values()){
                    if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                    if(!down.getRelative(f).isSolid()) return false;
                }
                return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 7, e -> CruxMob.is(e, mob)) < 3;
            }
            return false;
            //return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 6;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return CruxMath.random(1, 2);
        }
    };
    public static final NaturalEntitySpawn SPETTLE = new NaturalCruxMobSpawn(15, 0, USurviveMobs.SPETTLE) {
        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();

            if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                Block down = b.getRelative(BlockFace.DOWN);
                if(!down.isSolid() || SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                for(BlockFace f : BlockFace.values()){
                    if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                    if(!down.getRelative(f).isSolid()) return false;
                }
                return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 7, e -> CruxMob.is(e, mob)) < 4;
            }
            return false;
            //return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 6;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return 1;
        }
    };
    public static final NaturalEntitySpawn VOID_LURKER = new NaturalCruxMobSpawn(9, 0, USurviveMobs.VOID_LURKER) {
        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();

            if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                Block down = b.getRelative(BlockFace.DOWN);
                if(!down.isSolid() || SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                for(BlockFace f : BlockFace.values()){
                    if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                    if(!down.getRelative(f).isSolid()) return false;
                }
                return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 2;
            }
            return false;
            //return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 6;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return 1;
        }
    };
    public static final NaturalEntitySpawn WHIMLET = new NaturalCruxMobSpawn(2, 0, USurviveMobs.WHIMLET) {
        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(!CruxMath.testChance(10)) return false;
            Block b = ctx.getBlock();

            if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                Block down = b.getRelative(BlockFace.DOWN);
                if(!down.isSolid() || SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                for(BlockFace f : BlockFace.values()){
                    if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                    if(!down.getRelative(f).isSolid()) return false;
                }
                return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 8, e -> CruxMob.is(e, mob)) < 2;
            }
            return false;
            //return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 6;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return 1;
        }
    };
    public static final Collection<Material> WATER = Set.of(
        Material.WATER,
        Material.KELP,
        Material.KELP_PLANT,
        Material.SEAGRASS,
        Material.TALL_SEAGRASS
    );
    public static boolean isWater(Block b){
        return WATER.contains(b.getType());
    }
    public static final NaturalEntitySpawn LURKSCALE = new NaturalCruxMobSpawn(9, 0, USurviveMobs.LURKSCALE) {
        @NotNull
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock().getLocation().toCenterLocation(), consumer);
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();
            if(!isWater(b)) return false;

            for(Block check : CruxLoc.getNearbyBlocks(b, 3)){
                if(!isWater(check)) return false;
            }
            return b.getWorld().getNearbyEntities(b.getLocation(), 28D, 28D, 28D,
                x -> CruxMob.is(x, mob)).isEmpty() &&
                CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, null) < 6;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return 1;
        }
    };
}
