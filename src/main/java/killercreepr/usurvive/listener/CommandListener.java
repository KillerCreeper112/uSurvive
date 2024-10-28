package killercreepr.usurvive.listener;

import killercreepr.crux.data.communication.Communicator;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.registries.CruxRegistries;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CommandListener implements Listener {
    protected final List<String> commands = Arrays.asList("?", "pl", "about", "version", "ver", "plugins", "bukkit:?", "bukkit:pl", "bukkit:about", "bukkit:version",
        "bukkit:ver", "bukkit:plugins", "minecraft:pl", "minecraft:plugins", "minecraft:about", "minecraft:version", "minecraft:ver", "icanhasbukkit");
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        Player p = event.getPlayer();
        for (String all : commands) {
            if(!msg.equalsIgnoreCase("/" + all)) continue;
            event.setCancelled(true);

            List<CruxPlugin> plugins = new ArrayList<>(CruxRegistries.PLUGIN.values());
            plugins.sort(Comparator.comparing(PluginBase::getName));
            List<String> chat = new ArrayList<>(List.of(
                "<white>Server Plugins: (" + plugins.size() + ")",
                "<#0286CE>Paper Plugins:"
            ));
            StringBuilder builder = new StringBuilder(" <dark_gray>- ");
            int index = 0;
            for(CruxPlugin plugin : plugins){
                index++;
                builder.append("<green>").append(plugin.getName());
                if(index < plugins.size()) builder.append("<white>, ");
            }
            chat.add(builder.toString());
            Communicator.chat(chat).use(p);
            return;
        }
    }
}
