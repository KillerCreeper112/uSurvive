package killercreepr.usurvive.core.guide.listener;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxWorldUtil;
import killercreepr.cruxabyss.api.event.AbyssAltarActivatePortalEvent;
import killercreepr.cruxadvancements.api.advancement.CruxAdvancement;
import killercreepr.cruxadvancements.api.advancement.flag.CruxAdvancementFlag;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.api.event.CruxAdvancementGrantEvent;
import killercreepr.cruxadvancements.crazy.advancement.CrazyAdvancement;
import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import killercreepr.cruxblocks.core.mining.user.EntityMiner;
import killercreepr.cruxclaims.api.event.ClaimerClaimEvent;
import killercreepr.cruxclaims.core.claiming.data.PlayerClaimer;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxcore.api.event.PlayerEnterStructureEvent;
import killercreepr.cruxcurrency.api.event.CurrencyDepositEvent;
import killercreepr.cruxitems.api.event.ConsumableComponentUseEvent;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxmagic.api.event.EntityInfuseSpellOrbItemEvent;
import killercreepr.cruxmagic.api.event.EntitySoulOrbConsumeDamageEvent;
import killercreepr.cruxmagic.api.event.SoulOrbTransferEvent;
import killercreepr.cruxshops.api.event.EntityPurchaseTraderTradeEvent;
import killercreepr.cruxstructures.api.structure.generation.StructureGenerator;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.guide.GuideAdvances;
import killercreepr.usurvive.core.guide.GuideHolder;
import killercreepr.usurvive.core.guide.GuideStructurePlacer;
import killercreepr.usurvive.core.guide.GuideUtil;
import killercreepr.usurvive.core.guide.progression.*;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.util.AdvanceUtil;
import killercreepr.usurvive.core.world.USurviveWorldTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GuideListener implements Listener {
    protected final ValuesProvider cfg;
    protected final Holder<StructureGenerator> GUIDE_RESOURCE_WORLD_STRUCTURE;
    protected final Holder<NumberProvider> GUIDE_RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE;

    public GuideListener(ValuesProvider cfg, Holder<StructureGenerator> GUIDE_RESOURCE_WORLD_STRUCTURE, Holder<NumberProvider> guideResourceWorldStructureChunkDistance) {
        this.cfg = cfg;
        this.GUIDE_RESOURCE_WORLD_STRUCTURE = GUIDE_RESOURCE_WORLD_STRUCTURE;
        GUIDE_RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE = guideResourceWorldStructureChunkDistance;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if(AdvanceUtil.hasAnyAdvancement(p, GuideAdvances.ENTER_RESOURCE_OVERWORLD)) return;
        new BukkitRunnable(){
            byte tick = 0;
            @Override
            public void run() {
                if(!p.isOnline()){
                    cancel();
                    return;
                }
                tick++;
                if(tick > 60){
                    cancel();
                    Lang.GUIDE_FIRST_START.use(p);
                }
            }
        }.runTaskTimer(Crux.getMainPlugin(), 0L, 1L);
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCruxBlockBreak(CruxBlockBreakEvent event) {
        if(!(event.getContext().getMiner() instanceof EntityMiner miner)) return;
        if(!(miner.getEntity() instanceof Player p)) return;
        if(!event.getBlock().key().equals(Crux.key("orbit_ore"))) return;

        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof FirstGuideProgression){
            if(AdvanceUtil.hasAnyAdvancement(p, GuideAdvances.DISCOVER_GUIDE_METEOR)){
                holder.completeAndClearCurrent();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof Guide21Progression){
            CruxWorld world = CruxCore.core().worldManager().getWorld(event.getFrom().key());
            if(world != null){
                var type = world.get(CruxWorldsComponents.WORLD_TYPE);
                if(type != null && type.key().equals(Crux.key("abyss"))){
                    holder.completeAndClearCurrent();
                    return;
                }
            }
        }


        if(holder.getCurrentProgression() instanceof FourthGuideProgression){
            holder.completeAndClearCurrent();
            return;
        }
        if(holder.getCurrentProgression() instanceof SeventeenthGuideProgression){
            holder.completeAndClearCurrent();
            return;
        }

        String world = cfg.SPAWN_WORLD().value();
        if(world == null) return;

        if(holder.getCurrentProgression() instanceof SecondGuideProgression prog){
            if(!p.getWorld().getName().equals(world)) return;
            Crux.scheduler().runTaskAsync(() ->{
                prog.findAndSetConduit(p.getLocation().getBlock());
            });
            Lang.GUIDE_SECOND_GUIDE_SPAWN.use(p);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCurrencyDeposit(CurrencyDepositEvent event) {
        if(!event.getCurrency().key().equals(Crux.key("orbit"))) return;
        if(!event.getReason().equalsIgnoreCase("usurvive:menu/currency_deposit")) return;
        Player p = event.getPlayer().getPlayer();
        if(p == null) return;

        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof SecondGuideProgression){
            holder.completeAndClearCurrent();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPurchaseTraderTrade(EntityPurchaseTraderTradeEvent event) {
        if(!(event.getEntity() instanceof Player p)) return;
        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof ThirdGuideProgression){
            holder.completeAndClearCurrent();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClaimerClaim(ClaimerClaimEvent event) {
        if(!(event.getClaimer() instanceof PlayerClaimer claimer)) return;
        Player p = claimer.getPlayer();
        if(p == null) return;

        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof FifthGuideProgression){
            holder.completeAndClearCurrent();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInventorySlotChange(PlayerInventorySlotChangeEvent event) {
        ItemStack item = event.getNewItemStack();
        if(CruxItem.isEmpty(item)) return;

        Player p = event.getPlayer();
        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof EighthGuideProgression){
            if(PluginItem.isPluginItem(item, Crux.key("copper_staff"))){
                holder.completeAndClearCurrent();
            }
        }else if(holder.getCurrentProgression() instanceof NinthGuideProgression){
            if(PluginItem.isPluginItem(item, Crux.key("soul_essence"))){
                holder.completeAndClearCurrent();
            }
        }else if(holder.getCurrentProgression() instanceof TenthGuideProgression){
            if(PluginItem.isPluginItem(item, Crux.key("soul_orb"))){
                holder.completeAndClearCurrent();
            }
        }else if(holder.getCurrentProgression() instanceof EighteenthGuideProgression){
            if(PluginItem.isPluginItem(item, Crux.key("mouldite_upgrade_smithing_template"))){
                holder.completeAndClearCurrent();
            }
        }else if(holder.getCurrentProgression() instanceof NineteenthGuideProgression){
            if(PluginItem.isPluginItem(item, Crux.key("mouldite"))){
                holder.completeAndClearCurrent();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCruxAdvancementGrantLowest(CruxAdvancementGrantEvent event) {
        CruxAdvancement advancement = event.getAdvancement();
        if(advancement.key().equals(GuideAdvances.ENTER_RESOURCE_OVERWORLD)){
            UUID who = event.getWho();
            Player p = Crux.getServer().getPlayer(who);
            if(p == null) return;
            if(!GuideUtil.hasGuideDisabled(p)) return;
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCruxAdvancementGrant(CruxAdvancementGrantEvent event) {
        CruxAdvancement advancement = event.getAdvancement();

        UUID who = event.getWho();
        if(advancement instanceof CrazyAdvancement advance && !advance.hasFlag(CruxAdvancementFlag.GLOBAL)){
            Player p = Crux.getServer().getPlayer(who);
            if(p == null) return;

            GuideHolder holder = getGuideHolder(p);
            if(holder != null){
                if(holder.getCurrentProgression() instanceof SixthGuideProgression){
                    holder.completeAndClearCurrent();
                }
            }
        }
        if(advancement.key().equals(GuideAdvances.ENTER_RESOURCE_OVERWORLD)){
            onEnterResourceOverworld(event);
            return;
        }

        if(advancement.key().equals(GuideAdvances.OBTAIN_FULL_DIAMOND_ARMOR) || advancement.key().equals(GuideAdvances.OBTAIN_FULL_ROSE_GOLD_ARMOR)){
            Player p = Crux.getServer().getPlayer(who);
            if(p == null) return;

            GuideHolder holder = getGuideHolder(p);
            if(holder != null){
                if(holder.getCurrentProgression() instanceof SeventhGuideProgression){
                    holder.completeAndClearCurrent();
                }
            }
            return;
        }
        if(advancement.key().equals(GuideAdvances.BUILD_ABYSS_ALTAR)){
            Player p = Crux.getServer().getPlayer(who);
            if(p == null) return;

            GuideHolder holder = getGuideHolder(p);
            if(holder != null){
                if(holder.getCurrentProgression() instanceof ThirteenthGuideProgression){
                    holder.completeAndClearCurrent();
                }
            }
            return;
        }
    }
    public void onEnterResourceOverworld(CruxAdvancementGrantEvent event){
        CruxAdvancementManager manager = event.getManager();
        UUID who = event.getWho();
        Player p = Crux.getServer().getPlayer(who);
        if(p == null) return;

        World world = p.getWorld();
        CruxWorld cruxWorld = CruxCore.core().worldManager().getWorld(world.key());
        if(cruxWorld==null) return;
        CruxWorldType type = cruxWorld.get(CruxWorldsComponents.WORLD_TYPE);
        if(!USurviveWorldTypes.RESOURCE_OVERWORLD.compare(type)) return;

        StructureGenerator gen = GUIDE_RESOURCE_WORLD_STRUCTURE.value();
        if(gen == null) return;

        GuideStructurePlacer placer = new GuideStructurePlacer(p.getChunk(), 48, gen, GUIDE_RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE.value(),
            p::isOnline);
        placer.placeStructure().orTimeout(15L, TimeUnit.SECONDS).whenComplete((loc, throwable) ->{
            if(throwable != null){
                throwable.printStackTrace();
                p.sendMessage(Component.text("An error occurred. Contact developer. CODE 7392"));
                return;
            }

            if(!p.isOnline()){
                if(loc != null) Crux.logError(p.getName() + " guide structure was placed even though they are offline!");
                return;
            }
            if(loc == null){
                manager.revokeAdvancement(who, event.getAdvancement());
                Lang.GUIDE_STRUCTURE_CANNOT_BE_PLACED.use(p);
                return;
            }

            GuideHolder holder = EntityMemory.getOrCreateDataHolder(p, GuideHolder.class, mem -> new GuideHolder((PlayerMemory) mem));
            if(holder == null || !holder.hasProgressOrNull(FirstGuideProgression.class)){
                p.sendMessage(Component.text("An error occurred. Contact developer. CODE 7391"));
                return;
            }
            FirstGuideProgression prog = new FirstGuideProgression();
            prog.setFallenMeteorLocation(loc);
            holder.setCurrentProgression(prog);
            Lang.GUIDE_STRUCTURE_PLACED.use(p);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySoulOrbConsumeDamage(EntitySoulOrbConsumeDamageEvent event) {
        if(!(event.getEntity() instanceof Player p)) return;
        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof EleventhGuideProgression){
            holder.completeAndClearCurrent();
        }
    }

    public GuideHolder getGuideHolder(Player p){
        return EntityMemory.getDataHolder(p, GuideHolder.class);
        /*GuideHolder holder = EntityMemory.getDataHolder(p, GuideHolder.class);
        if(holder == null) return null;
        return GuideUtil.hasGuideDisabled(p) ? null : holder;*/
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSoulOrbTransfer(SoulOrbTransferEvent event) {
        if(!(event.getKiller() instanceof Player p)) return;
        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof TwelfthGuideProgression){
            holder.completeAndClearCurrent();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityInfuseSpellOrbItem(EntityInfuseSpellOrbItemEvent event) {
        if(!(event.getEntity() instanceof Player p)) return;
        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof FourteenthGuideProgression){
            holder.completeAndClearCurrent();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsumableComponentUse(ConsumableComponentUseEvent event) {
        Player p = event.getContext().getPlayer();
        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof FifteenthGuideProgression){
            holder.completeAndClearCurrent();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbyssAltarActivatePortal(AbyssAltarActivatePortalEvent event) {
        Player p = event.getPlayer();
        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof SixteenthGuideProgression){
            holder.completeAndClearCurrent();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerEnterStructure(PlayerEnterStructureEvent event) {
        Player p = event.getPlayer();
        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof Guide20Progression){
            holder.completeAndClearCurrent();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSmithItem(SmithItemEvent event) {
        if(!(event.getWhoClicked() instanceof Player p)) return;
        GuideHolder holder = getGuideHolder(p);
        if(holder == null) return;
        if(holder.getCurrentProgression() instanceof Guide22Progression){
            holder.completeAndClearCurrent();
        }
    }

}
