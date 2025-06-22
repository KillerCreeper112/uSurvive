package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxitems.api.item.CruxedItem;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.api.potion.StoredPotion;
import killercreepr.cruxpotions.core.component.PotionComponents;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.usurvive.api.component.WaterProjectile;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.entity.memory.StickyChargeMemory;
import killercreepr.usurvive.core.entity.memory.WaterProjectileHolder;
import killercreepr.usurvive.core.item.QuickAccessItem;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Random;

public class MiscListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity attacker = event.getDamager();

        boolean blocked = CruxEntityUtil.isDamageSourceBlocked(victim, attacker);

        if(CruxTag.has(attacker, "ignore_shield_pierce_attack_logic")){
            CruxTag.remove(attacker, "ignore_shield_pierce_attack_logic");
            return;
        }

        if(blocked){
            double shieldPierce = CruxAttribute.get(attacker, CruxAttribute.SHIELD_PIERCING);
            if(shieldPierce > 0D){
                shieldPierce(victim, attacker, shieldPierce);
                return;
            }
        }

        if(!(event.getEntity() instanceof LivingEntity hit)) return;
        CruxEntity crux = CruxEntity.entity(event.getDamager());
        List<PotionEffect> potions = crux.get(USurviveComponents.PROJECTILE_HIT_POTIONS);
        if(potions != null){
            hit.addPotionEffects(potions);
        }

        List<StoredPotion> cruxPotions = crux.get(PotionComponents.STORED_CRUX_POTIONS);
        if(cruxPotions != null){
            PotionHolder holder = EntityMemory.getOrCreateDataHolder(hit, SimplePotionHolder.class, SimplePotionHolder::new);
            if(holder != null){
                var inflictor = new EntityInflictor(crux.entity());
                cruxPotions.forEach(pot ->{
                    holder.addPotion(pot.create(hit, inflictor));
                });
            }
        }
    }

    public void shieldPierce(Entity victim, Entity attacker, double shieldPierce){
        double multiplier = shieldPierce/100D;
        CruxTag.set(attacker, "ignore_shield_pierce_attack_logic", PersistentDataType.BOOLEAN, true);
        EntityDamager.entityDamager(victim, attacker)
            .attackWithMultiplier(multiplier);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getHotbarButton() >= 0){
            ItemStack got = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            if(!CruxItem.isEmpty(got)){
                var crux = CruxedItem.cruxed(got);
                if(crux.getPluginItem() instanceof QuickAccessItem){
                    if(event.getSlot() == 40){
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if(CruxItem.isEmpty(event.getCursor())) return;
        var crux = CruxedItem.cruxed(event.getCursor());
        if(crux.getPluginItem() instanceof QuickAccessItem){
            if(event.getSlot() == 40){
                event.setCancelled(true);
                return;
            }
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onEntityPlace(EntityPlaceEvent event) {
        if(event.getEntity() instanceof ArmorStand s){
            s.setArms(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityShootBow(EntityShootBowEvent event) {
        Entity proj = event.getProjectile();
        if(!CruxItem.isEmpty(event.getBow())){
            CruxItem bow = CruxItem.wrap(event.getBow());
            var transferAttributes = bow.getOrDefaultData(CruxAttributeComponents.PROJECTILE_CRUX_ATTRIBUTES);
            if(transferAttributes != null){
                transferAttributes.getAttributeInstances().forEach(instance ->{
                    instance.getModifiers().forEach(mod ->{
                        CruxAttribute.addModifier(proj, instance.getAttribute(), mod);
                    });
                });
            }
        }

        ItemStack arrow = event.getConsumable();
        if(CruxItem.isEmpty(arrow)) return;
        CruxItem crux = CruxItem.wrap(arrow);
        WaterProjectile water = crux.getOrDefaultData(USurviveComponents.WATER_PROJECTILE);
        if(water==null) return;
        EntityMemory.getOrCreateDataHolder(proj, WaterProjectileHolder.class, mem ->{
            proj.setPersistent(false);
            return new WaterProjectileHolder(mem, water);
        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockDispenseLoot(BlockDispenseLootEvent event) {
        var loot = event.getDispensedLoot();
        Random random;
        World world = event.getBlock().getWorld();
        var crux = CruxCore.core().worldManager().getWorld(world.key());
        if(crux == null) random = new Random(world.getSeed());
        else random = crux.getRandom();
        USurvivePlugin.inst().values().BLOCK_DISPENSE_LOOT_DATA().ifPresent(list ->{
            list.forEach(data ->{
                if(!CruxMath.testChance(random, data.chance())) return;
                ItemStack item = data.item().value().clone();
                int amount = data.amount().sample(random).intValue();
                int maxStack = CruxItem.getMaxStackSize(item);
                while(amount > 0){
                    int x = Math.min(maxStack, amount);
                    amount -= x;
                    ItemStack clone = item.clone();
                    clone.setAmount(x);
                    loot.add(clone);
                }
            });
        });
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        var lootTable = event.getLootTable();
        if(!lootTable.key().value().startsWith("chests/")) return;
        var loot = event.getLoot();
        Random random;
        World world = event.getWorld();
        var crux = CruxCore.core().worldManager().getWorld(world.key());
        if(crux == null) random = new Random(world.getSeed());
        else random = crux.getRandom();
        USurvivePlugin.inst().values().CHESTS_LOOT_DATA().ifPresent(list ->{
            list.forEach(data ->{
                if(!CruxMath.testChance(random, data.chance())) return;
                ItemStack item = data.item().value().clone();
                int amount = data.amount().sample(random).intValue();
                int maxStack = CruxItem.getMaxStackSize(item);
                while(amount > 0){
                    int x = Math.min(maxStack, amount);
                    amount -= x;
                    ItemStack clone = item.clone();
                    clone.setAmount(x);
                    loot.add(clone);
                }
            });
        });
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(EntityMemory.get(event.getEntity()) instanceof StickyChargeMemory m){
            m.onDamage(event);
        }
    }

}
