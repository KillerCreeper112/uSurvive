package killercreepr.usurvive.core.config;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.data.Reloadable;
import killercreepr.crux.api.data.WorldLocation;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.guide.GuidePoint;
import killercreepr.usurvive.api.values.GuideCfg;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

@killercreepr.cruxconfig.config.common.annotations.Config(autoUpdate = true)
public class GuideConfig extends Cfg implements GuideCfg, Reloadable {
    public final CfgValue<WorldLocation> GUIDE_1_SPAWN = new CommonValue<>(){};
    public final CfgValue<List<GuidePoint>> GUIDE_1_POINTS = new CommonValue<>(){};
    public final NumCfgValue GUIDE_1_MAX_PARTICLE_POINTS = new NumCfgValue(NumberProvider.constant(15));
    public final CfgValue<DialogContent> GUIDE_1_ENTER_RESOURCE_WORLD_DIALOG = new CommonValue<>(){};
    public final CfgValue<DialogContent> GUIDE_1_STRUCTURE_SUCCESS_DIALOG = new CommonValue<>(){};

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
    public Holder<WorldLocation> GUIDE_1_SPAWN() {
        return GUIDE_1_SPAWN;
    }

    @Override
    public Holder<List<GuidePoint>> GUIDE_1_POINTS() {
        return GUIDE_1_POINTS;
    }

    @Override
    public Holder<NumberProvider> GUIDE_1_MAX_PARTICLE_POINTS() {
        return GUIDE_1_MAX_PARTICLE_POINTS;
    }

    @Override
    public Holder<DialogContent> GUIDE_1_ENTER_RESOURCE_WORLD_DIALOG() {
        return GUIDE_1_ENTER_RESOURCE_WORLD_DIALOG;
    }

    @Override
    public Holder<DialogContent> GUIDE_1_STRUCTURE_SUCCESS_DIALOG() {
        return GUIDE_1_STRUCTURE_SUCCESS_DIALOG;
    }

    @Override
    public void reload(@NotNull CruxPlugin plugin) {
        reload();
        setup();
    }
}
