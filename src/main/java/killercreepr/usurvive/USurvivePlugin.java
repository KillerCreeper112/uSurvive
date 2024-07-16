package killercreepr.usurvive;

import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.usurvive.block.USurviveBlocks;
import killercreepr.usurvive.death.DeathManager;
import killercreepr.usurvive.entity.USurviveMobs;
import killercreepr.usurvive.item.USurviveItems;
import org.bukkit.event.Listener;

public class USurvivePlugin extends CruxPlugin implements Listener {
    private static USurvivePlugin instance;
    public static USurvivePlugin inst(){ return instance; }

    protected final DeathManager deathManager = new DeathManager();
    public DeathManager getDeathManager() {
        return deathManager;
    }

    @Override
    public void enabled() {
        instance = this;
        USurviveBlocks.register();
        USurviveItems.register();
        USurviveMobs.register();
        registerListeners(
            deathManager
        );
    }

    @Override
    public void disabled() {
        super.disabled();
    }

    @Override
    public void reload() {
        super.reload();
    }
}
