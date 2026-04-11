package killercreepr.usurvive.external.discordsrv;

import github.scarsz.discordsrv.DiscordSRV;import org.bukkit.Bukkit;import org.bukkit.entity.Player;import org.bukkit.plugin.Plugin;

public class DiscordSRVHook {
  public static boolean enabled = false;
  public static void onEnable(Plugin plugin){
    enabled = true;
    DiscordSRV.api.subscribe(new DiscordSRVListener());
    DiscordSRV.getPlugin().getPluginHooks().add(new USurviveVanishHook());
  }

  public static void fakeJoin(Player player){
  }
}
