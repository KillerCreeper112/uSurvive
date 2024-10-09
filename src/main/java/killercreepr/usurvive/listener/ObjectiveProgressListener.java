package killercreepr.usurvive.listener;

import killercreepr.crux.tags.container.MergedTagContainer;
import killercreepr.crux.tags.container.TagContainer;
import killercreepr.cruxadvancements.advancement.ObjectiveAdvancement;
import killercreepr.cruxadvancements.event.objective.NumberObjectiveProgressChangeEvent;
import killercreepr.usurvive.lang.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ObjectiveProgressListener implements Listener {
    protected final Plugin plugin;
    public ObjectiveProgressListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onNumberObjectiveProgressChange(NumberObjectiveProgressChangeEvent event) {
        //don't send any progress message if it has been completed
        if(event.getNewProgress() >= event.getObjective().getMaxProgress()) return;

        Player p = plugin.getServer().getPlayer(event.getWho());
        if(p == null) return;
        ObjectiveAdvancement advancement = event.getAdvancement();
        plugin.getServer().getScheduler().runTask(plugin, task ->{
            MergedTagContainer tags = TagContainer.merged()
                .hook(advancement)
                .hook(p)
                ;
            Lang.ADVANCEMENT_PROGRESS.use(p, tags);
        });
    }
}
