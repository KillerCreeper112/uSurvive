package killercreepr.usurvive.listener;

import killercreepr.crux.Crux;
import killercreepr.crux.data.communication.Communicator;
import killercreepr.crux.tags.container.MergedTagContainer;
import killercreepr.crux.tags.container.TagContainer;
import killercreepr.crux.util.CruxMath;
import killercreepr.usurvive.lang.Lang;
import killercreepr.usurvive.values.ValuesProvider;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;
import java.util.List;

public class SpawnListener implements Listener {
    protected final ValuesProvider cfg;
    protected final Plugin plugin;

    public SpawnListener(ValuesProvider cfg, Plugin plugin) {
        this.cfg = cfg;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        Player p = event.getPlayer();
        if(p.hasPlayedBefore()) return;
        String worldName = cfg.SPAWN_WORLD().value();
        if(worldName == null) return;
        World world = plugin.getServer().getWorld(worldName);
        if(world == null){
            Communicator.chat("<red>An error has occurred - " + worldName).use(p);
            return;
        }
        event.setSpawnLocation(world.getSpawnLocation());
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if(p.hasPlayedBefore()) return;

        MergedTagContainer tags = TagContainer.merged().hook(p);
        cfg.FIRST_JOIN_COMMANDS().valueOr(List.of()).forEach(cmd ->{
            cmd = Crux.FORMAT.deserializeString(cmd, tags);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
        });

        Lang.FIRST_JOIN_WELCOME.use(p, p, TagContainer.merged().hook(p));
        spawnFireworks(p);
    }

    public void spawnFireworks(Player p){
        int amount = CruxMath.random(4, 6);
        Location center = p.getLocation().toCenterLocation().add(0, CruxMath.random(6, 8), 0);
        while(amount > 0){
            amount--;
            Location spawn = center.clone().add(
                CruxMath.random(-10, 10), CruxMath.random(-3, 3), CruxMath.random(-10, 10)
            );
            spawnFirework(spawn);
        }
    }

    public static final List<Color> colors = List.of(
        Color.WHITE,
        Color.fromRGB(0xF5F367), //yellow
        Color.fromRGB(0xF56B6B), //red
        Color.fromRGB(0xF59146), //orange
        Color.fromRGB(0xF086DE), //pink
        Color.fromRGB(0x89E7F4) //aqua
    );

    public Color buildColor(){
        new java.awt.Color(0x89E7F4);
        return colors.get(CruxMath.random(0, colors.size()-1));
    }

    public Color[] buildColors(){
        return buildColors(CruxMath.random(1, colors.size()));
    }


    public Color[] buildColors(int amount){
        List<Color> list = new ArrayList<>();
        while(amount > 0){
            amount--;
            list.add(buildColor());
        }
        return list.toArray(new Color[0]);
    }

    public FireworkEffect buildFireworkEffect(){
        FireworkEffect.Builder builder = FireworkEffect.builder()
            .trail(true)
            .with(FireworkEffect.Type.values()[CruxMath.random(0, FireworkEffect.Type.values().length-1)])
            .flicker(CruxMath.random().nextBoolean())
            .withColor(buildColors());
        if(CruxMath.random().nextBoolean()){
            builder.withFade(buildColors());
        }
        return builder.build();
    }

    public void spawnFirework(Location l){
        l.getWorld().spawn(l, Firework.class, e ->{
            int ticksToDetonate = 10 + (CruxMath.random(0, 5) + 1) + CruxMath.random(0, 5) + CruxMath.random(0, 6);
            e.setTicksToDetonate(ticksToDetonate);
            e.setVelocity(new Vector(0, 1, 0));
            FireworkMeta meta = e.getFireworkMeta();
            meta.addEffect(buildFireworkEffect());
            e.setFireworkMeta(meta);
        });
    }
}
