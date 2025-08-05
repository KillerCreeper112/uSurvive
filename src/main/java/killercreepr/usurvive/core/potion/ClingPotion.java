package killercreepr.usurvive.core.potion;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.communication.CreateTitle;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxmenus.core.registries.Menus;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.api.potion.inflictor.PotionInflictor;
import killercreepr.cruxpotions.core.potions.InflictedPotion;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.usurvive.core.damage.type.USurviveDamageTypes;
import killercreepr.usurvive.core.menu.ClingEffectMenu;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClingPotion extends InflictedPotion implements Listener {
    protected final boolean isPlayer;
    public ClingPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
        isPlayer = entity instanceof HumanEntity;
    }

    public ClingPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
        super(potion, entity, duration, amplifier, inflictor);
        isPlayer = entity instanceof HumanEntity;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if(!event.getPlayer().equals(entity)) return;
        Player p = event.getPlayer();
        event.setCancelled(true);

        var menu = new ClingEffectMenu(p, maxClicks, e ->{
            duration = Math.max(0, duration - 600);
            CreateSound.sound(Sound.ENTITY_PLAYER_LEVELUP, .35f, 2f).playAt(e);
        });
        menu.load();
        menu.open(p);
        CreateSound.sound(Sound.ITEM_LEAD_BREAK, .5f, 2f).playAt(p);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(Menus.getOpened(entity) instanceof ClingEffectMenu){
            if(entity instanceof HumanEntity h){
                Crux.scheduler().runTask(() ->{
                    h.closeInventory();
                });
            }
        }
    }

    @Override
    protected void onUpdate(int oldDuration, int oldAmplifier) {
        super.onUpdate(oldDuration, oldAmplifier);
        maxClicks = calculateMaxClicks();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cooldown = calculateCooldown();
        maxClicks = calculateMaxClicks();
    }

    protected int tick = 0;
    protected int cooldown;
    protected int maxClicks;
    @Override
    protected void onTick() {
        super.onTick();
        tick++;
        if(tick < cooldown) return;
        tick = 0;
        cooldown = calculateCooldown();
        Crux.scheduler().runTask(() ->{
            damage(1D, cooldown);
        });
    }

    public int calculateMaxClicks(){
        return CruxMath.random(
            3 + (int) (amplifier*1.5f),
            5 + (amplifier*2)
        );
    }

    public int calculateCooldown(){
        return CruxMath.random(
            Math.max(10, 35 - (amplifier*10)),
            Math.max(20, 50 - (amplifier*5))
        );
    }

    public void damage(double dmg, int cooldown){
        Location loc = entity.getLocation().add(
            CruxMath.random(-1, 1),
            CruxMath.random(-1, 1),
            CruxMath.random(-1, 1)
        );

        var dmger = EntityDamager.entityDamager(entity)
            .setHitPosition(loc);
        var source = DamageSource.builder(USurviveDamageTypes.CLING)
            .withDamageLocation(loc);
        if(inflictor instanceof EntityInflictor inflict){
            var inflictorEntity = inflict.value();
            dmger.setDamager(inflictorEntity);
            if(inflictorEntity != null){
                source.withDirectEntity(inflictorEntity);
            }
        }
        dmger.setSource(source.build())
            .attack(dmg);
        CreateSound.sound(Sound.ENTITY_PLAYER_ATTACK_STRONG, .4f, 2f)
            .playAt(entity);
        //optimize for other entities
        if(!isPlayer) return;

        int fadeOutTicks = (int) Math.ceil(cooldown * .15f);
        int stayTicks = cooldown - fadeOutTicks;

        String spacing = "<crux_space:" + CruxMath.random(-100, 100) + ">";
        CreateTitle title;
        if(CruxMath.random().nextBoolean()){
            title = CreateTitle.title(
                null, spacing + "<font:crux:cling>1", 0, stayTicks, fadeOutTicks
            );
        }else{
            title = CreateTitle.title(
                spacing + "<font:crux:cling>1", null, 0, stayTicks, fadeOutTicks
            );
        }
        title.use(entity);
    }
}
