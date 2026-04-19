package killercreepr.usurvive.core.config.enchant;

import killercreepr.crux.api.data.Reloadable;
import killercreepr.crux.api.data.WorldLocation;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.paper.ItemHolder;
import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.guide.GuidePoint;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;
import java.util.Map;

@killercreepr.cruxconfig.config.common.annotations.Config(autoUpdate = true)
public class EnchantsConfig extends Cfg implements Reloadable {
    public final CfgValue<Map<Key, ItemHolder>> BLOCK_TO_SEEDS = new CommonValue<>(){
        @Override
        public void setDefault(@NonNull DataFile cfg, @NotNull String path) {
            FileObject o = new FileObject();
            o.addProperty("minecraft:potatoes", "minecraft:potato");
            o.addProperty("minecraft:carrots", "minecraft:carrot");
            o.addProperty("minecraft:beetroots", "minecraft:beetroot_seeds");
            o.addProperty("minecraft:pitcher_crop", "minecraft:pitcher_pod");
            cfg.serialize(path, o);
        }
    };

    public EnchantsConfig(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public EnchantsConfig(@NotNull File file) {
        super(file);
    }

    public EnchantsConfig(@NotNull CruxConfig cfg) {
        super(cfg);
    }

    @Override
    public void reload(@NotNull CruxPlugin plugin) {
        reload();
        setup();
    }
}
