package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class WaterboundPotion extends ActiveEntityPotion<LivingEntity> {
    public WaterboundPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
        if(getParsedEntity() == null) return;
        onUpdate(getParsedEntity(), duration, amplifier);
    }

    @Override
    protected void onUpdate(@NotNull LivingEntity e, int oldDuration, int oldAmplifier) {
        subtractAir = 1 + (amplifier*2);
        damageTicks = Math.max(20 - (amplifier), 8);
    }

    protected boolean inWater;
    protected int subtractAir;
    protected boolean died = false;
    protected int damageTicks = 20;

    @Override
    protected void onTick(@NotNull LivingEntity e) {
        if(e instanceof Player p){
            switch (p.getGameMode()){
                case CREATIVE, SPECTATOR -> {
                    return;
                }
            }
        }
        inWater = e.getEyeLocation().getBlock().getType() == Material.WATER;
        Crux.scheduler().runTask(() ->{
            if(inWater){
                if(e.getRemainingAir() < 0) e.setRemainingAir(0);
                e.setRemainingAir(Math.min(e.getRemainingAir()+4, e.getMaximumAir()));
            }else{
                e.setRemainingAir(Math.min(e.getRemainingAir()-subtractAir, e.getMaximumAir()));
                if(e.getRemainingAir() < 0 && e.getRemainingAir() % 20 == 0){
                    if(e.getHealth()-2D <= 0D) died = true;
                    e.damage(2D);
                    e.getWorld().playSound(e.getLocation(), Sound.ENTITY_PLAYER_HURT_DROWN, 1f, 1f);
                }
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!event.getPlayer().equals(entity)) return;
        if(died){
            event.deathMessage(
                Crux.format().deserialize(entity.getName() + " drowned....... on land.")
            );
        }
    }

    @Override
    protected void onStart(@NotNull LivingEntity e) {
        lastRemainingAir = e.getRemainingAir();
    }

    private int lastRemainingAir;
    @EventHandler
    public void onEntityAirChange(EntityAirChangeEvent event) {
        if(!event.getEntity().equals(entity)) return;
        if(inWater){
            if(event.getAmount() < lastRemainingAir){
                event.setCancelled(true);
                return;
            }
        }else{
            if(event.getAmount() > lastRemainingAir){
                event.setCancelled(true);
                return;
            }
        }
        lastRemainingAir = event.getAmount();
    }
}
