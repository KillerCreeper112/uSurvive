package killercreepr.usurvive.core.potion.rage;

import killercreepr.usurvive.core.entity.mob.goals.RageGoal;
import killercreepr.usurvive.core.potion.RagePotion;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class RagePotionMobHandler extends RagePotionHandler {
    protected final Mob mob;
    public RagePotionMobHandler(@NotNull Mob entity, @NotNull RagePotion effect) {
        super(entity, effect);
        this.mob = entity;
    }

    @Override
    public void onEntityDismount(EntityDismountEvent event) {
        super.onEntityDismount(event);
        if(!event.getEntity().equals(entity)) return;
        event.setCancelled(true);
    }

    @Override
    public void started() {
        MobDisguise disguise = new MobDisguise(DisguiseType.valueOf(entity.getType().toString()));
        if(entity.hasPotionEffect(PotionEffectType.GLOWING)) disguise.getWatcher().setGlowing(true);
        if(entity.hasPotionEffect(PotionEffectType.INVISIBILITY)) disguise.getWatcher().setInvisible(true);
        /*for(PotionEffect e : entity.getActivePotionEffects()){
            if(!e.hasParticles()) continue;
            disguise.getWatcher().addPotionEffect(e.getType());
        }*/
        DisguiseAPI.disguiseEntity(mob, disguise);

        Bukkit.getMobGoals().addGoal(mob, 0, new RageGoal(mob));
    }

    @Override
    public void stopped() {
        super.stopped();
        DisguiseAPI.undisguiseToAll(entity);

        Bukkit.getMobGoals().removeGoal(mob, RageGoal.KEY);
    }
}
