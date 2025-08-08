package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.core.Crux;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

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
                .append(getHPEmote(hpPercent))
                .append(Component.text(" "))
                .append(getFoodEmote(foodPercent));
        }

        return Component.empty()
            .append(getHPEmote(hpPercent));
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
        if(hpPercent > .15D){
            return emoteC("l");//fearful
        }
        return emoteC("~");//scream
    }

    protected TextDisplay display;

    @Override
    public void tick() {
        if(Crux.getCurrentTick() % 5 != 0) return;
        if(display == null) return;
        display.text(buildText());
    }
}
