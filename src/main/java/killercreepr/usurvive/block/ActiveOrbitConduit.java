package killercreepr.usurvive.block;

import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlockImpl;
import killercreepr.cruxblocks.block.context.BlockContext;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.usurvive.entity.USurviveMobs;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class ActiveOrbitConduit extends ActiveCruxBlockImpl {
    protected final Key currencyKey;
    public ActiveOrbitConduit(@NotNull Block block, @NotNull CruxBlock cruxBlock, Key currencyKey) {
        super(block, cruxBlock);
        this.currencyKey = currencyKey;
    }

    public Entity getOrb(){
        BoundingBox box = BoundingBox.of(block.getLocation().toCenterLocation(), 1, 1, 1).shift(0, 1, 0);
        for(Entity e : block.getWorld().getNearbyEntities(box, e -> CruxMob.is(e, USurviveMobs.ORBIT_CONDUIT))){
            return e;
        }
        return null;
    }

    public Entity spawnOrb(){
        Location spawn = block.getLocation().toCenterLocation().add(0, 1, 0);
        return USurviveMobs.ORBIT_CONDUIT.spawn(spawn);
    }

    @Override
    public void placed(@NotNull BlockContext ctx) {
        super.placed(ctx);
        spawnOrb();
    }

    @Override
    public void broken(@NotNull BlockContext ctx) {
        super.broken(ctx);
        Entity orb = getOrb();
        if(orb != null) orb.remove();
    }
}
