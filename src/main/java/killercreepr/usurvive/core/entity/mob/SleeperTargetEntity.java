package killercreepr.usurvive.core.entity.mob;

import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.usurvive.core.entity.mob.goals.SleeperTargetGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SleeperTargetEntity extends SimpleCruxMob {
    public SleeperTargetEntity(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Villager.class, e ->{
            e.setSilent(true);
            e.setPersistent(false);
            e.setRemoveWhenFarAway(true);
            //e.setInvisible(true);
            e.setVisibleByDefault(false);
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CruxGoalUtil.addIfNotPresent(mob, SleeperTargetGoal.class, 0, () -> new SleeperTargetGoal(mob));
    }
}
