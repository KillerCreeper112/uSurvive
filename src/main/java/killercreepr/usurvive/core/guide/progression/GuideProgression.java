package killercreepr.usurvive.core.guide.progression;

import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.usurvive.api.values.GuideCfg;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.guide.GuideHolder;
import net.kyori.adventure.key.Keyed;
import org.bukkit.entity.Player;

public interface GuideProgression extends Keyed {
    default void onTick(Player p){}

    default void load(DataFile file){}
    default void save(DataFile file){}

    default void onComplete(Player p, GuideHolder holder){}
    default void onStart(Player p, GuideHolder holder){}

    default GuideCfg cfg(){
        return USurvivePlugin.inst().cfgContainer().GUIDE;
    }
}
