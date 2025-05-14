package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScramblePotion extends ActiveEntityPotion<LivingEntity> {
    protected final NamespacedKey key = Crux.key("potion/scramble");
    public ScramblePotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
    }
    private int cachedEvery;
    private void updateCache(){
        cachedEvery = Math.max(600 - (amplifier*60), 1);
    }

    @Override
    protected void onUpdate(int oldDuration, int oldAmplifier) {
        super.onUpdate(oldDuration, oldAmplifier);
        updateCache();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateCache();
    }

    private int tick = 0;

    @Override
    protected void onTick() {
        if(tick % cachedEvery == 0){
            tick = 0;
            if(entity instanceof Player p) randomizeHotBar(p);
        }
        tick++;
    }

    private void randomizeHotBar(@NotNull Player p){
        List<ItemStack> items = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            items.add(p.getInventory().getItem(i));
        }
        Collections.shuffle(items);
        int i = -1;
        for(ItemStack item : items){
            i++;
            p.getInventory().setItem(i, item);
        }
    }
}
