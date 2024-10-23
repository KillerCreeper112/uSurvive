package killercreepr.usurvive.component.impl;

import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.block.component.CruxBlockComponent;
import killercreepr.usurvive.block.ActiveOrbitConduit;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OrbitConduitComponent implements CruxBlockComponent {
    protected final Key currencyKey;

    public OrbitConduitComponent(Key currencyKey) {
        this.currencyKey = currencyKey;
    }

    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveOrbitConduit(block, crux, currencyKey);
    }
}
