package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.data.Holder;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Collection;

public class WrappedLocationDistanceDialog extends WrappedDialogContent {
  protected final Holder<Location> location;
  protected final double distance;
  protected final double distanceSqr;

  public WrappedLocationDistanceDialog(DialogContent child, Holder<Location> location, double distance) {
    super(child);
    this.location = location;
    this.distance = distance;
    this.distanceSqr = distance * distance;
  }

  @Override
  public boolean canPlay(DialogPlayer player) {
    return super.canPlay(player) && isAnyNear(player.audience());
  }

  public boolean isAnyNear(Collection<Audience> list){
    var loc = location.value();
    for (Audience audience : list) {
      if(audience instanceof Entity e && isNear(e, loc)) return true;
    }
    return false;
  }

  public boolean isNear(Entity e, Location loc){
    var world = e.getWorld();
    if(!world.equals(loc.getWorld())) return false;
    return e.getLocation().distanceSquared(loc) <= distanceSqr;
  }
}
