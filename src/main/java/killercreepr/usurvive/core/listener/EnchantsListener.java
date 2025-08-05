package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.CruxComponents;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickableModifier;
import killercreepr.cruxtickables.api.event.EntityTickablesCalculateEvent;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.api.event.EntityTrampleEnchantEvent;
import killercreepr.usurvive.core.data.entity.TrampleHolder;
import killercreepr.usurvive.core.enchant.USurviveEnchant;
import killercreepr.usurvive.core.enchant.logic.JoyWhirl;
import killercreepr.usurvive.core.entity.memory.CleansingEnchantHolder;
import killercreepr.usurvive.core.entity.tickable.USurviveTickables;
import killercreepr.usurvive.core.potion.FrostbitePotion;
import killercreepr.usurvive.core.potion.USurvivePotions;
import killercreepr.usurvive.core.potion.inflictor.UDamageEntityInflictor;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnchantsListener implements Listener {
    protected final Plugin plugin;

    public EnchantsListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTickablesCalculate(EntityTickablesCalculateEvent event) {
        if(!(event.getEntity() instanceof LivingEntity e)) return;
        EntityEquipment equipment = e.getEquipment();
        if(equipment == null) return;
        if(entityTickablesFocus(equipment.getItemInMainHand(), CruxSlot.MAIN_HAND, event)) return;
        entityTickablesFocus(equipment.getItemInOffHand(), CruxSlot.OFF_HAND, event);
    }

    public boolean entityTickablesFocus(ItemStack item, CruxSlot slot, EntityTickablesCalculateEvent event){
        if(CruxItem.isEmpty(item)) return false;
        int level = item.getEnchantmentLevel(USurviveEnchant.FOCUS);
        if(level > 0){
            event.addTickable(USurviveTickables.ENCHANT_FOCUS.buildActive(
                event.getEntity(), slot,
                EntityTickableModifier.modifier(Key.key("enchant/focus"), USurviveTickables.ENCHANT_FOCUS, CruxSlotGroup.HAND)
            ));
            return true;
        }
        return false;
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        int level = item.getEnchantmentLevel(USurviveEnchant.BREAKING_CURSE);
        if(level > 0){
            event.setDamage(event.getDamage() * (level * 7));
        }
    }

    public int getArmorEnchantLevels(Entity e, Enchantment enchantment){
        if(!(e instanceof HumanEntity p)) return 0;
        int level = 0;
        for(EquipmentSlot slot : AttributeBukkitAdaptor.ARMOR_SLOTS){
            ItemStack item = p.getInventory().getItem(slot);
            if(CruxItem.isEmpty(item)) continue;
            level += item.getEnchantmentLevel(enchantment);
        }
        return level;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(CruxItem.isEmpty(event.getItem())) return;

        HumanEntity p = event.getEntity();
        int level = getArmorEnchantLevels(p, USurviveEnchant.SPOILAGE_CURSE);
        if(level > 0){
            float multiplier = level * .20f;
            event.setFoodLevel((int) ((float) event.getFoodLevel() * multiplier));
        }
    }

    //failsafe
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPotionEffectMonitor(EntityPotionEffectEvent event) {
        Entity e = event.getEntity();
        var mem = EntityMemory.get(e);
        if(mem != null){
            var holder = mem.getDataHolder(CleansingEnchantHolder.class);
            if(holder != null && !holder.isRemove()){
                holder.setRemove(true);
                return;
            }
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        Entity e = event.getEntity();
        PotionEffect newEffect = event.getNewEffect();
        if(newEffect == null) return;
        PotionEffect oldEffect = event.getOldEffect();

        var mem = EntityMemory.get(e);
        if(mem != null){
            var holder = mem.getDataHolder(CleansingEnchantHolder.class);
            if(holder != null && !holder.isRemove()){
                holder.setRemove(true);
                return;
            }
        }

        onAddPotion(e, oldEffect, newEffect, event);
    }

    public void onAddPotion(Entity e, PotionEffect oldEffect, PotionEffect newEffect, EntityPotionEffectEvent event){
        if(newEffect.isInfinite()) return;
        if(!(e instanceof LivingEntity living)) return;
        int durationDifference;
        if(oldEffect != null && oldEffect.getType() == newEffect.getType() && !oldEffect.isInfinite()){
            durationDifference = newEffect.getDuration() - oldEffect.getDuration();
        }else durationDifference = newEffect.getDuration();

        if(durationDifference < 1) return;

        int level = getArmorEnchantLevels(e, USurviveEnchant.CLEANSING);
        if(level < 1) return;

        event.setCancelled(true);

        int removeDuration = 10 + (level * 15);
        int newDuration = Math.max(newEffect.getDuration() - removeDuration, (int) (newEffect.getDuration() * .5f));
        if(newDuration < 1) return;
        Crux.scheduler().runTask(() ->{
            PotionEffect edited = newEffect.withDuration(newDuration);
            EntityMemory.getOrCreateDataHolder(e, CleansingEnchantHolder.class, CleansingEnchantHolder::new);
            living.addPotionEffect(edited);
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if(event.hasExplicitlyChangedBlock()){
            secondWindTick(p);
        }

        if(p.getFallDistance() < 1f) return;
        ItemStack boots = p.getInventory().getBoots();
        if(CruxItem.isEmpty(boots)) return;
        Map<Enchantment, Integer> enchants = boots.getEnchantments();
        if(enchants.containsKey(USurviveEnchant.TRAMPLE)){
            RayTraceResult ray = p.getWorld().rayTrace(event.getTo(), new Vector(0, -1, 0), .5D,
                FluidCollisionMode.SOURCE_ONLY, true, .2D,
                e -> !e.equals(p));
            if(ray == null || ray.getHitEntity() == null || !(ray.getHitEntity() instanceof LivingEntity e)) return;
            int level = enchants.get(USurviveEnchant.TRAMPLE);

            TrampleHolder holder = EntityMemory.getOrCreateDataHolder(p, TrampleHolder.class, mem ->{
                if(!(mem instanceof PlayerMemory m)) return null;
                TrampleHolder h = new TrampleHolder(m);
                h.setEnchantLevel(level);
                return h;
            });
            if(holder == null) return;

            if(!holder.canTrample()){
                return;
            }

            double damage = p.getFallDistance() * level;
            EntityTrampleEnchantEvent trampleEvent = new EntityTrampleEnchantEvent(p, ray.getHitEntity(), damage);
            if(!trampleEvent.callEvent()) return;

            //take durablity damage based on fall distance
            holder.setTrampleAmount(holder.getTrampleAmount()+1);

            damage = trampleEvent.getDamage();
            e.damage(damage, p);
            p.setFallDistance(0f);
            p.setVelocity(new Vector(0, .6, 0));

            p.damageItemStack(boots, 1);

            p.getWorld().playSound(event.getFrom(), Sound.ENTITY_PLAYER_BIG_FALL, 1f, .8f);
            Location loc = ray.getHitPosition().toLocation(p.getWorld());
            for(int i = 0; i < 13; i++){
                new ParticleBuilder(Particle.CLOUD)
                    .location(loc.clone().add(
                        CruxMath.random(-.3, .3),
                        CruxMath.random(-.3, .3),
                        CruxMath.random(-.3, .3)
                    ))
                    .offset(
                        CruxMath.random(-.5, .5),
                        CruxMath.random(-.5, .5),
                        CruxMath.random(-.5, .5)
                    )
                    .extra(1)
                    .count(0)
                    .spawn();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectileHitLowest(ProjectileHitEvent event) {
        Entity proj = event.getEntity();
        Map<Enchantment, Integer> enchants = CruxEntity.entity(proj)
            .get(CruxComponents.INHERITED_ENCHANTMENTS);
        if(enchants == null || enchants.isEmpty()) return;

        if(enchants.containsKey(USurviveEnchant.TRUESHOT)){
            Entity hit = event.getHitEntity();
            if(hit != null){
                UUID owner = EntityDamager.getOwnerUUID(proj);
                if(isFriendly(hit, owner)){
                    event.setCancelled(true);
                }
            }
        }
    }

    public boolean isFriendly(Entity hit, UUID owner){
        if(hit instanceof Tameable t && owner.equals(t.getOwnerUniqueId())) return true;
        if(hit instanceof Player){
            UPlayer player = UPlayer.getPlayer(owner);
            if(player != null){
                if(player.hasFriend(hit.getUniqueId()) || player.isApartOfParty(hit.getUniqueId())) return true;
            }
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityEquipmentChanged(EntityEquipmentChangedEvent event) {
        LivingEntity e = event.getEntity();
        var kbResistance = e.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
        if(kbResistance == null) return;
        event.getEquipmentChanges().forEach((slot, change) ->{
            kbResistance.removeModifier(Crux.key("enchant/braced/" + slot.toString().toLowerCase()));

            ItemStack newItem = change.newItem();
            if(CruxItem.isEmpty(newItem)) return;

            int level = newItem.getEnchantmentLevel(USurviveEnchant.BRACED);
            if(level > 0){
                kbResistance.addTransientModifier(new AttributeModifier(
                    Crux.key("enchant/braced/" + slot.toString().toLowerCase()),
                    .05 * level, AttributeModifier.Operation.ADD_NUMBER
                ));
            }
        });
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity e = event.getEntity();
        int level = CruxTag.get(e, "enchant/boomshot", PersistentDataType.INTEGER, 0);
        if(level > 0){
            explodeBoomshot(e, level);
        }
    }

    public void explodeBoomshot(Entity e, int level){
        double range = 1.6D + (level-1);
        double dmg = 1D + ((level-1) * 1.5D);
        double kb = 2D + ((level-1) * 1.5D);

        new GetEntityNear<>(LivingEntity.class)
            .center(e)
            .range(range)
            .find().forEach(hit ->{
                EntityDamager.entityDamager(hit, e).attack(dmg, kb);
            });

        new ParticleBuilder(Particle.EXPLOSION)
            .location(e.getLocation())
            .offset(.6, .6, .6)
            .count(CruxMath.random(2, 4))
            .extra(.1)
            .spawn()
        ;
        CreateSound.sound(Sound.ENTITY_GENERIC_EXPLODE, 1.6f).playAt(e);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        int droppedExp = event.getExpToDrop();
        if(droppedExp < 1) return;

        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if(CruxItem.isEmpty(item)) return;

        int level = item.getEnchantmentLevel(USurviveEnchant.INSIGHT);
        if(level < 1) return;
        float chance = level * 11.5f;
        if(!CruxMath.testChance(chance)) return;

        float multiplier = 1f + (level * .15f);
        droppedExp = (int) ((float) droppedExp * multiplier);
        event.setExpToDrop(droppedExp);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeathSporeburst(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Player killer = victim.getKiller();
        if(killer == null) return;
        ItemStack item = killer.getInventory().getItemInMainHand();
        if(CruxItem.isEmpty(item)) return;

        int level = item.getEnchantmentLevel(USurviveEnchant.SPOREBURST);
        if(level < 1) return;

        float radius = 1.2f + ((level-1) * .8f);
        victim.getWorld().spawn(victim.getLocation(), AreaEffectCloud.class, e ->{
            e.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 80 + (level*20),
                level > 3 ? 1 : 0), true);
            e.setRadius(radius);
        });
    }


    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        int droppedExp = event.getDroppedExp();
        if(droppedExp < 1) return;

        if(victim instanceof HumanEntity) return;
        Player killer = victim.getKiller();
        if(killer == null) return;
        ItemStack item = killer.getInventory().getItemInMainHand();
        if(CruxItem.isEmpty(item)) return;

        int level = item.getEnchantmentLevel(USurviveEnchant.INSIGHT);
        if(level < 1) return;
        float chance = level * 11.5f;
        if(!CruxMath.testChance(chance)) return;

        float multiplier = 1f + (level * .15f);
        droppedExp = (int) ((float) droppedExp * multiplier);
        event.setDroppedExp(droppedExp);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerLaunchProjectile(PlayerLaunchProjectileEvent event) {
        Entity e = event.getProjectile();
        ItemStack item = event.getItemStack();
        if(CruxItem.isEmpty(item)) return;
        onProjectile(event.getPlayer(), e, item);
    }

    public void onProjectile(Entity owner, Entity e, ItemStack item){
        CruxEntity.entity(e).set(CruxComponents.INHERITED_ENCHANTMENTS, item.getEnchantments());
        int level = item.getEnchantmentLevel(USurviveEnchant.BOOMSHOT);
        if(level > 0){
            float chance = 10f + ((level-1) * 4);
            if(CruxMath.testChance(chance)){
                CruxTag.set(e, "enchant/boomshot", PersistentDataType.INTEGER, level);
            }
        }
        level = item.getEnchantmentLevel(USurviveEnchant.VELOCITY);
        if(level > 0){
            double multiplier = 1D + (level * 0.09D);
            Vector vec = e.getVelocity();
            e.setVelocity(vec.multiply(multiplier));
        }
        level = item.getEnchantmentLevel(USurviveEnchant.PENETRATE);
        if(level > 0){
            CruxAttribute.addModifier(e, CruxAttribute.ARMOR_PENETRATION,
                CruxAttributeModifier.modifier(
                    Crux.key("enchant/penetrate"),
                    level * 7D
                ));
        }
        level = item.getEnchantmentLevel(USurviveEnchant.FOCUS);
        if(level > 0 && owner instanceof LivingEntity p && item.getType() == Material.BOW){
            int usedTime = p.getActiveItemUsedTime();
            int usedMore = usedTime - 20;
            if(usedMore > 0){
                float multiplier = usedTime / (50f + ((level-1) * 15f));
                if(e instanceof AbstractArrow arrow){
                    arrow.setDamage(arrow.getDamage() * multiplier);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        Entity e = event.getProjectile();
        ItemStack item = event.getBow();
        if(CruxItem.isEmpty(item)) return;
        onProjectile(event.getEntity(), e, item);
    }


    public void secondWindTick(Player p){
        if(!p.isSprinting()) return;

        ItemStack item = p.getInventory().getItem(EquipmentSlot.FEET);
        if(!CruxItem.isEmpty(item)){
            int level = item.getEnchantmentLevel(USurviveEnchant.SECOND_WIND);
            if(level > 0){
                if(isLowSecondWind(p, level)){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, level > 2 ? 1 : 0, false, false, true));
                }
            }
        }
    }

    public boolean isLowSecondWind(Player p, int level){
        double hp = p.getHealth();
        double max = p.getAttribute(Attribute.MAX_HEALTH).getValue();
        double multiplier = 0.2 + ((level-1) * 0.05);
        double check = max * multiplier;
        return hp < check;
    }

    public void burningThorns(EntityDamageByEntityEvent event,
                              LivingEntity victim, EntityEquipment victimEquip){
        int fireTicks = 0;
        double maxDmg = 0D;
        Map<EquipmentSlot, Integer> levels = new HashMap<>();
        for(EquipmentSlot slot : AttributeBukkitAdaptor.ARMOR_SLOTS){
            if(!victim.canUseEquipmentSlot(slot)) continue;
            ItemStack item = victimEquip.getItem(slot);
            if(CruxItem.isEmpty(item)) continue;
            int level = item.getEnchantmentLevel(USurviveEnchant.BURNING_THORNS);
            if(level == 0) continue;
            levels.put(slot,level);
            fireTicks += 80 * level;
            maxDmg += level * 0.5D;
        }
        if(fireTicks < 1 || levels.isEmpty()) return;
        float chance = CruxCollection.getRandom(levels.values()) * 15f;
        if(!CruxMath.testChance(chance)) return;
        Entity dmger = event.getDamager();
        if(dmger.getFireTicks() >= fireTicks) return;

        dmger.setFireTicks(fireTicks);
        double dmg = CruxMath.random(1D, maxDmg + 1D);
        EntityDamager.entityDamager(dmger, victim)
            .setSource(DamageSource.builder(DamageType.THORNS)
                .withDirectEntity(victim).build())
            .setCause(CruxEntityDamageEvent.DamageCause.fromBukkit(EntityDamageEvent.DamageCause.THORNS))
            .attack(dmg, 8D);

        EquipmentSlot slot = CruxCollection.getRandom(levels.keySet());
        ItemStack item = victimEquip.getItem(slot);
        Crux.handlers().item().damageItem(item, 1, victim);
        if(!(victimEquip instanceof PlayerInventory)){
            victimEquip.setItem(slot, item, true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof LivingEntity e)) return;
        EntityEquipment equip = e.getEquipment();
        if(equip == null) return;

        if(!e.hasPotionEffect(PotionEffectType.RESISTANCE)){
            int withstandLevel = 0;
            for(EquipmentSlot slot : AttributeBukkitAdaptor.ARMOR_SLOTS){
                ItemStack item = equip.getItem(slot);
                if(CruxItem.isEmpty(item)) continue;
                withstandLevel += item.getEnchantmentLevel(USurviveEnchant.WITHSTAND);
            }

            if(withstandLevel > 0 && tookLargeDamage(e, withstandLevel, event.getFinalDamage())){
                onWithstand(e, withstandLevel);
            }
        }
    }

    public void onWithstand(LivingEntity e, int withstandLevel){
        int amplifier = Math.min(withstandLevel / 3, 2);
        int duration = 60 + ((withstandLevel-1) * 30);
        e.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, amplifier));
    }

    public boolean tookLargeDamage(LivingEntity e, int withstandLevel, double damage){
        var instance = e.getAttribute(Attribute.MAX_HEALTH);
        if(instance == null) return false;
        double x = 0.75 - ((withstandLevel-1) * 0.07);
        double check = instance.getValue() * x;
        return damage >= check;
    }

    public boolean isValidJoywhirlAttacker(Entity e){
        if(e instanceof HumanEntity p){
            return p.getAttackCooldown() > 0.6f;
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity vic = event.getEntity();
        Entity attacker = event.getDamager();

        if(event.getEntity() instanceof LivingEntity victim){
            EntityEquipment victimEquip = victim.getEquipment();
            if(victimEquip != null){
                burningThorns(event, victim, victimEquip);
            }
        }
        LivingEntity damager;
        if(event.getDamager() instanceof LivingEntity dd && dd.getEquipment() != null) damager = dd;
        else damager = null;
        LivingEntity victim;
        if(event.getEntity() instanceof LivingEntity dd && dd.getEquipment() != null) victim = dd;
        else victim = null;

        if(damager != null){
            EntityEquipment dmgerEquip = damager.getEquipment();
            ItemStack mainHand = dmgerEquip.getItemInMainHand();
            Map<Enchantment, Integer> enchants = mainHand.getEnchantments();
            if(victim != null){
                if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && enchants.containsKey(USurviveEnchant.JOY_WHIRL)){
                    if(!JoyWhirl.ACTIVE.containsKey(victim.getUniqueId()) && isValidJoywhirlAttacker(damager)){
                        int level = enchants.getOrDefault(USurviveEnchant.JOY_WHIRL, 0);
                        float chance = 15f + ((level-1) * 10f);
                        if(CruxMath.testChance(chance)){
                            Crux.handlers().item().damageItem(mainHand, 1, damager);
                            Crux.scheduler().runTask(() ->{
                                new JoyWhirl(plugin, victim).start(level);
                            });
                        }
                    }
                }
                if(mainHand.getEnchantments().containsKey(USurviveEnchant.FROSTBITE)){
                    PotionHolder holder = EntityMemory.getOrCreateDataHolder(victim, SimplePotionHolder.class, SimplePotionHolder::new);
                    if(holder != null){
                        int level = mainHand.getEnchantments().getOrDefault(USurviveEnchant.FROSTBITE, 0);
                        int amplifier = level -1;
                        int duration = FrostbitePotion.calculateDuration(amplifier);
                        holder.addPotion(USurvivePotions.FROSTBITE.create(victim, duration, amplifier,
                            new EntityInflictor(damager)));
                    }
                }
            }
            if(enchants.containsKey(USurviveEnchant.BACKSTAB) && isBehind(vic, attacker)){
                int level = enchants.getOrDefault(USurviveEnchant.BACKSTAB, 0);
                double multiplier = 1D + (level * 0.2);
                event.setDamage(event.getDamage() * multiplier);
            }
            if(enchants.containsKey(USurviveEnchant.MIRRORSTRIKE)){
                int level = enchants.getOrDefault(USurviveEnchant.MIRRORSTRIKE, 0);
                mirrorstrike(vic, victim, attacker, level, event);
            }
        }
    }

    public void mirrorstrike(Entity vic, LivingEntity victim, Entity attacker, int level, EntityDamageByEntityEvent event){
        double finalDmg = event.getFinalDamage();
        if(victim != null){
            double hp = victim.getHealth() - finalDmg;
            if(hp <= 0D) return;
        }
        if(attacker instanceof HumanEntity h){
            if(h.getAttackCooldown() <= .7f) return;
        }
        applyMirrorstrike(vic, attacker, level, finalDmg * .7);
    }

    public void applyMirrorstrike(Entity victim, Entity attacker, int level, double damage){
        PotionHolder holder = PotionHolder.potionHolderOrCreate(victim);
        if(holder == null) return;
        int time = Math.max(10, 55 - ((level-1) * 10));
        if(holder.getPotion(USurvivePotions.MIRRORSTRIKE) instanceof UDamageEntityInflictor inflict){
            inflict.addDamage((float) damage);
        }

        holder.addPotion(USurvivePotions.MIRRORSTRIKE.create(victim, time, level-1, new UDamageEntityInflictor(attacker, (float) damage)));
    }

    public boolean isBehind(Entity victim, Entity attacker) {
        Vector victimFacing = victim.getLocation().getDirection().normalize(); // Direction victim is looking
        Vector toAttacker = attacker.getLocation().toVector().subtract(victim.getLocation().toVector()).normalize();

        double dot = victimFacing.dot(toAttacker); // Cosine of angle between vectors

        // dot == -1 means 180° (directly behind)
        // dot == 1 means 0° (directly in front)
        return dot < -0.7; // About 135°-180° cone behind
    }
}
