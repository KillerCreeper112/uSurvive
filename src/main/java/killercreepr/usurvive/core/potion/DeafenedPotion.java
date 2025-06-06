package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import net.kyori.adventure.sound.SoundStop;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class DeafenedPotion extends ActiveEntityPotion<LivingEntity> {
    public DeafenedPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
    }

    /*@EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player p = event.getPlayer();
        if(!p.equals(entity)) return;
        event.message(Component.empty().decorate(TextDecoration.OBFUSCATED)
            .append(event.message()));
    }*/

    @Override
    protected void onTick() {
        super.onTick();
        entity.stopSound(SoundStop.all());
    }
}
