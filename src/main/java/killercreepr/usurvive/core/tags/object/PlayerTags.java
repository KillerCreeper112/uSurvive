package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.HookedObjectContainer;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.hook.StringHookedObjectTag;
import killercreepr.crux.core.text.hook.StringListHookedObjectTag;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.cruxstatistics.api.bukkit.BukkitStatisticHolder;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.data.entity.DeathHolder;
import killercreepr.usurvive.core.data.entity.MemoryPlayerSettingsHolder;
import killercreepr.usurvive.core.entity.memory.SleeperHolder;
import killercreepr.usurvive.core.statistic.USurviveStatistics;
import killercreepr.usurvive.core.vanish.VanishHandler;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class PlayerTags implements ObjectTag<Player> {
    @NotNull
    @Override
    public Class<Player> getObjectType() {
        return Player.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("player_");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull Player object, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("is_vanished",(args, ctx) -> VanishHandler.isVanished(object) + ""))
            .add(Tag.string("is_afk",(args, ctx) ->{
                return (object.getIdleDuration().compareTo(Duration.ofMinutes(3)) >= 0) + "";
            }))
            .add(Tag.string("utotal_play_time", (args, ctx) ->{
                long playTime = object.getStatistic(Statistic.PLAY_ONE_MINUTE);
                var holder = BukkitStatisticHolder.statisticHolder(object);
                if(holder != null){
                    long timeSeconds = holder.getStatistic(USurviveStatistics.IDLE_TIME);
                    playTime -= (timeSeconds * 1000L);
                }
                return Math.max(playTime, 0) + "";
            }))
            .add(Tag.string("urecent_deaths", (args, ctx) ->{
                UPlayer u = UPlayer.getPlayer(object);
                if(u == null) return "0";
                return u.updateAndGetRecentDeaths() + "";
            }))
            ;
    }

    @Nullable
    @Override
    public HookedObjectContainer<StringHookedObjectTag<?>> hookStrings(@NotNull Player object, Object base, Object parent, @NotNull TagParser tags) {
        var c = HookedObjectContainer.string();
        PlayerSettingsHolder holder = EntityMemory.getOrCreateDataHolder(object, MemoryPlayerSettingsHolder.class);
        if(holder != null){
            c.addAll(tags.hookStrings(holder, base, object, FormatPrefix.simple("settings/")));
        }
        SleeperHolder sleeper = EntityMemory.getOrCreateDataHolder(object, SleeperHolder.class);
        if(sleeper != null){
            c.addAll(tags.hookStrings(sleeper, base, object, FormatPrefix.simple("sleeper/")));
        }
        return c;
    }

    @Nullable
    @Override
    public HookedObjectContainer<StringListHookedObjectTag<?>> hookStringLists(@NotNull Player object, Object base, Object parent, @NotNull TagParser tags) {
        var c = HookedObjectContainer.stringList();
        PlayerSettingsHolder holder = EntityMemory.getOrCreateDataHolder(object, MemoryPlayerSettingsHolder.class);
        if(holder != null){
            c.addAll(tags.hookStringLists(holder, base, object, FormatPrefix.simple("settings/")));
        }
        return c;
    }
}
