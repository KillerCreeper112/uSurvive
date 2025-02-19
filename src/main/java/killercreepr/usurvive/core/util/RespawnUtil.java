package killercreepr.usurvive.core.util;

import killercreepr.crux.core.data.util.Pair;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;

import java.util.function.Supplier;

public class RespawnUtil {
    public static Location findRespawnLocationOrTop(Block block){
        if(block.getBlockData() instanceof Bed bed){
            Pair<Block, Block> pair = getFullBed(block, bed);
            if(pair != null) return findRespawnLocationOrTop(pair.getFirst(), pair.getSecond());
        }
        return findRespawnLocationOrTop(new Block[]{block});
    }

    public static Location findRespawnLocationOrTop(Block... points){
        for(Block b : points){
            if(canSpawn(b)){
                return formatSafeRespawnLocation(b);
            }
            Block check = findSafeSpawnNear(b, 1);
            if(check != null) return formatSafeRespawnLocation(check);
        }
        Location fallback = null;
        for(Block b : points){
            Block check = b.getRelative(BlockFace.UP);
            if(fallback == null) fallback = formatSafeRespawnLocation(check);
            if(canSpawn(check)) return formatSafeRespawnLocation(check);
        }
        return fallback;
    }

    public static Location findRespawnLocation(Block block){
        return findRespawnLocation(null, block);
    }

    public static Location findRespawnLocation(Supplier<Location> fallback, Block block){
        if(block.getBlockData() instanceof Bed bed){
            Pair<Block, Block> pair = getFullBed(block, bed);
            if(pair != null) return findRespawnLocation(fallback, pair.getFirst(), pair.getSecond());
        }
        return findRespawnLocation(fallback, new Block[]{block});
    }

    public static Location findRespawnLocation(Block... points){
        return findRespawnLocation(null, points);
    }

    public static Location findRespawnLocation(Supplier<Location> fallback, Block... points){
        for(Block b : points){
            if(canSpawn(b)){
                return formatSafeRespawnLocation(b);
            }
            Block check = findSafeSpawnNear(b, 1);
            if(check != null) return formatSafeRespawnLocation(check);
        }
        for(Block b : points){
            Block check = b.getRelative(BlockFace.UP);
            if(canSpawn(check)) return formatSafeRespawnLocation(check);
        }
        return fallback == null ? null : fallback.get();
    }

    public static Block findSafeSpawnNear(Block b, int range){
        for(int x = -range; x <= range; x++){
            for(int z = -range; z <= range; z++){
                if(x == 0 && z == 0) continue;
                Block check = b.getRelative(x, 0, z);
                if(canSpawn(check)) return check;
            }
        }
        return null;
    }

    public static Pair<Block, Block> getFullBed(Block block, org.bukkit.block.data.type.Bed bed){
        BlockFace direction;
        if(bed.getPart() == org.bukkit.block.data.type.Bed.Part.FOOT) direction = bed.getFacing();
        else direction = bed.getFacing().getOppositeFace();

        Block part = block.getRelative(direction);
        if(!(part.getBlockData() instanceof org.bukkit.block.data.type.Bed)) return null;
        if(bed.getPart() == org.bukkit.block.data.type.Bed.Part.FOOT){
            return new Pair<>(part, block);
        }
        return new Pair<>(block, part);
    }

    public static Location formatSafeRespawnLocation(Block block){
        Location respawn = formatRespawnLocation(block);
        return formatRespawnLocation(respawn, block);
    }

    public static Location formatRespawnLocation(Location loc, Block point) {
        Location bedLoc = point.getLocation().toCenterLocation();

        double dx = bedLoc.getX() - loc.getX();
        //double dy = bedLoc.getY() - loc.getY();
        double dz = bedLoc.getZ() - loc.getZ();

        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));

        //float pitch = (float) Math.toDegrees(Math.atan2(-dy, distance));

        // Set new rotation
        loc.setYaw(yaw);
        loc.setPitch(0f);
        return loc;
    }


    public static Location formatRespawnLocation(Block b){
        return b.getLocation().toCenterLocation().subtract(0, .5, 0)
            .add(0, b.getBoundingBox().getHeight()+.01, 0);
    }

    public static boolean canSpawn(Block b){
        if(!b.isEmpty() && !b.isPassable()) return false;
        Block check = b.getRelative(BlockFace.UP);
        if(!check.isEmpty() && !check.isPassable()) return false;
        check = b.getRelative(BlockFace.DOWN);
        return check.isSolid();
    }
}
