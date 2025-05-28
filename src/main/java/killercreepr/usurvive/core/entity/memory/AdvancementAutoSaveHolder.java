package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerDataHolder;
import killercreepr.cruxadvancements.core.config.loader.CfgCrazyAdvancementManagerCfgLoader;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class AdvancementAutoSaveHolder extends PlayerDataHolder implements Loadable {
    public static final Key KEY = Crux.key("usurvive:advancements_auto_save/usurvive");

    protected final CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader;
    public AdvancementAutoSaveHolder(@NotNull Key key, @NotNull PlayerMemory parent, CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader) {
        super(key, parent);
        this.cfgManagerLoader = cfgManagerLoader;
    }

    public AdvancementAutoSaveHolder(@NotNull PlayerMemory parent, CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader) {
        this(KEY, parent, cfgManagerLoader);
    }

    public boolean isLoaded() {
        return parent.getDataHolder(AdvancementLoaderHolder.KEY) == null;
    }

    @Override
    public void save() {
        if(!isLoaded()) return;
        cfgManagerLoader.save(parent.getUUID());
    }

    @Override
    public void load() {

    }
}
