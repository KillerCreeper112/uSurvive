package killercreepr.usurvive.core.menu.action;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.text.tags.TagsPrefixBuilder;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxmenus.api.menu.contex.ActionContext;
import killercreepr.cruxmenus.core.menu.action.SimpleMenuAction;
import killercreepr.cruxteleport.api.teleport.CruxTeleporter;
import killercreepr.usurvive.api.tpa.TPARequest;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.component.teleport.TPACheckAccepter;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class ConfirmTPAAction extends SimpleMenuAction {
    public ConfirmTPAAction(@NotNull Key key) {
        super(key);
    }

    @Override
    public boolean execute(@NotNull ActionContext ctx, @NotNull String[] args) {
        HumanEntity p = ctx.getPlayer();

        if(CruxTeleporter.teleporter().hasScheduledTeleport(p)){
            Lang.TPA_CONFIRM_ALREADY_TELEPORTING.use(p);
            return true;
        }
        DataExchange info = ctx.getAllMergedInfo();

        MergedTagContainer tags = TagContainer.merged()
            .hook(p, TagsPrefixBuilder.overwriteBase("user_"))
            ;

        ValuesProvider cfg = USurvivePlugin.inst().values();

        Entity accepter = info.getOrThrow("accepter", Entity.class);
        TPARequest request = info.getOrThrow(TPARequest.class);
        if(!CruxMath.hasOccurredWithin(request.timeAccepted(), cfg.TPA_CONFIRM_EXPIRE_TICKS().value().intValue()) || !CruxEntityUtil.isValid(accepter)){
            Lang.TPA_CONFIRM_EXPIRED.use(p, tags);
            return true;
        }
        Entity sender = request.getSender();
        if(sender == null){
            Lang.TPA_CONFIRM_EXPIRED.use(p, tags);
            return true;
        }

        Lang.TPA_CONFIRM_RECEIVED.use(accepter, tags);
        CruxTeleporter.teleporter().scheduleTeleport(p, request.teleport()
            .component(USurviveComponents.TPA_CHECK_WORLD,
                new TPACheckAccepter(request, sender, accepter)).build());
        return true;
    }
}
