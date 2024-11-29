package killercreepr.usurvive.listener;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.usurvive.api.event.EntityTrampleEnchantEvent;
import killercreepr.usurvive.data.entity.TrampleHolder;
import killercreepr.usurvive.enchant.USurviveEnchant;
import killercreepr.usurvive.enchant.logic.JoyWhirl;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof LivingEntity damager && event.getEntity() instanceof LivingEntity victim){
            EntityEquipment dmgerEquip = damager.getEquipment();
            if(dmgerEquip != null && !CruxItem.isEmpty(dmgerEquip.getItemInMainHand())){
                ItemStack mainHand = dmgerEquip.getItemInMainHand();
                Map<Enchantment, Integer> enchants = mainHand.getEnchantments();
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
        }
    }
}
