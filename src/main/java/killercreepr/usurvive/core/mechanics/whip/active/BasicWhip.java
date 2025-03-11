package killercreepr.usurvive.core.mechanics.whip.active;

import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.api.mechanics.whip.ActiveWhip;
import killercreepr.usurvive.core.attribute.USurviveAttribute;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public abstract class BasicWhip implements ActiveWhip {

    public void onEntityHit(Entity owner, Entity hit){
        EntityDamager.entityDamager(
            hit, owner
        ).attack(
            CruxAttribute.get(owner, USurviveAttribute.WHIP_DAMAGE),
            CruxAttribute.get(owner, USurviveAttribute.WHIP_KNOCKBACK),
            CruxAttribute.get(owner, USurviveAttribute.WHIP_KNOCKBACK_UP)
        );
    }

    protected boolean scheduledForRemoval = false;

    @Override
    public boolean isScheduledForRemoval() {
        return scheduledForRemoval;
    }

    @Override
    public void scheduleForRemoval() {
        this.scheduledForRemoval = true;
    }

    @Override
    public boolean isValid(@NotNull Entity owner) {
        return !isScheduledForRemoval() && owner.isValid();
    }
}
