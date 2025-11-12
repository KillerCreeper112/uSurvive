package killercreepr.usurvive.core.guide;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.data.AutoSavable;
import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.guide.progression.Guide1Progression;
import killercreepr.usurvive.core.guide.progression.GuideProgression;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GuideHolder extends PlayerTickedDataHolder implements AutoSavable, Loadable {
    public static final Key KEY = Crux.key("guide/tutorial");
    public GuideHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
        load();
    }
    public GuideHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }
    protected boolean loaded = false;

    @Override
    protected void removingFromMemory(@Nullable Player e) {
        super.removingFromMemory(e);
        save();
    }

    @Override
    public void onMemoryUnload(@NotNull Entity e) {
        super.onMemoryUnload(e);
        if(e instanceof Player p){
            if(currentProgression != null) currentProgression.onMemoryUnload(p);
        }
    }

    public DataFile getSaveFile(boolean createIfNeeded){
        return BukkitDataFile.parseFromGeneralPath(
            new CruxFolder(USurvivePlugin.inst(), "data/guide/player/" + parent.getUUID() + ".json").file(),
            createIfNeeded
        );
    }

    public boolean deleteSaveFile(){
        DataFile file = getSaveFile(false);
        if(file == null) return false;
        return file.delete();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean hasProgressOrNull(Class<? extends GuideProgression> type){
        return currentProgression == null || type.isAssignableFrom(currentProgression.getClass());
    }

    public GuideProgression getCurrentProgression() {
        return currentProgression;
    }

    public void setCurrentProgression(GuideProgression currentProgression){
        setCurrentProgression(currentProgression, false);
    }

    public void setCurrentProgression(GuideProgression currentProgression, boolean silent) {
        if(!silent && this.currentProgression != null){
            this.currentProgression.onEnd(parent.value(), this);
        }
        this.currentProgression = currentProgression;
        if(currentProgression != null){
            if(isLoaded()){
                currentProgression.onLoaded(parent.value());
            }
            currentProgression.onStart(parent.value(), this);
        }
    }

    public void onLoaded(Player p){
        if(currentProgression != null) currentProgression.onLoaded(p);
        setLoaded(true);
    }

    protected GuideProgression currentProgression;
    protected final Map<Key, Long> completed = new HashMap<>();

    public Map<Key, Long> getCompleted() {
        return completed;
    }

    public boolean isCompleted(Key key){
        return completed.containsKey(key);
    }

    public void setCompleted(Key key, boolean complete){
        if(complete){
            completed.put(key, System.currentTimeMillis());
        }else completed.remove(key);
    }

    public void complete(Key key){
        setCompleted(key, true);
    }

    public void completeAndClearCurrent(){
        if(currentProgression == null) return;
        complete(currentProgression.key());
        var prog = this.currentProgression;
        setCurrentProgression(null, true);
        prog.onComplete(parent.value(), this);
        prog.onEnd(parent.value(), this);
    }

    @Override
    protected void onTick(@NotNull Player p) {
        super.onTick(p);
        if(currentProgression != null){
            currentProgression.onTick(p);
        }
    }

    @Override
    public void save() {
        DataFile cfg = getSaveFile(true);
        cfg.serialize("completed", completed);
        cfg.serialize("current_progression", currentProgression);
        cfg.save();
    }

    @Override
    public void load() {
        DataFile cfg = getSaveFile(false);
        if(cfg == null){
            if(currentProgression == null) setCurrentProgression(new Guide1Progression());
            return;
        }
        setCurrentProgression(cfg.deserialize("current_progression", GuideProgression.class));
        completed.clear();
        completed.putAll(cfg.deserializeOrDefault("completed", new TypeToken<Map<Key, Long>>(){}.getType(), Map.of()));
        cfg.close();

        if(currentProgression == null && !isCompleted(Guide1Progression.KEY)){
            setCurrentProgression(new Guide1Progression());
        }
    }
}
