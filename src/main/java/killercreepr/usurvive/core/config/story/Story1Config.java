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

public class Story1Config extends StoryConfig{
    public final CfgValue<StructureGenerator> RESOURCE_WORLD_STRUCTURE_1 = new CommonValue<>(){};
    public final NumCfgValue RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE = new NumCfgValue(NumberProvider.uniform(12,16));
    public final CfgValue<WorldLocation> NPC_SPAWN_1 = new CommonValue<>(){};
    public final CfgValue<List<GuidePoint>> NPC_POINTS_1 = new CommonValue<>(){};
    public final NumCfgValue MAX_PARTICLE_POINTS = new NumCfgValue(NumberProvider.constant(15));
    public final CfgValue<DialogContent> ENTER_RESOURCE_WORLD_DIALOG = new CommonValue<>(){};
    public final CfgValue<DialogContent> STRUCTURE_LOCATED_DIALOG = new CommonValue<>(){};
    public final CfgValue<DialogContent> STRUCTURE_NEARBY_DIALOG = new CommonValue<>(){};
    public final CfgValue<DialogContent> PUSHED_NPC_IN_PORTAL_DIALOG = new CommonValue<>(){};
    public final CfgValue<DialogContent> PUNCHED_NPC_DIALOG = new CommonValue<>(){};
    public final CfgValue<DialogContent> BOUNDERS_INVADE_SPAWNED_DIALOG = new CommonValue<>(){};

    public Story1Config(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public Story1Config(@NotNull File file) {
        super(file);
    }

    public Story1Config(@NotNull CruxConfig cfg) {
        super(cfg);
    }
}
