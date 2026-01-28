package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.loot.conditions.LootCondition;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.List;

public class LocationDistanceDialog extends SimpleDialogContent {
  protected final Holder<Location> location;
  protected final double distance;
  protected final double distanceSqr;
  public LocationDistanceDialog(List<DialogLine> dialog, List<DialogAction> actions,
                                DataExchange info,
                                LootCondition canPlayCondition, Holder<Location> location, double distance) {
    super(dialog, actions, info, canPlayCondition);
    this.location = location;
    this.distance = distance;
    this.distanceSqr = distance * distance;
  }

  @Override
  public boolean canPlay(DialogPlayer player) {
    return super.canPlay(player) || isAnyNotNear(player.audience());
  }

  public boolean isAnyNotNear(Collection<Audience> list){
    var loc = location.value();
    for (Audience audience : list) {
      if(audience instanceof Entity e && !isNear(e, loc)) return true;
    }
    return false;
  }

  public boolean isNear(Entity e, Location loc){
    var world = e.getWorld();
    if(!world.equals(loc.getWorld())) return false;
    return e.getLocation().distanceSquared(loc) <= distanceSqr;
  }
}
