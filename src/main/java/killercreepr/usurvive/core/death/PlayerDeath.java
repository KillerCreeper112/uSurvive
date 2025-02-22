package killercreepr.usurvive.core.death;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.api.math.PositionPosed;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class PlayerDeath implements PositionPosed {
    protected final @NotNull UUID playerUUID;
    protected @NotNull Key worldKey;
    protected @NotNull CruxPosition deathPosition;
    protected @NotNull CruxPosition gravePosition;
    protected final @NotNull UUID guardianUUID;
    protected @NotNull Collection<ItemStack> drops;
    protected final int experience;
    protected final long time;
    protected final int expiryTime;

    public PlayerDeath(@NotNull UUID playerUUID,
                       @NotNull Key worldKey,
                       @NotNull CruxPosition deathPosition,
                       @NotNull CruxPosition gravePosition,
                       @NotNull UUID guardianUUID,
                       @NotNull Collection<ItemStack> drops,
                       int experience, long time, int expiryTime) {
        this.playerUUID = playerUUID;
        this.worldKey = worldKey;
        this.deathPosition = deathPosition;
        this.gravePosition = gravePosition;
        this.guardianUUID = guardianUUID;
        this.drops = drops;
        this.experience = experience;
        this.time = time;
        this.expiryTime = expiryTime;
    }

    public int getExpiryTime() {
        return expiryTime;
    }

    public int getTimeUntilExpire(){
        return Math.max(expiryTime - (int) ((System.currentTimeMillis() - time) / 50L), 0);
    }

    public boolean hasExpired(){
        return getTimeUntilExpire() < 1;
    }

    public void setWorldKey(@NotNull Key worldKey) {
        this.worldKey = worldKey;
    }

    public void setDrops(@NotNull Collection<ItemStack> drops) {
        this.drops = drops;
    }

    public long getTime() {
        return time;
    }

    @NotNull
    public Collection<ItemStack> getDrops() {
        return drops;
    }

    public int getExperience() {
        return experience;
    }

    public @Nullable World getWorld(){
        return Bukkit.getWorld(worldKey);
    }

    public @Nullable Entity getGuardian(){
        return Bukkit.getEntity(guardianUUID);
    }

    @NotNull
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @NotNull
    public Key getWorldKey() {
        return worldKey;
    }

    @NotNull
    public UUID getGuardianUUID() {
        return guardianUUID;
    }

    @NotNull
    public CruxPosition getGravePosition() {
        return gravePosition;
    }

    public void setGravePosition(@NotNull CruxPosition gravePosition) {
        this.gravePosition = gravePosition;
    }

    @NotNull
    public CruxPosition getDeathPosition() {
        return deathPosition;
    }

    public void setDeathPosition(@NotNull CruxPosition deathPosition) {
        this.deathPosition = deathPosition;
    }

    @Override
    public @NotNull CruxPosition getPosition() {
        return gravePosition;
    }
}
