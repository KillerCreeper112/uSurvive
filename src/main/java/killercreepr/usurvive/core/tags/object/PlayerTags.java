package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.entity.memory.EntityMemory;
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
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstatistics.api.bukkit.BukkitStatisticHolder;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.data.entity.MemoryPlayerSettingsHolder;
import killercreepr.usurvive.core.entity.memory.SleeperHolder;
import killercreepr.usurvive.core.statistic.USurviveStatistics;
import killercreepr.usurvive.core.vanish.VanishHandler;
import net.kyori.adventure.key.Key;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class PlayerTags implements ObjectTag<OfflinePlayer> {
    @NotNull
    @Override
    public Class<OfflinePlayer> getObjectType() {
        return OfflinePlayer.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("player_");
    }

    public int getURank(Entity e){
        int highest = 0;
        for (PermissionAttachmentInfo perm : e.getEffectivePermissions()) {
            if(!perm.getValue()) continue;
            String x = perm.getPermission();
            if(x.startsWith("usurvive.rank.")){
                try{
                    int value = Integer.parseInt(x.replace("usurvive.rank.", ""));
                    if(value > highest) highest = value;
                }catch (IllegalArgumentException ignored){}
            }
        }
        return highest;
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull OfflinePlayer offline, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("is_vanished",(args, ctx) ->{
                if(!offline.isOnline()) return "false";
                return VanishHandler.isVanished(offline.getPlayer()) + "";
            }))
            .add(Tag.string("urank",(args, ctx) ->{
                if(!offline.isOnline()) return "false";
                var p = offline.getPlayer();
                return getURank(p) + "";
            }))
            .add(Tag.string("is_afk",(args, ctx) ->{
                if(!offline.isOnline()) return "false";
                Player object = offline.getPlayer();
                return (object.getIdleDuration().compareTo(Duration.ofMinutes(3)) >= 0) + "";
            }))
            .add(Tag.string("utotal_play_time", (args, ctx) ->{
                long playTime = offline.getStatistic(Statistic.PLAY_ONE_MINUTE);
                var holder = BukkitStatisticHolder.statisticHolder(offline.getUniqueId());
                if(holder != null){
                    long timeSeconds = holder.getStatistic(USurviveStatistics.IDLE_TIME);
                    playTime -= (timeSeconds * 20L);
                }
                return Math.max(playTime, 0) + "";
            }))
            .add(Tag.string("uworld/players", (args, ctx) ->{
                if(!offline.isOnline()) return "0";
                World world = offline.getPlayer().getWorld();
                int got = 0;
                for (Player p : world.getPlayers()) {
                    if(VanishHandler.isVanished(p)) continue;
                    got++;
                }
                return got + "";
            }))
            .add(Tag.string("urecent_deaths", (args, ctx) ->{
                UPlayer u = UPlayer.getPlayer(offline.getUniqueId());
                if(u == null) return "0";
                return u.updateAndGetRecentDeaths() + "";
            }))
            .add(Tag.string("uhas_set_rtp", (args, ctx) ->{
                var u = UPlayer.getPlayer(offline.getUniqueId());
                if(u == null) return "false";
                if(u.getRandomTPData() == null) return "false";
                Key world = Crux.key(ctx.deserializeString(args.get(0)));
                return u.getRandomTPData().hasLastTeleported(world) + "";
            }))
            .add(Tag.string("u_last_rtp_x", (args, ctx) ->{
                var u = UPlayer.getPlayer(offline.getUniqueId());
                if(u == null) return "0";
                if(u.getRandomTPData() == null) return "0";
                Key world = Crux.key(ctx.deserializeString(args.get(0)));
                var pos = u.getRandomTPData().getLastTeleported(world);
                if(pos==null) return "0";
                return pos.getLocation().x() + "";
            }))
            .add(Tag.string("u_last_rtp_y", (args, ctx) ->{
                var u = UPlayer.getPlayer(offline.getUniqueId());
                if(u == null) return "0";
                if(u.getRandomTPData() == null) return "0";
                Key world = Crux.key(ctx.deserializeString(args.get(0)));
                var pos = u.getRandomTPData().getLastTeleported(world);
                if(pos==null) return "0";
                return pos.getLocation().y() + "";
            }))
            .add(Tag.string("u_last_rtp_z", (args, ctx) ->{
                var u = UPlayer.getPlayer(offline.getUniqueId());
                if(u == null) return "0";
                if(u.getRandomTPData() == null) return "0";
                Key world = Crux.key(ctx.deserializeString(args.get(0)));
                var pos = u.getRandomTPData().getLastTeleported(world);
                if(pos==null) return "0";
                return pos.getLocation().z() + "";
            }))
            .add(Tag.string("u_last_rtp_time", (args, ctx) ->{
                var u = UPlayer.getPlayer(offline.getUniqueId());
                if(u == null) return "0";
                if(u.getRandomTPData() == null) return "0";
                Key world = Crux.key(ctx.deserializeString(args.get(0)));
                var pos = u.getRandomTPData().getLastTeleported(world);
                if(pos==null) return "0";
                return pos.getTime() + "";
            }))
            .add(Tag.string("uhas_same_last_rtp_time", (args, ctx) ->{
                var u = UPlayer.getPlayer(offline.getUniqueId());
                if(u == null) return "false";
                if(u.getRandomTPData() == null) return "false";
                Key world = Crux.key(ctx.deserializeString(args.get(0)));
                var pos = u.getRandomTPData().getLastTeleported(world);
                if(pos==null) return "false";
                CruxWorld crux = CruxCore.core().worldManager().getWorld(world);
                if(crux == null) return "false";
                Long time = crux.get(USurviveComponents.WORLD_RANDOM_TELEPORT_TIME);
                return (time != null && time == pos.getWorldTime()) + "";
            }))
            ;
    }

    @Nullable
    @Override
    public HookedObjectContainer<StringHookedObjectTag<?>> hookStrings(@NotNull OfflinePlayer offline, Object base, Object parent, @NotNull TagParser tags) {
        var c = HookedObjectContainer.string();
        if(!offline.isOnline()) return null;
        Player object = offline.getPlayer();
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
    public HookedObjectContainer<StringListHookedObjectTag<?>> hookStringLists(@NotNull OfflinePlayer offline, Object base, Object parent, @NotNull TagParser tags) {
        var c = HookedObjectContainer.stringList();
        if(!offline.isOnline()) return null;
        Player object = offline.getPlayer();
        PlayerSettingsHolder holder = EntityMemory.getOrCreateDataHolder(object, MemoryPlayerSettingsHolder.class);
        if(holder != null){
            c.addAll(tags.hookStringLists(holder, base, object, FormatPrefix.simple("settings/")));
        }
        return c;
    }
}
