package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public class ZombieEatGoal extends CruxMobModeledGoal implements Listener {
    public ZombieEatGoal(@NotNull Mob mob) {
        super(mob);
    }

    public ZombieEatGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }

    @Override
    public void onPlayerInteractLow(PlayerInteractEntityEvent event) {
        super.onPlayerInteractLow(event);

        Player user = event.getPlayer();
        ItemStack item = user.getInventory().getItem(event.getHand());
        if(CruxItem.isEmpty(item)) return;
        if(!CruxItem.isEmpty(getHeldItem())) return;
        eat(item);
    }


    public void eat(ItemStack item){
        playAnimation("eat", true);
        Crux.scheduler().runTaskLater(() ->{
            setHeldItem(item);
        }, 2L);
    }

    public void setHeldItem(ItemStack item){
        getModel().getBone("right_item").get().setModel(item == null ? new ItemStack(Material.AIR) : item);
    }

    public ItemStack getHeldItem(){
        return getModel().getBone("right_item").get().getModel();
    }

    public Location getHeldItemLocation(){
        return getModel().getBone("right_item").get().getLocation();
    }

    public void particles(){
        if(mob.getActivePotionEffects().isEmpty()) return;

        net.minecraft.world.entity.Mob mob = ((CraftMob) this.mob).getHandle();
        int i = 4;
        int i1 = 1;
        if (mob.getRandom().nextInt(i * i1) == 0) {
            PotionEffect effect = CruxCollection.getRandom(this.mob.getActivePotionEffects());
            this.mob.getWorld().spawnParticle(
                Particle.ENTITY_EFFECT,
                mob.getRandomX(0.5), mob.getRandomY(),
                mob.getRandomZ(0.5), 0, 1.0, 1.0, 1.0,
                effect.getType().getColor()
            );
            /*mob.level().addParticle(Util.getRandom(list, mob.random), mob.getRandomX(0.5), mob.getRandomY(),
                mob.getRandomZ(0.5), 1.0, 1.0, 1.0);*/
        }
    }

    protected boolean wasEating = false;
    protected byte tick = 0;
    @Override
    public void tick() {
        particles();
        if(isPlayingAnimation("eat")){
            tick++;
            if(!wasEating){
                getModel().getAnimationHandler().setDefaultProperty(
                    new AnimationHandler.DefaultProperty(
                        ModelState.IDLE, "none", .1D, .1D, 1D
                    )
                );
                getModel().getAnimationHandler().setDefaultProperty(
                    new AnimationHandler.DefaultProperty(
                        ModelState.WALK, "none", .1D, .1D, 1D
                    )
                );
                stopAnimation("idle", true);
                stopAnimation("walk", true);
            }
            wasEating = true;

            ItemStack item = getHeldItem();
            if(!CruxItem.isEmpty(item)){
                Location loc = getHeldItemLocation().clone().add(0, .2, 0);
                int amount = CruxMath.random(5, 8);
                for(int i = 0; i < amount; i++){
                    new ParticleBuilder(Particle.ITEM)
                        .location(
                            loc.clone().add(
                                CruxMath.random(-.05, .05),
                                CruxMath.random(-.1, .1),
                                CruxMath.random(-.05, .05)
                            )
                        )
                        .count(0)
                        .offset(
                            CruxMath.random(-.1, 1),
                            CruxMath.random(.1, .3),
                            CruxMath.random(-.1, 1)
                        )
                        .speed(.08)
                        .data(item)
                        .spawn();
                }
            }

            if(tick % 2 == 0){
                CreateSound.sound(Sound.ENTITY_GENERIC_EAT, .5f, 1f).playAt(mob.getEyeLocation());
            }
            if(getPlayingAnimationProgress("eat") > .95f){
                setHeldItem(null);

                if(!CruxItem.isEmpty(item)){
                    Consumable consumable = item.getData(DataComponentTypes.CONSUMABLE);
                    if(consumable != null){
                        consumable.consumeEffects().forEach(effect ->{
                            if(effect instanceof ConsumeEffect.ApplyStatusEffects e && CruxMath.testChance(e.probability()*100f)){
                                mob.addPotionEffects(e.effects());
                            }
                        });
                    }
                }
            }
        }else{
            if(wasEating){
                wasEating = false;
                setHeldItem(null);

                getModel().getAnimationHandler().setDefaultProperty(
                    new AnimationHandler.DefaultProperty(
                        ModelState.IDLE, .1D, .1D, 1D
                    )
                );
                getModel().getAnimationHandler().setDefaultProperty(
                    new AnimationHandler.DefaultProperty(
                        ModelState.WALK, .1D, .1D, 1D
                    )
                );
                playAnimation("idle", 0D, 0D, 1D, true);
                CreateSound.sound(Sound.ENTITY_GENERIC_EAT, .5f, .75f).playAt(mob.getEyeLocation());
            }
        }
    }
}
