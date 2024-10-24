package killercreepr.usurvive.block;

import killercreepr.crux.Crux;
import killercreepr.crux.tags.container.MergedTagContainer;
import killercreepr.crux.tags.container.TagContainer;
import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlockImpl;
import killercreepr.cruxblocks.block.active.ActiveCruxInteractable;
import killercreepr.cruxblocks.block.context.BlockContext;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.usurvive.entity.USurviveMobs;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ActiveOrbitConduit extends ActiveCruxBlockImpl implements ActiveCruxInteractable {
    protected final @NotNull List<String> interactCommands;
    public ActiveOrbitConduit(@NotNull Block block, @NotNull CruxBlock cruxBlock, @NotNull List<String> interactCommands) {
        super(block, cruxBlock);
        this.interactCommands = interactCommands;
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

    @NotNull
    @Override
    public Event.Result interact(@NotNull PlayerInteractEvent e) {
        Player p = e.getPlayer();
        MergedTagContainer tags = TagContainer.merged().hook(p);
        for(String s : interactCommands){
            s = Crux.FORMAT.deserializeString(s, tags);
            Crux.getServer().dispatchCommand(Crux.getServer().getConsoleSender(), s);
        }
        return Event.Result.DENY;
    }
}
