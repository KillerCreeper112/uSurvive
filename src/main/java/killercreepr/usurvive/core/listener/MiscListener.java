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
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxitems.api.item.CruxedItem;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.api.potion.StoredPotion;
import killercreepr.cruxpotions.core.component.PotionComponents;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import killercreepr.usurvive.api.component.WaterProjectile;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.damage.type.USurviveDamageTypes;
import killercreepr.usurvive.core.entity.memory.StickyChargeMemory;
import killercreepr.usurvive.core.entity.memory.WaterProjectileHolder;
import killercreepr.usurvive.core.item.QuickAccessItem;
import killercreepr.usurvive.core.mechanics.FungireRepairComponent;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MiscListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntityNormal(EntityDamageByEntityEvent event) {
        var victim = event.getEntity();
        var attacker = CruxEntity.entity(event.getDamager());

        Map<Key, Double> extraDamage = attacker.get(USurviveComponents.EXTRA_MOB_TYPE_DAMAGE);
        if(extraDamage != null){
            Double highest = null;
            for(MobCategory category : CruxMob.getCategories(victim)){
                if(!extraDamage.containsKey(category.key())) continue;
                double check = extraDamage.get(category.key());
                if(highest == null || check > highest){
                    highest = check;
                }
            }
            if(highest != null){
                event.setDamage(event.getDamage()*highest);
            }
        }

        if(attacker instanceof LivingEntity attackLiving && victim instanceof LivingEntity victimLiving){
            EntityEquipment equip = attackLiving.getEquipment();
            if(equip != null){
                ItemStack item = equip.getItemInMainHand();
                if(!CruxItem.isEmpty(item)){
                    CruxItem cruxItem = CruxItem.wrap(item);
                    var hitEffects = cruxItem.getOrDefaultData(USurviveComponents.ATTACK_POTION_EFFECTS);
                    if(hitEffects != null){
                        hitEffects.getEffects().forEach(effect ->{
                            if(!CruxMath.testChance(effect.getChance())) return;
                            victimLiving.addPotionEffects(effect.getEffects());
                        });
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        int damage = event.getDamage();
        if(damage < 1) return;

        Player p = event.getPlayer();
        if(p.getFoodLevel() < 1) return;

        CruxItem item = CruxItem.wrap(event.getItem());

        FungireRepairComponent data = item.getOrDefaultData(USurviveComponents.FUNGIRE_REPAIR);
        if(data != null){
            onFungireRepair(p, item, data);
        }
    }

    public void onFungireRepair(HumanEntity p, CruxItem item, FungireRepairComponent data){
        if(!CruxMath.testChance(data.getRepairChance())) return;
        int amount = data.getRepairAmount().value().intValue();
        if(amount != 0){
            item.editMeta(Damageable.class, meta ->{
                if(meta.getDamage() < 1) return;
                meta.setDamage(Math.max(0, meta.getDamage() - amount));
            });
        }

        if(CruxMath.testChance(data.getExhaustionChance())){
            float exhaust = data.getExhaustionAmount().value().floatValue();
            p.setExhaustion(p.getExhaustion() + exhaust);
        }

        CruxWorldType type = CruxCore.core().worldManager().getWorldType(p.getWorld());
        if(type == null) return;
        var worldData = data.getWorldRepair(type.key());
        if(worldData == null) return;
        if(!CruxMath.testChance(worldData.repairChance())) return;
        int worldAmount = worldData.repairAmount().value().intValue();
        if(worldAmount != 0){
            item.editMeta(Damageable.class, meta ->{
                if(meta.getDamage() < 1) return;
                meta.setDamage(Math.max(0, meta.getDamage() - worldAmount));
            });
        }
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity attacker = event.getDamager();

        boolean blocked = CruxEntityUtil.isBlocking(victim) && CruxEntityUtil.isDamageSourceBlocked(victim, attacker);

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
        var builder = DamageSource.builder(USurviveDamageTypes.ENTITY_ATTACK_BYPASS_SHIELD)
            .withDirectEntity(attacker);
        Entity owner = EntityDamager.getOwner(attacker);
        if(owner != null && !owner.equals(attacker)){
            builder.withCausingEntity(owner);
        }
        var event = EntityDamager.entityDamager(victim, attacker)
            .setSource(builder.build())
            .attackWithMultiplier(multiplier);
        if(event == null || event.isCancelled()){
            CruxTag.remove(attacker, "ignore_shield_pierce_attack_logic");
        }
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
        CruxEntity projCrux = CruxEntity.entity(proj);
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

            var transferExtraTypeDmg = bow.getOrDefaultData(USurviveComponents.PROJECTILE_EXTRA_MOB_TYPE_DAMAGE);
            if(transferExtraTypeDmg != null){
                Map<Key, Double> current = new HashMap<>(projCrux.getOrDefault(USurviveComponents.EXTRA_MOB_TYPE_DAMAGE, Map.of()));
                current.putAll(transferExtraTypeDmg);
                projCrux.set(USurviveComponents.EXTRA_MOB_TYPE_DAMAGE, current);
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
