package killercreepr.usurvive.block;

import killercreepr.cruxblocks.block.GenericDirectionalBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.block.texture.TextureData;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

public class CharredLogBlock extends GenericDirectionalBlock {
    public CharredLogBlock(@NotNull Key key, @NotNull TextureData textureData, @NotNull BlockFace direction) {
        super(key, textureData, direction);
    }

    @Override
    public @NotNull ActiveCruxBlock createActive(@NotNull Block block) {
        return new ActiveCharredLogBlock(block, this);
    }
}