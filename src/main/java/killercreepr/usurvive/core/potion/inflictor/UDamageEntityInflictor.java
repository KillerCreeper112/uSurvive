package killercreepr.usurvive.core.potion.inflictor;

import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class UDamageEntityInflictor extends EntityInflictor {
    public static final String ID = "udamage_entity";
    protected final float damage;
    public UDamageEntityInflictor(@NotNull Entity e, float damage) {
        super(e);
        this.damage = damage;
    }

    public UDamageEntityInflictor(@NotNull UUID uuid, @Nullable Key type, float damage) {
        super(uuid, type);
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }
}
