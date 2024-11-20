package killercreepr.usurvive.persistence.tags;

import killercreepr.crux.core.persistence.CruxPersistence;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.usurvive.mechanics.grapple.Grapple;
import net.kyori.adventure.key.Key;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GrapplePersistence implements PersistentDataType<PersistentDataContainer, Grapple> {
    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<Grapple> getComplexType() {
        return Grapple.class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(@NotNull Grapple complex, @NotNull PersistentDataAdapterContext ctx) {
        PersistentDataContainer c = ctx.newPersistentDataContainer();
        CruxTag.set(c, "hook", CruxPersistence.CRUX_KEY, complex.getHook());
        CruxTag.set(c, "max_hooks", PersistentDataType.INTEGER, complex.getMaxHooks());
        return c;
    }

    @Override
    public @NotNull Grapple fromPrimitive(@NotNull PersistentDataContainer c, @NotNull PersistentDataAdapterContext ctx) {
        Key hook = CruxTag.get(c, "hook", CruxPersistence.CRUX_KEY);
        Objects.requireNonNull(hook);
        return new Grapple(hook, CruxTag.get(c, "max_hooks", PersistentDataType.INTEGER));
    }
}
