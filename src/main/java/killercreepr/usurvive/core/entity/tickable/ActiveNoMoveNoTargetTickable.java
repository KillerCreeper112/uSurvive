package killercreepr.usurvive.core.entity.tickable;

import com.destroystokyo.paper.ParticleBuilder;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import killercreepr.crux.api.data.ParticleBuilderSupplier;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ActiveNoMoveNoTargetTickable extends ListenerActiveEntityTickable implements Listener {
    protected final int dontMoveTicks;
    protected final ParticleBuilderSupplier supplier;
    protected final ParticleBuilder particle;
    protected final int particleEvery;
    public ActiveNoMoveNoTargetTickable(Entity entity, EntityTickable tickable, CruxSlot slot, int dontMoveTicks, ParticleBuilderSupplier supplier, int particleEvery) {
        super(entity, tickable, slot);
        this.dontMoveTicks = dontMoveTicks;
        this.supplier = supplier;
        this.particleEvery = particleEvery;
        this.particle = supplier.build();
    }

    protected long lastMoved;
    protected int tick = 0;
    @Override
    public void tick() {
        if(particle == null) return;
        if(CruxMath.hasOccurredWithin(lastMoved, dontMoveTicks)) return;
        tick++;
        if(tick >= particleEvery){
            tick = 0;
            particle.location(entity.getLocation().add(0, entity.getHeight()/2, 0)).spawn();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if(!entity.equals(event.getTarget())) return;
        if(CruxMath.hasOccurredWithin(lastMoved, dontMoveTicks)) return;
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.getPlayer().equals(entity)) return;
        if(!event.hasChangedBlock()) return;
        lastMoved = System.currentTimeMillis();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerArmSwing(PlayerArmSwingEvent event) {
        if(!event.getPlayer().equals(entity)) return;
        lastMoved = System.currentTimeMillis();
    }
}
