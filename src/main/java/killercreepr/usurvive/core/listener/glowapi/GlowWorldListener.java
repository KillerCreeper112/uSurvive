package killercreepr.usurvive.core.listener.glowapi;

import fr.skytasul.glowingentities.GlowingEntities;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.core.Crux;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GlowWorldListener implements Listener {
    protected final World world;
    protected final GlowingEntities glowingEntities;
    protected final Map<GlowingEntities.EntityIdentifier, Data> data = new HashMap<>();

    public GlowWorldListener(World world, GlowingEntities glowingEntities) {
        this.world = world;
        this.glowingEntities = glowingEntities;
    }

    public void apply(Player p){
        for (Data data : data.values()) {
            data.apply(p);
        }
    }

    public void unapply(Player p){
        for (Data data : data.values()) {
            data.unapply(p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        var e = event.getEntity();
        if(!world.equals(e.getWorld())) return;
        data.remove(new GlowingEntities.EntityidentifierRealEntity(e));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event){
        Player p = event.getPlayer();
        new BukkitRunnable(){
            int tick = 0;
            @Override
            public void run() {
                tick++;
                if(!p.isOnline()){
                    cancel();
                    return;
                }

                if(tick >= 15){
                    cancel();
                    if(!p.getWorld().equals(world)) return;
                    apply(p);
                }
            }
        }.runTaskTimer(Crux.getMainPlugin(), 0L, 1L);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event){
        Player p = event.getPlayer();
        if(event.getFrom().equals(world)){
            unapply(p);
            return;
        }
        if(!world.equals(p.getWorld())) return;
        apply(p);
    }

    public void forEachPlayer(Consumer<Player> p){
        world.getPlayers().forEach(p);
    }

    public void setGlowing(Entity entity) {
        setGlowing(entity, null);
    }

    public void setData(GlowingEntities.EntityIdentifier id, Data data){
        this.data.put(id, data);
    }

    public Data removeData(GlowingEntities.EntityIdentifier id){
        return this.data.remove(id);
    }

    public void setGlowing(Entity entity, ChatColor color) {
        var id = new GlowingEntities.EntityidentifierRealEntity(entity);
        Data data = new Data(
            id,
            entityHolder(entity),
            (i, p) -> {
                try {
                    glowingEntities.setGlowing(i, p, color);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            },
            (i, p) ->{
                try {
                    glowingEntities.unsetGlowing(i, p);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        );
        this.data.put(data.id, data);
        forEachPlayer(data::apply);
    }

    public void unsetGlowing(Entity entity){
        unsetGlowing(new GlowingEntities.EntityidentifierRealEntity(entity));
    }

    public void unsetGlowing(GlowingEntities.EntityIdentifier id){
        var data = removeData(id);
        if(data == null) return;
        forEachPlayer(this::unapply);
    }

    public Holder<Entity> entityHolder(Entity e){
        return Holder.weakReference(e);
    }

    public static class Data{
        public final GlowingEntities.EntityIdentifier id;
        public final Holder<Entity> entity;
        public final BiConsumer<Entity, Player> apply;
        public final BiConsumer<GlowingEntities.EntityIdentifier, Player> unapply;

        public Data(GlowingEntities.EntityIdentifier id, Holder<Entity> entity, BiConsumer<Entity, Player> apply, BiConsumer<GlowingEntities.EntityIdentifier, Player> unapply) {
            this.id = id;
            this.entity = entity;
            this.apply = apply;
            this.unapply = unapply;
        }

        public void apply(Player p){
            var entity = this.entity.value();
            if(entity == null) return;
            apply.accept(entity, p);
        }

        public void unapply(Player p){
            unapply.accept(id, p);
        }
    }
}
