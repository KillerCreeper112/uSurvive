package killercreepr.usurvive.core.entity.predicate;

import killercreepr.crux.api.entity.predicate.EntityPredicate;
import killercreepr.crux.core.util.CruxTag;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class MythicMobsEntityPredicate implements EntityPredicate {
  public final String id;

  public MythicMobsEntityPredicate(String id) {
    this.id = id;
  }

  @Override
  public boolean test(@NotNull Entity entity) {
    return id.equalsIgnoreCase(CruxTag.get(entity, "mythicmobs:type", PersistentDataType.STRING, null));
  }
}
