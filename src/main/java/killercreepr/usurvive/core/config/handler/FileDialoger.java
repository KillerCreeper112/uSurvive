package killercreepr.usurvive.core.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.Dialoger;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.dialog.SimpleDialoger;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FileDialoger implements FileObjectHandler<Dialoger> {
    public static final FileDialoger FILE_DIALOGER = new FileDialoger();
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull Dialoger guidePoint) {
        return null;
    }

    @Nullable
    @Override
    public Dialoger deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ele) {
        var r = ctx.getRegistry();
        if(ele.isFilePrimitive()){
            String id = r.deserializeFromFile(String.class, ele);
            if(id != null){
                if(id.startsWith("#")){
                    return USurviveRegistries.DIALOGER.get(Crux.key(id.substring(1)));
                }
            }
        }

        Key key;
        if(!(ele instanceof FileObject o)){
            key = null;
        }else key = r.deserializeFromFile(Key.class, o.get("key"));
        return deserializeFromFile(ctx, ele, key);
    }

    @Nullable
    public Dialoger deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ele, Key key) {
        var r = ctx.getRegistry();
        if(!(ele instanceof FileObject o)){
            return new SimpleDialoger(key, r.deserializeFromFile(new TypeToken<List<DialogContent>>(){}.getType(), ele), List.of(),DataExchange.empty());
        }
        return new SimpleDialoger(
            key,
            r.deserializeFromFile(
                new TypeToken<List<DialogContent>>(){}.getType(),
                o.get("content")
            ),
            r.deserializeFromFile(
                new TypeToken<List<DialogAction>>(){}.getType(),
                o.get("actions")
            ),
            r.deserializeFromFileOrDefault(
                DataExchange.class,
                o.get("info"),
                DataExchange.empty()
            )
        );
    }
}
