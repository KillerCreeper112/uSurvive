package killercreepr.usurvive.core.component.impl;

import killercreepr.cruxblocks.api.block.component.CruxBlockComponent;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.List;

public class AttackPotionEffectsComponent implements CruxBlockComponent {
    protected final Collection<AttackPotionEffect> effects;

    public AttackPotionEffectsComponent(Collection<AttackPotionEffect> effects) {
        this.effects = effects;
    }

    public Collection<AttackPotionEffect> getEffects() {
        return effects;
    }

    public static class AttackPotionEffect{
        protected final List<PotionEffect> effect;
        protected final float chance;

        public AttackPotionEffect(List<PotionEffect> effect, float chance) {
            this.effect = effect;
            this.chance = chance;
        }

        public List<PotionEffect> getEffects() {
            return effect;
        }

        public float getChance() {
            return chance;
        }
    }
}
