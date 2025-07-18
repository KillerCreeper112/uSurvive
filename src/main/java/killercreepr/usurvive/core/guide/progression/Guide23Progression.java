package killercreepr.usurvive.core.guide.progression;

import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.core.guide.GuideHolder;
import killercreepr.usurvive.core.guide.GuideUtil;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Guide23Progression implements GuideProgression{
    public static final Key KEY = Crux.key("23");

    public static class Loader implements FileObjectHandler<Guide23Progression>{
        @Override
        public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull Guide23Progression object) {
            var r = ctx.getRegistry();
            FileObject o = new FileObject();
            return o;
        }

        @Nullable
        @Override
        public Guide23Progression deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
            var r = ctx.getRegistry();
            FileObject o = (FileObject) e;
            Guide23Progression progression = new Guide23Progression();
            return progression;
        }
    }

    @Override
    public void onComplete(Player p, GuideHolder holder) {
        //holder.setCurrentProgression(new NineteenthGuideProgression());
    }

    @Override
    public void onStart(Player p, GuideHolder holder) {
        if(GuideUtil.hasGuideDisabled(p)) return;
    }

    public void msg(Player p){
        Crux.scheduler().runTaskLater(() ->{
            Lang.GUIDE_23_START.use(p);
        }, 2L);
    }

    @NotNull
    @Override
    public Key key() {
        return KEY;
    }
}
