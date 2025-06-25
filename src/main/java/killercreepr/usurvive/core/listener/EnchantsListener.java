package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.CruxComponents;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.api.event.EntityTrampleEnchantEvent;
import killercreepr.usurvive.core.data.entity.TrampleHolder;
import killercreepr.usurvive.core.enchant.USurviveEnchant;
import killercreepr.usurvive.core.enchant.logic.JoyWhirl;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class EnchantsListener implements Listener {
    protected final Plugin plugin;

    public EnchantsListener(Plugin plugin) {
        this.plugin = plugin;
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
        float chance = 0f;
        for(EquipmentSlot slot : AttributeBukkitAdaptor.ARMOR_SLOTS){
            if(!victim.canUseEquipmentSlot(slot)) continue;
            ItemStack item = victimEquip.getItem(slot);
            if(CruxItem.isEmpty(item)) continue;
            int level = item.getEnchantmentLevel(USurviveEnchant.BURNING_THORNS);
            if(level == 0) continue;
            fireTicks += 80 * level;
            chance += 3 * level;
        }
        if(fireTicks < 1) return;
        if(!CruxMath.testChance(chance)) return;
        Entity dmger = event.getDamager();
        if(dmger.getFireTicks() >= fireTicks) return;

        dmger.setFireTicks(fireTicks);
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
                //todo make into own potion
                if(mainHand.getEnchantments().containsKey(USurviveEnchant.FROSTBITE)){
                    int level = mainHand.getEnchantments().getOrDefault(USurviveEnchant.FROSTBITE, 0);
                    victim.setFreezeTicks(140 + ((6 * level) * 20));
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, (40 + ((level - 1) * 80)), level));
                    new BukkitRunnable(){
                        int tick = 0;
                        @Override
                        public void run() {
                            if(!victim.isValid() || victim.getFreezeTicks() <= 140){
                                cancel();
                                return;
                            }
                            tick++;
                            if(tick >= 30 - (level * 10)){
                                tick = 0;
                                victim.damage(1D);
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 1L);
                }
            }
            if(enchants.containsKey(USurviveEnchant.BACKSTAB) && isBehind(vic, attacker)){
                int level = enchants.getOrDefault(USurviveEnchant.BACKSTAB, 0);
                double multiplier = 1D + (level * 0.2);
                event.setDamage(event.getDamage() * multiplier);
            }
        }
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
