package killercreepr.usurvive;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.Crux;
import killercreepr.crux.data.communication.*;
import killercreepr.crux.data.entity.EntityMemory;
import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.crux.item.dynamic.DynamicItem;
import killercreepr.crux.module.StandardModules;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.registries.CruxRegistries;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.cruxadvancements.config.loader.CfgCrazyAdvancementManagerCfgLoader;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.standard.SimpleLangConfig;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxitems.config.CruxItemsConfigHook;
import killercreepr.cruxitems.config.handler.FilePluginItemHandler;
import killercreepr.cruxitems.item.plugin.PluginItem;
import killercreepr.cruxmenus.api.menu.module.MenuModule;
import killercreepr.cruxmenus.api.menu.module.config.MenuModuleBuilder;
import killercreepr.cruxworlds.world.manager.CruxWorldManager;
import killercreepr.usurvive.block.USurviveBlocks;
import killercreepr.usurvive.command.USurviveCommands;
import killercreepr.usurvive.config.Config;
import killercreepr.usurvive.config.component.CfgUSurviveComponents;
import killercreepr.usurvive.config.handler.FilePlayerDeath;
import killercreepr.usurvive.data.entity.DeathHolder;
import killercreepr.usurvive.death.DeathManager;
import killercreepr.usurvive.death.PlayerDeath;
import killercreepr.usurvive.entity.USurviveMobs;
import killercreepr.usurvive.item.GrappleItem;
import killercreepr.usurvive.item.QuickAccessItem;
import killercreepr.usurvive.item.USurviveItems;
import killercreepr.usurvive.lang.Lang;
import killercreepr.usurvive.listener.*;
import killercreepr.usurvive.mechanics.grapple.standard.StandardGrappleHooks;
import killercreepr.usurvive.menu.module.CurrencyDepositModule;
import killercreepr.usurvive.registries.USurviveRegistries;
import killercreepr.usurvive.values.DefaultValues;
import killercreepr.usurvive.values.ValuesProvider;
import killercreepr.usurvive.world.USurviveOverworld;
import killercreepr.usurvive.world.entity.StandardOverworldGroups;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

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

    protected final CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader = new CfgCrazyAdvancementManagerCfgLoader(this);
    @Override
    public void onLoad() {
        instance = this;
        super.onLoad();
        new USurviveCommands(this).register();

        CfgRegistries.FILE.forEach(this::registerCfgHandlers);
        CfgUSurviveComponents.register(BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers());

        CruxItemsConfigHook.filePluginItem().registerTypeHandler(new FilePluginItemHandler(){
            @Override
            public @NotNull Key key() {
                return USurvivePlugin.this.key("grapple");
            }

            @NotNull
            @Override
            public PluginItem deserialize(@NotNull FileContext<?> ctx, @NotNull FileElement e, @NotNull Key key, @NotNull DynamicItem item) {
                return new GrappleItem(key, item);
            }
        });

        CruxItemsConfigHook.filePluginItem().registerTypeHandler(new FilePluginItemHandler(){
            @Override
            public @NotNull Key key() {
                return USurvivePlugin.this.key("quick_access");
            }

            @NotNull
            @Override
            public PluginItem deserialize(@NotNull FileContext<?> ctx, @NotNull FileElement e, @NotNull Key key, @NotNull DynamicItem item) {
                return new QuickAccessItem(key, item);
            }
        });

        CruxCore.inst().cruxMenus().menuModuleRegistry().register(new MenuModuleBuilder() {
            @Override
            public @Nullable MenuModule build(@NotNull FileContext<?> ctx, @NotNull FileElement e, @Nullable FileObject menuCtx) {
                if(!(e instanceof FileObject o)) return null;
                FileRegistry reg = ctx.getRegistry();
                String id = o.getObject(String.class, "id");
                Collection<Key> acceptedCurrencies = reg.deserializeFromFile(
                    new TypeToken<Set<Key>>(){}.getType(), o.get("accepted_currencies")
                );
                NumberProvider currencyIndexes = reg.deserializeFromFile(NumberProvider.class, o.get("currency_slots"));
                Communicator depositMsg = reg.deserializeFromFile(MsgContainer.class, o.get("deposit_msg"));
                return new CurrencyDepositModule(
                    key(), id, acceptedCurrencies, currencyIndexes, depositMsg
                );
            }

            @Override
            public @NotNull Key key() {
                return Crux.key("currency_deposit");
            }
        });

        EntityMemory.registerFunction(this, mem ->{
            if(!(mem instanceof PlayerMemory m)) return;
            m.getDataHolders().register(new DeathHolder(m, values(), getDeathManager()));
        });
        StandardGrappleHooks.register(USurviveRegistries.GRAPPLE_HOOK);
        StandardOverworldGroups.register(USurviveRegistries.OVERWORLD_NATURAL_ENTITY_SPAWN_GROUP);

        CruxWorldManager worldManager = CruxCore.inst().worldManager();
        worldManager.getCreatorRegistry().register("world", USurviveOverworld::new);
    }

    protected LangProvider langProvider;
    @Override
    public void enabled() {
        if(CruxRegistries.MODULES.containsKey(StandardModules.CRUX_CONFIGS)){
            values(new Config(this, "config"));
        }else{
            values(new DefaultValues());
        }

        USurviveBlocks.register();
        USurviveItems.register();
        USurviveMobs.register();

        if(CruxRegistries.MODULES.containsKey(StandardModules.CRUX_CONFIGS)){
            langProvider = new SimpleLangConfig(this, "lang", this::lang, Lang.class);
        }else{
            langProvider = this;
            LangPopulator.populate(lang, Msg.class);
        }

        registerListeners(
            deathManager,
            this,
            new GrappleListener(),
            new QuickAccessListener(),
            new ObjectiveProgressListener(this),
            new SpawnListener(values, this),
            new CommandListener()
        );

        super.enabled();

        deathManager.load();

        //cfgManagerLoader.load();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        getServer().getScheduler().runTaskLater(this, task ->{
            cfgManagerLoader.load(p);
        }, 20L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        getServer().getScheduler().runTask(this, task ->{
            cfgManagerLoader.saveAndUnload(p);
        });
    }

    @Override
    public void disabled() {
        super.disabled();
        deathManager.save();
        cfgManagerLoader.saveAndUnload(getServer().getOnlinePlayers());
    }

    @Override
    public void reload() {
        super.reload();
        values.reload(this);
        langProvider.reload(this);

        CruxCore.inst().cruxMenus().menuRegistry().loadConfiguration(
            new CruxFolder(this, "menus").file()
        );

        cfgManagerLoader.refresh();
    }

    public void registerCfgHandlers(@NotNull FileRegistry registry){
        registry.registerFileHandler(PlayerDeath.class, new FilePlayerDeath());
    }
}
