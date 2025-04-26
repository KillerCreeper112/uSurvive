package killercreepr.usurvive.external.quickshop;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.economy.AbstractEconomy;
import com.ghostchu.quickshop.api.obj.QUser;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxcurrency.core.CruxCurrencyPlugin;
import net.kyori.adventure.key.Key;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class UQuickShop {
    public static OfflinePlayer getOr(QUser user){
        var p = user.getBukkitPlayer().orElse(null);
        return p == null ?
            (user.getUniqueId() == null ? null : Crux.getServer().getOfflinePlayer(user.getUniqueId())) :
            p;
    }

    public static void setup(CruxPlugin plugin){
        Key orbit = Crux.key("orbit");
        QuickShop shop = QuickShop.getInstance();
        try{
            Field field = shop.getClass().getDeclaredField("economy");
            field.setAccessible(true);
            field.set(shop, new AbstractEconomy() {
                @Override
                public boolean withdraw(@NotNull QUser qUser, double v, @NotNull World world, @Nullable String s) {
                    var p = getOr(qUser);
                    if(p == null) return false;
                    CruxCurrencyPlugin.inst().ecoHandler().withdrawBalance(p, orbit, v);
                    return true;
                }

                @Override
                public boolean deposit(@NotNull QUser qUser, double v, @NotNull World world, @Nullable String s) {
                    var p = getOr(qUser);
                    if(p == null) return false;
                    CruxCurrencyPlugin.inst().ecoHandler().depositBalance(p, orbit, v);
                    return true;
                }

                @Override
                public double getBalance(@NotNull QUser qUser, @NotNull World world, @Nullable String s) {
                    var p = getOr(qUser);
                    if(p == null) return 0D;
                    return CruxCurrencyPlugin.inst().ecoHandler().getBalance(p, orbit);
                }

                @Override
                public String getProviderName() {
                    return "Orbit";
                }

                @Override
                public String toString() {
                    return "Orbit";
                }

                @Override
                public String format(double v, @NotNull World world, @Nullable String s) {
                    return v > 1D ? (CruxMath.format(v) + " orbits") : (CruxMath.format(v) + " orbit");
                }

                @Nullable
                @Override
                public String getLastError() {
                    return null;
                }

                @Override
                public @NotNull Plugin getPlugin() {
                    return plugin;
                }

                @Override
                public boolean hasCurrency(@NotNull World world, @NotNull String s) {
                    return true;
                }

                @Override
                public boolean isValid() {
                    return true;
                }

                @Override
                public boolean supportCurrency() {
                    return true;
                }
            });
            field.setAccessible(false);
        }catch (IllegalAccessException | NoSuchFieldException ignored){
            ignored.printStackTrace();
        }
    }

}
