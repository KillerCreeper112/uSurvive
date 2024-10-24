package killercreepr.usurvive.component.impl;

import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.block.component.CruxBlockComponent;
import killercreepr.usurvive.block.ActiveOrbitConduit;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OrbitConduitComponent implements CruxBlockComponent {
    protected final List<String> interactCommands;

    public OrbitConduitComponent(List<String> interactCommands) {
        this.interactCommands = interactCommands;
    }

    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveOrbitConduit(block, crux, interactCommands);
    }
}
