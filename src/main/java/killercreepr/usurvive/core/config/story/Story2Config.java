package killercreepr.usurvive.core.config.story;

import killercreepr.crux.api.data.WorldLocation;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import killercreepr.cruxstructures.api.structure.generation.StructureGenerator;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.guide.GuidePoint;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class Story2Config extends StoryConfig{
    public final CfgValue<DialogContent> START_DIALOG = new CommonValue<>(){};
    public final CfgValue<DialogContent> WENT_TO_DIALOG = new CommonValue<>(){};

    public Story2Config(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public Story2Config(@NotNull File file) {
        super(file);
    }

    public Story2Config(@NotNull CruxConfig cfg) {
        super(cfg);
    }
}
