package killercreepr.usurvive.core.world.module;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.data.tick.Ticked;
import killercreepr.crux.api.text.context.InputContext;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
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
    protected final Collection<UUID> deepSleepers = new HashSet<>();
    protected final Holder<NumberProvider> sleeperTimeSkip;
    protected int timeSkip;
    protected final Runnable timeSkipTask;
    public SimpleSleeperWorldModule(@NotNull CruxWorld parent, Holder<NumberProvider> sleeperTimeSkip) {
        super(parent);
        this.sleeperTimeSkip = sleeperTimeSkip;
        timeSkipTask = () ->{
            World world = parent.toBukkitWorld();
            long time = CruxMath.wrap(world.getTime() + timeSkip, 0, 24000);
            world.setTime(time);
        };
    }

    protected int tick = 0;
    @Override
    public void tick() {
        //long timeSkipped = (newTime - startTime + 24000) % 24000;
        if(timeSkip == 0) return;
        tick++;
        if(tick < 2){
            return;
        }
        tick = 0;
        World world = parent.toBukkitWorld();
        if(world.isFixedTime()) return;
        Crux.scheduler().runTask(timeSkipTask);
    }

    public int calculateTimeSpeed(){
        if(deepSleepers.isEmpty()) return 0;
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
    }

    @Override
    public void removeSleeper(@NotNull UUID uuid) {
        sleepers.remove(uuid);
        removeDeepSleeper(uuid);
    }

    @Override
    public boolean hasSleeper(@NotNull UUID uuid) {
        return sleepers.contains(uuid);
    }

    @Override
    public int getDeepSleepersAmount() {
        return deepSleepers.size();
    }

    @NotNull
    @Override
    public Collection<UUID> getDeepSleepers() {
        return deepSleepers;
    }

    @Override
    public void addDeepSleeper(@NotNull UUID uuid) {
        deepSleepers.add(uuid);
        timeSkip = calculateTimeSpeed();
    }

    @Override
    public void removeDeepSleeper(@NotNull UUID uuid) {
        deepSleepers.remove(uuid);
        timeSkip = calculateTimeSpeed();
    }

    @Override
    public boolean hasDeepSleeper(@NotNull UUID uuid) {
        return deepSleepers.contains(uuid);
    }

    @Override
    public int getTimeSkipPerTick() {
        return timeSkip;
    }
}
