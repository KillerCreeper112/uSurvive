package killercreepr.usurvive.core.pathfinding;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class GuidePathfinder {

    private static final int MAX_NODES = 6000;
    private static final double MAX_DISTANCE = 120;

    public record Node(Block block, Node parent, double gCost, double hCost) {
        double fCost() { return gCost + hCost; }
    }

    private static final BlockFace[] HORIZONTAL_DIRS = {
        BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST,
        BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST
    };

    protected final LivingEntity entity;

    public GuidePathfinder(LivingEntity entity) {
        this.entity = entity;
    }

    private List<Location> generateDensePath(List<Node> nodePath, double spacing, int maxAmount) {
        List<Location> densePath = new ArrayList<>();
        if (nodePath.isEmpty()) return densePath;

        // Start from the last node (goal)
        Location lastPoint = nodePath.getLast().block().getLocation();
        lastPoint.add(0.5, getWalkableHeight(nodePath.getLast().block()), 0.5);
        densePath.add(lastPoint);

        for (int i = nodePath.size() - 2; i >= 0; i--) {
            Node node = nodePath.get(i);
            Location current = node.block().getLocation().clone();
            current.add(0.5, getWalkableHeight(node.block()), 0.5);

            Vector segment = lastPoint.toVector().subtract(current.toVector());
            double segmentLength = segment.length();
            if (segmentLength < 0.001) continue;
            Vector dir = segment.normalize();

            double traveled = spacing;
            while (traveled < segmentLength) {
                Location stepPoint = current.clone().add(dir.clone().multiply(traveled));

                // Snap to walkable height
                double walkHeight = getWalkableHeight(stepPoint.getBlock());
                stepPoint.setY(stepPoint.getY() - 0.5 + walkHeight);

                Block below = stepPoint.getBlock().getRelative(BlockFace.DOWN);
                if (canPass(below) || isClimbable(below)) {
                    densePath.add(stepPoint);
                    if(densePath.size() > maxAmount) break;
                }

                traveled += spacing;
            }

            densePath.add(current);
            lastPoint = current;
            if(densePath.size() > maxAmount) break;
        }
        return densePath;
    }

    /**
     * Returns the Y offset to place the entity/particle at
     * according to the walkable block's surface (stairs, slabs, full block, etc.)
     */
    private double getWalkableHeight(Block b) {
        // Full block
        if (isSolid(b) && !isClimbable(b)) return b.getBoundingBox().getHeight() + 0.1;

        // Ladder or vine
        if (isClimbable(b)) return 0.0;

        // Slabs / stairs
        BoundingBox bb = b.getBoundingBox();
        return bb.getHeight() > 0 ? bb.getHeight() + 0.1 : 0.1;
    }


    public List<Location> findPath(Location start, Location target, double spacing, int maxLocations) {
        if (!start.getWorld().equals(target.getWorld())) return List.of();

        Block startBlock = start.getBlock();
        Block targetBlock = target.getBlock();

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::fCost));
        Map<Block, Double> costSoFar = new HashMap<>();
        Set<Block> closedSet = new HashSet<>();

        Node startNode = new Node(startBlock, null, 0, startBlock.getLocation().distance(target));
        openSet.add(startNode);
        costSoFar.put(startBlock, 0.0);

        Node found = null;

        while (!openSet.isEmpty() && closedSet.size() < MAX_NODES) {
            Node current = openSet.poll();
            Block cBlock = current.block();
            closedSet.add(cBlock);

            // Goal snapping: allow nearby walkable block
            if (cBlock.getLocation().distanceSquared(targetBlock.getLocation()) < 1.5) {
                found = current;
                break;
            }

            for (Block neighbor : getNeighbors(cBlock)) {
                if (closedSet.contains(neighbor)) continue;

                double g = current.gCost + moveCost(cBlock, neighbor) * getTerrainCost(neighbor);
                if (g > MAX_DISTANCE) continue;

                double oldG = costSoFar.getOrDefault(neighbor, Double.MAX_VALUE);
                if (g < oldG) {
                    costSoFar.put(neighbor, g);
                    double h = neighbor.getLocation().distance(target);
                    openSet.add(new Node(neighbor, current, g, h));
                }
            }
        }

        if (found == null) return List.of();

        // Backtrack
        List<Location> path = new ArrayList<>();
        for (Node n = found; n != null; n = n.parent()) {
            path.add(n.block().getLocation().add(0.5, n.block().getBoundingBox().getHeight() + 0.1, 0.5));
        }
        Collections.reverse(path);

        List<Node> nodePath = new ArrayList<>();
        for (Node n = found; n != null; n = n.parent()) nodePath.add(n);

        List<Location> densePath = generateDensePath(nodePath, spacing, maxLocations);
        return densePath;
    }

    // --- utilities ---

    private double heuristic(Block a, Block b) {
        return a.getLocation().distance(b.getLocation());
    }

    private double moveCost(Block from, Block to) {
        int dy = to.getY() - from.getY();
        return dy == 0 ? 1.0 : 1.4; // slightly higher cost for elevation change
    }

    public double getEntityHeight(){
        return entity.getHeight();
    }

    public double getEntityWidth(){
        return entity.getWidth();
    }

    public double getEntityJumpStrength(){
        var instance = entity.getAttribute(Attribute.JUMP_STRENGTH);
        return instance == null ? 0.5D : instance.getValue();
    }

    public boolean isClimbable(Block b){
        Material m = b.getType();
        return m == Material.LADDER || m == Material.VINE;
    }

    public boolean isPassable(Block b){
        return b.isEmpty() || b.isPassable() || b.isLiquid() || isClimbable(b);
    }

    public boolean isDangerous(Block b){
        return b.getType() == Material.LAVA;
    }

    public boolean isValidPassable(Block b){
        return isPassable(b) && !isDangerous(b);
    }

    public boolean isValidSolid(Block b){
        return isSolid(b) && !isDangerous(b);
    }

    public boolean isSolid(Block b){
        return b.isSolid();
    }

    private boolean canPass(Block b) {
        Block ground = b.getRelative(BlockFace.DOWN);

        if(!isValidSolid(ground)) return false;

        Block above = b.getRelative(BlockFace.UP);
        if(isClimbable(b) && isValidPassable(b)){
            return isValidPassable(above);
        }

        double jumpStrength = getEntityJumpStrength();

        double bHeight = b.getBoundingBox().getHeight();
        if(bHeight > (jumpStrength * 1.5D)) return false;

        Location moveTo = b.getLocation().toCenterLocation().subtract(0, .5, 0).add(0, bHeight, 0);
        BoundingBox check = entity.getBoundingBox().clone()
            .shift(moveTo.toVector().subtract(entity.getLocation().toVector()));
        if(b.getWorld().hasCollisionsIn(check)) return false;
        return true;
    }

    private double getTerrainCost(Block b) {
        if (b.isEmpty() || b.isPassable()) return 1.0;
        //if (b.isSolid()) return Double.MAX_VALUE; // blocked
        if (b.isLiquid()) return 2.0; // slow through water
        // Custom: sand = 1.2, grass = 1.0, etc.
        return 1D;
    }


    private List<Block> getNeighbors(Block b) {
        World w = b.getWorld();
        List<Block> neighbors = new ArrayList<>();
        for (BlockFace dir : HORIZONTAL_DIRS) {
            Block neighbor = b.getRelative(dir);
            if (canPass(neighbor)) neighbors.add(neighbor);

            // vertical movement for ladder
            if (b.getType() == Material.LADDER || neighbor.getType() == Material.LADDER) {
                Block up = neighbor.getRelative(BlockFace.UP);
                Block down = neighbor.getRelative(BlockFace.DOWN);
                if (canPass(up)) neighbors.add(up);
                if (canPass(down)) neighbors.add(down);
            }

            // step up/down normally
            Block stepUp = w.getBlockAt(neighbor.getX(), neighbor.getY() + 1, neighbor.getZ());
            if (canPass(stepUp)) neighbors.add(stepUp);

            Block stepDown = w.getBlockAt(neighbor.getX(), neighbor.getY() - 1, neighbor.getZ());
            if (canPass(stepDown)) neighbors.add(stepDown);
        }
        return neighbors;
    }



    private List<Location> smoothPath(List<Location> path) {
        if (path.size() < 3) return path;

        List<Location> smooth = new ArrayList<>();
        Location last = path.get(0);
        smooth.add(last);

        for (int i = 1; i < path.size(); i++) {
            Location next = path.get(i);
            if (last.distanceSquared(next) > 1.5) {
                smooth.add(next);
                last = next;
            }
        }

        return smooth;
    }

    private List<Location> prunePath(List<Location> path) {
        if (path.size() < 3) return path;
        List<Location> pruned = new ArrayList<>();
        pruned.add(path.get(0));

        int i = 0;
        while (i < path.size() - 1) {
            int j = path.size() - 1;
            for (; j > i + 1; j--) {
                if (hasLineOfSight(pruned.get(pruned.size() - 1), path.get(j))) break;
            }
            pruned.add(path.get(j));
            i = j;
        }
        return pruned;
    }

    private boolean hasLineOfSight(Location from, Location to) {
        Vector dir = to.toVector().subtract(from.toVector()).normalize();
        double distance = from.distance(to);
        Location check = from.clone();
        for (double i = 0; i < distance; i += 0.3) {
            Block b = check.getBlock();
            if (b.getType().isSolid() && b.getType() != Material.LADDER) return false;
            check.add(dir.clone().multiply(0.3));
        }
        return true;
    }

}
