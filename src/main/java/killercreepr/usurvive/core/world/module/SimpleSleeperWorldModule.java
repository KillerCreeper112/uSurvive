package killercreepr.usurvive.core.world.module;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.data.tick.Ticked;
import killercreepr.crux.api.text.context.InputContext;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import killercreepr.usurvive.api.world.module.SleeperWorldModule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class SimpleSleeperWorldModule extends SimpleWorldModule implements Ticked, SleeperWorldModule {
    protected final Collection<UUID> sleepers = new HashSet<>();
    protected final Holder<NumberProvider> sleeperTimeSkip;
    protected int timeSkip;
    public SimpleSleeperWorldModule(@NotNull CruxWorld parent, Holder<NumberProvider> sleeperTimeSkip) {
        super(parent);
        this.sleeperTimeSkip = sleeperTimeSkip;
    }
    @Override
    public void tick() {
        if(sleepers.isEmpty()) return;
        World world = parent.toBukkitWorld();
        if(world.isFixedTime()) return;
        long time = CruxMath.wrap(world.getTime() + timeSkip, 0, 24000);
        world.setTime(time);
    }

    public int calculateTimeSpeed(){
        return sleeperTimeSkip.value().sample(InputContext.inputContext(
            TagContainer.string().hook(parent.toBukkitWorld())
        )).intValue();
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
        timeSkip = calculateTimeSpeed();
    }

    @Override
    public void removeSleeper(@NotNull UUID uuid) {
        sleepers.remove(uuid);
        timeSkip = calculateTimeSpeed();
    }

    @Override
    public boolean hasSleeper(@NotNull UUID uuid) {
        return sleepers.contains(uuid);
    }
}
