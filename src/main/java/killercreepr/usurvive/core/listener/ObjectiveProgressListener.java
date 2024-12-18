package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.cruxadvancements.api.advancement.CruxAdvancement;
import killercreepr.cruxadvancements.api.advancement.ObjectiveAdvancement;
import killercreepr.cruxadvancements.api.advancement.icon.CriterionDisplay;
import killercreepr.cruxadvancements.api.event.CruxAdvancementGrantEvent;
import killercreepr.cruxadvancements.api.event.objective.PostNumberObjectiveProgressChangeEvent;
import killercreepr.cruxadvancements.core.advancement.objective.NumberObjective;
import killercreepr.usurvive.core.lang.Lang;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCruxAdvancementGrant(CruxAdvancementGrantEvent event) {
        Player p = plugin.getServer().getPlayer(event.getWho());
        if(p == null) return;
        CruxAdvancement advancement = event.getAdvancement();
        if(!(advancement instanceof ObjectiveAdvancement)) return;

        MergedTagContainer tags = TagContainer.merged()
            .hook(advancement)
            .hook(p)
            ;
        Lang.ADVANCEMENT_COMPLETED.use(p, tags);
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
