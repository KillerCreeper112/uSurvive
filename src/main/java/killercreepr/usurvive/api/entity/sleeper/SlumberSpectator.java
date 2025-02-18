package killercreepr.usurvive.api.entity.sleeper;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;

public interface SlumberSpectator {
    void onStopSpectating(PlayerStopSpectatingEntityEvent event);
}
