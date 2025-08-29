package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.ParticleBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxColor;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class GlitterZombieGoal extends CruxMobGoal implements Listener {
    private final float[] hues = new float[4]; // one hue per armor piece

    public GlitterZombieGoal(@NotNull Mob mob) {
        super(mob);

        // Initialize each hue to a different starting point
        for (int i = 0; i < hues.length; i++) {
            hues[i] = i * 0.25f; // spaced evenly around the color wheel
        }
    }

    @Override
    public void tick() {
        if (!(mob instanceof LivingEntity living)) return;

        ItemStack[] armor = {
            living.getEquipment().getHelmet(),
            living.getEquipment().getChestplate(),
            living.getEquipment().getLeggings(),
            living.getEquipment().getBoots()
        };

        int index = -1;
        for(var slot : AttributeBukkitAdaptor.ARMOR_SLOTS){
            index++;
            if(index >= armor.length) break;
            if(!mob.canUseEquipmentSlot(slot)) continue;
            ItemStack piece = mob.getEquipment().getItem(slot);
            if(CruxItem.isEmpty(piece)) continue;
            piece.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(CruxColor.hsbToBukkitColor(hues[index] * 360f, 100f, 100f)));
            mob.getEquipment().setItem(slot, piece, true);

            hues[index] += CruxMath.random(0.01f, 0.04f);
            if (hues[index] > 1f) hues[index] = 0f;
        }
    }


    @Override
    protected void onRemovalOrDeath(boolean died) {
        super.onRemovalOrDeath(died);
        if(!died) return;
        playDeathEffect();
    }

    public void playDeathEffect(){
        String particle = "confetti";
        Location loc = mob.getLocation();
        loc.setPitch(CruxMath.random(-90, -75));
        loc.setYaw(CruxMath.random(-180, 180));
        int parAmount = CruxMath.random(25, 35);
        double force = CruxMath.random(1.5D, 2.5D);
        NumberProvider parLifeSpan = NumberProvider.uniform(80, 120);

        Crux.getServer().dispatchCommand(
            Crux.getServer().getConsoleSender(),
            "comet spawn " + particle + " " +
                loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() +
                " {\"optimize\":false,\"force\":" + force + ",\"parAmount\":" + parAmount + ",\"pitch\":" + loc.getPitch() + ",\"yaw\":" + loc.getYaw()
                + ",\"lifeSpanMin\":" + parLifeSpan.getMinValue().intValue() + ",\"lifeSpanMax\":" + parLifeSpan.getMaxValue().intValue()
                + "}"
        );

        CreateSound.sound(Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST,
            net.kyori.adventure.sound.Sound.Source.HOSTILE,
            2.5f, 1.2f).playAt(mob);
        CreateSound.sound(Sound.ENTITY_FIREWORK_ROCKET_TWINKLE,
            net.kyori.adventure.sound.Sound.Source.HOSTILE,
            2f, 1.5f).playAt(mob);

        Vector dir = loc.getDirection();
        int parCount = CruxMath.random(8, 10);
        for(int i = 0; i < parCount; i++){
            double angleX = Math.toRadians(CruxMath.random(-20, 20));
            double angleZ = Math.toRadians(CruxMath.random(-20, 20));
            Vector x = dir.clone().rotateAroundX(angleX).rotateAroundZ(angleZ);
            new ParticleBuilder(Particle.FIREWORK)
                .location(mob.getLocation().add(0, mob.getHeight()*.5, 0))
                .count(0)
                .extra(.35)
                .offset(x.getX(), x.getY(), x.getZ())
                .spawn();
        }

        var data = CruxEntity.entity(mob).get(CruxAttributeComponents.PROJECTILE_CRUX_ATTRIBUTES);
        if(data == null) return;

        double range = CruxMath.random(3D, 5D);
        new GetEntityNear<>(LivingEntity.class)
            .center(mob)
            .range(range)
            .filter(e -> !e.equals(mob) && e instanceof HumanEntity)
            .find().forEach(hit ->{
                double potency = CruxMath.distanceFalloff(
                    hit, mob, range, 1.5f
                );

                EntityDamager.entityDamager(hit, mob)
                    .attack(
                        data.getValue(CruxAttribute.ATTACK_DAMAGE) * potency,
                        data.getValue(CruxAttribute.ATTACK_KNOCKBACK) * potency,
                        data.getValue(CruxAttribute.ATTACK_KNOCKBACK_UP) * potency
                    );
            });
    }
}
