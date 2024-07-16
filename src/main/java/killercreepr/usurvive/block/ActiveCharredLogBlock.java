package killercreepr.usurvive.block;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlockImpl;
import killercreepr.cruxblocks.block.context.BlockContext;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ActiveCharredLogBlock extends ActiveCruxBlockImpl {
    public ActiveCharredLogBlock(@NotNull Block block, @NotNull CruxBlock cruxBlock) {
        super(block, cruxBlock);
    }

    @Override
    public void broken(@NotNull BlockContext ctx) {
        super.broken(ctx);
        new ParticleBuilder(Particle.SMOKE)
            .count(4)
            .offset(.5, .5, .5)
            .extra(.1)
            .location(block.getLocation().toCenterLocation())
            .spawn()
        ;
    }
}