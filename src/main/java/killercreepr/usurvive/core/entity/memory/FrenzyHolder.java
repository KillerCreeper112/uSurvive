package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxKey;
import killercreepr.crux.core.util.CruxMath;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrenzyHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("enchantment/frenzy");

    public FrenzyHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public FrenzyHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Entity e) {
        return super.shouldRemoveFromMemory(e) || isLastHitExpired();
    }

    public boolean isLastHitExpired(){
        return !CruxMath.hasOccurredWithin(lastHit, calculateExpireTime());
    }

    public int calculateExpireTime(){
        return level * 80;
    }

    @Override
    protected void removingFromMemory(@Nullable Entity e) {
        super.removingFromMemory(e);
        reset(e);
    }

    public void reset(Entity e){
        if(!(e instanceof LivingEntity living)) return;
        var instance = living.getAttribute(Attribute.ATTACK_SPEED);
        if(instance == null) return;
        instance.removeModifier(key);
    }

    @Override
    public void onMemoryUnload(@NotNull Entity e) {
        super.onMemoryUnload(e);
        reset(e);
    }

    public void updateAttribute(Entity e){
        if(!(e instanceof LivingEntity living)) return;
        var instance = living.getAttribute(Attribute.ATTACK_SPEED);
        if(instance == null) return;
        instance.removeModifier(key);
        double amount = calculateAttackSpeedMultiplier();
        if(amount == 0D) return;
        instance.addTransientModifier(new AttributeModifier(
            CruxKey.key(key), amount, AttributeModifier.Operation.MULTIPLY_SCALAR_1
        ));
    }

    public double calculateAttackSpeedMultiplier(){
        if(hits < 1) return 0D;
        double amount = level * 0.03;
        float hitAmounts = hits / 3f;

        return Math.min(amount * hitAmounts, calculateMaxAttackSpeedMultiplier());
    }

    public double calculateMaxAttackSpeedMultiplier(){
        return .4f + ((level-1) * .15f);
    }

    protected int hits;
    protected long lastHit;
    protected int level;

    @Override
    public void tick(@NotNull Entity entity) {

    }

    public void onHit(Entity entity, Entity victim, int level){
        hits++;
        lastHit = System.currentTimeMillis();
        this.level = level;
        updateAttribute(entity);
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public long getLastHit() {
        return lastHit;
    }

    public void setLastHit(long lastHit) {
        this.lastHit = lastHit;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
