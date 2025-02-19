package killercreepr.usurvive.core.entity.sleeper;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleColorData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.usurvive.api.entity.sleeper.SlumberData;
import killercreepr.usurvive.api.entity.sleeper.SlumberSneaker;
import killercreepr.usurvive.api.entity.sleeper.SlumberSpectator;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.util.RespawnUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class BaseSlumberData implements SlumberData, SlumberSpectator, SlumberSneaker {
    protected final Entity entity;
    protected final Location location;
    protected Entity display;
    protected Entity spectator;
    protected GameMode previousGameMode;
    protected boolean forceStop = false;

    public BaseSlumberData(Entity entity, Location location) {
        this.entity = entity;
        this.location = location;
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
    }

    @Override
    public void tick() {
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
}
