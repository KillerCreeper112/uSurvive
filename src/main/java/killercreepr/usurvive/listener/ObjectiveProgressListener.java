package killercreepr.usurvive.listener;

import killercreepr.crux.tags.container.MergedTagContainer;
import killercreepr.crux.tags.container.TagContainer;
import killercreepr.crux.tags.resolver.Tag;
import killercreepr.cruxadvancements.advancement.ObjectiveAdvancement;
import killercreepr.cruxadvancements.advancement.icon.CriterionDisplay;
import killercreepr.cruxadvancements.advancement.objective.NumberObjective;
import killercreepr.cruxadvancements.event.objective.PostNumberObjectiveProgressChangeEvent;
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
    public void onPostNumberObjectiveProgressChange(PostNumberObjectiveProgressChangeEvent event) {
        ObjectiveAdvancement advancement = event.getAdvancement();
        if(advancement.isGranted(event.getWho())) return;

        Player p = plugin.getServer().getPlayer(event.getWho());
        if(p == null) return;
        CriterionDisplay display = advancement.getIcon().getCriterionDisplay();
        MergedTagContainer tags = TagContainer.merged()
            .hook(advancement)
            .hook(p)
            ;
        NumberObjective objective = event.getObjective();
        if(display == null || display.getCriterionFormat(objective.getCriterion()) == null){
            Lang.ADVANCEMENT_PROGRESS.use(p, tags);
            return;
        }
        tags.add(Tag.parsed("progress", event.getNewProgress() + ""))
            .add(Tag.parsed("max", objective.getMaxProgress() + ""))
            .add(Tag.parsed("format", display.getCriterionFormat(objective.getCriterion()) + ""));

        Lang.ADVANCEMENT_PROGRESS_CRITERION_DISPLAY.use(p, tags);
    }
}
