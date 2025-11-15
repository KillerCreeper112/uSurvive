package killercreepr.usurvive.api.story;

import killercreepr.crux.api.data.CruxKeyed;
import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.common.file.DataFile;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.List;

public interface StoryProgression extends CruxKeyed {
    List<StoryFlag> getRequiredFlags();
    default boolean hasAllFlags(){
        return state().hasAllFlags(getRequiredFlags());
    }
    default void onStepStarted(StoryStep step){}
    default void onStepCompleted(StoryStep step){}
    List<StoryStep> getSteps();
    StoryState state();

    default void onTick(){}

    default void onComplete(Player p, StoryHolder holder) {
        onEnd(p, holder);
    }

    default void onStart(Player p, StoryHolder holder){
        if(this instanceof Listener l){
            Crux.getServer().getPluginManager().registerEvents(l, Crux.getMainPlugin());
        }
    }
    default void onEnd(Player p, StoryHolder holder){
        if(this instanceof Listener l){
            HandlerList.unregisterAll(l);
        }
    }

    default DataFile cfg(){ return null; }

    default void onLoaded(Player p, StoryHolder holder){}
    default void onMemoryUnload(Player p, StoryHolder holder){
        onEnd(p, holder);
    }

    default void load(DataFile file){}
    default void save(DataFile file){}

    StoryProgressData getProgress();
    void setProgress(StoryProgressData data);
}
