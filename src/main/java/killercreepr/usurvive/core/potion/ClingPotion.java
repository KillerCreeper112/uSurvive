package killercreepr.usurvive.core.potion;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.communication.CreateTitle;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.api.potion.inflictor.PotionInflictor;
import killercreepr.cruxpotions.core.potions.InflictedPotion;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.usurvive.core.damage.type.USurviveDamageTypes;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClingPotion extends InflictedPotion {
    public ClingPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
    }

    public ClingPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
        super(potion, entity, duration, amplifier, inflictor);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cooldown = calculateCooldown();
    }

    protected int tick = 0;
    protected int cooldown;
    @Override
    protected void onTick() {
        super.onTick();
        tick++;
        if(tick < cooldown) return;
        tick = 0;
        cooldown = calculateCooldown();
        Crux.scheduler().runTask(() ->{
            damage(1D, cooldown);
        });
    }

    public int calculateCooldown(){
        return CruxMath.random(
            Math.max(10, 35 - (amplifier*10)),
            Math.max(20, 50 - (amplifier*5))
        );
    }

    public void damage(double dmg, int cooldown){
        Location loc = entity.getLocation().add(
            CruxMath.random(-1, 1),
            CruxMath.random(-1, 1),
            CruxMath.random(-1, 1)
        );

        var dmger = EntityDamager.entityDamager(entity)
            .setHitPosition(loc);
        var source = DamageSource.builder(USurviveDamageTypes.CLING)
            .withDamageLocation(loc);
        if(inflictor instanceof EntityInflictor inflict){
            var inflictorEntity = inflict.value();
            dmger.setDamager(inflictorEntity);
            if(inflictorEntity != null){
                source.withDirectEntity(inflictorEntity);
            }
        }
        dmger.setSource(source.build())
            .attack(dmg);
        CreateSound.sound(Sound.ENTITY_PLAYER_ATTACK_STRONG, .4f, 2f)
            .playAt(entity);

        int fadeOutTicks = (int) (cooldown * .1f);
        int stayTicks = cooldown - fadeOutTicks;

        String spacing = "<crux_space:" + CruxMath.random(-100, 100) + ">";
        CreateTitle title;
        if(CruxMath.random().nextBoolean()){
            title = CreateTitle.title(
                null, spacing + "<font:crux:cling>1", 0, stayTicks, fadeOutTicks
            );
        }else{
            title = CreateTitle.title(
                spacing + "<font:crux:cling>1", null, 0, stayTicks, fadeOutTicks
            );
        }
        title.use(entity);
    }
}
