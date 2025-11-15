package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.loot.conditions.LootCondition;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleDialogContent implements DialogContent {
    protected final List<DialogLine> dialog;
    protected final List<DialogAction> actions;
    protected final DataExchange info;
    protected final LootCondition canPlayCondition;

    public SimpleDialogContent(List<DialogLine> dialog, List<DialogAction> actions, DataExchange info, LootCondition canPlayCondition) {
        this.dialog = dialog;
        this.actions = actions;
        this.info = info;
        this.canPlayCondition = canPlayCondition;
    }

    @Override
    public List<DialogLine> dialog() {
        return dialog;
    }

    @Override
    public List<DialogAction> actions() {
        return actions;
    }

    @Override
    public int priority() {
        return info.getOrDefault("priority", Integer.class, 0);
    }

    @Override
    public boolean hasCustomCanPlayLogic() {
        return canPlayCondition != null;
    }

    @Override
    public boolean canPlay(DialogPlayer player) {
        if(canPlayCondition == null) return true;
        if(!(CruxCollection.getFirst(player.audience()) instanceof Player p)) return true;
        LootContext ctx = LootContext.builder()
            .looter(p)
            .looted(player)
            .build();
        return canPlayCondition.test(ctx);
    }

    @Override
    public @NotNull DataExchange info() {
        return info;
    }
}
