package killercreepr.usurvive.core.entity.tickable;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import killercreepr.crux.core.Crux;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class ActiveEmotiveEnchantTickable extends ListenerActiveEntityTickable implements Listener {
    protected final LivingEntity living;
    public ActiveEmotiveEnchantTickable(Entity entity, EntityTickable tickable, CruxSlot slot) {
        super(entity, tickable, slot);
        living = (LivingEntity) entity;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDismount(EntityDismountEvent event) {
        if(event.getEntity().equals(display)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager().equals(entity)){
            if(!(event.getEntity() instanceof LivingEntity victim)) return;
            double hp = victim.getHealth();
            if(hp - event.getFinalDamage() <= 0D){
                if(victim instanceof Tameable t && t.isTamed() && entity.getUniqueId().equals(t.getOwnerUniqueId())){
                    setTempHPEmote(emoteC("@"), 100);
                }else setTempHPEmote(emoteC("v"), 100);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(!event.getPlayer().equals(entity)) return;
        if(event.getRightClicked() instanceof Tameable){
            setTempHPEmote(emoteC("|"), 40);
        }
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(entity)) return;
        switch (event.getCause()){
            case FIRE, FIRE_TICK, LAVA, FREEZE, WITHER, POISON -> { return; }
        }

        setTempHPEmote(emoteC(")"), 10);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDropItem(BlockDropItemEvent event) {
        if(!event.getPlayer().equals(entity)) return;

        Material type = event.getBlockState().getType();
        if(isShiny(type)){
            if(event.getItems().isEmpty()){
                setTempHPEmote(emoteC(")"), 100);
            }else{
                setTempHPEmote(emoteC(";"), 100);
            }
        }
    }

    public boolean isShiny(Material type){
        if(type == Material.DIAMOND_BLOCK || type == Material.GOLD_BLOCK || type == Material.EMERALD_BLOCK ||
        type == Material.NETHERITE_BLOCK) return true;
        return MaterialSetTag.DIAMOND_ORES.isTagged(type) ||
            MaterialSetTag.GOLD_ORES.isTagged(type) ||
            MaterialSetTag.EMERALD_ORES.isTagged(type) ||
            type == Material.ANCIENT_DEBRIS;
    }


    public LivingEntity getLiving() {
        return living;
    }

    @Override
    public void started() {
        super.started();
        Crux.scheduler().runTask(() ->{
            display = spawnDisplay();
            entity.addPassenger(display);
        });
    }

    @Override
    public void stopped() {
        super.stopped();
        if(display != null){
            Crux.scheduler().runTask(() ->{
                display.remove();
                display = null;
            });
        }
    }

    public TextDisplay spawnDisplay(){
        Location loc = getDisplayLocation();
        return loc.getWorld().spawn(loc, TextDisplay.class, e ->{
            e.setPersistent(false);
            e.setBillboard(Display.Billboard.CENTER);
            e.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            var t = e.getTransformation();
            t.getScale().mul(2f);
            t.getTranslation().add(0, .3f, 0);
            e.setTransformation(t);
        });
    }

    public Location getDisplayLocation(){
        return entity.getLocation().add(0, entity.getHeight()*1.1, 0);
    }
    public Component buildText(){
        double hp = living.getHealth();
        double hpPercent = hp / living.getAttribute(Attribute.MAX_HEALTH).getValue();

        if(living instanceof HumanEntity h){
            double foodPercent = h.getFoodLevel() / 20f;
            return Component.empty()
                .append(getFullHPEmote(hpPercent))
                .append(Component.text(" "))
                .append(getFullFoodEmote(foodPercent));
        }

        return Component.empty()
            .append(getFullHPEmote(hpPercent));
    }

    public Component emoteC(String id){
        return Crux.format().deserialize(emote(id));
    }
    public String emote(String id){
        return "<font:crux:chat_emote>" + id + "</font>";
    }

    public Component getFoodEmote(double hpPercent){
        if(hpPercent > .75D){
            return emoteC("_");//stuck_out_tongue_closed_eyes
        }
        if(hpPercent > .5D){
            return emoteC("-");//stuck_out_tongue
        }
        if(hpPercent > .25D){
            return emoteC("u");//no mouth
        }
        if(hpPercent > .15D){
            return emoteC("=");//tired
        }
        return emoteC("]");//woozy
    }

    public Component getFullFoodEmote(double hpPercent){
        if(living.hasPotionEffect(PotionEffectType.HUNGER)) return emoteC("7");//angry
        return getFoodEmote(hpPercent);
    }

    public Component getFullHPEmote(double hpPercent){
        if(tempHPEmote != null) return tempHPEmote;
        if(living.hasPotionEffect(PotionEffectType.WITHER)) return emoteC("%");//skull
        if(living.hasPotionEffect(PotionEffectType.POISON)) return emoteC("$");//sick
        if(living.getFireTicks() > 5) return emoteC("p");//hot
        if(living.getFreezeTicks() >= living.getMaxFreezeTicks()) return emoteC("a");//cold
        return getHPEmote(hpPercent);
    }

    public Component getHPEmote(double hpPercent){
        if(hpPercent > .75D){
            return emoteC("n");//grin
        }
        if(hpPercent > .5D){
            return emoteC("*");//smile
        }
        if(hpPercent > .25D){
            return emoteC("w");//pensive
        }
        if(hpPercent > .1D){
            return emoteC("~");//fearful
        }
        return emoteC("f");//dizzy
    }

    public void setTempHPEmote(Component emote, int time){
        this.tempHPEmote = emote;
        this.tempHPEmoteTicks = time;
    }

    protected Component tempHPEmote;
    protected int tempHPEmoteTicks;

    protected TextDisplay display;

    @Override
    public void tick() {
        if(tempHPEmoteTicks > 0){
            tempHPEmoteTicks--;
            if(tempHPEmoteTicks < 1){
                tempHPEmote = null;
            }
        }
        if(Crux.getCurrentTick() % 5 != 0){
            return;
        }
        if(display == null) return;

        display.text(buildText());
    }
}
