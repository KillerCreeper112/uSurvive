package killercreepr.usurvive.core.util;

import killercreepr.crux.core.Crux;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

public class AdvanceUtil {
    public static boolean hasAnyAdvancement(Player p, Key... advancements){
        for(Key key : advancements){
            String[] args = key.value().split("/");
            CruxAdvancementManager manager = AdvancementRegistries.ADVANCEMENT_MANAGERS.get(
                Crux.key(key.namespace() + ":" + args[0])
            );
            if(manager == null){
                Crux.logError("No advancement manager of " +  Crux.key(key.namespace() + ":" + args[0]) + " found");
                continue;
            }
            var advancement = manager.getAdvancement(key);
            if(advancement == null) continue;
            if(advancement.isGranted(p)) return true;
        }
        return false;
    }
}
