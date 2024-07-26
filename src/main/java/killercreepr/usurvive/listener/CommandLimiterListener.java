package killercreepr.usurvive.listener;

import killercreepr.usurvive.values.ValuesProvider;
import killercreepr.usurvive.world.WorldUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CommandLimiterListener implements Listener {
    protected final @NotNull ValuesProvider cfg;
    public CommandLimiterListener(@NotNull ValuesProvider cfg) {
        this.cfg = cfg;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        World world = p.getWorld();

        String dimension = WorldUtil.getDimensionID(world);

        Map<String, List<String>> disabledCmds = cfg.DIMENSION_DISABLED_COMMANDS().value();
        if(disabledCmds==null) return;
        List<String> disabled = disabledCmds.get(dimension);
        if(disabled==null) return;


    }
}
