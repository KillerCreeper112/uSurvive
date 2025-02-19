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
    protected final Pair<Block, Block> fullBed;
    public BedSlumberData(Entity entity, Location location) {
        super(entity, location);
        Block b = location.getBlock();
        if(b.getBlockData() instanceof Bed bed){
            this.fullBed = getFullBed(b, bed);
        }else fullBed = null;
    }

    public boolean canSleep(){
        Block b = location.getBlock();
        if(!(b.getBlockData() instanceof Bed bed)){
            return isEmptyOrPassable(b.getRelative(BlockFace.UP));
        }
        Pair<Block, Block> fullBed = getFullBed(b, bed);
        if(fullBed == null){
            return isEmptyOrPassable(b.getRelative(BlockFace.UP));
        }
        return isEmptyOrPassable(fullBed.getFirst().getRelative(BlockFace.UP)) &&
            isEmptyOrPassable(fullBed.getSecond().getRelative(BlockFace.UP));
    }

    public boolean isEmptyOrPassable(Block b){
        return b.isEmpty() || b.isPassable();
    }

    public boolean shouldSetBedLocation(){
        return true;
    }

    @Override
    public void started() {
        super.started();
        if(fullBed == null){
            Block b = location.getBlock();
            if(b.getBlockData() instanceof Bed bed){
                bed.setOccupied(true);
                b.setBlockData(bed);
            }
            return;
        }
        var pair = fullBed;
        if(fullBed.getFirst().getBlockData() instanceof Bed bed){
            bed.setOccupied(true);
            pair.getFirst().setBlockData(bed);
        }
        if(fullBed.getSecond().getBlockData() instanceof Bed bed){
            bed.setOccupied(true);
            pair.getSecond().setBlockData(bed);
        }
    }

    @Override
    public void stopped() {
        super.stopped();
        if(fullBed == null){
            Block b = location.getBlock();
            if(b.getBlockData() instanceof Bed bed){
                bed.setOccupied(false);
                b.setBlockData(bed);
            }
            return;
        }
        var pair = fullBed;
        if(fullBed.getFirst().getBlockData() instanceof Bed bed){
            bed.setOccupied(false);
            pair.getFirst().setBlockData(bed);
        }
        if(fullBed.getSecond().getBlockData() instanceof Bed bed){
            bed.setOccupied(false);
            pair.getSecond().setBlockData(bed);
        }
    }

    @Override
    public Location getUserTeleportLocation() {
        Pair<Block, Block> pair = fullBed;
        if(pair == null) return formatSpectateLocation(location.getBlock());
        return formatSpectateLocation(pair.getFirst());
    }

    @Override
    public Location getTargetLocation() {
        if(fullBed == null) return super.getTargetLocation();
        Block first = fullBed.getFirst();
        if(!(first.getBlockData() instanceof Bed bed)) return super.getTargetLocation();
        BlockFace direction = bed.getFacing().getOppositeFace();
        Location loc = first.getLocation().toCenterLocation().subtract(0, .5, 0);
        loc = CruxLoc.shift(loc, direction.getDirection(), .5, 0, 0);
        return loc;
    }

    @Override
    public Location getDisplayLocation() {
        Pair<Block, Block> pair = fullBed;
        if(pair == null) return formatDisplayLocation(location.getBlock());
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

    /*public Location formatSpectateLocation(Block b){
        Location loc = b.getLocation().toCenterLocation().add(0, -1.1, 0);
        if(b.getBlockData() instanceof Directional dir){
            loc.setDirection(dir.getFacing().getOppositeFace().getDirection());
        }
        loc.setPitch(-15f);
        return loc;
    }*/

    public Location formatSpectateLocation(Block b){
        Location loc = b.getLocation().toCenterLocation().add(0, .455, 0);
        if(b.getBlockData() instanceof Directional dir){
            loc.setDirection(dir.getFacing().getOppositeFace().getDirection());
        }
        loc.setPitch(-15f);
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
