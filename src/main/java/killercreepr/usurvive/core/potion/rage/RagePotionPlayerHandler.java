package killercreepr.usurvive.core.potion.rage;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.usurvive.core.potion.RagePotion;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class RagePotionPlayerHandler extends RagePotionHandler {
    protected final Player p;
    protected GameMode lastGameMode;
    protected Mob johnny;
    public RagePotionPlayerHandler(@NotNull Player entity, @NotNull RagePotion effect) {
        super(entity, effect);
        this.p = entity;
    }

    protected boolean isJohnny(@NotNull Entity e){
        return e.equals(johnny);
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event){
        if(!event.getEntity().equals(johnny) && !event.getEntity().equals(entity)) return;
        //johnny died, cus
        if(isJohnny(event.getEntity())){
            event.getDrops().clear();
            p.setGameMode(lastGameMode);
            entity.damage(9999999999D, event.getEntity().getKiller());
            return;
        }
    }

    @Override
    public void onPlayerStopSpectatingEntity(PlayerStopSpectatingEntityEvent event) {
        super.onPlayerStopSpectatingEntity(event);
        if(!event.getPlayer().equals(entity)) return;
        if(!event.getSpectatorTarget().isValid()) return;
        event.setCancelled(true);
    }

    @Override
    public void onPlayerStartSpectatingEntity(PlayerStartSpectatingEntityEvent event) {
        super.onPlayerStartSpectatingEntity(event);
        if(!event.getPlayer().equals(entity)) return;
        if(!event.getNewSpectatorTarget().equals(johnny)) event.setCancelled(true);
    }

    @Override
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        super.onPlayerGameModeChange(event);
        if(!event.getPlayer().equals(entity)) return;
        if(event.getNewGameMode() == GameMode.SPECTATOR) return;
        effect.setDuration(0, true);
    }

    @Override
    public void started() {
        johnny = spawnJohnny();
        PlayerDisguise playerDisguise = new PlayerDisguise(p);
        if(p.hasPotionEffect(PotionEffectType.GLOWING)) playerDisguise.getWatcher().setGlowing(true);
        if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) playerDisguise.getWatcher().setInvisible(true);
        /*for(PotionEffect e : p.getActivePotionEffects()){
            if(!e.hasParticles()) continue;
            playerDisguise.getWatcher().addPotionEffect(e.getType());
        }*/
        DisguiseAPI.disguiseEntity(johnny, playerDisguise);

        lastGameMode = p.getGameMode();
        p.setGameMode(GameMode.SPECTATOR);
        Bukkit.getScheduler().runTaskLater(Crux.getMainPlugin(), task -> p.setSpectatorTarget(johnny), 2L);
    }

    @Override
    public void tick() {
        super.tick();
        if(johnny != null && !johnny.isValid()){
            effect.setDuration(0, true);
        }else if(johnny != null && Crux.getCurrentTick() % 5 == 0) spawnParticle(johnny);
    }

    @Override
    public void stopped() {
        super.stopped();
        Crux.scheduler().runTask(() ->{
            if(johnny != null){
                double hp = johnny.getHealth();
                p.setHealth(
                    CruxMath.clamp(hp, 0D, p.getAttribute(Attribute.MAX_HEALTH).getValue())
                );

                johnny.remove();
            }
            if(lastGameMode != null) p.setGameMode(lastGameMode);
        });
    }
}
