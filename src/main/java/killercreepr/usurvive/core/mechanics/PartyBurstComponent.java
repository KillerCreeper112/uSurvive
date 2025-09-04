package killercreepr.usurvive.core.mechanics;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.ParticleBuilderSupplier;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.*;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxitems.api.item.component.ConsumableComponent;
import killercreepr.cruxitems.api.item.consume.ItemConsumeContext;
import killercreepr.cruxitems.api.item.consume.ItemConsumeResult;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyBurstComponent implements ConsumableComponent {
    protected final double force;
    protected final double size;
    protected final int parAmount;
    protected final int itemDamage;
    protected final int itemRemoval;
    protected final String particle;
    protected final CreateSound sound;
    protected final CreateSound secondSound;
    protected final int secondSoundDelay;
    protected final NumberProvider parLifeSpan;
    protected final CruxAttributeContainer attributes;
    protected final ParticleBuilderSupplier vanillaParticle;

    public PartyBurstComponent(double force, double size, int parAmount, int itemDamage, int itemRemoval, String particle, CreateSound sound, CreateSound secondSound, int secondSoundDelay, NumberProvider parLifeSpan, CruxAttributeContainer attributes, ParticleBuilderSupplier vanillaParticle) {
        this.force = force;
        this.size = size;
        this.parAmount = parAmount;
        this.itemDamage = itemDamage;
        this.itemRemoval = itemRemoval;
        this.particle = particle;
        this.sound = sound;
        this.secondSound = secondSound;
        this.secondSoundDelay = secondSoundDelay;
        this.parLifeSpan = parLifeSpan;
        this.attributes = attributes;
        this.vanillaParticle = vanillaParticle;
    }

    public ParticleBuilderSupplier getVanillaParticle() {
        return vanillaParticle;
    }

    public double getSize() {
        return size;
    }

    public int getItemRemoval() {
        return itemRemoval;
    }

    public CreateSound getSound() {
        return sound;
    }

    public CreateSound getSecondSound() {
        return secondSound;
    }

    public int getSecondSoundDelay() {
        return secondSoundDelay;
    }

    public NumberProvider getParLifeSpan() {
        return parLifeSpan;
    }

    public CruxAttributeContainer getAttributes() {
        return attributes;
    }

    public double getForce() {
        return force;
    }

    public int getParAmount() {
        return parAmount;
    }

    public int getItemDamage() {
        return itemDamage;
    }

    public String getParticle() {
        return particle;
    }

    public void launch(Entity user, Location loc, CruxAttributeContainer attributes){

        Crux.getServer().dispatchCommand(
            Crux.getServer().getConsoleSender(),
            "comet spawn " + particle + " " +
                loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() +
                " {\"optimize\":false,\"force\":" + force + ",\"parAmount\":" + parAmount + ",\"pitch\":" + -90 + ",\"yaw\":" + loc.getYaw()
                + ",\"lifeSpanMin\":" + parLifeSpan.getMinValue().intValue() + ",\"lifeSpanMax\":" + parLifeSpan.getMaxValue().intValue()
                + "}"
        );
        if(sound != null) sound.playAt(loc);
        if(secondSound != null){
            if(secondSoundDelay < 1){
                secondSound.playAt(loc);
            }else Crux.scheduler().runTaskLater(() -> secondSound.playAt(loc), secondSoundDelay);
        }
        if(vanillaParticle != null){
            vanillaParticle.build().location(loc).spawn();
        }

        if(attributes == null) return;

        CruxTeamComparer teamComparer = CruxTeamComparer.teamComparer(false, team -> !team.allowFriendlyFire());

        double range = attributes.getValue(CruxAttribute.ATTACK_RANGE);
        new GetEntityNear<>(LivingEntity.class)
            .center(user)
            .range(range)
            .amount(attributes.hasAttribute(CruxAttribute.ATTACK_PIERCE) ? (int) attributes.getValue(CruxAttribute.ATTACK_PIERCE) : null)
            .filter(e ->{
                if(!CruxMob.UNDESIRED_BEHAVIOR.test(e)) return false;
                return !e.equals(user) && !teamComparer.areOnSameTeam(e, user);
            }).find().forEach(hit ->{
                double potency = CruxMath.distanceFalloff(hit, user, range, attributes.getValueOrDefault(
                    CruxAttribute.IMPACT_EFFECTIVENESS, 1.5D
                ));

                EntityDamager.entityDamager(
                    hit, user
                ).attack(
                    attributes.getValue(CruxAttribute.ATTACK_DAMAGE) * potency,
                    attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK) * potency,
                    attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK_UP) * potency
                );
            });

        double forceBack = attributes.getValue(CruxAttribute.PUSHBACK);
        double forceBackY = attributes.getValue(CruxAttribute.UPWARDS_PUSHBACK);
        if(forceBack != 0D || forceBackY != 0D){
            user.setVelocity(user.getVelocity().add(
                EntityDamager.calculateEntityVelocity(CruxLoc.shift(user.getLocation(), 1D, 0D, 0D), user, forceBack, forceBackY)
            ));
        }
    }

    @Override
    public @NotNull ItemConsumeResult onConsume(@NotNull ItemConsumeContext ctx) {
        Player p = ctx.getPlayer();
        CruxItem crux = ctx.getItem();
        launch(p, p.getEyeLocation(), attributes);
        if(!CruxEntityUtil.isNonSurvival(p)){
            p.damageItemStack(ctx.getSlot(), itemDamage);
        }

        var useCooldown = crux.item().getData(DataComponentTypes.USE_COOLDOWN);
        if(useCooldown != null){
            p.setCooldown(crux.item(), (int) (useCooldown.seconds() * 20f));
        }

        return itemRemoval < 1 ? ItemConsumeResult.cancelled() : ItemConsumeResult.empty();
    }

    @Override
    public boolean isConsumable(@NotNull ItemConsumeContext ctx) {
        return true;
    }
}
