package killercreepr.usurvive.core.entity.mob;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.entity.mob.goals.UseRangedItemGoal;
import killercreepr.usurvive.core.mechanics.ConfettiCannonComponent;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ConfettiZombie extends UCruxMob {
    public ConfettiZombie(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Zombie.class, e ->{
            //e.setCustomNameVisible(false);
            //e.setSilent(true);
            load(e);

            e.getEquipment().setItem(EquipmentSlot.HAND, Crux.handlers().item().getItem(Crux.key("confetti_cannon")).value());

            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CruxGoalUtil.addIfNotPresent(mob, UseRangedItemGoal.class, 0, () ->
            new UseRangedItemGoal(
                mob, ItemPredicate.fromType(Crux.key("confetti_cannon")),
                1D, 20, 15f,
                goal ->{
                    ItemStack item = mob.getEquipment().getItem(goal.getUsableItemSlot());
                    CruxItem crux = CruxItem.wrap(item);
                    ConfettiCannonComponent data = crux.getOrDefaultData(USurviveComponents.CONFETTI_CANNON);
                    Location loc = mob.getEyeLocation();
                    loc = CruxLoc.lookAt(loc, goal.getTarget().getLocation().add(0, goal.getTarget().getHeight()*.5, 0));
                    data.launch(mob, loc, crux.getOrDefaultData(CruxAttributeComponents.PROJECTILE_CRUX_ATTRIBUTES));
                }
            ){
                @Override
                public void warn() {
                    CreateSound.sound(Sound.BLOCK_NOTE_BLOCK_HAT, net.kyori.adventure.sound.Sound.Source.HOSTILE,
                        2.5f, 1.1f).playAt(mob);
                }
            }
        );
    }
}
