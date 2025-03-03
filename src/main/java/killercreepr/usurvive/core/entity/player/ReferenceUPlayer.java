package killercreepr.usurvive.core.entity.player;

import killercreepr.crux.api.math.CruxLocation;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.util.UUID;

public class ReferenceUPlayer extends DataUPlayer {
    protected final Reference<HumanEntity> reference;
    public ReferenceUPlayer(@NotNull UUID uuid, Reference<HumanEntity> reference) {
        super(uuid);
        this.reference = reference;
    }

    @Override
    public String getLastKnownName() {
        var online = reference.get();
        if(online != null) return online.getName();
        return super.getLastKnownName();
    }

    @Override
    public boolean isOnline() {
        return reference.get() != null;
    }

    @Nullable
    @Override
    public CruxLocation getLastKnownPosition() {
        var online = reference.get();
        if(online != null) return CruxLocation.location(online.getLocation());
        return super.getLastKnownPosition();
    }

    @Nullable
    @Override
    public Key getLastKnownWorld() {
        var online = reference.get();
        if(online != null) return online.getWorld().key();
        return super.getLastKnownWorld();
    }
}
