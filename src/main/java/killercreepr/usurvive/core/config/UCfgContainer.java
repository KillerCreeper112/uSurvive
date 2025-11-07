package killercreepr.usurvive.core.config;

import killercreepr.cruxconfig.config.bukkit.data.CfgContainer;
import org.bukkit.plugin.Plugin;

public class UCfgContainer extends CfgContainer {
    public final GuideConfig GUIDE;

    public UCfgContainer(Plugin plugin) {
        this.GUIDE = new GuideConfig(plugin, "guide");
        add(
            GUIDE
        );
    }
}
