package killercreepr.usurvive.core.menu.data;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.text.format.FormatSerializer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxclaims.api.claiming.ClaimProfile;
import killercreepr.cruxclaims.api.claiming.Claimer;
import killercreepr.cruxclaims.api.claiming.permission.ClaimPermission;
import killercreepr.cruxclaims.api.claiming.setting.ClaimSetting;
import killercreepr.cruxclaims.api.claiming.team.ClaimRank;
import killercreepr.cruxclaims.core.CruxClaims;
import killercreepr.cruxclaims.core.registries.ClaimRegistries;
import killercreepr.cruxmenus.api.menu.CfgMenu;
import killercreepr.cruxmenus.api.menu.contex.MenuContext;
import killercreepr.cruxmenus.api.menu.holder.MenuHolder;
import killercreepr.cruxmenus.api.menu.holder.MenuItemHolder;
import killercreepr.cruxmenus.core.menu.data.SimpleItemDataParser;
import killercreepr.usurvive.api.rank.Rank;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class USurviveDataParser extends SimpleItemDataParser {
    public USurviveDataParser(@NotNull Key key) {
        super(key);
    }

    @Override
    public @NotNull DataExchange parse(@NotNull Entity p,
                                       @NotNull MenuContext ctx,
                                       @NotNull MenuItemHolder itemHolder,
                                       @NotNull DataExchange info) {
        DataExchange mergedInfo = ctx.getAllMergedInfo();
        CfgMenu menu = ctx.getMenu();
        MenuHolder holder = menu.getHolder();
        FormatSerializer format = holder.getRegistry().getFormat();

        DataExchange.Builder builder = DataExchange.builder();
        if(info.has("urank")){
            Object o = info.getOrThrow("urank");
            Key key = Crux.key(
                ctx.getMenu().getHolder().getRegistry().getFormat().deserializeString(o.toString(), ctx.getAllMergedResolvers())
            );
            Rank rank = USurviveRegistries.RANK.get(key);
            if(rank != null){
                builder.putAll(rank.info());
            }
        }
        return builder.build();
    }
}
