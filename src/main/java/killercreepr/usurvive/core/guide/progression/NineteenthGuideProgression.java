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

public class NineteenthGuideProgression implements GuideProgression{
    public static final Key KEY = Crux.key("nineteenth");

    public static class Loader implements FileObjectHandler<NineteenthGuideProgression>{
        @Override
        public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull NineteenthGuideProgression object) {
            var r = ctx.getRegistry();
            FileObject o = new FileObject();
            return o;
        }

        @Nullable
        @Override
        public NineteenthGuideProgression deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
            var r = ctx.getRegistry();
            FileObject o = (FileObject) e;
            NineteenthGuideProgression progression = new NineteenthGuideProgression();
            return progression;
        }
    }

    @Override
    public void onComplete(Player p, GuideHolder holder) {
        holder.setCurrentProgression(new Guide20Progression());
    }

    @Override
    public void onStart(Player p, GuideHolder holder) {
        if(GuideUtil.hasGuideDisabled(p)) return;
        Crux.scheduler().runTaskLater(() ->{
            Lang.GUIDE_NINETEENTH_START.use(p);
        }, 2L);
    }

    @NotNull
    @Override
    public Key key() {
        return KEY;
    }
}
