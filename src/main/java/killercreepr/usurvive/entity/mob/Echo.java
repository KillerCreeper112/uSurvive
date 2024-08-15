package killercreepr.usurvive.entity.mob;

import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxGoalUtil;
import killercreepr.cruxentities.entity.GenericCruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.usurvive.entity.mob.goals.EchoGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Skeleton;
import org.jetbrains.annotations.NotNull;

public class Echo extends GenericCruxMob {
    public static final Key KEY = Crux.key("echo");
    public Echo(@NotNull Key key) {
        super(key);
    }
    public Echo(){
        this(KEY);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location) {
        return location.getWorld().spawn(location, Skeleton.class, e ->{
            e.setShouldBurnInDay(false);
            e.getEquipment().clear();
            //todo model
            load(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CruxGoalUtil.addIfNotPresent(mob, CruxMobGoal.defaultKey(), 0, () ->{
            return new EchoGoal(mob);//todo
        });
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{
            MobCategory.ETERNAL
        };
    }
}
