package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.persistence.PersistTag;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.api.event.EntityTrampleEnchantEvent;
import killercreepr.usurvive.core.data.entity.TrampleHolder;
import killercreepr.usurvive.core.enchant.USurviveEnchant;
import killercreepr.usurvive.core.enchant.logic.JoyWhirl;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        ItemStack item = event.getBow();
        if(CruxItem.isEmpty(item)) return;
        int level = item.getEnchantmentLevel(USurviveEnchant.BOOMSHOT);
        if(level > 0){
            float chance = 10f + ((level-1) * 4);
            if(CruxMath.testChance(chance)){
                CruxTag.set(event.getProjectile(), "enchant/boomshot", PersistentDataType.INTEGER, level);
            }
        }
    }


    public void secondWindTick(Player p){
        if(!p.isSprinting()) return;

        ItemStack item = p.getInventory().getItem(EquipmentSlot.FEET);
        if(!CruxItem.isEmpty(item)){
            int level = item.getEnchantmentLevel(USurviveEnchant.SECOND_WIND);
            if(level > 0){
                if(!p.hasPotionEffect(PotionEffectType.SPEED) && isLowSecondWind(p, level)){
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
            chance += 12 * level;
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
        double x = 0.75 - ((withstandLevel-1) * 0.03);
        double check = instance.getValue() * x;
        return damage >= check;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
                if(enchants.containsKey(USurviveEnchant.JOY_WHIRL)){
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            new JoyWhirl(plugin, victim).start(enchants.getOrDefault(USurviveEnchant.JOY_WHIRL, 0));
                        }
                    }.runTaskLater(plugin, 1L);
                }
                /*if(mainHand.getEnchantments().containsKey(CustomEnchants.FROSTBITE)){
                    int level = mainHand.getEnchantments().getOrDefault(CustomEnchants.FROSTBITE, 0);
                    victim.setFreezeTicks(140 + ((6 * level) * 20));
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (40 + ((level - 1) * 80)), level));
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
                    }.runTaskTimer(Core.instance, 0L, 1L);
                }*/
            }
            if(enchants.containsKey(USurviveEnchant.BACKSTAB) && isBehind(vic, attacker)){
                int level = enchants.getOrDefault(USurviveEnchant.BACKSTAB, 0);
                double multiplier = 1D + (level * 0.08);
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
