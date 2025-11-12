package killercreepr.usurvive.core.config.story;

import killercreepr.crux.api.data.Reloadable;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@killercreepr.cruxconfig.config.common.annotations.Config(autoUpdate = true)
public class StoryConfig extends Cfg implements Reloadable {

    public StoryConfig(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public StoryConfig(@NotNull File file) {
        super(file);
    }

    public StoryConfig(@NotNull CruxConfig cfg) {
        super(cfg);
    }

    @Override
    public void reload(@NotNull CruxPlugin plugin) {
        reload();
        setup();
    }
}
