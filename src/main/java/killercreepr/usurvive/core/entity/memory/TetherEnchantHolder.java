package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateLine;
import killercreepr.usurvive.core.damage.type.USurviveDamageTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TetherEnchantHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("enchant/tether");

    protected final Entity target;
    protected final int lifespan;
    protected final double breakDistanceSquared;
    protected final int level;
    protected final ShapeScheduler scheduler;
    public TetherEnchantHolder(@NotNull Key key, @NotNull EntityMemory parent, Entity target, int lifespan, double breakDistanceSquared, int level) {
        super(key, parent);
        this.target = target;
        this.lifespan = lifespan;
        this.breakDistanceSquared = breakDistanceSquared;
        this.level = level;

        scheduler = ShapeScheduler.builder()
            .locationTick(ctx ->{
                Location loc = ctx.getLocation().toLocation(target.getWorld());
                loc.getWorld().spawnParticle(Particle.CRIT, loc, 0);
            })
            .shape(CreateLine.builder()
                .start(() ->{
                    Entity e = parent.value();
                    if(e == null) return CruxPosition.location(target.getLocation());
                    return CruxPosition.location(e.getLocation().add(0, e.getHeight()*.5, 0));
                })
                .end(() ->{
                    return CruxPosition.location(target.getLocation().add(0, target.getHeight()*.5, 0));
                })
                .spacing(1D)
                .build())
            .buildCached();
    }
    public TetherEnchantHolder(@NotNull EntityMemory parent, Entity target, int lifespan, double breakDistanceSquared, int level) {
        this(KEY, parent, target, lifespan, breakDistanceSquared, level);
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Entity e) {
        return cancel || tick >= lifespan || super.shouldRemoveFromMemory(e) || !isValid();
    }

    protected boolean cancel = false;
    public boolean isValid(){
        return CruxEntityUtil.isValid(target) && !CruxEntityUtil.isNonSurvival(target);
    }

    public static double getDistance(Entity entity, Entity target){
        Location pos1 = entity.getLocation().add(0, entity.getHeight()*.5, 0);
        Location pos2 = target.getLocation().add(0, target.getHeight()*.5, 0);
        return pos1.distanceSquared(pos2);
    }

    public double getDistance(Entity entity){
        return getDistance(entity, target);
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    protected int tick = 0;
    @Override
    public void tick(@NotNull Entity entity) {
        if(!target.getWorld().equals(entity.getWorld())){
            setCancel(true);
            return;
        }

        if(tick % 3 == 0){
            scheduler.scheduleAsync(0);
        }

        tick++;
        double distance = getDistance(entity);
        if(distance < breakDistanceSquared) return;
        setCancel(true);
        onBreak(entity);
    }

    public void onBreak(Entity entity){
        double dmg = 6D + ((level-1)*3D);
        double kb = 6D + ((level-1)*2D);
        Crux.scheduler().runTask(() ->{
            EntityDamager.entityDamager(target, entity)
                .setSource(DamageSource.builder(USurviveDamageTypes.TETHER)
                    .withDirectEntity(entity)
                    .build())
                .attack(dmg, kb);
            CreateSound.sound(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.5f, 1.6f)
                .playAt(target)
                .playAt(entity);
        });
    }
}
