package killercreepr.usurvive.external.quickshop;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.economy.EconomyProvider;
import com.ghostchu.quickshop.api.obj.QUser;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxcurrency.core.CruxCurrencyPlugin;
import net.kyori.adventure.key.Key;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

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
        shop.getEconomyManager().provider(new EconomyProvider() {
            @NotNull
            @Override
            public String name() {
                return "Orbit";
            }

            @Override
            public boolean withdraw(@NotNull QUser qUser, @NotNull String s, @Nullable String s1, @NotNull BigDecimal bigDecimal) {
                var p = getOr(qUser);
                if(p == null) return false;
                CruxCurrencyPlugin.inst().ecoHandler().withdrawBalance(p, orbit, bigDecimal.doubleValue());
                return true;
            }

            @Override
            public boolean deposit(@NotNull QUser qUser, @NotNull String s, @Nullable String s1, @NotNull BigDecimal bigDecimal) {
                var p = getOr(qUser);
                if(p == null) return false;
                CruxCurrencyPlugin.inst().ecoHandler().depositBalance(p, orbit, bigDecimal.doubleValue());
                return true;
            }

            @NotNull
            @Override
            public BigDecimal balance(@NotNull QUser qUser, @NotNull String s, @Nullable String s1) {
                var p = getOr(qUser);
                if(p == null) return BigDecimal.ZERO;
                return BigDecimal.valueOf(CruxCurrencyPlugin.inst().ecoHandler().getBalance(p, orbit));
            }

            @NotNull
            @Override
            public String format(@NotNull BigDecimal vv, @NotNull String s, @Nullable String s1) {
                var v = vv.doubleValue();
                return v > 1D ? (CruxMath.format(v) + " orbits") : (CruxMath.format(v) + " orbit");
            }

            @Override
            public boolean supportsCurrency(@NotNull String s, @Nullable String s1) {
                return true;
            }

            @Override
            public boolean multiCurrency() {
                return false;
            }

            @Override
            public boolean valid() {
                return true;
            }

            @NotNull
            @Override
            public String lastError() {
                return "";
            }

            @Override
            public String providerName() {
                return "Orbit";
            }

            @Override
            public boolean transfer(@NotNull QUser from, @NotNull QUser to, @NotNull String world, @Nullable String currency, @NotNull BigDecimal amount) {
                if(!withdraw(from, world, currency,  amount)) return false;
                return deposit(to, world, currency, amount);
            }

            @Override
            public String toString() {
                return "Orbit";
            }
        });
        /*try{
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
        }*/
    }

}
