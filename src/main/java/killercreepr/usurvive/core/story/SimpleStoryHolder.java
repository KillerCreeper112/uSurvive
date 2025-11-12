package killercreepr.usurvive.core.story;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.data.AutoSavable;
import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.api.story.StoryProgressData;
import killercreepr.usurvive.api.story.StoryProgression;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.guide.progression.Guide1Progression;
import killercreepr.usurvive.core.guide.progression.GuideProgression;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleStoryHolder extends PlayerTickedDataHolder implements AutoSavable, Loadable, StoryHolder {
    public static final Key KEY = Crux.key("story/main");
    public SimpleStoryHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
        load();
    }
    public SimpleStoryHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }
    protected boolean loaded = false;
    protected final Map<Key, StoryProgressData> storyProgressData = new HashMap<>();
    protected final Map<Key, StoryProgression> storyProgression = new HashMap<>();

    @Override
    protected void removingFromMemory(@Nullable Player e) {
        super.removingFromMemory(e);
        save();
    }

    @Override
    public void onMemoryUnload(@NotNull Entity e) {
        super.onMemoryUnload(e);
        if(e instanceof Player p){
            for (StoryProgression value : storyProgression.values()) {
                value.onMemoryUnload(p, this);
            }
        }
    }

    public DataFile getSaveFile(boolean createIfNeeded){
        return BukkitDataFile.parseFromGeneralPath(
            new CruxFolder(USurvivePlugin.inst(), "data/story/player/" + parent.getUUID() + ".json").file(),
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

    @Override
    protected void onTick(@NotNull Player p) {
        super.onTick(p);
        for (StoryProgression value : storyProgression.values()) {
            value.onTick();
        }
    }

    @Override
    public void save() {
        DataFile cfg = getSaveFile(true);
        cfg.serialize("completed", storyProgressData);
        cfg.serialize("current_progression", storyProgression);
        cfg.save();
    }

    @Override
    public void load() {
        DataFile cfg = getSaveFile(false);
        if(cfg == null){
            //if(currentProgression == null) setCurrentProgression(new Guide1Progression());
            return;
        }
        storyProgressData.putAll(cfg.deserializeOrDefault("current_progression", new TypeToken<Map<Key, StoryProgression>>(){}.getType(), Map.of()));
        storyProgressData.putAll(cfg.deserializeOrDefault("completed", new TypeToken<Map<Key, StoryProgressData>>(){}.getType(), Map.of()));
        cfg.close();

        /*if(currentProgression == null && !isCompleted(Guide1Progression.KEY)){
            setCurrentProgression(new Guide1Progression());
        }*/
    }

    @Override
    public Collection<StoryProgression> getCurrentStoryProgression() {
        return storyProgression.values();
    }

    @Override
    public Map<Key, StoryProgressData> getStoryProgressData() {
        return storyProgressData;
    }

    @Override
    public StoryProgressData getStoryProgressData(Key key) {
        return storyProgressData.get(key);
    }

    @Override
    public StoryProgression getStoryProgression(Key key) {
        return storyProgression.get(key);
    }

    @Override
    public boolean hasCompleted(Key storyKey) {
        StoryProgressData data = getStoryProgressData(storyKey);
        return data != null && data.isComplete();
    }

    @Override
    public Player player() {
        return parent.value();
    }
}
