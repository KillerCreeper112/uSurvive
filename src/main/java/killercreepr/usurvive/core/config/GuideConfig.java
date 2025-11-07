package killercreepr.usurvive.core.config;

import killercreepr.crux.api.data.Reloadable;
import killercreepr.crux.api.data.WorldPosition;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.usurvive.api.values.GuideCfg;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

@killercreepr.cruxconfig.config.common.annotations.Config(autoUpdate = true)
public class GuideConfig extends Cfg implements GuideCfg, Reloadable {
    public final CfgValue<List<WorldPosition>> GUIDE_1_POINTS = new CommonValue<>(){};

    public GuideConfig(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public GuideConfig(@NotNull File file) {
        super(file);
    }

    public GuideConfig(@NotNull CruxConfig cfg) {
        super(cfg);
    }

    @Override
    public CfgValue<List<WorldPosition>> GUIDE_1_POINTS() {
        return GUIDE_1_POINTS;
    }

    @Override
    public void reload(@NotNull CruxPlugin plugin) {
        reload();
        setup();
    }
}
