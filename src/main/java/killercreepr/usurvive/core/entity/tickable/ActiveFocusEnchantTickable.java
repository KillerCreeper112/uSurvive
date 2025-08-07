package killercreepr.usurvive.core.entity.tickable;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.communication.CreateTitle;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.SimpleActiveEntityTickable;
import killercreepr.usurvive.core.enchant.USurviveEnchant;
import killercreepr.usurvive.core.entity.memory.ParticleTrailHolder;
import killercreepr.usurvive.core.entity.memory.StandardParticleTrailHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ActiveFocusEnchantTickable extends ListenerActiveEntityTickable {
    protected final HumanEntity p;
    public ActiveFocusEnchantTickable(Entity entity, EntityTickable tickable, CruxSlot slot) {
        super(entity, tickable, slot);
        p = (HumanEntity) entity;
    }

    public static float getMaxMultiplier(int level, float scale, float growth) {
        if (level <= 0) return 1f;
        return 1f + (scale * (float) Math.pow(level, growth));
    }

    public static float getMultiplier(int usedTime, int level){
        return getMultiplier(usedTime, level, exponent, scale, growth);
    }

    public static float getMultiplier(int usedTime, int level, float exponent, float scale, float growth) {
        if (level <= 0) return 1f;

        int chargeTime = usedTime - 20;
        if (chargeTime <= 0) return 1f;

        float baseTime = 70f;
        float reductionPerLevel = 0f;
        float maxBonusTime = Math.max(10f, baseTime - ((level - 1) * reductionPerLevel));

        float progress = Math.min(chargeTime / maxBonusTime, 1f);
        float curvedProgress = (float) Math.pow(progress, exponent);

        float maxMultiplier = getMaxMultiplier(level, scale, growth);
        return 1f + (maxMultiplier - 1f) * curvedProgress;
    }

    protected int lastUsedTime = -2;

    protected int chargeLevel = 0;
    protected static int maxChargeLevel = 4;

    public void resetCharge(){
        chargeLevel = 0;
    }

    public float getChargeLevelProgress(float maxMultiplier, int level){
        float x = (maxMultiplier-1f) / maxChargeLevel;
        return x * level;
    }

    public void chargeTick(float multiplier, float maxMultiplier){
        float nextCharge = 1f + getChargeLevelProgress(maxMultiplier, chargeLevel+1);
        if(multiplier < nextCharge) return;
        chargeLevel++;
        onChargeLevel(chargeLevel);
    }

    public void onChargeLevel(int level){
        Location loc = CruxLoc.shift(p.getEyeLocation(), .5D, -.3D, .1D);
        loc.getWorld().spawnParticle(Particle.TRIAL_OMEN, loc, level);

        Crux.scheduler().runTask(() ->{
            CreateSound.sound(Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, .5f, 2f - (level*.15f)).playAt(entity);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if(!event.getEntity().equals(entity)) return;

        Entity projectile = event.getProjectile();
        if(chargeLevel < 1) return;

        ParticleBuilder particle = new ParticleBuilder(Particle.TRIAL_OMEN)
            .extra(.05);
        int tickRate = switch (chargeLevel){
            case 2 -> 7;
            case 3 -> 5;
            case 4 -> 3;
            default -> 10;
        };

        int level = 1;
        ItemStack item = event.getBow();
        if(!CruxItem.isEmpty(item)){
            level = item.getEnchantmentLevel(USurviveEnchant.FOCUS);
        }

        double multiplier = 1f + (level * 0.007f);
        double x = 1.5D * ((float) chargeLevel / (float) maxChargeLevel);

        Vector vec = projectile.getVelocity();
        projectile.setVelocity(vec.multiply(multiplier * x));

        EntityMemory.getOrCreateDataHolder(projectile, StandardParticleTrailHolder.class, mem ->{
            return new StandardParticleTrailHolder(mem, particle, tickRate);
        });

        CreateSound.sound(Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, .5f, 2f).playAt(entity);
    }


    protected static float scale = 2f;
    protected static float growth = 1f;
    protected static float exponent = 2f;
    @Override
    public void tick() {
        ItemStack item = p.getActiveItem();
        if(CruxItem.isEmpty(item)) return;
        int usedTime = p.getActiveItemUsedTime();

        if(usedTime < 1){
            if(chargeLevel > 0){
                if(!CruxMath.hasOccurredWithin(lastUsedTime, 5)) resetCharge();
            }
            return;
        }

        if(lastUsedTime != -2 && usedTime < lastUsedTime){
            resetCharge();
        }

        lastUsedTime = usedTime;

        int level = item.getEnchantmentLevel(USurviveEnchant.FOCUS);
        float multiplier = getMultiplier(usedTime,  level);

        chargeTick(multiplier, getMaxMultiplier(level, scale, growth));

        if(chargeLevel > 0){
            CreateTitle.title("       <!shadow><font:crux:focus>" + chargeLevel, null, 0, 8, 0)
                .use(p);
        }
    }
}
