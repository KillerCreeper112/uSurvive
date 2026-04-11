package killercreepr.usurvive.external.discordsrv;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.SchedulerUtil;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;import org.bukkit.entity.Player;import org.bukkit.plugin.Plugin;

public class DiscordSRVHook {
  public static boolean enabled = false;
  public static void onEnable(Plugin plugin){
    enabled = true;
    DiscordSRV.api.subscribe(new DiscordSRVListener());
    DiscordSRV.getPlugin().getPluginHooks().add(new USurviveVanishHook());
  }

  public static void fakeJoin(Player player){
    var message = PlainTextComponentSerializer.plainText().serialize(
      Crux.format().deserialize("<yellow><player_name> joined the game", TagContainer.merged()
        .hook(player))
    );
    SchedulerUtil.runTaskAsynchronously(DiscordSRV.getPlugin(),
      () -> DiscordSRV.getPlugin().sendJoinMessage(player, message));
  }

  public static void fakeLeave(Player player){
    var message = PlainTextComponentSerializer.plainText().serialize(
      Crux.format().deserialize("<yellow><player_name> left the game", TagContainer.merged()
        .hook(player))
    );
    SchedulerUtil.runTaskAsynchronously(DiscordSRV.getPlugin(),
      () -> DiscordSRV.getPlugin().sendLeaveMessage(player, message));
  }
}
