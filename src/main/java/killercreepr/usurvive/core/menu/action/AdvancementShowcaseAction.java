package killercreepr.usurvive.core.menu.action;

import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxmenus.api.menu.contex.ActionContext;
import killercreepr.cruxmenus.core.menu.action.SimpleMenuAction;
import killercreepr.usurvive.core.menu.module.ActiveAdvancementShowcaseModule;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class AdvancementShowcaseAction extends SimpleMenuAction {
    public AdvancementShowcaseAction(@NotNull Key key) {
        super(key);
    }

    @Override
    public boolean execute(@NotNull ActionContext ctx, @NotNull String[] args) {
        String action = ctx.parserCtx().input(args[0]);
        switch (action.toLowerCase()){
            case "navigate" ->{
                ActiveAdvancementShowcaseModule module = (ActiveAdvancementShowcaseModule) ctx.getMenu().getModules().getByID(ctx.parserCtx().input(args[1]));
                module.navigate(
                    (int) CruxMath.evaluate(ctx.parserCtx().input(args[2])),
                    (int) CruxMath.evaluate(ctx.parserCtx().input(args[3]))
                );
            }
            case "set_navigate" ->{
                ActiveAdvancementShowcaseModule module = (ActiveAdvancementShowcaseModule) ctx.getMenu().getModules().getByID(ctx.parserCtx().input(args[1]));
                module.setXY(
                    (int) CruxMath.evaluate(ctx.parserCtx().input(args[2])),
                    (int) CruxMath.evaluate(ctx.parserCtx().input(args[3]))
                );
            }
            case "update" ->{
                ActiveAdvancementShowcaseModule module = (ActiveAdvancementShowcaseModule) ctx.getMenu().getModules().getByID(ctx.parserCtx().input(args[1]));
                module.update();
            }
        }
        return true;
    }
}
