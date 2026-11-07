package killercreepr.usurvive.core.block;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxblocks.api.block.component.CruxBlockComponent;
import killercreepr.cruxblocks.api.block.context.BlockContext;
import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class GlintingOreBlockComponent implements CruxBlockComponent {
    @Override
    public void onBroken(@NotNull CruxBlockBreakEvent event) {
        CruxBlockComponent.super.onBroken(event);
        BlockContext ctx = event.getContext();
        Location spawn = ctx.getBlock().getLocation().toCenterLocation();
        new ParticleBuilder(Particle.ELECTRIC_SPARK)
            .location(spawn)
            .count(CruxMath.random(5, 8))
            .offset(.3, .3,.3)
            .speed(.3)
            .spawn()
        ;
        new ParticleBuilder(Particle.FIREWORK)
            .location(spawn)
            .count(CruxMath.random(5, 8))
            .offset(.3, .3,.3)
            .speed(.3)
            .spawn()
        ;

        new BukkitRunnable(){
            int tick = 0;
            final int max = CruxMath.random(3, 4);
            @Override
            public void run() {
                tick++;
                if(tick >= max){
                    cancel();
                }

                CreateSound.sound(Sound.BLOCK_AMETHYST_BLOCK_BREAK, CruxMath.random(1.3f, 1.6f)).playAt(spawn);
            }
        }.runTaskTimer(Crux.getMainPlugin(), 0L, 3L);
    }
}
