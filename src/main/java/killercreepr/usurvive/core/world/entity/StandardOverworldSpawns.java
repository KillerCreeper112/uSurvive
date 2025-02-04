package killercreepr.usurvive.core.world.entity;

import killercreepr.crux.core.data.util.CollectionBuilder;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.cruxentities.world.entity.NaturalCruxMobSpawn;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawn;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntitySpawn;
import killercreepr.usurvive.core.entity.USurviveMobs;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

public class StandardOverworldSpawns {
    public static final NaturalEntitySpawn EMPTY = new SimpleNaturalEntitySpawn(30, 0f) {
        @Nullable
        @Override
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return null;
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            return true;
        }
    };
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
}
