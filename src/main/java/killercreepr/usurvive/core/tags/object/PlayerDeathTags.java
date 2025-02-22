package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.HookedObjectContainer;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.hook.StringHookedObjectTag;
import killercreepr.crux.core.text.hook.StringListHookedObjectTag;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.core.death.PlayerDeath;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDeathTags implements ObjectTag<PlayerDeath> {
    @Override
    public @NotNull Class<PlayerDeath> getObjectType() {
        return PlayerDeath.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("player_death_");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull PlayerDeath death, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("world_loaded", (args, ctx) -> (death.getWorld() != null) + ""))
            .add(Tag.string("drops", (args, ctx) -> death.getDrops().size() + ""))
            .add(Tag.string("player_uuid", (args, ctx) -> death.getPlayerUUID() + ""))
            .add(Tag.string("guardian_uuid", (args, ctx) -> death.getGuardianUUID() + ""))
            .add(Tag.string("time", (args, ctx) -> death.getTime() + ""))
            .add(Tag.string("world_key", (args, ctx) -> death.getWorldKey() + ""))
            .add(Tag.string("time_until_expire", (args, ctx) -> death.getTimeUntilExpire() + ""))
            .add(Tag.string("expiry_time", (args, ctx) -> death.getExpiryTime() + ""))
            ;
    }

    @Nullable
    @Override
    public HookedObjectContainer<StringHookedObjectTag<?>> hookStrings(@NotNull PlayerDeath object, Object base, Object parent, @NotNull TagParser tags) {
        HookedObjectContainer<StringHookedObjectTag<?>> hooks = HookedObjectContainer.string()
            .addAll(tags.hookStrings(object.getPosition(), base, object, FormatPrefix.simple("pos/")))
            ;
        World world = object.getWorld();
        if(world != null) hooks.addAll(tags.hookStrings(world, base, object, FormatPrefix.simple("world/")));
        return hooks;
    }

    @Nullable
    @Override
    public HookedObjectContainer<StringListHookedObjectTag<?>> hookStringLists(@NotNull PlayerDeath object, Object base, Object parent, @NotNull TagParser tags) {
        var hooks = HookedObjectContainer.stringList()
            .addAll(tags.hookStringLists(object.getPosition(), base, object, FormatPrefix.simple("pos/")))
            ;
        World world = object.getWorld();
        if(world != null) hooks.addAll(tags.hookStringLists(world, base, object, FormatPrefix.simple("world/")));
        return hooks;
    }
}
