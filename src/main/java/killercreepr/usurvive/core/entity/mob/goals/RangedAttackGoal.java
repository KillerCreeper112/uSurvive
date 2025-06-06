package killercreepr.usurvive.core.entity.mob.goals;

import killercreepr.crux.api.data.Holder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

public class RangedAttackGoal {
    public final Mob mob;
    public final double strafeSpeed;
    public int direction = 1; // 1 = right, -1 = left
    public int time = 0;
    public final double minApproachDistanceSq;
    public final double maxStrafeDistanceSq;
    public final Holder<LivingEntity> targetHolder;
    public int cooldown = 0;

    public RangedAttackGoal(Mob mob, double strafeSpeed, double minApproachDistance, double maxStrafeDistance, Holder<LivingEntity> targetHolder) {
        this.mob = mob;
        this.strafeSpeed = strafeSpeed;
        this.targetHolder = targetHolder;
        this.maxStrafeDistanceSq = maxStrafeDistance * maxStrafeDistance;
        this.minApproachDistanceSq = minApproachDistance * minApproachDistance;
    }

    public boolean canUse(){
        return cooldown < 1;
    }

    public void tick() {
        if(cooldown > 0){
            cooldown--;
        }
        if(!canUse()) return;
        LivingEntity target = targetHolder.value();
        if (target == null) return;
        if(!mob.hasLineOfSight(target)) return;

        double distanceSq = mob.getLocation().distanceSquared(target.getLocation());

        if (distanceSq > maxStrafeDistanceSq) {
            // Too far — move closer to target (centered on ground)
            Location targetLoc = target.getLocation();
            ((CraftMob) mob).getHandle().getMoveControl()
                .setWantedPosition(targetLoc.getX(), targetLoc.getY(), targetLoc.getZ(), 1.0D);
            return;
        }

        if (distanceSq < minApproachDistanceSq) {
            // Optional: If too close, you could back away or stop
            return;
        }

        time++;
        if (time % 40 == 0) {
            direction *= -1; // Change strafe direction every 2 seconds
        }

        // Vector from entity to target
        Vector toTarget = target.getLocation().toVector().subtract(mob.getLocation().toVector()).normalize();

        // Perpendicular strafe vector
        Vector strafeVec = new Vector(-toTarget.getZ(), 0, toTarget.getX()).multiply(strafeSpeed * direction);

        Location strafeLocation = mob.getLocation().add(strafeVec);

        CraftMob e = (CraftMob) mob;
        e.getHandle().getMoveControl().setWantedPosition(strafeLocation.getX(), strafeLocation.getY(), strafeLocation.getZ(), 1.0f);
    }
}
