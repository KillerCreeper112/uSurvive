package killercreepr.usurvive.block;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.context.BlockContext;
import killercreepr.cruxblocks.core.block.active.SimpleActiveCruxBlock;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ActiveSoulParticleBlock extends SimpleActiveCruxBlock {
    public ActiveSoulParticleBlock(@NotNull Block block, @NotNull CruxBlock cruxBlock) {
        super(block, cruxBlock);
    }

    @Override
    public void broken(@NotNull BlockContext ctx) {
        super.broken(ctx);
        new ParticleBuilder(Particle.SOUL)
            .count(1)
            .offset(0, 0, 0)
            .extra(.05)
            .location(block.getLocation().toCenterLocation())
            .spawn()
        ;
    }
}