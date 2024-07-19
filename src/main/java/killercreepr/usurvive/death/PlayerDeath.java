package killercreepr.usurvive.death;

import killercreepr.crux.data.world.CruxPosition;
import killercreepr.crux.data.world.PositionPosed;
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
    protected @NotNull UUID worldUUID;
    protected @NotNull CruxPosition position;
    protected final @NotNull UUID guardianUUID;
    protected @NotNull Collection<ItemStack> drops;
    protected final int experience;
    protected final long time;

    public PlayerDeath(@NotNull UUID playerUUID, @NotNull UUID worldUUID, @NotNull CruxPosition position, @NotNull UUID guardianUUID, @NotNull Collection<ItemStack> drops, int experience, long time) {
        this.playerUUID = playerUUID;
        this.worldUUID = worldUUID;
        this.position = position;
        this.guardianUUID = guardianUUID;
        this.drops = drops;
        this.experience = experience;
        this.time = time;
    }

    public void setWorldUUID(@NotNull UUID worldUUID) {
        this.worldUUID = worldUUID;
    }

    public void setPosition(@NotNull CruxPosition position) {
        this.position = position;
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
        return Bukkit.getWorld(worldUUID);
    }

    public @Nullable Entity getGuardian(){
        return Bukkit.getEntity(guardianUUID);
    }

    @NotNull
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @NotNull
    public UUID getWorldUUID() {
        return worldUUID;
    }

    @NotNull
    public UUID getGuardianUUID() {
        return guardianUUID;
    }

    @Override
    public @NotNull CruxPosition getPosition() {
        return position;
    }
}
