package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.cruxadvancements.core.config.loader.CfgCrazyAdvancementManagerCfgLoader;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementLoaderHolder;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class USurviveAdvancementLoaderHolder extends AdvancementLoaderHolder {
    public static final Key KEY = Crux.key("usurvive:advancements_load/usurvive");

    public USurviveAdvancementLoaderHolder(@NotNull Key key, @NotNull PlayerMemory parent, CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader) {
        super(key, parent, cfgManagerLoader);
    }


    public USurviveAdvancementLoaderHolder(@NotNull PlayerMemory parent, CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader) {
        this(KEY, parent, cfgManagerLoader);
    }
}
