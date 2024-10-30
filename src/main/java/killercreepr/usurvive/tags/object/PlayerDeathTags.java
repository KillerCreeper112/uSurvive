package killercreepr.usurvive.tags.object;

import killercreepr.crux.tags.TagParser;
import killercreepr.crux.tags.container.TagContainer;
import killercreepr.crux.tags.context.FormatPrefix;
import killercreepr.crux.tags.hook.HookedObjectContainer;
import killercreepr.crux.tags.hook.ObjectTag;
import killercreepr.crux.tags.hook.impl.StringHookedObjectTag;
import killercreepr.crux.tags.hook.impl.StringListHookedObjectTag;
import killercreepr.crux.tags.hook.prefix.HookedPrefixBuilder;
import killercreepr.crux.tags.resolver.StringResolver;
import killercreepr.crux.tags.resolver.Tag;
import killercreepr.usurvive.death.PlayerDeath;
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
            .add(Tag.string("world_uuid", (args, ctx) -> death.getWorldUUID() + ""))
            ;
    }

    @Override
    public @Nullable HookedObjectContainer<StringHookedObjectTag<?>> hookStrings(@NotNull PlayerDeath object, @NotNull TagParser tags) {
        HookedObjectContainer<StringHookedObjectTag<?>> hooks = HookedObjectContainer.string()
            .addAll(tags.hookStrings(object.getPosition(), HookedPrefixBuilder.overwrite(FormatPrefix.simple("player_death_pos/"))))
            ;
        World world = object.getWorld();
        if(world != null) hooks.addAll(tags.hookStrings(world, HookedPrefixBuilder.overwrite(FormatPrefix.simple("player_death_world/"))));
        return hooks;
    }

    @Override
    public @Nullable HookedObjectContainer<StringListHookedObjectTag<?>> hookStringLists(@NotNull PlayerDeath object, @NotNull TagParser tags) {
        HookedObjectContainer<StringListHookedObjectTag<?>> hooks = HookedObjectContainer.stringList()
            .addAll(tags.hookStringLists(object.getPosition(), HookedPrefixBuilder.overwrite(FormatPrefix.simple("player_death_pos/"))))
            ;
        World world = object.getWorld();
        if(world != null) hooks.addAll(tags.hookStringLists(world, HookedPrefixBuilder.overwrite(FormatPrefix.simple("player_death_world/"))));
        return hooks;
    }
}
