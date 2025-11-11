package killercreepr.usurvive.core.config.handler;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.util.MapBuilder;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.cruxconfig.config.common.handler.GetterFileHandler;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.core.dialog.action.SetWaitingDialogAction;
import killercreepr.usurvive.core.dialog.action.SoundDialogAction;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FileDialogAction implements FileObjectHandler<DialogAction> {
    public final Map<Key, FileObjectHandler<DialogAction>> ACTION_TYPES = new MapBuilder<Key, FileObjectHandler<DialogAction>>()
        .put(Crux.key("set_waiting"), new GetterFileHandler<>() {
            @Nullable
            @Override
            public DialogAction deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ele) {
                var r = ctx.getRegistry();
                if(!(ele instanceof FileObject o)){
                    return null;
                }
                return new SetWaitingDialogAction(
                    r.deserializeFromFile(NumberProvider.class, o.get("start")),
                    r.deserializeFromFile(NumberProvider.class, o.get("end")),
                    r.deserializeFromFile(NumberProvider.class, o.get("speak"))
                );
            }
        })
        .put(Crux.key("sound"), new GetterFileHandler<>() {
            @Nullable
            @Override
            public DialogAction deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ele) {
                var r = ctx.getRegistry();
                if(!(ele instanceof FileObject o)){
                    return null;
                }
                return new SoundDialogAction(
                    r.deserializeFromFile(CreateSound.class, o.get("start")),
                    r.deserializeFromFile(CreateSound.class, o.get("end")),
                    r.deserializeFromFile(CreateSound.class, o.get("speak"))
                );
            }
        })
        .build();

    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull DialogAction guidePoint) {
        return null;
    }

    @Nullable
    @Override
    public DialogAction deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ele) {
        var r = ctx.getRegistry();
        if(!(ele instanceof FileObject o)){
            return null;
        }

        Key key = r.deserializeFromFile(Key.class, o.get("action"));
        if(key == null) return null;
        var handler = ACTION_TYPES.get(key);
        if(handler == null) return null;
        return handler.deserializeFromFile(ctx, ele);
    }
}
