package killercreepr.usurvive.core.entity;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.usurvive.core.entity.memory.GustTNTHolder;
import killercreepr.usurvive.core.entity.memory.StickyChargeMemory;
import killercreepr.usurvive.core.entity.mob.UCruxMob;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.bukkit.entity.TNTPrimed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class StickyCharge extends UCruxMob {
    public StickyCharge(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            e.customName(Component.text("Sticky Charge"));
            e.setCustomNameVisible(false);
            e.setSilent(true);

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK,
                CruxAttributeModifier.baseModifier(20));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.baseModifier(6));

            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(e instanceof Mob mob){

            Bukkit.getMobGoals().getAllGoals(mob).forEach(goal ->{
                Bukkit.getMobGoals().removeGoal(mob, goal);
            });

            CompletableFuture<ActiveModel> model = new DesignEntity(mob).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
            model.whenComplete((m, throwable) ->{
                m.getAnimationHandler().playAnimation("explode", 0D, 0D, 1D, true);
            });
            applyWhenCompleteModel(mob, model);
        }

        var mem = new StickyChargeMemory(e);
        mem.setFuseTicks(60);
        EntityMemory.register(mem, true);
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{
            MobCategory.OBJECT
        };
    }
}
