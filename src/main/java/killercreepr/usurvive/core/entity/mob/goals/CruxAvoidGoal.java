package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.location.EntityLocation;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.crux.core.util.GetNear;
import killercreepr.usurvive.core.entity.mob.goals.animal.AnimalEnhancedPanicGoal;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class CruxAvoidGoal extends AnimalEnhancedPanicGoal {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("avoid_entity"));
    protected final @NotNull Predicate<Entity> avoid;

    public CruxAvoidGoal(@NotNull Mob mob, @NotNull Predicate<Entity> avoid) {
        this(KEY, mob, avoid);
    }

    public CruxAvoidGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob, @NotNull Predicate<Entity> avoid) {
        super(key, mob);
        this.avoid = avoid;
        this.getNear = new GetEntityNear<>(new EntityLocation(mob), Entity.class)
            .filter(avoid)
            .amount(1)
            .range(8D)
            .operation(GetNear.Operation.NEAREST)
            .center(mob)
        ;
    }

    @Override
    public void tick() {
    }

    @Override
    protected boolean isValid() {
        if(super.isValid() && panicTicks > 0) return true;
        Entity nearby = checkNearby();
        if(nearby==null) return false;
        panic(new EntityLocation(nearby), CruxMath.random(50, 160));
        CreateSound.sound(Sound.ENTITY_FOX_SCREECH, 1.5f).playAt(mob);
        return true;
    }

    protected final GetNear<? extends Entity> getNear;
    public @Nullable Entity checkNearby(){
        return getNear.findFirst();
    }
}
