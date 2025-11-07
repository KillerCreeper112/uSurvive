package killercreepr.usurvive.core.entity.mob.guide;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.usurvive.core.entity.mob.goals.guide.GuidePathGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Guide1NPC extends SimpleCruxMob {
    public Guide1NPC(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location loc, @Nullable Consumer<Entity> consumer) {
        return loc.getWorld().spawn(loc, Villager.class, e ->{
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        Crux.getServer().getMobGoals().removeAllGoals(mob);
        CruxGoalUtil.addIfNotPresent(mob, GuidePathGoal.class, 0, () -> new GuidePathGoal(mob));
    }
}
