package killercreepr.usurvive.entity.mob.goals.animal;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.api.data.holder.LocationHolder;
import killercreepr.crux.api.valueproviders.number.NumberHolder;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.valueproviders.number.ConstantNumber;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class AnimalEnhancedPanicGoal extends CruxMobGoal {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("animal_enhanced_panic"));
    public AnimalEnhancedPanicGoal(@NotNull Mob mob) {
        super(KEY, mob);
    }

    public AnimalEnhancedPanicGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }
    protected int panicTicks = 0;

    public int getPanicTicks() {
        return panicTicks;
    }

    public void setPanicTicks(int panicTicks) {
        this.panicTicks = panicTicks;
    }

    protected @NotNull NumberHolder speedMultiplier = new ConstantNumber(2D);

    private double currentSpeedMultiplier = 1D;
    @Override
    public void start() {
        super.start();
        mob.getPathfinder().stopPathfinding();
    }

    @Override
    protected boolean isValid() {
        return super.isValid() && panicTicks > 0;
    }

    @Override
    public void tick() {
        panicTick();
    }

    public void panic(@NotNull LocationHolder from, int panicTicks){
        setPanicTicks(panicTicks);
        hitFrom = from;
        currentSpeedMultiplier = speedMultiplier.value().doubleValue();
    }

    protected LocationHolder hitFrom;
    public void panicTick(){
        panicTicks--;
        if(panicTicks < 1){
            return;
        }
        if(mob.getPathfinder().getCurrentPath() != null) return;

        Location l = CruxGoalUtil.findRandomAwayLocation(hitFrom.value(), mob.getLocation(), 16D, 0D);
        l.setY(mob.getY());
        moveTo(l, currentSpeedMultiplier);
    }

    public @NotNull NumberHolder getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(@NotNull NumberHolder speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public double getCurrentSpeedMultiplier() {
        return currentSpeedMultiplier;
    }

    public void setCurrentSpeedMultiplier(double currentSpeedMultiplier) {
        this.currentSpeedMultiplier = currentSpeedMultiplier;
    }

    public LocationHolder getHitFrom() {
        return hitFrom;
    }

    public void setHitFrom(LocationHolder hitFrom) {
        this.hitFrom = hitFrom;
    }
}
