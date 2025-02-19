package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.usurvive.api.world.module.SleeperWorldModule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldTags implements ObjectTag<World> {
    @NotNull
    @Override
    public Class<World> getObjectType() {
        return World.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("world_");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull World object, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("usurvive_sleepers", (args, ctx) ->{
                CruxWorld crux = CruxCore.core().worldManager().getWorld(object.getUID());
                if(crux == null) return "0";
                SleeperWorldModule sleeperModule = crux.getModule(SleeperWorldModule.class);
                if(sleeperModule == null) return "0";
                return sleeperModule.getSleepersAmount() + "";
            }))
            .add(Tag.string("usurvive_deep_sleepers", (args, ctx) ->{
                CruxWorld crux = CruxCore.core().worldManager().getWorld(object.getUID());
                if(crux == null) return "0";
                SleeperWorldModule sleeperModule = crux.getModule(SleeperWorldModule.class);
                if(sleeperModule == null) return "0";
                return sleeperModule.getDeepSleepersAmount() + "";
            }))
            .add(Tag.string("usurvive_time_skip_per_tick", (args, ctx) ->{
                CruxWorld crux = CruxCore.core().worldManager().getWorld(object.getUID());
                if(crux == null) return "0";
                SleeperWorldModule sleeperModule = crux.getModule(SleeperWorldModule.class);
                if(sleeperModule == null) return "0";
                return sleeperModule.getTimeSkipPerTick() + "";
            }))
            ;
    }
}
