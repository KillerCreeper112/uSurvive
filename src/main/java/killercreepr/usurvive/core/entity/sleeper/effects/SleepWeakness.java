package killercreepr.usurvive.core.entity.sleeper.effects;

import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.Set;

public class SleepWeakness {
    protected final Collection<PotionEffect> potions;

    public SleepWeakness(Collection<PotionEffect> potions) {
        this.potions = potions == null ? Set.of() : potions;
    }

    public Collection<PotionEffect> getPotions() {
        return potions;
    }
}
