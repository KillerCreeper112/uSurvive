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
import killercreepr.usurvive.core.vanish.VanishHandler;
import killercreepr.usurvive.core.world.module.ResetWorldModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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
                CruxWorld crux = CruxCore.core().worldManager().getWorld(object.key());
                if(crux == null) return "0";
                SleeperWorldModule sleeperModule = crux.getModule(SleeperWorldModule.class);
                if(sleeperModule == null) return "0";
                var total = 0;
                for (UUID uuid : sleeperModule.getSleepers()) {
                    var online = Bukkit.getPlayer(uuid);
                    if(online == null || !VanishHandler.isVanished(online)) total++;
                }
                return total + "";
            }))
            .add(Tag.string("usurvive_deep_sleepers", (args, ctx) ->{
                CruxWorld crux = CruxCore.core().worldManager().getWorld(object.key());
                if(crux == null) return "0";
                SleeperWorldModule sleeperModule = crux.getModule(SleeperWorldModule.class);
                if(sleeperModule == null) return "0";
                var total = 0;
                for (UUID uuid : sleeperModule.getDeepSleepers()) {
                    var online = Bukkit.getPlayer(uuid);
                    if(online == null || !VanishHandler.isVanished(online)) total++;
                }
                return total + "";
                //return sleeperModule.getDeepSleepersAmount() + "";
            }))
            .add(Tag.string("usurvive_time_skip_per_tick", (args, ctx) ->{
                CruxWorld crux = CruxCore.core().worldManager().getWorld(object.key());
                if(crux == null) return "0";
                SleeperWorldModule sleeperModule = crux.getModule(SleeperWorldModule.class);
                if(sleeperModule == null) return "0";
                return sleeperModule.getTimeSkipPerTick() + "";
            }))
          .add(Tag.string("uplayers", (args, ctx) ->{
              int got = 0;
              for (Player p : object.getPlayers()) {
                  if(VanishHandler.isVanished(p)) continue;
                  got++;
              }
              return got + "";
          }))
            .add(Tag.string("usurvive_resetting_in", (args, ctx) ->{
                CruxWorld crux = CruxCore.core().worldManager().getWorld(object.key());
                if(crux == null) return "0";
                ResetWorldModule sleeperModule = crux.getModule(ResetWorldModule.class);
                if(sleeperModule == null) return "0";
                return sleeperModule.getResettingIn() + "";
            }))
            ;
    }
}
