package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.HookedObjectContainer;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.hook.StringHookedObjectTag;
import killercreepr.crux.core.text.hook.StringListHookedObjectTag;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.usurvive.api.values.GravestoneCfg;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.death.PlayerDeath;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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
            .add(Tag.string("tp_cost", (args, ctx) ->{
                var cfg = ((GravestoneCfg) USurvivePlugin.inst().values());
                String equation = cfg.GRAVESTONE_COSTS().valueOr(Map.of())
                    .getOrDefault(
                        death.getWorldKey(),
                        cfg.GRAVESTONE_COSTS().valueOr(Map.of()).get(Crux.key("default"))
                    );
                if(equation == null) return "50";

                var p = Crux.getServer().getPlayer(death.getPlayerUUID());
                var tag = TagContainer.merged(tags).hook(p).hook(death);
                return CruxMath.evaluate(ctx.deserializeString(equation, tag)) + "";
            }))
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
