package killercreepr.usurvive.persistence;

import killercreepr.crux.persistence.PersistTag;
import killercreepr.usurvive.mechanics.grapple.Grapple;
import org.bukkit.persistence.PersistentDataType;

public class USurvivePersist {
    public static final PersistTag<String> DIMENSION = PersistTag.register(new PersistTag<>(PersistentDataType.STRING, "dimension"));
    public static final PersistTag<Grapple> GRAPPLE = PersistTag.register(new PersistTag<>(USurvivePersistence.GRAPPLE, "grapple"));
}
