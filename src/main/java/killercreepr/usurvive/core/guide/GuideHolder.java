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
import killercreepr.usurvive.core.guide.progression.GuideProgression;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    public boolean hasProgressOrNull(Class<? extends GuideProgression> type){
        return currentProgression == null || type.isAssignableFrom(currentProgression.getClass());
    }

    public GuideProgression getCurrentProgression() {
        return currentProgression;
    }

    public void setCurrentProgression(GuideProgression currentProgression) {
        this.currentProgression = currentProgression;
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
        setCurrentProgression(null);
        prog.onComplete(parent.value(), this);
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
        if(cfg == null) return;
        currentProgression = cfg.deserialize("current_progression", GuideProgression.class);
        completed.clear();
        completed.putAll(cfg.deserializeOrDefault("completed", new TypeToken<Map<Key, Long>>(){}.getType(), Map.of()));
        cfg.close();
    }
}
