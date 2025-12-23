package killercreepr.usurvive.core.listener.glowapi;

import fr.skytasul.glowingentities.GlowingEntities;
import killercreepr.usurvive.external.glowing.GlowAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;

public class GlowingInitListener implements Listener {
    protected final Plugin plugin;

    public GlowingInitListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorld(WorldLoadEvent event){
        if(GlowAPI.glowingEntities() == null){
            GlowAPI.glowingEntities(new GlowingEntities(plugin));
        }
        HandlerList.unregisterAll(this);
    }
}
