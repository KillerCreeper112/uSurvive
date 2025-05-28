package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityDataHolder;
import killercreepr.cruxadvancements.core.config.loader.CfgCrazyAdvancementManagerCfgLoader;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancementSaverHolder extends EntityDataHolder {
    public static final Key KEY = Crux.key("usurvive:advancements_save/usurvive");

    protected final CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader;
    protected final Player p;

    public AdvancementSaverHolder(@NotNull Key key, @NotNull EntityMemory parent, CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader, Player p) {
        super(key, parent);
        this.cfgManagerLoader = cfgManagerLoader;
        this.p = p;
    }


    public AdvancementSaverHolder(@NotNull EntityMemory parent, CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader, Player p) {
        this(KEY, parent, cfgManagerLoader, p);
    }

    @Override
    protected void removingFromMemory(@Nullable Entity e) {
        super.removingFromMemory(e);
        cfgManagerLoader.saveAndUnload(p);
    }
}
