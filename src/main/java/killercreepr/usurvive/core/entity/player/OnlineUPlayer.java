package killercreepr.usurvive.core.entity.player;

import killercreepr.crux.api.math.CruxLocation;
import killercreepr.usurvive.api.entity.player.UPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.UUID;

@Deprecated(forRemoval = true, since = "todo, not sure what to do with this")
public class OnlineUPlayer implements UPlayer {
    protected final Reference<HumanEntity> reference;
    protected final DataUPlayer data;
    public OnlineUPlayer(HumanEntity entity) {
        this.reference = new WeakReference<>(entity);
        this.data = new DataUPlayer(entity.getUniqueId());
    }

    public HumanEntity getOnline(){
        return reference.get();
    }

    public boolean isOnline(){
        return reference.get() != null;
    }

    @NotNull
    @Override
    public UUID getUUID() {
        return data.getUUID();
    }

    @Override
    public long getLastLogout() {
        return data.getLastLogout();
    }

    @Nullable
    @Override
    public CruxLocation getLastKnownPosition() {
        return data.getLastKnownPosition();
    }

    @Nullable
    @Override
    public String getLastKnownWorld() {
        return data.getLastKnownWorld();
    }

    @Nullable
    @Override
    public String getLastKnownName() {
        return data.getLastKnownName();
    }

    @Override
    public void setLastLogout(long time) {
        data.setLastLogout(time);
    }

    @Override
    public void setLastKnownPosition(@Nullable CruxLocation pos) {
        data.setLastKnownPosition(pos);
    }

    @Override
    public void setLastKnownWorld(@Nullable String name) {
        data.setLastKnownWorld(name);
    }

    @Override
    public void setLastKnownName(@Nullable String name) {
        data.setLastKnownName(name);
    }

    @Override
    public boolean hasFriend(@NotNull UUID uuid) {
        return data.hasFriend(uuid);
    }

    @Override
    public boolean isApartOfParty(@NotNull UUID uuid) {
        return data.isApartOfParty(uuid);
    }

    @Override
    public void save() {
        data.save();
    }

    @Override
    public void load() {
        data.load();
    }

    @Override
    public void save(@NotNull Plugin plugin) {
        data.save(plugin);
    }

    @Override
    public void load(@NotNull Plugin plugin) {
        data.load(plugin);
    }
}
