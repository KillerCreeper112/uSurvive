package killercreepr.usurvive.api.event;

import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class NPCFinishPathAndDialogEvent extends Event {
  public static final HandlerList HANDLER_LIST = new HandlerList();

  protected final Entity entity;
  protected final PathTargetMobGoal goal;
  protected final DialogPlayer dialogPlayer;
  protected final DialogContent dialogContent;

  public NPCFinishPathAndDialogEvent(Entity entity, PathTargetMobGoal goal, DialogPlayer dialogPlayer, DialogContent dialogContent) {
    this.entity = entity;
    this.goal = goal;
    this.dialogPlayer = dialogPlayer;
    this.dialogContent = dialogContent;
  }

  public DialogContent getDialogContent() {
    return dialogContent;
  }

  public DialogPlayer getDialogPlayer() {
    return dialogPlayer;
  }

  public Entity getEntity() {
    return entity;
  }

  public PathTargetMobGoal getGoal() {
    return goal;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }
  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
