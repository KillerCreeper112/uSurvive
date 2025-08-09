package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.math.CruxLocation;
import killercreepr.crux.api.valueproviders.number.NumberHolder;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.CruxComponents;
import killercreepr.crux.core.persistence.CruxPersist;
import killercreepr.crux.core.persistence.CruxPersistence;
import killercreepr.crux.core.util.*;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.persistence.CruxEntitiesPersist;
import killercreepr.cruxform.api.shape.CreateCircle;
import killercreepr.cruxgeneration.util.CruxNoise;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.api.potion.inflictor.PotionInflictor;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickableModifier;
import killercreepr.cruxtickables.api.event.EntityTickablesCalculateEvent;
import killercreepr.cruxtickables.core.entity.memory.EntityTickablesHolder;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.api.event.EntityTrampleEnchantEvent;
import killercreepr.usurvive.api.event.ProjectileRicochetEvent;
import killercreepr.usurvive.core.damage.type.USurviveDamageTypes;
import killercreepr.usurvive.core.data.entity.TrampleHolder;
import killercreepr.usurvive.core.enchant.USurviveEnchant;
import killercreepr.usurvive.core.enchant.logic.JoyWhirl;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.memory.CleansingEnchantHolder;
import killercreepr.usurvive.core.entity.memory.EmotiveMobHolder;
import killercreepr.usurvive.core.entity.memory.FrenzyHolder;
import killercreepr.usurvive.core.entity.memory.TetherEnchantHolder;
import killercreepr.usurvive.core.entity.tickable.ActiveChainshockTickable;
import killercreepr.usurvive.core.entity.tickable.ActiveFocusEnchantTickable;
import killercreepr.usurvive.core.entity.tickable.USurviveTickables;
import killercreepr.usurvive.core.mechanics.Shockwave;
import killercreepr.usurvive.core.potion.FrostbitePotion;
import killercreepr.usurvive.core.potion.USurvivePotions;
import killercreepr.usurvive.core.potion.inflictor.UDamageEntityInflictor;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EnchantsListener implements Listener {
    protected final Plugin plugin;

    public EnchantsListener(Plugin plugin) {
        this.plugin = plugin;
    }

    protected final Enchantment[] armorTickableEnchants = new Enchantment[]{
        USurviveEnchant.HOLLOWING_CURSE,
        USurviveEnchant.BLOOMING_CURSE,
        USurviveEnchant.EMOTIVE
    };

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(drop ->{
           if(CruxItem.isEmpty(drop)) return false;
           int level = drop.getEnchantmentLevel(USurviveEnchant.SOULBOUND);
           if(level > 0){
               if(level == 1){
                   drop.removeEnchantment(USurviveEnchant.SOULBOUND);
               }else drop.addUnsafeEnchantment(USurviveEnchant.SOULBOUND, level-1);
               event.getItemsToKeep().add(drop);
               return true;
           }
           return false;
        });
    }


    @EventHandler(ignoreCancelled = true)
    public void onEntityTickablesCalculate(EntityTickablesCalculateEvent event) {
        if(!(event.getEntity() instanceof LivingEntity e)) return;
        EntityEquipment equipment = e.getEquipment();
        if(equipment == null) return;
        if(!entityTickablesFocus(equipment.getItemInMainHand(), CruxSlot.MAIN_HAND, event)){
            entityTickablesFocus(equipment.getItemInOffHand(), CruxSlot.OFF_HAND, event);
        }
        entityTickablesChainlink(equipment.getItemInMainHand(), CruxSlot.MAIN_HAND, event);

        Map<Enchantment, Integer> armorEnchants = new HashMap<>();
        Map<Enchantment, CruxSlot> mainSlots = new HashMap<>();

        for(CruxSlot slot : CruxSlotGroup.ARMOR){
            EquipmentSlot bukkitSlot = AttributeBukkitAdaptor.adapt(slot);
            if(bukkitSlot == null) continue;
            if(!e.canUseEquipmentSlot(bukkitSlot)) continue;
            ItemStack item = equipment.getItem(bukkitSlot);
            if(CruxItem.isEmpty(item)) continue;

            for(Enchantment ench : armorTickableEnchants){
                int currentLevel = item.getEnchantmentLevel(ench);
                if(currentLevel < 1) continue;
                armorEnchants.compute(ench, (ignored, x) -> x == null ? currentLevel : (x + currentLevel));

                if(mainSlots.get(ench) != CruxSlot.CHEST){
                    mainSlots.put(ench, slot);
                }
            }
        }

        int level = armorEnchants.getOrDefault(USurviveEnchant.HOLLOWING_CURSE, 0);
        if(level > 0){
            event.addTickable(USurviveTickables.ENCHANT_HOLLOWING_CURSE.buildActive(
                event.getEntity(), mainSlots.get(USurviveEnchant.HOLLOWING_CURSE),
                EntityTickableModifier.modifier(Key.key("enchant/hollowing_curse"), USurviveTickables.ENCHANT_HOLLOWING_CURSE, CruxSlotGroup.ARMOR,
                    null, Map.of("level", level))
            ));
        }

        level = armorEnchants.getOrDefault(USurviveEnchant.BLOOMING_CURSE, 0);
        if(level > 0){
            event.addTickable(USurviveTickables.ENCHANT_BLOOMING_CURSE.buildActive(
                event.getEntity(), mainSlots.get(USurviveEnchant.BLOOMING_CURSE),
                EntityTickableModifier.modifier(Key.key("enchant/blooming_curse"), USurviveTickables.ENCHANT_BLOOMING_CURSE, CruxSlotGroup.ARMOR,
                    null, Map.of("level", level))
            ));
        }

        level = armorEnchants.getOrDefault(USurviveEnchant.EMOTIVE, 0);
        if(level > 0){
            event.addTickable(USurviveTickables.EMOTIVE.buildActive(
                event.getEntity(), mainSlots.get(USurviveEnchant.EMOTIVE),
                EntityTickableModifier.modifier(Key.key("enchant/emotive"), USurviveTickables.EMOTIVE, CruxSlotGroup.ARMOR,
                    null, Map.of("level", level))
            ));
        }
    }

    public boolean entityTickablesChainlink(ItemStack item, CruxSlot slot, EntityTickablesCalculateEvent event){
        if(CruxItem.isEmpty(item)) return false;
        int level = item.getEnchantmentLevel(USurviveEnchant.CHAINSHOCK);
        if(level > 0){
            event.addTickable(USurviveTickables.CHAINSHOCK.buildActive(
                event.getEntity(), slot,
                EntityTickableModifier.modifier(Key.key("enchant/chainshock"),
                    USurviveTickables.CHAINSHOCK, CruxSlotGroup.MAIN_HAND,
                    null,
                    Map.of(
                        "max_links", 1 + (level * 3),
                        "link_range", 4D + (level * 4D),
                        "display_type", Key.key("chain"),
                        "level", level,
                        "link_delay", 5
                    ))
            ));
            return true;
        }
        return false;
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

    public @Nullable Multimap<ItemStack, Integer> getArmorEnchantLevelsMap(Entity e, Enchantment enchantment){
        if(!(e instanceof HumanEntity p)) return null;
        Multimap<ItemStack, Integer> map = HashMultimap.create();
        for(EquipmentSlot slot : AttributeBukkitAdaptor.ARMOR_SLOTS){
            ItemStack item = p.getInventory().getItem(slot);
            if(CruxItem.isEmpty(item)) continue;
            int x = item.getEnchantmentLevel(enchantment);
            if(x < 1) continue;
            map.put(item, x);
        }
        return map.isEmpty() ? null : map;
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

            float chance = level * 15f;
            if(CruxMath.testChance(chance)){
                p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,
                    400, level-1));
            }
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

    @EventHandler(ignoreCancelled = true)
    public void onProjectileRicochet(ProjectileRicochetEvent event) {
        Entity proj = event.getNewProjectile();
        Map<Enchantment, Integer> enchants = CruxEntity.entity(proj).get(CruxComponents.INHERITED_ENCHANTMENTS);
        if(enchants == null || enchants.isEmpty()) return;

        int level = enchants.getOrDefault(USurviveEnchant.RICOCHET, 0);
        if(level > 0){
            onEnchantRicochet(proj, event, level);
        }
    }

    public void onEnchantRicochet(Entity proj, ProjectileRicochetEvent event, int level){
        Entity nearbyEnemy = findNearbyEnemy(proj.getLocation(), proj, EntityDamager.getOwnerUUID(proj), EntityDamager.getOwner(proj),
            12D + (4D * (level-1)));
        if(nearbyEnemy == null) return;

        Vector original = event.getReflectDirection();
        double speed = original.length(); // preserve original speed

        Location from = proj.getLocation();
        Location to = nearbyEnemy.getLocation().add(0, nearbyEnemy.getHeight() * 0.5, 0);
        Vector dir = to.toVector().subtract(from.toVector()).normalize().multiply(speed);

        dir.setY(dir.getY() + 0.35);

        event.setReflectDirection(dir);
    }

    public Entity findNearbyEnemy(Location center, Entity proj, UUID owner, Entity ownerEntity, double range){
        return new GetEntityNear<>(Entity.class)
            .center(center)
            .range(range)
            .filter(check ->{
                if(CruxEntityUtil.isNonSurvival(check)) return false;
                if(CruxPersist.hasIgnoreEntity(proj, check.getUniqueId())) return false;
                return owner == null || !isFriendly(check, owner, ownerEntity);
            })
            .operation(GetNear.Operation.NEAREST)
            .findFirst();
    }

    public boolean isFriendly(Entity e, UUID owner, Entity ownerEntity){
        if(e.getUniqueId().equals(owner)) return true;
        if(e instanceof Tameable tame && owner.equals(tame.getOwnerUniqueId())){
            return true;
        }
        UUID checkOwner = EntityDamager.getOwnerUUID(e);
        if(owner.equals(checkOwner)) return true;

        UPlayer uPlayer = UPlayer.getPlayer(owner);
        if(uPlayer == null) return mobsIsFriendly(e, owner, ownerEntity);

        if(uPlayer.hasFriend(e.getUniqueId()) || uPlayer.hasFriend(e.getUniqueId())) return true;
        return false;
    }

    public boolean mobsIsFriendly(Entity e, UUID owner, Entity ownerEntity){
        if(!(ownerEntity instanceof Mob mob)) return false;
        if(mob instanceof Enemy && e instanceof Enemy) return true;
        if(mob instanceof Animals && e instanceof Animals) return true;
        return false;
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

        PotionEffectType type = newEffect.getType();
        if(type != PotionEffectType.POISON && type != PotionEffectType.SLOWNESS && type != PotionEffectType.WEAKNESS) return;

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
            if(living.addPotionEffect(edited)){
                EntityEquipment equipment = living.getEquipment();
                if(equipment == null) return;
                for(EquipmentSlot slot : AttributeBukkitAdaptor.ARMOR_SLOTS){
                    if(!living.canUseEquipmentSlot(slot)) continue;
                    ItemStack item = equipment.getItem(slot);
                    if(CruxItem.isEmpty(item)) continue;
                    if(item.getEnchantmentLevel(USurviveEnchant.CLEANSING) < 1) continue;
                    Crux.handlers().item().damageItem(item, 2, living);

                    if(!(equipment instanceof PlayerInventory)){
                        equipment.setItem(slot, item, true);
                    }
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockDropItem(BlockDropItemEvent event) {
        Player p = event.getPlayer();
        Block b = yoinkingBlock.get(p.getUniqueId());
        if(event.getBlock().equals(b)){
            event.setCancelled(true);
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerFish(PlayerFishEvent event) {
        Player p = event.getPlayer();
        FishHook hook = event.getHook();
        if(event.getState() == PlayerFishEvent.State.REEL_IN || event.getState() == PlayerFishEvent.State.IN_GROUND){
             if(event.getHand() == null) return;
             ItemStack item = p.getInventory().getItem(event.getHand());
             if(CruxItem.isEmpty(item)) return;
             int level = item.getEnchantmentLevel(USurviveEnchant.YOINK);
             if(level < 1) return;
             onYoink(p, hook.getLocation(), p.getLocation(), level);
        }
    }

    protected final Map<UUID, Block> yoinkingBlock = new HashMap<>();
    public void onYoink(Player p, Location from, Location to, int level){
        Collection<Block> blocks = new ArrayList<>();
        if(level == 1){
            Block b = null;
            for(BlockFace face : YOINK_DIRECTIONS){
                RayTraceResult result = from.getWorld().rayTraceBlocks(
                    from, face.getDirection(), .15, FluidCollisionMode.NEVER,
                    false
                );
                if(result != null && result.getHitBlock() != null){
                    b = result.getHitBlock();
                    break;
                }
            }
            if(b == null) return;
            blocks.add(b);
        }else{
            blocks.addAll(getNoisyBlockClump(from, level, 0.2f, 1.5f));
        }

        float speed = CruxMath.random(1.2f, 1.6f);
        Vector direction = to.toVector().subtract(from.toVector()).normalize();

        for(Block b : blocks){
            if(b.isEmpty()) continue;
            BlockState blockState = b.getState(true);
            yoinkingBlock.put(p.getUniqueId(), b);
            if(!p.breakBlock(b)){
                yoinkingBlock.remove(p.getUniqueId());
                continue;
            }
            yoinkingBlock.remove(p.getUniqueId());

            Vector baseDir = direction.clone().normalize();

            double maxAngle = Math.toRadians(15);

            double yawOffset = CruxMath.random(-maxAngle, maxAngle);
            double pitchOffset = CruxMath.random(-maxAngle, maxAngle);

            Vector variedDir = rotateVector(baseDir, yawOffset, pitchOffset).normalize();

            Vector flingDirection = variedDir.multiply(speed);
            flingDirection.setY(flingDirection.getY() + CruxMath.random(.5, .8));


            /*Vector flingDirection = direction.clone().add(new Vector(
                CruxMath.random(-.1, .1),
                CruxMath.random(-.1, .1),
                CruxMath.random(-.1, .1)
            )).multiply(speed);*/

            b.getWorld().spawn(b.getLocation().toCenterLocation(), FallingBlock.class, e ->{
                e.setBlockState(blockState);
                e.setCancelDrop(false);
                e.setDropItem(true);
                e.setVelocity(flingDirection);
            });
        }
    }

    public static Vector rotateVector(Vector v, double yaw, double pitch) {
        // Convert to spherical coordinates
        double currentYaw = Math.atan2(v.getZ(), v.getX());
        double currentPitch = Math.asin(v.getY() / v.length());

        // Add the offsets
        currentYaw += yaw;
        currentPitch += pitch;

        // Convert back to Cartesian
        double x = Math.cos(currentPitch) * Math.cos(currentYaw);
        double y = Math.sin(currentPitch);
        double z = Math.cos(currentPitch) * Math.sin(currentYaw);

        return new Vector(x, y, z);
    }


    public Set<Block> getNoisyBlockClump(Location center, int radius, float noiseScale, float threshold) {
        Set<Block> blocks = new HashSet<>();

        CruxNoise noise = CruxNoise.fast()
            .seed((int) (Math.random() * Integer.MAX_VALUE))
            .frequency(noiseScale)
            .noiseType(CruxNoise.NoiseType.OpenSimplex2);

        double cx = center.getX();
        double cy = center.getY();
        double cz = center.getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + y * y + z * z);

                    // Get noise for this block position
                    float n = noise.noise((float) (cx + x), (float) (cy + y), (float) (cz + z));

                    // Noise influences the radius threshold
                    if (dist <= radius + (n * threshold)) {
                        blocks.add(center.clone().add(x, y, z).getBlock());
                    }
                }
            }
        }

        return blocks;
    }



    protected final static BlockFace[] YOINK_DIRECTIONS = new BlockFace[]{
        BlockFace.DOWN,
        BlockFace.UP,
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST
    };


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
                if(isFriendly(hit, owner, EntityDamager.getOwner(proj))){
                    event.setCancelled(true);
                }
            }
        }
    }

    /*public boolean isFriendly(Entity hit, UUID owner){
        if(hit instanceof Tameable t && owner.equals(t.getOwnerUniqueId())) return true;
        if(hit instanceof Player){
            UPlayer player = UPlayer.getPlayer(owner);
            if(player != null){
                if(player.hasFriend(hit.getUniqueId()) || player.isApartOfParty(hit.getUniqueId())) return true;
            }
        }
        return false;
    }*/

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityEquipmentChangedMonitor(EntityEquipmentChangedEvent event) {
        LivingEntity e = event.getEntity();
        if(e instanceof Mob mob){
            var change = event.getEquipmentChanges().get(EquipmentSlot.HEAD);
            if(change == null) return;
            EntityMemory.getOrCreateDataHolder(mob, EmotiveMobHolder.class, EmotiveMobHolder::new);
        }
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
        Entity hit = event.getHitEntity();
        Entity e = event.getEntity();
        int level = CruxTag.get(e, "enchant/boomshot", PersistentDataType.INTEGER, 0);
        if(level > 0){
            explodeBoomshot(e, level);
        }

        if(hit == null) return;
        Map<Enchantment, Integer> enchants = CruxEntity.entity(e).get(CruxComponents.INHERITED_ENCHANTMENTS);
        if(enchants == null || enchants.isEmpty()) return;

        level = enchants.getOrDefault(USurviveEnchant.CLING, 0);
        if(level > 0){
            PotionHolder holder = PotionHolder.potionHolderOrCreate(hit);
            if(holder != null){
                int duration = 300 + ((level-1) * 100);
                PotionInflictor inflictor = new EntityInflictor(EntityDamager.getOwnerOrSelf(e));
                holder.addPotion(USurvivePotions.CLING.create(hit, duration, level-1, inflictor));
            }
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeathMonitor(EntityDeathEvent event) {
        LivingEntity e = event.getEntity();
        var data = EntityMemory.get(e);
        if(data != null){
            data.getDataHolders().remove(FrenzyHolder.KEY);
        }
    }

    public void onRemnant(LivingEntity e, Entity attacker){
        if(CruxMob.is(e, USurviveMobs.REMNANT_SPIRIT)) return;
        if(CruxMob.isInCategory(e, MobCategory.ETERNAL, MobCategory.OBJECT)) return;
        EntityEquipment equipment = e.getEquipment();
        if(equipment == null) return;

        for(EquipmentSlot slot : AttributeBukkitAdaptor.ARMOR_SLOTS){
            ItemStack item = equipment.getItem(slot);
            if(CruxItem.isEmpty(item)) continue;
            int level = item.getEnchantmentLevel(USurviveEnchant.REMNANT);
            if(level < 1) continue;

            if(level > 1){
                item.addUnsafeEnchantment(USurviveEnchant.REMNANT, level-1);
            }else{
                item.removeEnchantment(USurviveEnchant.REMNANT);
            }

            spawnRemnant(e, attacker, level);
            break;
        }
    }

    public void spawnRemnant(Entity e, Entity attacker, int level){
        CreateSound.sound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, .6f, 1.5f)
            .playAt(e);
        CreateSound.sound(Sound.ENTITY_VEX_CHARGE, .6f, 1.5f)
            .playAt(e);

        new ParticleBuilder(Particle.SOUL_FIRE_FLAME)
            .location(e.getLocation().add(0, e.getHeight()*.5, 0))
            .count(CruxMath.random(10, 15))
            .extra(.1)
            .offset(.25, .25, .25)
            .spawn();
        new ParticleBuilder(Particle.SOUL)
            .location(e.getLocation().add(0, e.getHeight()*.5, 0))
            .count(CruxMath.random(4, 6))
            .extra(.05)
            .offset(.1, .1, .1)
            .spawn();
        if(e instanceof Player p){
            USurviveMobs.REMNANT_SPIRIT.spawn(level, p, p.getLocation(), x ->{
                if(x instanceof LivingEntity living){
                    var instance = living.getAttribute(Attribute.GRAVITY);
                    if(instance != null){
                        instance.addModifier(new AttributeModifier(Crux.key("enchant/remnant"), -.65D, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                    }
                }
            });
            return;
        }
        if(!(e instanceof Mob)) return;

        Entity newEntity = e.copy();
        CruxEntitiesPersist.ENTITY.set(newEntity, USurviveMobs.REMNANT_SPIRIT.key());
        CruxPersist.DROP_ITEMS.set(newEntity, false);
        CruxPersist.DROP_EXPERIENCE.set(newEntity, false);
        newEntity.setInvisible(true);
        if(newEntity instanceof Mob mob){
            mob.setCanPickupItems(false);
            for(EquipmentSlot slot : EquipmentSlot.values()){
                mob.getEquipment().setDropChance(slot, 0f);
            }
            if(attacker instanceof LivingEntity living){
                mob.setTarget(living);
            }
        }
        if(newEntity instanceof LivingEntity living){
            var instance = living.getAttribute(Attribute.GRAVITY);
            if(instance != null){
                instance.addModifier(new AttributeModifier(Crux.key("enchant/remnant"), -.65D, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            }
        }
        newEntity.spawnAt(e.getLocation());
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
            float multiplier = ActiveFocusEnchantTickable.getMultiplier(p.getActiveItemUsedTime(), level);
            if(multiplier > 1f){
                if(e instanceof AbstractArrow arrow){
                    arrow.setDamage(arrow.getDamage() * multiplier);
                }
            }
        }
        level = item.getEnchantmentLevel(USurviveEnchant.RICOCHET);
        if(level > 0){
            CruxAttribute.addModifier(
                e, CruxAttribute.PROJECTILE_RICOCHET_ENTITY,
                CruxAttributeModifier.addModifier(Crux.key("enchant/ricochet"), level+2)
            );
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
                    p.getWorld().spawnParticle(
                        Particle.DUST_COLOR_TRANSITION,
                        p.getLocation().add(0, .1, 0),
                        1,
                        new Particle.DustTransition(Color.AQUA, Color.BLUE, 1f)
                    );
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

        new ParticleBuilder(Particle.FLAME)
            .count(CruxMath.random(8, 12))
            .location(victim.getLocation().add(0, victim.getHeight()*.5, 0))
            .offset(.1, .1, .1)
            .extra(.07)
            .spawn();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageLowest(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if(CruxMob.is(e, USurviveMobs.MIRRORSTRIKE)){
            if(!(event instanceof EntityDamageByEntityEvent dmgEvent)){
                event.setCancelled(true);
                return;
            }
            UUID target = CruxTag.get(e, "target", CruxPersistence.UUID, null);
            if(target != null && !target.equals(dmgEvent.getDamager().getUniqueId())){
                event.setCancelled(true);
                return;
            }
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

        double width = e.getWidth();
        new ParticleBuilder(Particle.ENCHANTED_HIT)
            .location(e.getLocation().add(0, e.getHeight()*.5, 0))
            .offset(width *.5, width *.5, width *.5)
            .extra(.1)
            .count(CruxMath.random(10, 15))
            .spawn();
        CreateSound.sound(Sound.BLOCK_ANVIL_LAND, net.kyori.adventure.sound.Sound.Source.PLAYER,
            .5f, 2f).playAt(e);
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

                Location baseLoc = CruxEntityUtil.getEyeOrLocation(vic);
                baseLoc.setPitch(0f);
                double width = vic.getWidth();
                double height = vic.getHeight();
                Location backLoc = CruxLoc.shift(baseLoc, width * -.7, height * .7, 0D);

                new ParticleBuilder(Particle.CRIT)
                    .location(backLoc)
                    .count(CruxMath.random(3, 5))
                    .extra(.05)
                    .offset(.05 * width, .05 * height, .05 * width)
                    .spawn();
            }
            if(enchants.containsKey(USurviveEnchant.SHATTER) && isValidShatterTarget(vic)){
                int level = enchants.getOrDefault(USurviveEnchant.SHATTER, 0);
                double addon = level * 2.75D;
                event.setDamage(event.getDamage() + addon);

                double width = vic.getWidth();
                double height = vic.getHeight();
                new ParticleBuilder(Particle.ITEM)
                    .data(ItemType.IRON_CHESTPLATE.createItemStack())
                    .location(vic.getLocation().add(0, height*.5, 0))
                    .count(CruxMath.random(12, 15))
                    .extra(.05)
                    .offset(.01 * width, .01 * height, .01 * width)
                    .spawn();
            }
            if(enchants.containsKey(USurviveEnchant.MIRRORSTRIKE)){
                int level = enchants.getOrDefault(USurviveEnchant.MIRRORSTRIKE, 0);
                mirrorstrike(vic, victim, attacker, level, event);
            }
            if(enchants.containsKey(USurviveEnchant.STAGGER) && event.isCritical() && victim != null){
                int level = enchants.getOrDefault(USurviveEnchant.STAGGER, 0);
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,
                    30 + (level * 40),
                    level-1, false, false, false));
                victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,
                    20 + (level * 40),
                    level-1, false, false, false));
            }
            if(enchants.containsKey(USurviveEnchant.FRENZY)){
                onFrenzy(attacker, vic, enchants.getOrDefault(USurviveEnchant.FRENZY, 0));
            }
            if(enchants.containsKey(USurviveEnchant.SNARE)){
                onSnare(attacker, vic, enchants.getOrDefault(USurviveEnchant.SNARE, 0));
            }

            if(enchants.containsKey(USurviveEnchant.TETHER) && victim != null && event.getDamageSource().getDamageType() != USurviveDamageTypes.TETHER){
                double hp = victim.getHealth() - event.getFinalDamage();
                if(hp > 0D){
                    onTether(attacker, vic, enchants.getOrDefault(USurviveEnchant.TETHER, 0));
                }
            }

            if(enchants.containsKey(USurviveEnchant.REAP) && victim != null && event.getDamageSource().getDamageType() != USurviveDamageTypes.REAP_SHOCKWAVE){
                double hp = victim.getHealth() - event.getFinalDamage();
                if(hp <= 0D){
                    int level = enchants.getOrDefault(USurviveEnchant.REAP, 0);
                    reap(vic, attacker, level, mainHand);
                }
            }
            if(victim != null){
                double hp = victim.getHealth() - event.getFinalDamage();
                if(hp <= 0D){
                    onRemnant(victim, attacker);
                }else{
                    if(enchants.containsKey(USurviveEnchant.CHAINSHOCK)){
                        int level = enchants.get(USurviveEnchant.CHAINSHOCK);
                        EntityTickablesHolder holder = EntityMemory.getDataHolder(attacker, EntityTickablesHolder.class);
                        if(holder != null){
                            if(holder.getTickable(USurviveTickables.CHAINSHOCK) instanceof ActiveChainshockTickable tickable){
                                if(!tickable.hasValidLink() && tickable.getScheduledStarterLink() == null){
                                    tickable.setScheduledStarterLink(vic);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void onTether(Entity attacker, Entity vic, int level){
        TetherEnchantHolder holder = EntityMemory.getDataHolder(attacker, TetherEnchantHolder.class);
        if(holder != null) return;
        int lifespan = 60 + ((level-1) * 20);
        double breakDistance = TetherEnchantHolder.getDistance(attacker, vic) + (10D - ((level-1) * 1.5D));
        EntityMemory.getOrCreateDataHolder(attacker, TetherEnchantHolder.class, mem ->{
            return new TetherEnchantHolder(mem, vic, lifespan, breakDistance*breakDistance, level);
        });
    }

    public void onSnare(Entity attacker, Entity vic, int level){
        double chance = level * 14D;
        if(!CruxMath.testChance(chance)) return;

        if(!CruxEntityUtil.isGrounded(vic)) return;

        BoundingBox box = CruxedBoundingBox.boundingBox(vic.getLocation().toCenterLocation(), 1);
        if(!vic.getWorld().getNearbyEntities(box, e -> CruxMob.is(e, USurviveMobs.SNARE_ENCHANT)).isEmpty()) return;

        Crux.scheduler().runTask(() ->{
            BoundingBox boundingBox = CruxedBoundingBox.boundingBox(vic.getLocation().toCenterLocation(), 1);
            if(!vic.getWorld().getNearbyEntities(boundingBox, e -> CruxMob.is(e, USurviveMobs.SNARE_ENCHANT)).isEmpty()) return;
            vic.setVelocity(new Vector());
            Entity snare = USurviveMobs.SNARE_ENCHANT.spawn(vic.getLocation());
            CreateSound.sound(Sound.ENTITY_CREAKING_ACTIVATE, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,1.7f)
                .playAt(snare);

            new ParticleBuilder(Particle.BLOCK)
                .location(snare.getLocation().add(0, snare.getHeight()*.5, 0))
                .offset(.1, .1, .1)
                .extra(.15)
                .count(CruxMath.random(13, 17))
                .data(Material.DIRT.createBlockData())
                .spawn();
        });
    }

    public void onFrenzy(Entity attacker, Entity vic, int level){
        if(attacker instanceof HumanEntity h){
            if(h.getAttackCooldown() < .75f) return;
        }

        FrenzyHolder holder = EntityMemory.getDataHolder(attacker, FrenzyHolder.class);
        if(holder != null){
            holder.onHit(attacker, vic, level);
            return;
        }

        EntityMemory.getOrCreateDataHolder(attacker, FrenzyHolder.class, mem ->{
            var newHolder = new FrenzyHolder(mem);
            newHolder.onHit(attacker, vic, level);
            return newHolder;
        });
    }


    public boolean isValidShatterTarget(Entity target){
        if(!(target instanceof LivingEntity victim)) return false;
        if(victim.getAbsorptionAmount() > .5D) return true;
        var instance = victim.getAttribute(Attribute.ARMOR);
        if(instance != null && instance.getValue() > .5D) return true;
        if(CruxEntityUtil.isBlocking(target)) return true;
        return false;
    }

    public boolean reapCheckRay(World world, Entity e, Location visualLocation, double range) {
        double[] locationOffsets = new double[]{0D, .4D, -.4D};
        double[] yOffsets;
        if(e instanceof LivingEntity living && living.getEyeLocation().getY() < visualLocation.getY()){
            yOffsets = new double[]{0, 0.5, 0.1, 1.0}; // Location, Mid-body, lower, head
        }else{
            yOffsets = new double[]{0, 0.5, 0.1}; // no head check
        }

        for (double offset : yOffsets) {
            for(double locationY : locationOffsets){
                Location from = visualLocation.clone().add(0, locationY, 0);
                if(from.getBlock().isSolid()) continue;

                Location to = e.getLocation().add(0, e.getHeight() * offset, 0);
                Vector direction = to.toVector().subtract(from.toVector()).normalize();

                RayTraceResult ray = world.rayTraceBlocks(
                    from,
                    direction,
                    range,
                    FluidCollisionMode.NEVER,
                    true
                );

                if (ray == null || ray.getHitBlock() == null || !ray.getHitBlock().isSolid()) {
                    return true; // Valid hit path to that body part
                }
            }
        }

        return false;
    }


    public void reap(Entity victim, Entity dmger, int level, ItemStack item){
        double range = 3D + (level * 2.5D);

        UUID ownerUUID = EntityDamager.getOwnerUUID(dmger);
        Entity owner = EntityDamager.getOwnerOrSelf(dmger);

        Location center = victim.getLocation();
        GetNear<Entity> getNear = new GetEntityNear<>(Entity.class)
            .center(center)
            .range(range)
            .amount(1)
            .filter(e ->{
                if(e.equals(dmger)) return false;
                return ownerUUID == null || !isFriendly(e, ownerUUID, owner);
            });

        if(getNear.find().isEmpty()) return;

        Location visualLocation = center.clone().add(0, 0.2, 0);

        World world = victim.getWorld();
        getNear.amount((NumberHolder) null)
            .filter(e ->{
                if(ownerUUID != null && isFriendly(e, ownerUUID, owner)) return false;
                if(e.equals(dmger)) return false;
                if(!reapCheckRay(world, e, visualLocation, range)) return false;
                return true;
            });

        if(!CruxEntityUtil.isNonSurvival(dmger)){
            Crux.handlers().item().damageItem(item, level+1, dmger);
        }

        CruxLocation visualLoc = CruxLocation.location(visualLocation, 0f, 0f);
        double damage = level * 4.5D;
        double kb = level * 8D;
        double upKb = level * 2D;
        int totalTicks = Math.max(60 - ((level-1) * 20), 0);
        new Shockwave(
            victim.getWorld(),
            range,
            () -> visualLoc,
            CreateCircle.Type.WHOLE,
            getNear,
            hit ->{
                var sourceBuilder = DamageSource.builder(USurviveDamageTypes.REAP_SHOCKWAVE)
                    .withDirectEntity(dmger)
                    .withDamageLocation(getNear.center().value());

                if(!owner.equals(dmger)){
                    sourceBuilder.withCausingEntity(owner);
                }

                var entityDamager = EntityDamager.entityDamager(
                    hit, dmger
                ).setSource(sourceBuilder.build());
                entityDamager.attack(damage, kb, upKb);
            },
            10
        ).start(totalTicks);
        CreateSound.sound(Sound.ENTITY_IRON_GOLEM_ATTACK, .5f, .7f)
            .playAt(visualLocation);
        CreateSound.sound(Sound.ITEM_TRIDENT_THROW, .5f, .6f)
            .playAt(visualLocation);
        CreateSound.sound(Sound.ENTITY_ENDER_DRAGON_FLAP, .5f, .7f)
            .playAt(visualLocation);

        new ParticleBuilder(Particle.TRIAL_OMEN)
            .location(visualLocation)
            .extra(.05)
            .spawn();
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
        int time = Math.max(20, 80 - ((level-1) * 10));
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
