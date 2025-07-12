package killercreepr.usurvive.core.game;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.math.CruxLocation;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.data.tick.SimpleCruxTick;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateWarpedLine;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.potion.SporelinkPotion;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SporelinkVisualizer extends SimpleCruxTick {
    public static final Key KEY = USurvivePlugin.inst().key("sporelink_visualizer");
    protected final int removeTime;
    protected final int particleTickRate;
    public SporelinkVisualizer(@NotNull Key key, int removeTime, int particleTickRate) {
        super(key);
        this.removeTime = removeTime;
        this.particleTickRate = particleTickRate;
    }
    public SporelinkVisualizer(int removeTime, int particleTickRate) {
        this(KEY, removeTime, particleTickRate);
    }

    protected final Map<Entity, SporelinkPotion> links = new HashMap<>();
    protected int lastAccessed;

    public int getLastAccessed() {
        return lastAccessed;
    }

    public void setLink(Entity e, SporelinkPotion links){
        lastAccessed = tick;
        this.links.put(e, links);
    }

    public boolean attemptClear(){
        if(tick > (lastAccessed+particleTickRate)){
            links.clear();
            return true;
        }
        return false;
    }

    public void setLastAccessed(int lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    @Override
    public boolean markedForRemoval() {
        return super.markedForRemoval() || shouldRemove();
    }

    public boolean shouldRemove(){
        return tick > (lastAccessed + removeTime);
    }

    protected int tick = 0;

    public int getTick() {
        return tick;
    }

    @Override
    public void tick() {
        tick++;
        if(links.isEmpty()) return;

        if(tick % 5 != 0) return;
        links.entrySet().removeIf(entry ->{
            Entity main = entry.getKey();
            if(!entry.getValue().isActive()) return true;
            if(!CruxEntityUtil.isValid(main)) return true;

            for (Entity linked : entry.getValue().getLastLinked()) {
                boolean hasBackLink = links.containsKey(linked) && links.get(linked).getLastLinked().contains(main);
                if (hasBackLink && main.getUniqueId().compareTo(linked.getUniqueId()) >= 0) continue;

                ShapeScheduler.builder()
                    .locationTick(ctx ->{
                        new ParticleBuilder(Particle.ELECTRIC_SPARK)
                            .location(ctx.getLocation().toLocation(main.getWorld()))
                            .offset(0, 0, 0)
                            .extra(0)
                            .spawn();
                    })
                    .shape(CreateWarpedLine.builder()
                        .start(() -> CruxLocation.location(main.getLocation().add(0, main.getHeight()/2, 0)))
                        .end(() -> CruxLocation.location(linked.getLocation().add(0, linked.getHeight()/2, 0)))
                        .spacing(1D)
                        .warpStrength(0.5D)
                        .tickOffset(NumberProvider.holder(() ->{
                            return (System.currentTimeMillis() / 50L) % 10000;
                        }))
                        .build())
                    .build().scheduleAsync(0);
            }
            return false;
        });
    }
}
