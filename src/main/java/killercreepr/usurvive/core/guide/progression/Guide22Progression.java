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

public class Guide22Progression implements GuideProgression{
    public static final Key KEY = Crux.key("22");

    public static class Loader implements FileObjectHandler<Guide22Progression>{
        @Override
        public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull Guide22Progression object) {
            var r = ctx.getRegistry();
            FileObject o = new FileObject();
            return o;
        }

        @Nullable
        @Override
        public Guide22Progression deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
            var r = ctx.getRegistry();
            FileObject o = (FileObject) e;
            Guide22Progression progression = new Guide22Progression();
            return progression;
        }
    }

    @Override
    public void onComplete(Player p, GuideHolder holder) {
        var progression = new Guide23Progression();
        holder.setCurrentProgression(progression);
        progression.msg(p);
    }

    @Override
    public void onStart(Player p, GuideHolder holder) {
        if(GuideUtil.hasGuideDisabled(p)) return;
        Crux.scheduler().runTaskLater(() ->{
            Lang.GUIDE_22_START.use(p);
        }, 2L);
    }

    @NotNull
    @Override
    public Key key() {
        return KEY;
    }
}
