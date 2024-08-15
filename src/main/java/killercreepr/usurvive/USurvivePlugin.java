package killercreepr.usurvive;

import killercreepr.crux.data.communication.CreateLang;
import killercreepr.crux.data.communication.LangPopulator;
import killercreepr.crux.data.communication.LangProvider;
import killercreepr.crux.data.communication.SimpleCreateLang;
import killercreepr.crux.data.entity.EntityMemory;
import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.registries.CruxRegistries;
import killercreepr.cruxadvancements.crazy.CfgCrazyAdvancementManager;
import killercreepr.cruxadvancements.registries.AdvancementRegistries;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.loot.LootTableLoader;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.usurvive.block.USurviveBlocks;
import killercreepr.usurvive.config.Config;
import killercreepr.usurvive.config.SurviveLangConfig;
import killercreepr.usurvive.config.handler.FilePlayerDeath;
import killercreepr.usurvive.data.entity.DeathHolder;
import killercreepr.usurvive.death.DeathManager;
import killercreepr.usurvive.death.PlayerDeath;
import killercreepr.usurvive.entity.USurviveMobs;
import killercreepr.usurvive.item.USurviveItems;
import killercreepr.usurvive.lang.Lang;
import killercreepr.usurvive.lang.Msg;
import killercreepr.usurvive.values.DefaultValues;
import killercreepr.usurvive.values.ValuesProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class USurvivePlugin extends CruxPlugin implements Listener, LangProvider {
    private static USurvivePlugin instance;
    public static USurvivePlugin inst(){ return instance; }

    protected final DeathManager deathManager = new DeathManager(this);
    public DeathManager getDeathManager() {
        return deathManager;
    }

    protected ValuesProvider values;

    public ValuesProvider values() {
        return values;
    }

    public void values(@NotNull ValuesProvider values) {
        this.values = values;
    }

    protected final CreateLang lang = Lang.setLang(new SimpleCreateLang());
    @NotNull
    public CreateLang lang() {
        return lang;
    }

    protected final CfgCrazyAdvancementManager MAIN_ADVANCEMENTS = AdvancementRegistries.ADVANCEMENT_MANAGERS.register(
        CfgCrazyAdvancementManager.createNew(
            key("main"), this
        )
    );
    @Override
    public void onLoad() {
        super.onLoad();

        CfgRegistries.FILE.forEach(this::registerCfgHandlers);

        USurviveBlocks.register();
        USurviveItems.register();
        USurviveMobs.register();

        EntityMemory.registerFunction(this, mem ->{
            if(!(mem instanceof PlayerMemory m)) return;
            m.getDataHolders().register(new DeathHolder(m, values(), getDeathManager()));
        });
    }

    protected LangProvider langProvider;
    @Override
    public void enabled() {
        instance = this;
        if(CruxRegistries.MODULES.containsKey("CruxConfigs")){
            values(new Config(this, "config"));
        }else{
            values(new DefaultValues());
        }

        registerListeners(
            deathManager,
            this
        );

        if(CruxRegistries.MODULES.containsKey("CruxConfigs")){
            langProvider = new SurviveLangConfig(this, "lang", lang);
        }else{
            langProvider = this;
            LangPopulator.populate(lang, Msg.class);
        }

        super.enabled();

        deathManager.load();

        MAIN_ADVANCEMENTS.load(this);
        //MAIN_ADVANCEMENTS.registerAdvancement(test);
        MAIN_ADVANCEMENTS.loadAllCrazyAdvancements();
    }

    /*protected final CrazyAdvancement test = new CrazyAdvancement(
        key("test"), null, new NumberCriteria(5),
        null,
        new CrazyAdvancementDisplay(new BukkitDynamicItem("stone", "1", null),
            "Test", "Description", AdvancementDisplay.AdvancementFrame.CHALLENGE,
            AdvancementVisibility.ALWAYS,
            "textures/gui/advancements/backgrounds/stone.png", 0, 0, null
        ),
        AdvancementFlag.TOAST_AND_MESSAGE);*/

    /*@EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        MAIN_ADVANCEMENTS.setCriteriaProgress(event.getPlayer().getUniqueId(), test,
            test.getProgress(event.getPlayer()).getCriteriaProgress() + 1);
    }*/

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        getServer().getScheduler().runTaskLater(this, task ->{
            MAIN_ADVANCEMENTS.loadProgress(p.getUniqueId());
            MAIN_ADVANCEMENTS.getCrazyManager().addPlayer(p);
        }, 20L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        getServer().getScheduler().runTask(this, task ->{
            MAIN_ADVANCEMENTS.saveProgress(p.getUniqueId());
            MAIN_ADVANCEMENTS.getCrazyManager().removePlayer(p);
            MAIN_ADVANCEMENTS.unloadProgress(p.getUniqueId());
        });
    }

    @Override
    public void disabled() {
        super.disabled();
        deathManager.save();
    }

    @Override
    public void reload() {
        super.reload();
        values.reload(this);
        langProvider.reload(this);

        CruxCore.inst().cruxMenus().menuRegistry().loadConfiguration(
            new CruxFolder(this, "menus").file()
        );

        new LootTableLoader().loadConfiguration(
            new CruxFolder(this, "loot_tables").file()
        );

        MAIN_ADVANCEMENTS.refresh(this);
    }

    public void registerCfgHandlers(@NotNull FileRegistry registry){
        registry.registerFileHandler(PlayerDeath.class, new FilePlayerDeath());
    }
}
