package killercreepr.usurvive.core.component.impl;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.api.block.component.CruxBlockComponent;
import killercreepr.usurvive.core.block.ActiveWatcherPillar;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WatcherPillarComponent implements CruxBlockComponent {
    public final Key otherBlockGroup;
    public final boolean open;
    public final float nightTimeOpenChance;
    public final CreateSound changeToOtherSound;
    public final NumberProvider maxSpawnsPerNight;

    public WatcherPillarComponent(Key otherBlockGroup, boolean open, float nightTimeOpenChance, CreateSound changeToOtherSound, NumberProvider maxSpawnsPerNight) {
        this.otherBlockGroup = otherBlockGroup;
        this.open = open;
        this.nightTimeOpenChance = nightTimeOpenChance;
        this.changeToOtherSound = changeToOtherSound;
        this.maxSpawnsPerNight = maxSpawnsPerNight;
    }

    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveWatcherPillar(block, crux, null, null, this);
    }
}
