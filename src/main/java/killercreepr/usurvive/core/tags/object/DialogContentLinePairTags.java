package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.SimpleObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.core.dialog.DialogContentLinePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DialogContentLinePairTags implements SimpleObjectTag<DialogContentLinePair> {
    @NotNull
    @Override
    public Class<DialogContentLinePair> getObjectType() {
        return DialogContentLinePair.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("dialog_pair_");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull DialogContentLinePair object, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("info", (args, ctx) ->{
                String id = ctx.deserializeString(args.get(0));
                return object.getSecond().info().getWithDefault(id, info ->{
                    return object.getFirst().info().getWithDefault(id, subInfo ->{
                        return args.has(1) ? ctx.deserializeString(args.get(1)) : "";
                    });
                }) + "";
            }))
            .add(Tag.string("info_closed", (args, ctx) ->{
                String id = ctx.deserializeString(args.get(0));
                String got = object.getSecond().info().getWithDefault(id, info ->{
                    return object.getFirst().info().getWithDefault(id, subInfo ->{
                        return args.has(1) ? ctx.deserializeString(args.get(1)) : "";
                    });
                }) + "";
                if(got.isBlank()) return got;
                if(got.startsWith("<")){
                    return "</" + got.substring(1);
                }
                return got;
            }))
            .add(Tag.string("has_info", (args, ctx) ->{
                String id = ctx.deserializeString(args.get(0));
                return (object.getSecond().info().getWithDefault(id, info ->{
                    return object.getFirst().info().getWithDefault(id, subInfo ->{
                        return null;
                    });
                }) != null) + "";
            }))
            ;
    }

    @Override
    public @Nullable Map<Object, FormatPrefix> hookObjects(DialogContentLinePair object) {
        return Map.of(
            object.getFirst(), FormatPrefix.simple("content/"),
            object.getSecond(), FormatPrefix.simple("line/")
        );
    }
}
