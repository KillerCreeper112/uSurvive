package killercreepr.usurvive.core.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.loot.conditions.LootCondition;
import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.api.dialog.Dialoger;
import killercreepr.usurvive.core.dialog.SimpleDialogContent;
import killercreepr.usurvive.core.dialog.SimpleKeyedDialogContent;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FileDialogContent implements FileObjectHandler<DialogContent> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull DialogContent guidePoint) {
        return null;
    }

    @Nullable
    @Override
    public DialogContent deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement ele) {
        var r = ctx.getRegistry();
        if(!(ele instanceof FileObject o)){
            return new SimpleDialogContent(r.deserializeFromFile(new TypeToken<List<DialogLine>>(){}.getType(), ele), List.of(), DataExchange.empty(), null);
        }

        Dialoger parent = r.deserializeFromFile(Dialoger.class, o.get("parent"));
        DataExchange info = DataExchange.empty();
        if(parent != null){
            info = info.append(parent.info());
        }
        info = info.append(
            r.deserializeFromFileOrDefault(
                DataExchange.class,
                o.get("info"), DataExchange.empty()
            )
        );

        List<DialogAction> actions = new ArrayList<>();
        if(parent != null){
            actions.addAll(parent.actions());
        }
        actions.addAll(
            r.deserializeFromFileOrDefault(
                new TypeToken<List<DialogAction>>(){}.getType(),
                o.get("actions"), List.of()
            )
        );

        String key = info.get("key", String.class);
        if(key != null){
            info = info.append("key", Holder.direct(Crux.key(key)));
        }

        LootCondition canPlay;
        if(o.get("conditions") instanceof FileObject oo){
            canPlay = r.deserializeFromFile(LootCondition.class, oo.get("can_play"));
        }else{
            canPlay = null;
        }

        Key finalKey = info.get("key", Key.class);
        if(finalKey != null){
            return new SimpleKeyedDialogContent(
                r.deserializeFromFileOrDefault(
                    new TypeToken<List<DialogLine>>(){}.getType(),
                    o.get("dialog"), List.of()
                ),
                actions,
                info,
                canPlay
            );
        }
        return new SimpleDialogContent(
            r.deserializeFromFileOrDefault(
                new TypeToken<List<DialogLine>>(){}.getType(),
                o.get("dialog"), List.of()
            ),
            actions,
            info,
            canPlay
        );
    }
}
