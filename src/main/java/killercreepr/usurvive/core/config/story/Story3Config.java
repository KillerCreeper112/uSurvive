package killercreepr.usurvive.core.config.story;

import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.usurvive.api.dialog.DialogContent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class Story3Config extends StoryConfig{
    public final CfgValue<List<String>> GIVE_COMMANDS = new CommonValue<>(){};
    public final CfgValue<DialogContent> START_DIALOG = new CommonValue<>(){};
    public final CfgValue<DialogContent> MERCHANT_BOUGHT_DIALOG = new CommonValue<>(){};

    public Story3Config(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public Story3Config(@NotNull File file) {
        super(file);
    }

    public Story3Config(@NotNull CruxConfig cfg) {
        super(cfg);
    }
}
