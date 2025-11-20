package killercreepr.usurvive.core.story.progression;

import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.api.story.StoryStep;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class ExclaimHolderStoryProgress extends StoryHolderStoryProgress implements Listener {
    protected final Map<UUID, TextDisplay> textDisplays = new HashMap<>();
    protected TextDisplay createText(UUID entity, Location loc, Consumer<TextDisplay> consumer){
        var e = loc.getWorld().spawn(loc, TextDisplay.class, spawned ->{
            spawned.setPersistent(false);
            spawned.setVisibleByDefault(false);
            p.showEntity(Crux.getMainPlugin(), spawned);
            if(consumer != null) consumer.accept(spawned);
        });
        textDisplays.put(entity, e);
        return e;
    }

    protected TextDisplay createTextIfNeeded(UUID entity, Location loc, Consumer<TextDisplay> consumer){
        var e = textDisplays.remove(entity);
        if(e != null) return e;
        return createText(entity, loc, consumer);
    }

    public void forEachTexts(Consumer<TextDisplay> consumer){
        textDisplays.values().forEach(consumer);
    }

    public void clearTexts(){
        forEachTexts(Entity::remove);
        textDisplays.clear();
    }

    @Override
    public void onStepCompleted(StoryStep step) {
        super.onStepCompleted(step);
        clearTexts();
    }

    @Override
    public void onEnd(Player p, StoryHolder holder) {
        super.onEnd(p, holder);
        clearTexts();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangedWorldTexts(PlayerChangedWorldEvent event) {
        applyCurrentStep(StoryStep.ChangedWorld.class, step ->{
            step.onChangedWorld(event.getFrom());
        });
        clearTexts();
    }

}
