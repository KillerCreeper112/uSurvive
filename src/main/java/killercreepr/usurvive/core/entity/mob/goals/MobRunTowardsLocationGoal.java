package killercreepr.usurvive.core.entity.mob.goals;

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

public class MobRunTowardsLocationGoal extends CruxMobGoal {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("run_towards_center_location"));
    public MobRunTowardsLocationGoal(@NotNull Mob mob) {
        super(KEY, mob);
    }

    public MobRunTowardsLocationGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }
    protected int runTicks = 0;

    public int getRunTicks() {
        return runTicks;
    }

    public void setRunTicks(int panicTicks) {
        this.runTicks = panicTicks;
    }

    protected @NotNull NumberHolder speedMultiplier = new ConstantNumber(1.2D);

    private double currentSpeedMultiplier = 1D;
    @Override
    public void start() {
        super.start();
        mob.getPathfinder().stopPathfinding();
    }

    @Override
    protected boolean isValid() {
        return super.isValid() && runTicks > 0;
    }

    @Override
    public void tick() {
        runTick();
    }

    public void run(@NotNull LocationHolder targetCenter, int runTicks){
        setRunTicks(runTicks);
        this.targetCenter = targetCenter;
        currentSpeedMultiplier = speedMultiplier.value().doubleValue();
    }

    public Location findTarget(){
        return CruxGoalUtil.findRandomTowardsLocation(targetCenter.value(), mob.getLocation(), getTargetRange(), getTargetMaxAngleDegrees());
    }

    public double getTargetRange(){
        return 16D;
    }

    public double getTargetMaxAngleDegrees(){
        return 20D;
    }

    protected LocationHolder targetCenter;
    public void runTick(){
        if(runTicks > 0) runTicks--;
        if(runTicks < 1){
            return;
        }
        if(mob.getPathfinder().getCurrentPath() != null) return;

        Location l = findTarget();
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

    public LocationHolder getTargetCenter() {
        return targetCenter;
    }

    public void setTargetCenter(LocationHolder targetCenter) {
        this.targetCenter = targetCenter;
    }
}
