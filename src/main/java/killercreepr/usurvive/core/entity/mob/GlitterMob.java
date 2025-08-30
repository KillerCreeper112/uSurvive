package killercreepr.usurvive.core.entity.mob;

import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.usurvive.core.entity.mob.goals.GlitterMobGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GlitterMob extends UCruxMob {
    protected final Class<? extends Mob> type;
    public GlitterMob(@NotNull Key key, Class<? extends Mob> type) {
        super(key);
        this.type = type;
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, type, e ->{
            //e.setCustomNameVisible(false);
            //e.setSilent(true);
            Map<EquipmentSlot, ItemStack> map = Map.of(
                EquipmentSlot.HEAD, new ItemStack(Material.LEATHER_HELMET),
                EquipmentSlot.CHEST, new ItemStack(Material.LEATHER_CHESTPLATE),
                EquipmentSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS),
                EquipmentSlot.FEET, new ItemStack(Material.LEATHER_BOOTS)
            );
            int armorPieces = CruxMath.random(1,4);

            List<EquipmentSlot> slots = new ArrayList<>(map.keySet());
            Collections.shuffle(slots);

            for (int i = 0; i < armorPieces; i++) {
                EquipmentSlot slot = slots.removeFirst();
                ItemStack piece = map.get(slot);
                e.getEquipment().setItem(slot, piece);
            }

            var crux = CruxEntity.entity(e);
            crux.set(CruxAttributeComponents.PROJECTILE_CRUX_ATTRIBUTES, CruxAttributeContainer.baseContainer(Map.of(
                CruxAttribute.ATTACK_DAMAGE, CruxMath.random(3D, 6D),
                CruxAttribute.ATTACK_KNOCKBACK, CruxMath.random(10D, 15D),
                CruxAttribute.ATTACK_KNOCKBACK_UP, CruxMath.random(3D, 5D)
            )));

            load(e);

            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CruxGoalUtil.addIfNotPresent(mob, GlitterMobGoal.class, 0, () ->
            new GlitterMobGoal(mob)
        );
    }
}
