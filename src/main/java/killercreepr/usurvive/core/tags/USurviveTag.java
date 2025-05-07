package killercreepr.usurvive.core.tags;

import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.format.FormatArgs;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.usurvive.core.vanish.VanishHandler;
import killercreepr.usurvive.core.world.module.ResetWorldModule;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class USurviveTag implements StringResolver {
    @NotNull
    @Override
    public String identifier() {
        return "usurvive";
    }

    @Nullable
    @Override
    public String resolve(@NotNull FormatArgs args, @NotNull TextParserContext ctx) {
        switch (ctx.deserializeString(args.get(0)).toLowerCase()){
            case "world" ->{
                Key key = Key.key(ctx.deserializeString(args.get(2)));
                CruxWorld crux = CruxCore.core().worldManager().getWorld(key);
                if(crux == null) return "0";
                switch (ctx.deserializeString(args.get(1)).toLowerCase()){
                    case "resetting_in" ->{
                        ResetWorldModule module = crux.getModule(ResetWorldModule.class);
                        if(module == null) return "0";
                        return module.getResettingIn() + "";
                    }
                }
            }
            case "players" ->{
                int amount = 0;
                for(var p : Crux.getServer().getOnlinePlayers()){
                    if(VanishHandler.isVanished(p)) continue;
                    amount++;
                }
                return amount+ "";
            }
        }
        return null;
    }
}
