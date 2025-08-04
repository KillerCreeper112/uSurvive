package killercreepr.usurvive.core.config.handler;

import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.core.potion.inflictor.UDamageEntityInflictor;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FileUDamageEntityInflictor implements FileObjectHandler<UDamageEntityInflictor> {
    @Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull UDamageEntityInflictor object) {
        return new FileObject()
            .add("entity_type", ctx.getRegistry().serializeToFile(object.getType()))
            .add("uuid", ctx.getRegistry().serializeToFile(object.uuid()))
            .addProperty("damage", object.getDamage())
            ;
    }

    @Override
    public @Nullable UDamageEntityInflictor deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        if(!(e instanceof FileObject o)) return null;
        return new UDamageEntityInflictor(
            ctx.getRegistry().deserializeFromFile(UUID.class, o.get("uuid")),
            ctx.getRegistry().deserializeFromFile(Key.class, o.get("type")),
            ctx.getRegistry().deserializeFromFile(Float.class, o.get("damage"))
        );
    }
}
