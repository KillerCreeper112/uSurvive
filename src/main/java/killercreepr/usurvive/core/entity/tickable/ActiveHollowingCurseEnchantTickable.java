package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.core.Crux;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class ActiveHollowingCurseEnchantTickable extends ListenerActiveEntityTickable implements Listener {
    protected final NamespacedKey key = Crux.key("enchant/hollowing_curse");
    protected final int level;
    public ActiveHollowingCurseEnchantTickable(Entity entity, EntityTickable tickable, CruxSlot slot, int level) {
        super(entity, tickable, slot);
        this.level = level;
    }

    public boolean isAbyss(World world){
        CruxWorld crux = CruxCore.core().worldManager().getWorld(world.key());
        if(crux == null) return false;
        CruxWorldType type = crux.get(CruxWorldsComponents.WORLD_TYPE);
        if(type == null) return false;
        return type.compare(Crux.key("abyss"));
    }

    public double calculateMaxHealth(){
        return level * 8D;
    }

    public void updateAttribute(Entity e){
        if(!(e instanceof LivingEntity living)) return;
        var instance = living.getAttribute(Attribute.MAX_HEALTH);
        if(instance == null) return;
        instance.removeModifier(key);

        if(isAbyss(e.getWorld())){
            instance.addTransientModifier(new AttributeModifier(
                key, calculateMaxHealth(), AttributeModifier.Operation.ADD_NUMBER
            ));
        }
    }

    public void removeAttribute(Entity e){
        if(!(e instanceof LivingEntity living)) return;
        var instance = living.getAttribute(Attribute.MAX_HEALTH);
        if(instance == null) return;
        instance.removeModifier(key);
    }

    @Override
    public void started() {
        super.started();
        updateAttribute(entity);
    }

    @Override
    public void stopped() {
        super.stopped();
        removeAttribute(entity);
    }

    @Override
    public void tick() {}

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        if(!p.equals(entity)) return;
        updateAttribute(entity);
    }

}
