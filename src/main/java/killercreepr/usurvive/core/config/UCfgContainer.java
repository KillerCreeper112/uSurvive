package killercreepr.usurvive.core.config;

import killercreepr.cruxconfig.config.bukkit.data.CfgContainer;
import killercreepr.usurvive.core.config.story.Story1Config;
import killercreepr.usurvive.core.config.story.Story2Config;
import org.bukkit.plugin.Plugin;

public class UCfgContainer extends CfgContainer {
    public final GuideConfig GUIDE;
    public final Story1Config STORY_1;
    public final Story2Config STORY_2;

    public UCfgContainer(Plugin plugin) {
        this.GUIDE = new GuideConfig(plugin, "guide");
        this.STORY_1 = new Story1Config(plugin, "story/1");
        this.STORY_2 = new Story2Config(plugin, "story/2");
        add(
            GUIDE,
            STORY_1,
            STORY_2
        );
    }
}
