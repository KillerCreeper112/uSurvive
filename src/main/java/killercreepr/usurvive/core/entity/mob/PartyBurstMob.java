package killercreepr.usurvive.core.entity.mob;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.entity.mob.goals.UseRunToTargetItemGoal;
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

public class PartyBurstMob extends UCruxMob {
    protected final Class<? extends Mob> type;
    protected final int attackIntervalMin;
    protected final float attackRadius;
    protected final float useItemRadius;
    protected final BiConsumer<UseRunToTargetItemGoal, Boolean> changePos;
    public PartyBurstMob(@NotNull Key key, Class<? extends Mob> type, int attackIntervalMin, float attackRadius, float useItemRadius, BiConsumer<UseRunToTargetItemGoal, Boolean> changePos) {
        super(key);
        this.type = type;
        this.attackIntervalMin = attackIntervalMin;
        this.attackRadius = attackRadius;
        this.useItemRadius = useItemRadius;
        this.changePos = changePos;
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, type, e ->{
            //e.setCustomNameVisible(false);
            //e.setSilent(true);
            load(e);

            e.getEquipment().setItem(EquipmentSlot.HAND, Crux.handlers().item().getItem(Crux.key("party_burst")).value());

            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CruxGoalUtil.addIfNotPresent(mob, UseRunToTargetItemGoal.class, 0, () ->
            new UseRunToTargetItemGoal(
                mob, ItemPredicate.fromType(Crux.key("party_burst")),
                1.3D, attackIntervalMin, attackRadius, useItemRadius,
                goal ->{
                    ItemStack item = mob.getEquipment().getItem(goal.getUsableItemSlot());
                    CruxItem crux = CruxItem.wrap(item);
                    var data = crux.getOrDefaultData(USurviveComponents.PARTY_BURST);
                    Location loc = mob.getEyeLocation();
                    loc = CruxLoc.lookAt(loc, mob.getTarget().getLocation().add(0, mob.getTarget().getHeight()*.5, 0));
                    data.launch(mob, loc, data.getAttributes());
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
                public void startUsingItem() {
                    ItemStack item = mob.getEquipment().getItem(getUsableItemSlot());
                    if(CruxItem.isEmpty(item)) return;


                    CruxItem crux = CruxItem.wrap(item);
                    var boneFlute = crux.getOrDefaultData(USurviveComponents.PARTY_BURST);
                    if(boneFlute == null) return;

                    super.startUsingItem();

                    CreateSound.sound(Sound.ENTITY_CREEPER_PRIMED, net.kyori.adventure.sound.Sound.Source.HOSTILE,
                        1.3f, 2f).playAt(mob);
                }
            }
        );
    }
}
