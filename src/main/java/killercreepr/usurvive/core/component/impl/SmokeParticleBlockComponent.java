package killercreepr.usurvive.core.component.impl;

import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.api.block.component.CruxBlockComponent;
import killercreepr.usurvive.core.block.ActiveCharredLogBlock;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmokeParticleBlockComponent implements CruxBlockComponent {
    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveCharredLogBlock(block, crux);
    }
}
