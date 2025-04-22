package killercreepr.usurvive.core.vanish;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxTag;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class VanishHandler {
    public static void setVanish(Player p, boolean value){
        if(value){
            CruxTag.set(p, "vanish", PersistentDataType.BOOLEAN, true);
            for(Player online : Crux.getServer().getOnlinePlayers()){
                if(online.equals(p)) continue;
                online.hidePlayer(Crux.getMainPlugin(), p);
            }
            return;
        }
        CruxTag.remove(p, "vanish");
        for(Player online : Crux.getServer().getOnlinePlayers()){
            if(online.equals(p)) continue;
            online.showPlayer(Crux.getMainPlugin(), p);
        }
    }

    public static void onJoin(Player p){
        boolean vanished = isVanished(p);
        for(Player online : Crux.getServer().getOnlinePlayers()){
            if(online.equals(p)) continue;

            if(isVanished(online)){
                p.hidePlayer(Crux.getMainPlugin(), online);
            }
            if(vanished){
                online.hidePlayer(Crux.getMainPlugin(), p);
            }
        }
    }

    public static boolean toggleVanish(Player p){
        boolean x = !isVanished(p);
        setVanish(p, x);
        return x;
    }

    public static boolean isVanished(Player p){
        return CruxTag.get(p, "vanish", PersistentDataType.BOOLEAN, false);
    }
}
