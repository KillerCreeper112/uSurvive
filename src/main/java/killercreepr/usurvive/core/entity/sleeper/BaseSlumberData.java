package killercreepr.usurvive.core.entity.sleeper;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleColorData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.persistence.CruxPersist;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.api.entity.sleeper.SlumberData;
import killercreepr.usurvive.api.entity.sleeper.SlumberSneaker;
import killercreepr.usurvive.api.entity.sleeper.SlumberSpectator;
import killercreepr.usurvive.api.entity.sleeper.SlumberTargetLogic;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.util.RespawnUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class BaseSlumberData implements SlumberData, SlumberSpectator, SlumberSneaker, SlumberTargetLogic {
    protected final Entity entity;
    protected final HumanEntity humanE;
    protected final Location location;
    protected Entity display;
    protected Entity spectator;
    protected Entity target;
    protected GameMode previousGameMode;
    protected boolean forceStop = false;

    protected EntityDamageEvent targetDmg;
    protected boolean starving = false;

    public BaseSlumberData(Entity entity, Location location) {
        this.entity = entity;
        this.location = location;
        if(entity instanceof HumanEntity h) humanE = h;
        else humanE = null;
    }

    public Location getLocation() {
        return location;
    }

    public Location getUserTeleportLocation(){
        return location;
    }

    public Location getDisplayLocation(){
        return location;
    }

    public Location getTargetLocation(){
        return location;
    }

    @Override
    public boolean shouldStop() {
        return forceStop || !CruxEntityUtil.isValid(entity) || (display != null && !display.isValid()) || (spectator != null && !spectator.isValid());
    }

    protected Boolean wasVisibleByDefault;
    @Override
    public void started() {
        if(!(entity instanceof Player p)){
            entity.setPose(Pose.SLEEPING, true);
            return;
        }

        spectator = USurviveMobs.SLEEPER_OBJECT.spawn(getUserTeleportLocation());
        display = USurviveMobs.SLEEPER_DISPLAY.spawn(getDisplayLocation());

        switch (p.getGameMode()){
            case CREATIVE, SPECTATOR -> {}
            default -> {
                target = USurviveMobs.SLEEP_TARGET.spawn(getTargetLocation(),e ->{
                    CruxPersist.OWNER.set(e, entity.getUniqueId());
                });
            }
        }

        PlayerDisguise disguise = new PlayerDisguise(p);

        for(EquipmentSlot slot : EquipmentSlot.values()){
            if(!p.canUseEquipmentSlot(slot)) continue;
            ItemStack item = p.getInventory().getItem(slot);
            if(CruxItem.isEmpty(item)) continue;
            disguise.getWatcher().setItemStack(slot, item);
        }

        for(PotionEffect effect : p.getActivePotionEffects()){
            if(!effect.hasParticles()) continue;
            disguise.getWatcher().addParticle(new Particle<>(ParticleTypes.EFFECT, new ParticleColorData(
                effect.getType().getColor().asRGB()
            )));
        }

        disguise.setName("");
        disguise.setNameVisible(false);
        disguise.getWatcher().setSleeping(true);
        DisguiseAPI.disguiseEntity(display, disguise);

        previousGameMode = p.getGameMode();
        //wasVisibleByDefault = p.isVisibleByDefault();
        //p.setVisibleByDefault(false);
        p.setGameMode(GameMode.SPECTATOR);
        p.teleport(spectator);
        if(p.getGameMode() == GameMode.SPECTATOR && spectator != null) p.setSpectatorTarget(spectator);
    }

    @Override
    public void stopped() {
        if(!(entity instanceof Player p)){
            entity.setPose(Pose.SLEEPING, true);
        }else{
            if(wasVisibleByDefault != null){
                p.setVisibleByDefault(wasVisibleByDefault);
            }
            if(p.getGameMode() == GameMode.SPECTATOR && spectator != null){
                p.setSpectatorTarget(null);
                if(previousGameMode != null) p.setGameMode(previousGameMode);
            }
            Location spawn = RespawnUtil.findRespawnLocationOrTop(location.getBlock());
            p.teleport(spawn, PlayerTeleportEvent.TeleportCause.EXIT_BED);
        }
        if(display != null) display.remove();
        if(spectator != null) spectator.remove();
        if(target != null) target.remove();

        if(targetDmg != null) damage(targetDmg);
        if(starving){
            EntityDamager.entityDamager(entity)
                .setCause(CruxEntityDamageEvent.DamageCause.STARVATION)
                .setSource(DamageSource.builder(DamageType.STARVE).build())
                .attack(1D);
        }
    }

    protected int hunger;
    @Override
    public void tick() {
        if(humanE == null) return;
        if(humanE.getFoodLevel() > 0) return;
        if(previousGameMode == GameMode.CREATIVE || previousGameMode == GameMode.SPECTATOR) return;
        hunger++;
        if(hunger < 100){
            return;
        }
        hunger = 0;
        forceStop = true;
        starving = true;
        //entity.teleportAsync(getUserTeleportLocation(), PlayerTeleportEvent.TeleportCause.UNKNOWN);
    }

    @Override
    public void onStopSpectating(PlayerStopSpectatingEntityEvent event) {
        event.setCancelled(true);
        forceStop = true;
    }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        if(!event.isSneaking()) return;
        forceStop = true;
    }

    public void damage(EntityDamageEvent event){
        Entity damager;
        if(event instanceof EntityDamageByEntityEvent by){
            damager = by.getDamager();
        }else damager = null;
        EntityDamager.entityDamager(entity, damager)
            .setCause(CruxEntityDamageEvent.DamageCause.grim(event.getCause()))
            .setSource(event.getDamageSource())
            .attack(event.getDamage());
    }

    @Override
    public void onTargetEntityTakeDamage(EntityDamageEvent event) {
        forceStop = true;
        targetDmg = event;
    }
}
