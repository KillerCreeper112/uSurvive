package killercreepr.usurvive.core.guide.progression;

import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.config.GuideConfig;
import killercreepr.usurvive.core.guide.GuideHolder;
import net.kyori.adventure.key.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public interface GuideProgression extends Keyed {
    default void onTick(Player p){}

    default void load(DataFile file){}
    default void save(DataFile file){}

    default void onComplete(Player p, GuideHolder holder){}
    default void onStart(Player p, GuideHolder holder){
        if(this instanceof Listener l){
            Crux.getServer().getPluginManager().registerEvents(l, Crux.getMainPlugin());
        }
    }
    default void onEnd(Player p, GuideHolder holder){
        if(this instanceof Listener l){
            HandlerList.unregisterAll(l);
        }
    }

    default void onLoaded(Player p){}
    default void onMemoryUnload(Player p){}
    default void onPlayerChangedWorldEvent(Player p, PlayerChangedWorldEvent event){}

    default GuideConfig cfg(){
        return USurvivePlugin.inst().cfgContainer().GUIDE;
    }
}
