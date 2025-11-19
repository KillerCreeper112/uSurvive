package killercreepr.usurvive.core.config;

import killercreepr.cruxconfig.config.bukkit.data.CfgContainer;
import killercreepr.usurvive.core.config.story.Story1Config;
import killercreepr.usurvive.core.config.story.Story2Config;
import killercreepr.usurvive.core.config.story.Story3Config;
import killercreepr.usurvive.core.config.story.Story4Config;
import org.bukkit.plugin.Plugin;

public class UCfgContainer extends CfgContainer {
    public final GuideConfig GUIDE;
    public final Story1Config STORY_1;
    public final Story2Config STORY_2;
    public final Story3Config STORY_3;
    public final Story4Config STORY_4;
    public final Story4Config STORY_5;

    public UCfgContainer(Plugin plugin) {
        this.GUIDE = new GuideConfig(plugin, "guide");
        this.STORY_1 = new Story1Config(plugin, "story/1");
        this.STORY_2 = new Story2Config(plugin, "story/2");
        this.STORY_3 = new Story3Config(plugin, "story/3");
        this.STORY_4 = new Story4Config(plugin, "story/4");
        this.STORY_5 = new Story4Config(plugin, "story/5");
        add(
            GUIDE,
            STORY_1,
            STORY_2,
            STORY_3,
            STORY_4,
            STORY_5
        );
    }
}
