package killercreepr.usurvive.core.story.progression;

import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.api.story.StoryStep;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
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
    protected transient final Map<UUID, TextDisplay> textDisplays = new HashMap<>();
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

    public void spawnGuide1Exclaim(World world){
        spawnNPCExclaim(world, "npc_guide_1");
    }

    public void spawnNPCExclaim(World world, String npc){
        Crux.scheduler().runTaskMain(() ->{
            applyLivingEntity(world, e -> isMythicNPC(e, npc), e ->{
                Location loc = e.getLocation().add(0, e.getHeight() + 0.75, 0);
                createTextIfNeeded(e.getUniqueId(), loc, text ->{
                    text.setBillboard(Display.Billboard.CENTER);
                    text.text(Crux.format().deserialize("<yellow><bold>!"));
                    var t = text.getTransformation();
                    t.getScale().mul(2);
                    text.setTransformation(t);
                });
            });
        });
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
