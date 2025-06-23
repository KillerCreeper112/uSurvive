package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttributeAccessor;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickableModifier;
import killercreepr.cruxtickables.core.component.CruxTickableComponents;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.api.mechanics.whip.Whip;
import killercreepr.usurvive.api.mechanics.whip.WhipHook;
import killercreepr.usurvive.api.mechanics.whip.context.WhipContext;
import killercreepr.usurvive.core.attribute.USurviveAttribute;
import killercreepr.usurvive.core.data.entity.WhipperHolder;
import killercreepr.usurvive.core.mechanics.whip.SimpleDisplayWhip;
import killercreepr.usurvive.core.mechanics.whip.context.DisplaySimpleWhipContext;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TentacleDefenseTickable extends ListenerActiveEntityTickable {
    protected final CruxAttributeAccessor attributes;
    protected final Whip whip;
    protected int maxTentacles;
    protected final NumberProvider cooldown;
    protected final NumberProvider nearRange;
    protected final Player p;

    public TentacleDefenseTickable(Entity entity, EntityTickable tickable, CruxSlot slot, CruxAttributeAccessor attributes, Whip whip, NumberProvider cooldown, NumberProvider nearRange) {
        super(entity, tickable, slot);
        this.attributes = attributes;
        this.whip = whip;
        this.cooldown = cooldown;
        this.nearRange = nearRange;
        this.p = (Player) entity;
    }

    public void updateLastAttacked(){
        lastAttacked.values().removeIf(hit -> !hit.isValid(entity));
    }

    public int calculateMaxTentacles(){
        int max = 0;
        for(var slot : AttributeBukkitAdaptor.ARMOR_SLOTS){
            ItemStack item = p.getInventory().getItemInMainHand();
            if(CruxItem.isEmpty(item)) continue;
            CruxItem crux = CruxItem.wrap(item);
            var tickables = crux.getOrDefaultData(CruxTickableComponents.ENTITY_TICKABLES);
            if(tickables == null) continue;
            for (EntityTickableModifier mod : tickables.getTickableModifiers()) {
                if(!mod.getTickable().equals(USurviveTickables.TENTACLE_DEFENSE)) continue;
                if(mod.getSlotGroup().test(AttributeBukkitAdaptor.adapt(slot))){
                    max++;
                    break;
                }
            }
        }
        return max;
    }

    protected int currentCooldown = 20;
    @Override
    public void tick() {
        if(lastAttacked.isEmpty()) return;

        if(currentCooldown > 0){
            currentCooldown--;
            return;
        }

        currentCooldown = cooldown.value().intValue();
        updateLastAttacked();

        maxTentacles = calculateMaxTentacles();
        int total = getCurrentWhips();
        if(total >= maxTentacles) return;
        Crux.scheduler().runTaskMain(() ->{
            int current = getCurrentWhips();
            if(current >= maxTentacles) return;
            for (LivingEntity target : new GetEntityNear<>(LivingEntity.class)
                .filter(e -> !e.equals(entity) && lastAttacked.containsKey(e.getUniqueId()) &&
                    CruxEntityUtil.isRealisticallyVisibleTo(e, p))
                .range(nearRange.value().doubleValue())
                .center(entity)
                .find()) {
                current++;

                launchTentacle(target, p);

                if(current >= maxTentacles) break;
            }
        });
    }

    public void launchTentacle(Entity target, Player p){
        Location loc = CruxEntityUtil.getEyeOrLocation(entity);
        loc = CruxLoc.lookAt(loc, target.getLocation().add(0, target.getHeight()/2, 0));

        WhipperHolder whipper = PlayerMemory.getOrCreate(p)
            .getPlayerDataHolderOrCompute(WhipperHolder.class, WhipperHolder::new);

        WhipContext ctx = buildWhipContext(loc, p, buildNextKey());
        WhipHook whipType = USurviveRegistries.WHIP_HOOK.get(whip.getWhipType());
        whipper.throwNewHook(whipType, ctx);
    }

    protected final Map<UUID, Hit> lastAttacked = new HashMap<>();
    public WhipContext buildWhipContext(Location loc, Entity p, Key key) {
        if(whip instanceof SimpleDisplayWhip w){
            return new DisplaySimpleWhipContext(
                loc, p, key,
                attributes.getValue(USurviveAttribute.WHIP_THROW_FORCE),
                (int) attributes.getValue(USurviveAttribute.WHIP_PIERCE),
                attributes,
                w.getTypeKey(),
                "item".equalsIgnoreCase(w.getDisplayType()) ? ItemDisplay.class : BlockDisplay.class,
                (int) attributes.getValue(USurviveAttribute.WHIP_RANGE),
                attributes.getValue(USurviveAttribute.WHIP_RETRACT_SPEED)
            );
        }
        return WhipContext.context(loc, p, key,
            attributes.getValue(USurviveAttribute.WHIP_THROW_FORCE),
            (int) attributes.getValue(USurviveAttribute.WHIP_PIERCE),
            attributes);
    }

    public int getCurrentWhips(){
        WhipperHolder whipper = EntityMemory.getDataHolder(p, WhipperHolder.class);
        if(whipper == null) return 0;
        if(whipper.getWhips().isEmpty()) return 0;
        int amount = 0;
        for(int i = 0; i < maxTentacles; i++){
            if(whipper.getWhip(buildKey(i)) != null) amount++;
        }
        return amount;
    }

    public Key buildKey(int id){
        return Crux.key("tentacle_defense/" + id);
    }

    public Key buildNextKey(){
        return buildKey(getCurrentWhips()+1);
    }

    public boolean isFriendly(Entity e){
        if(e.equals(entity)) return true;
        if(e instanceof Tameable t && entity.getUniqueId().equals(t.getOwnerUniqueId())) return true;
        if(e instanceof Player){
            UPlayer uPlayer = UPlayer.getPlayer(entity);
            if(uPlayer != null && (uPlayer.hasFriend(e.getUniqueId()) || uPlayer.isApartOfParty(e.getUniqueId()))) return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!event.getEntity().equals(entity)){

            if(event.getDamager().equals(entity)){
                Entity target = event.getEntity();
                if(isFriendly(target)) return;
                lastAttacked.put(target.getUniqueId(), new Hit(new WeakReference<>(target), System.currentTimeMillis()));
            }

            return;
        }

        Entity target = event.getDamager();
        if(isFriendly(target)) return;
        lastAttacked.put(target.getUniqueId(), new Hit(new WeakReference<>(target), System.currentTimeMillis()));
        if(true) return;

        Location loc = CruxEntityUtil.getEyeOrLocation(entity);
        loc = CruxLoc.lookAt(loc, target.getLocation().add(0, target.getHeight()/2, 0));

        WhipperHolder whipper = PlayerMemory.getOrCreate(p)
            .getPlayerDataHolderOrCompute(WhipperHolder.class, WhipperHolder::new);

        WhipContext ctx = buildWhipContext(loc, p, buildNextKey());
        WhipHook whipType = USurviveRegistries.WHIP_HOOK.get(whip.getWhipType());
        whipper.throwNewHook(whipType, ctx);
    }

    public record Hit(Reference<Entity> entity, long time){
        public boolean isValid(Entity owner){
            var e = entity.get();
            if(e == null) return false;
            if(!CruxEntityUtil.isValid(e)) return false;
            if(CruxEntityUtil.isNonSurvival(e)) return false;
            return owner.getWorld().equals(e.getWorld());
        }
    }

    public CruxAttributeAccessor getAttributes() {
        return attributes;
    }

    public Whip getWhip() {
        return whip;
    }

    public int getMaxTentacles() {
        return maxTentacles;
    }

    public NumberProvider getCooldown() {
        return cooldown;
    }

    public NumberProvider getNearRange() {
        return nearRange;
    }

    public Player getP() {
        return p;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public Map<UUID, Hit> getLastAttacked() {
        return lastAttacked;
    }
}
