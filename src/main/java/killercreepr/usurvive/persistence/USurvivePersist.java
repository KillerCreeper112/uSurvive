package killercreepr.usurvive.persistence;

import killercreepr.crux.persistence.PersistTag;
import org.bukkit.persistence.PersistentDataType;

public class USurvivePersist {
    public static final PersistTag<String> DIMENSION = PersistTag.register(new PersistTag<>(PersistentDataType.STRING, "dimension"));
}
