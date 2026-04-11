package killercreepr.usurvive.external.discordsrv;

import github.scarsz.discordsrv.hooks.vanish.VanishHook;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.vanish.VanishHandler;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class USurviveVanishHook implements VanishHook {
  /**
   * @param player
   * @deprecated since 1.26.0. Please use the "vanished" metadata key instead ({@link Player#setMetadata(String, MetadataValue)}).
   */
  @Override
  public boolean isVanished(Player player) {
    return VanishHandler.isVanished(player);
  }

  @Override
  public Plugin getPlugin() {
    return USurvivePlugin.inst();
  }
}
