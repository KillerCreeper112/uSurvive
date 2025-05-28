package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.cruxadvancements.core.config.loader.CfgCrazyAdvancementManagerCfgLoader;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancementLoaderHolder extends PlayerTickedDataHolder {
    public static final Key KEY = Crux.key("usurvive:advancements_load/usurvive");

    protected final CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader;
    public AdvancementLoaderHolder(@NotNull Key key, @NotNull PlayerMemory parent, CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader) {
        super(key, parent);
        this.cfgManagerLoader = cfgManagerLoader;
    }


    public AdvancementLoaderHolder(@NotNull PlayerMemory parent, CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader) {
        this(KEY, parent, cfgManagerLoader);
    }

    protected boolean loaded = false;
    protected int tick = 0;
    @Override
    public boolean shouldRemoveFromMemory(@Nullable Player e) {
        return super.shouldRemoveFromMemory(e) || loaded;
    }

    @Override
    protected void onTick(@NotNull Player e) {
        super.onTick(e);
        tick++;
        if(tick >= 20){
            cfgManagerLoader.load(e);
            setLoaded(true);
        }
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
