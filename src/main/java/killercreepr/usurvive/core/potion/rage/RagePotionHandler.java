package killercreepr.usurvive.core.potion.rage;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import killercreepr.usurvive.core.potion.RagePotion;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Vindicator;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public abstract class RagePotionHandler {
    protected final LivingEntity entity;
    protected final RagePotion effect;

    public RagePotionHandler(@NotNull LivingEntity entity, @NotNull RagePotion effect) {
        this.entity = entity;
        this.effect = effect;
    }

    public @NotNull LivingEntity entity(){
        return entity;
    }

    public void tick(){

    }


    public void onEntityDismount(EntityDismountEvent event){

    }

    public void onEntityDeath(EntityDeathEvent event){
    }
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event){
    }
    public void onPlayerStopSpectatingEntity(PlayerStopSpectatingEntityEvent event){

    }
    public void onPlayerStartSpectatingEntity(PlayerStartSpectatingEntityEvent event){

    }
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event){

    }

    public void onEntityTarget(EntityTargetEvent event){

    }

    public void stopped(){

    }
    public void started(){}
    protected @NotNull Mob spawnJohnny(){
        return entity.getWorld().spawn(entity.getLocation(), Vindicator.class, x ->{
            x.setJohnny(true);
            EntityEquipment equipment = entity.getEquipment();
            if(equipment != null){
                for(EquipmentSlot slot : EquipmentSlot.values()){
                    if(!entity.canUseEquipmentSlot(slot)) continue;
                    if(!x.canUseEquipmentSlot(slot)) continue;
                    ItemStack i = equipment.getItem(slot);
                    x.getEquipment().setItem(slot, i);
                }
            }
            for(Attribute a : Registry.ATTRIBUTE){
                if(a == Attribute.MOVEMENT_SPEED) continue;
                AttributeInstance in = x.getAttribute(a);
                if(in == null) continue;
                AttributeInstance pIn = entity.getAttribute(a);
                if(pIn == null) continue;
                in.setBaseValue(pIn.getBaseValue());
            }
            for(PotionEffect pot : entity.getActivePotionEffects()){
                x.addPotionEffect(pot);
            }
            x.setHealth(entity.getHealth());
        });
    }
}
