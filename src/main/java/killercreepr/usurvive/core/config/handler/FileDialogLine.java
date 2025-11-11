package killercreepr.usurvive.core.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.core.dialog.SimpleDialogLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FileDialogLine implements FileObjectHandler<DialogLine> {


    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull DialogLine guidePoint) {
        return null;
    }

    @Nullable
    @Override
    public DialogLine deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ele) {
        var r = ctx.getRegistry();
        if(!(ele instanceof FileObject o)){
            return new SimpleDialogLine(ele.getAsString(), List.of(), DataExchange.empty());
        }
        return new SimpleDialogLine(
            r.deserializeFromFile(String.class, o.get("line")),
            r.deserializeFromFileOrDefault(
                new TypeToken<List<DialogAction>>(){}.getType(),
                o.get("actions"), List.of()
            ),
            r.deserializeFromFileOrDefault(
                DataExchange.class,
                o.get("info"), DataExchange.empty()
            )
        );
    }
}
