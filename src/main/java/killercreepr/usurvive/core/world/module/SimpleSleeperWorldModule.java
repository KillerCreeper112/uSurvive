package killercreepr.usurvive.core.world.module;

import killercreepr.crux.api.data.tick.Ticked;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import killercreepr.usurvive.api.world.module.SleeperWorldModule;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class SimpleSleeperWorldModule extends SimpleWorldModule implements Ticked, SleeperWorldModule {
    protected final Collection<UUID> sleepers = new HashSet<>();
    public SimpleSleeperWorldModule(@NotNull CruxWorld parent) {
        super(parent);
    }
    @Override
    public void tick() {
        if(sleepers.isEmpty()) return;
    }

    @Override
    public int getSleepersAmount() {
        return sleepers.size();
    }

    @NotNull
    @Override
    public Collection<UUID> getSleepers() {
        return sleepers;
    }

    @Override
    public void addSleeper(@NotNull UUID uuid) {
        sleepers.add(uuid);
    }

    @Override
    public void removeSleeper(@NotNull UUID uuid) {
        sleepers.remove(uuid);
    }

    @Override
    public boolean hasSleeper(@NotNull UUID uuid) {
        return sleepers.contains(uuid);
    }
}
