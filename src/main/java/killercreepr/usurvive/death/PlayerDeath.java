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
    protected final @NotNull UUID worldUUID;
    protected final @NotNull CruxPosition position;
    protected final @NotNull UUID guardianUUID;
    protected final @NotNull Collection<ItemStack> drops;
    protected final int experience;

    public PlayerDeath(@NotNull UUID playerUUID, @NotNull UUID worldUUID, @NotNull CruxPosition position, @NotNull UUID guardianUUID, @NotNull Collection<ItemStack> drops, int experience) {
        this.playerUUID = playerUUID;
        this.worldUUID = worldUUID;
        this.position = position;
        this.guardianUUID = guardianUUID;
        this.drops = drops;
        this.experience = experience;
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
