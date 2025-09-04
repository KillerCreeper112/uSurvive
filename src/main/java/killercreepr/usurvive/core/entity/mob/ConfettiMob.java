package killercreepr.usurvive.core.entity.mob;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.entity.mob.goals.UseRangedItemGoal;
import killercreepr.usurvive.core.mechanics.ConfettiCannonComponent;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConfettiMob extends UCruxMob {
    protected final Class<? extends Mob> type;
    protected final int attackIntervalMin;
    protected final float attackRadius;
    protected final BiConsumer<UseRangedItemGoal, Boolean> changePos;
    public ConfettiMob(@NotNull Key key, Class<? extends Mob> type, int attackIntervalMin, float attackRadius, BiConsumer<UseRangedItemGoal, Boolean> changePos) {
        super(key);
        this.type = type;
        this.attackIntervalMin = attackIntervalMin;
        this.attackRadius = attackRadius;
        this.changePos = changePos;
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, type, e ->{
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
                1D, attackIntervalMin, attackRadius,
                goal ->{
                    ItemStack item = mob.getEquipment().getItem(goal.getUsableItemSlot());
                    CruxItem crux = CruxItem.wrap(item);
                    ConfettiCannonComponent data = crux.getOrDefaultData(USurviveComponents.CONFETTI_CANNON);
                    Location loc = mob.getEyeLocation();
                    loc = CruxLoc.lookAt(loc, mob.getTarget().getLocation().add(0, mob.getTarget().getHeight()*.5, 0));
                    data.launch(mob, loc, crux.getOrDefaultData(CruxAttributeComponents.PROJECTILE_CRUX_ATTRIBUTES));

                    if(data.getItemRemoval() > 0){
                        goal.editHeldItem(editItem ->{
                            editItem.setAmount(editItem.getAmount()-data.getItemRemoval());
                            return editItem;
                        });
                    }
                }
            ){
                @Override
                public void changePose(boolean on) {
                    if(changePos != null){
                        changePos.accept(this, on);
                        return;
                    }
                    super.changePose(on);
                }

                @Override
                public void warn() {
                    CreateSound.sound(Sound.BLOCK_NOTE_BLOCK_HAT, net.kyori.adventure.sound.Sound.Source.HOSTILE,
                        2.5f, 1.1f).playAt(mob);
                }
            }
        );
    }
}
