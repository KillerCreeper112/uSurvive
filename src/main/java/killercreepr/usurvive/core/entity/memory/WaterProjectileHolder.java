package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.usurvive.api.component.WaterProjectile;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WaterProjectileHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("water_projectile");
    public WaterProjectileHolder(@NotNull Key key, @NotNull EntityMemory parent, WaterProjectile data) {
        super(key, parent);
        this.data = data;
        this.maxTicks = data.maxTime() * (1.0 / data.dropOffRate()); // Scale the max ticks based on drop-off speed
    }

    public WaterProjectileHolder(@NotNull EntityMemory parent, WaterProjectile data) {
        this(KEY, parent, data);
    }

    public boolean isInWater(Entity e){
        return e.isInWater();
        //return e.getLocation().getBlock().getType() == Material.WATER;
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Entity e) {
        return super.shouldRemoveFromMemory(e) || tick > data.maxTime();
    }

    protected final WaterProjectile data;

    protected int tick = 0;
    protected int inWaterTick = 0;
    protected final double maxTicks;
    protected Vector last;
    protected Vector dir;
    protected boolean hadGravity;
    @Override
    public void tick(@NotNull Entity entity) {
        tick++;
        if(!isInWater(entity)){
            if(inWaterTick < 1) return;
            entity.setGravity(hadGravity);
            return;
        }

        if(entity instanceof AbstractArrow a && a.isInBlock()){
            return;
        }

        inWaterTick++;
        if(inWaterTick == 1){
            last = entity.getLocation().toVector();
            hadGravity = entity.hasGravity();
            if(entity instanceof AbstractArrow a) a.setDamage(a.getDamage()*1.5D);
            return;
        }
        if(entity.hasGravity()) entity.setGravity(false);
        if(dir == null){
            dir = entity.getLocation().toVector().subtract(last).normalize();
        }
        if(tick >= data.maxTime()) return;
        //double dropOffSpeed = 2.0; // Adjust this to control how fast the force weakens (Higher = faster drop-off)
        double decayFactor = Math.max(0, 1 - Math.pow(tick / maxTicks, 2));

        try{
            entity.setVelocity(dir.clone().multiply(data.force() * decayFactor));
        }catch (Exception ignored){}
    }
}
