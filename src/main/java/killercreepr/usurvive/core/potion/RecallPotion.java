package killercreepr.usurvive.core.potion;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.SimpleActivePotion;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RecallPotion extends SimpleActivePotion {
    public RecallPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
    }

    protected Location savedPosition;
    protected Collection<Location> circle;
    @Override
    protected void onStart() {
        savedPosition = entity.getLocation();
        Location l = savedPosition.clone();
        l.setPitch(0f);
        l.add(0, .1, 0);
        circle = CruxLoc.getCircleRelative(l, .3D, .2D);
    }

    protected final ParticleBuilder particle = new ParticleBuilder(Particle.DUST)
        .data(new Particle.DustOptions(Color.PURPLE, .5f))
        .extra(.1)
        ;
    @Override
    protected void onTick() {
        if(savedPosition==null) return;

        if(amplifier < 1 && !savedPosition.getWorld().equals(entity.getWorld())){
            duration = 0;
            return;
        }

        for(Location l : circle){
            particle.location(l).spawn();
        }
    }

    @Override
    protected void onStop() {
        if(savedPosition==null) return;
        Crux.scheduler().runTask(() ->{
            if(amplifier < 1 && !savedPosition.getWorld().equals(entity.getWorld())){
                CreateSound.sound(Sound.BLOCK_BEACON_DEACTIVATE, 1.7f).playAt(entity);
                return;
            }

            savedPosition.setDirection(entity.getLocation().getDirection());
            entity.teleportAsync(savedPosition).whenComplete((value, throwable) ->{
                if(throwable != null) throwable.printStackTrace();

                Location parLoc = savedPosition.clone();
                parLoc.add(0, entity.getHeight()/2D, 0D);
                new ParticleBuilder(Particle.WITCH)
                    .count(15)
                    .offset(.5, .5, .5)
                    .extra(.7)
                    .location(parLoc)
                    .spawn()
                ;
                CreateSound.sound(Sound.ENTITY_PLAYER_TELEPORT, 1.3f).playAt(savedPosition);
            });
        });
    }

    public Location getSavedPosition() {
        return savedPosition;
    }

    public void setSavedPosition(Location savedPosition) {
        this.savedPosition = savedPosition;
    }
}
