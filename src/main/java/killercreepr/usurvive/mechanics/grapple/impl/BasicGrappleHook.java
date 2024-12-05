package killercreepr.usurvive.mechanics.grapple.impl;

import killercreepr.usurvive.mechanics.grapple.ActiveGrappleHook;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public abstract class BasicGrappleHook implements ActiveGrappleHook {
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
