package killercreepr.usurvive.core.menu.action;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.text.tags.TagsPrefixBuilder;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxmenus.api.menu.contex.ActionContext;
import killercreepr.cruxmenus.core.menu.action.SimpleMenuAction;
import killercreepr.usurvive.api.tpa.TPARequest;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class CancelTPAAction extends SimpleMenuAction {
    public CancelTPAAction(@NotNull Key key) {
        super(key);
    }

    @Override
    public boolean execute(@NotNull ActionContext ctx, @NotNull String[] args) {
        HumanEntity p = ctx.getPlayer();
        DataExchange info = ctx.getAllMergedInfo();

        MergedTagContainer tags = TagContainer.merged()
            .hook(p, TagsPrefixBuilder.overwriteBase("user_"))
            ;

        ValuesProvider cfg = USurvivePlugin.inst().values();

        Entity accepter = info.get("accepter", Entity.class);
        TPARequest request = info.get(TPARequest.class);
        if(!CruxMath.hasOccurredWithin(request.timeAccepted(), cfg.TPA_CONFIRM_EXPIRE_TICKS().value().intValue())){
            Lang.TPA_CONFIRM_EXPIRED.use(p, tags);
            return true;
        }

        Lang.TPA_CONFIRM_CANCEL.use(p, tags);
        Lang.TPA_CONFIRM_CANCEL_RECEIVED.use(accepter, tags);
        return true;
    }
}
