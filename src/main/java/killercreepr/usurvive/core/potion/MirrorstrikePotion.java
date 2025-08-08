package killercreepr.usurvive.core.potion;

import killercreepr.crux.api.math.CruxLocation;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.persistence.CruxPersist;
import killercreepr.crux.core.persistence.CruxPersistence;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateSpiral;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.api.potion.inflictor.PotionInflictor;
import killercreepr.cruxpotions.core.potions.InflictedPotion;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.usurvive.core.damage.type.USurviveDamageTypes;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.potion.inflictor.UDamageEntityInflictor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MirrorstrikePotion extends InflictedPotion {
    public MirrorstrikePotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
    }

    public MirrorstrikePotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
        super(potion, entity, duration, amplifier, inflictor);
    }

    protected Entity e;
    @Override
    protected void onTick() {
        super.onTick();
        if(e != null && !CruxEntityUtil.isValid(e)){
            duration = 0;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(e != null && e.isValid()){
            Crux.scheduler().runTask(() -> e.remove());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        e = USurviveMobs.MIRRORSTRIKE.spawn(entity.getLocation(), spawned ->{
            CruxTag.set(spawned, "target", CruxPersistence.UUID, entity.getUniqueId());
            if(inflictor instanceof EntityInflictor inflict){
                CruxPersist.OWNER.set(spawned, inflict.uuid());
            }
        });
        ShapeScheduler.builder()
            .locationTick(ctx ->{
                if(!e.isValid() || !CruxEntityUtil.isValid(entity)){
                    ctx.setCancelled(true);
                    duration = 0;
                    return;
                }

                CruxPosition pos = ctx.getLocation();
                Location loc = pos.toLocation(entity.getWorld());
                loc.getWorld().spawnParticle(Particle.CRIT, loc, 0);
                e.teleport(loc.add(0, e.getHeight()*-.5, 0));
            })
            .endTask(() ->{
                if(!CruxEntityUtil.isValid(entity)) return;
                duration = 0;
                if(e.isValid()){
                    var builder = DamageSource.builder(USurviveDamageTypes.MIRRORSTRIKE)
                        .withDamageLocation(e.getLocation())
                        .withDirectEntity(e);
                    if(inflictor instanceof EntityInflictor inflict){
                        Entity value = inflict.value();
                        if(value != null) builder.withCausingEntity(value);
                    }

                    double damage;
                    if(inflictor instanceof UDamageEntityInflictor inflict){
                        damage = inflict.getDamage();
                    }else damage = amplifier+1;

                    EntityDamager.entityDamager(entity, e)
                        .setSource(
                            builder.build()
                        )
                        .attack(damage);

                    e.remove();
                }
            })
            .shape(CreateSpiral.builder()
                .center(() -> CruxLocation.location(entity.getLocation().add(0, entity.getHeight()/2, 0), 0f, 0f))
                .radius(entity.getWidth() + CruxMath.random(0.4, .6))
                .height(1)
                .spacing(.5)
                .turns(1)
                .spiralGrowth(CruxMath.random(2.5, 3.5))
                .invertX(CruxMath.random().nextBoolean())
                .invertZ(CruxMath.random().nextBoolean())
                .build())
            .buildCached().schedule(duration);
    }
}
