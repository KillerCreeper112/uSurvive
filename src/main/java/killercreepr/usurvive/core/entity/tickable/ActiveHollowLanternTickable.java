package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.core.Crux;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class ActiveHollowLanternTickable extends ListenerActiveEntityTickable {
    protected final short tickRate;
    protected final Collection<PotionEffect> potions;
    protected final LivingEntity living;
    public ActiveHollowLanternTickable(Entity entity, EntityTickable tickable, CruxSlot slot, short tickRate, Collection<PotionEffect> potions) {
        super(entity, tickable, slot);
        this.tickRate = tickRate;
        this.potions = potions;
        this.living = (LivingEntity) entity;
    }

    @Override
    public void tick() {
        if(tickRate != 0 && Crux.getCurrentTick() % tickRate != 0) return;

        if(potions != null && !potions.isEmpty()){
            Crux.scheduler().runTaskMain(() -> living.addPotionEffects(potions));
        }
    }
}
