package killercreepr.usurvive.api.entity.player;

import killercreepr.crux.api.data.Loadable;
import org.bukkit.entity.Player;

public interface ShopSpender extends Loadable {
    static ShopSpender shopSpender(Player e){
        if(!(UPlayer.getPlayer(e) instanceof ShopSpender s)){
            throw new IllegalStateException(e.getName() + " is not a ShopSpender!");
        }
        return s;
    }

    double getTotalPayed();
    void addTotalPayed(double amount);
    void setTotalPayed(double amount);

    void onPurchase(double spent, String reason);
}
