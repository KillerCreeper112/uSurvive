package killercreepr.usurvive.api.rank;

import killercreepr.crux.api.data.CruxKeyed;
import killercreepr.crux.api.data.holder.DataInfoHolder;
import org.bukkit.entity.Player;

public interface Rank extends CruxKeyed, DataInfoHolder {
    void onRankUp(Player p);
}
