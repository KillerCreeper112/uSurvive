package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.crux.core.util.GetNear;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.SimpleActivePotion;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class MagnetismPotion extends SimpleActivePotion{
    protected final Runnable runnable;
    public MagnetismPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
        boolean isPlayer = entity instanceof Player;
        runnable = () ->{
            double range = getRange();
            new GetEntityNear<>(Item.class).center(entity)
                .range(range)
                .operation(GetNear.Operation.NEAREST)
                .filter(item ->{
                    if(item.getPickupDelay() > 0) return false;
                    return (isPlayer && item.canPlayerPickup()) || item.canMobPickup();
                })
                .amount(maxAmount()).find().forEach(item ->{
                    double distance = item.getLocation().distanceSquared(entity.getLocation());
                    if(distance <= (1.5*1.5)) return;

                    Vector dir = entity.getLocation().toVector()
                        .subtract(item.getLocation().toVector())
                        .normalize()
                        .multiply(0.3);
                    dir.setY(dir.getY() + 0.2);
                    item.setVelocity(dir);
                });
        };
    }

    public int maxAmount(){
        return 5 + (amplifier * 5);
    }

    public double getRange(){
        return 6D + (amplifier * 5D);
    }

    protected int tick = 0;
    @Override
    protected void onTick() {
        super.onTick();
        tick++;
        if(tick % 5 != 0) return;
        Crux.scheduler().runTask(runnable);
    }
}
