package killercreepr.usurvive.core.guide.progression;

import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.core.guide.GuideHolder;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FourteenthGuideProgression implements GuideProgression{
    public static final Key KEY = Crux.key("fourteenth");

    public static class Loader implements FileObjectHandler<FourteenthGuideProgression>{
        @Override
        public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull FourteenthGuideProgression object) {
            var r = ctx.getRegistry();
            FileObject o = new FileObject();
            return o;
        }

        @Nullable
        @Override
        public FourteenthGuideProgression deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
            var r = ctx.getRegistry();
            FileObject o = (FileObject) e;
            FourteenthGuideProgression progression = new FourteenthGuideProgression();
            return progression;
        }
    }

    @Override
    public void onComplete(Player p, GuideHolder holder) {
        holder.setCurrentProgression(new FifteenthGuideProgression());
    }

    @Override
    public void onStart(Player p, GuideHolder holder) {
        Crux.scheduler().runTaskLater(() ->{
            Lang.GUIDE_FOURTEENTH_START.use(p);
        }, 2L);
    }

    @NotNull
    @Override
    public Key key() {
        return KEY;
    }
}
