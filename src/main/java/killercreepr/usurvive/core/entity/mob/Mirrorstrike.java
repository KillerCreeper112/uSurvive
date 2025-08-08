package killercreepr.usurvive.core.entity.mob;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Mirrorstrike extends UCruxMob {
    public Mirrorstrike(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            e.customName(Component.text("Mirrorstrike"));
            e.setCustomNameVisible(false);
            e.setAI(false);
            e.setSilent(true);
            e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(1.5D);
            e.setHealth(e.getAttribute(Attribute.MAX_HEALTH).getValue());
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CompletableFuture<ActiveModel> model = new DesignEntity(mob).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        //CruxGoalUtil.addIfNotPresent(mob, BuzzrotBeeGoal.class, 0, () -> new BuzzrotBeeGoal(mob));
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(
            MobCategory.OBJECT
        );
    }
}
