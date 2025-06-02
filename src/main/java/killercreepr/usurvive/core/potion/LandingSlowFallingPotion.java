package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LandingSlowFallingPotion extends ActiveEntityPotion<LivingEntity> {
    protected final NamespacedKey key = Crux.key("potion/landing_slow_falling");
    public LandingSlowFallingPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
    }

    @Override
    protected void onStop(@NotNull LivingEntity e) {
        super.onStop(e);
        e.removePotionEffect(PotionEffectType.SLOW_FALLING);
    }

    protected int tick = 0;

    @Override
    protected void onTick(@NotNull LivingEntity e) {
        super.onTick(e);
        tick++;
        if(tick % 10 == 0){
            Block b = CruxEntityUtil.getBlockStandingOn(entity);
            if(b != null && (b.isSolid() || b.isLiquid())){
                duration = 0;
                return;
            }
        }
        e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20, 0));
    }
}
