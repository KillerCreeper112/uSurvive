package killercreepr.usurvive.core.component.impl;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.ParticleBuilderSupplier;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.core.block.component.standard.EntitySpawnerComponent;
import killercreepr.usurvive.core.block.ActiveWatcherPillar;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class WatcherPillarOpenComponent extends WatcherPillarComponent {
    public final Collection<PotionEffect> nearbyPotionEffects;
    public final float potionEffectRange;
    public final ParticleBuilderSupplier particle;
    public final EntitySpawnerComponent entitySpawner;

    public WatcherPillarOpenComponent(Key otherBlockGroup, boolean open, float nightTimeOpenChance, CreateSound changeToOtherSound, NumberProvider maxSpawnsPerNight, Collection<PotionEffect> nearbyPotionEffects, float potionEffectRange, ParticleBuilderSupplier particle, EntitySpawnerComponent entitySpawner) {
        super(otherBlockGroup, open, nightTimeOpenChance, changeToOtherSound, maxSpawnsPerNight);
        this.nearbyPotionEffects = nearbyPotionEffects;
        this.potionEffectRange = potionEffectRange;
        this.particle = particle;
        this.entitySpawner = entitySpawner;
    }

    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveWatcherPillar(block, crux, entitySpawner, entitySpawner.spawner, this);
    }
}
