package killercreepr.usurvive.core.entity.sleeper;

import killercreepr.crux.core.data.util.Pair;
import killercreepr.crux.core.util.CruxBlockFace;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.usurvive.core.util.RespawnUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;

public class BedSlumberData extends BaseSlumberData {
    public BedSlumberData(Entity entity, Location location) {
        super(entity, location);
    }

    public boolean shouldSetBedLocation(){
        return true;
    }

    @Override
    public Location getUserTeleportLocation() {
        Block b = location.getBlock();
        if(!(b.getBlockData() instanceof Bed bed)) return super.getUserTeleportLocation();
        Pair<Block, Block> pair = getFullBed(b, bed);
        if(pair == null) return formatSpectateLocation(b);
        return formatSpectateLocation(pair.getFirst());
    }

    @Override
    public Location getDisplayLocation() {
        Block b = location.getBlock();
        if(!(b.getBlockData() instanceof Bed bed)) return super.getUserTeleportLocation();
        Pair<Block, Block> pair = getFullBed(b, bed);
        if(pair == null) return formatDisplayLocation(b);
        return formatDisplayLocation(pair.getSecond());
    }

    public Location formatDisplayLocation(Block b){
        Location loc = b.getLocation().toCenterLocation().add(0, .2, 0);
        if(b.getBlockData() instanceof Directional dir){
            BlockFace direction = dir.getFacing().getOppositeFace();
            loc = CruxLoc.shift(loc, direction.getDirection(), .45D, 0D, 0D);
            BlockFace rotated;
            if(dir.getFacing() == BlockFace.SOUTH || dir.getFacing() == BlockFace.NORTH){
                rotated = CruxBlockFace.rotateLeft(direction);
            }else rotated = CruxBlockFace.rotateRight(direction);
            loc.setDirection(rotated.getDirection());
        }
        loc.setPitch(0f);
        return loc;
    }

    public Location formatSpectateLocation(Block b){
        Location loc = b.getLocation().toCenterLocation().add(0, .4, 0);
        if(b.getBlockData() instanceof Directional dir){
            loc.setDirection(dir.getFacing().getOppositeFace().getDirection());
        }
        loc.setPitch(-90f);
        return loc;
    }

    //<head, foot>
    public Pair<Block, Block> getFullBed(Block block, Bed bed){
        return RespawnUtil.getFullBed(block, bed);
    }

    @Override
    public boolean shouldStop() {
        return super.shouldStop() || !(location.getBlock().getBlockData() instanceof Bed);
    }
}
