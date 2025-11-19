package killercreepr.usurvive.core.config.story;

import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.usurvive.api.dialog.DialogContent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Story4Config extends StoryConfig{
    public final CfgValue<DialogContent> START_DIALOG = new CommonValue<>(){};
    public final CfgValue<DialogContent> ENTER_WORLD_DIALOG = new CommonValue<>(){};
    public final CfgValue<DialogContent> CLAIM_DIALOG = new CommonValue<>(){};
    public final CfgValue<DialogContent> QUEST_COMPLETED_DIALOG = new CommonValue<>(){};

    public Story4Config(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public Story4Config(@NotNull File file) {
        super(file);
    }

    public Story4Config(@NotNull CruxConfig cfg) {
        super(cfg);
    }
}
