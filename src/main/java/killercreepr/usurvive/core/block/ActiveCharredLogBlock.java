package killercreepr.usurvive.core.block;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.context.BlockContext;
import killercreepr.cruxblocks.core.block.active.SimpleActiveCruxBlock;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ActiveCharredLogBlock extends SimpleActiveCruxBlock {
    public ActiveCharredLogBlock(@NotNull Block block, @NotNull CruxBlock cruxBlock) {
        super(block, cruxBlock);
    }

    @Override
    public void broken(@NotNull BlockContext ctx) {
        super.broken(ctx);
        new ParticleBuilder(Particle.LARGE_SMOKE)
            .count(CruxMath.random(3, 5))
            .offset(.5, .5, .5)
            .speed(.05)
            .location(block.getLocation().toCenterLocation())
            .spawn()
        ;
    }
}