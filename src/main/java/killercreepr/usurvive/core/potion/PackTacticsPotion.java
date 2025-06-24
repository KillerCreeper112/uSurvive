package killercreepr.usurvive.core.potion;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.persistence.CruxPersist;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.crux.core.util.GetNear;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import killercreepr.cruxpotions.core.potions.SimpleActivePotion;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.usurvive.api.entity.player.UPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PackTacticsPotion extends SimpleActivePotion implements Listener {
    public PackTacticsPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
    }

    public double calculateRange(){
        return 8D + (amplifier*4);
    }

    public int calculateMaxFriendlyAmount(){
        return 10 * (amplifier+1);
    }

    public boolean isFriendly(Entity e){
        if(e.equals(entity)) return false;
        if(e instanceof Tameable tame && e.getUniqueId().equals(tame.getOwnerUniqueId())) return true;
        if(e instanceof Player){
            UPlayer player = UPlayer.getPlayer(entity);
            if(player != null){
                if(player.isApartOfParty(e.getUniqueId()) || player.hasFriend(e.getUniqueId())) return true;
            }
        }
        return entity.getUniqueId().equals(CruxPersist.OWNER.get(e, null));
    }

    public Collection<Entity> getNearbyAllies(double range){
        return new GetEntityNear<>(Entity.class)
            .center(entity)
            .range(range)
            .amount(calculateMaxFriendlyAmount())
            .operation(GetNear.Operation.NEAREST)
            .filter(this::isFriendly)
            .find();
    }

    @Override
    protected void onTick() {
        super.onTick();

        if(Crux.getCurrentTick() % 40 != 0) return;

        getNearbyAllies(calculateRange()).forEach(hit ->{
            PotionHolder holder = EntityMemory.getOrCreateDataHolder(hit, SimplePotionHolder.class, SimplePotionHolder::new);
            if(holder == null) return;
            holder.addPotion(USurvivePotions.PACK_ALLIANCE.create(
                hit, 100, amplifier, new EntityInflictor(entity)
            ));
        });
    }
}
