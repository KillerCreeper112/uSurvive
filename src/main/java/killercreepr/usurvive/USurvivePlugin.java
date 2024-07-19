package killercreepr.usurvive;

import killercreepr.crux.data.communication.CreateLang;
import killercreepr.crux.data.communication.LangPopulator;
import killercreepr.crux.data.communication.LangProvider;
import killercreepr.crux.data.communication.SimpleCreateLang;
import killercreepr.crux.data.entity.EntityMemory;
import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.registries.CruxRegistries;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
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
import org.bukkit.event.Listener;
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

    protected LangProvider langProvider;
    @Override
    public void enabled() {
        instance = this;
        if(CruxRegistries.MODULES.containsKey("CruxConfigs")){
            values(new Config(this, "config"));
        }else{
            values(new DefaultValues());
        }

        registerCfgHandlers(CfgRegistries.JSON);
        registerCfgHandlers(CfgRegistries.YAML);

        USurviveBlocks.register();
        USurviveItems.register();
        USurviveMobs.register();
        registerListeners(
            deathManager
        );

        if(CruxRegistries.MODULES.containsKey("CruxConfigs")){
            langProvider = new SurviveLangConfig(this, "lang", lang);
        }else{
            langProvider = this;
            LangPopulator.populate(lang, Msg.class);
        }

        EntityMemory.registerFunction(this, mem ->{
            if(!(mem instanceof PlayerMemory m)) return;
            m.getPlayerSpecificHolders().register(new DeathHolder(m, values(), getDeathManager()));
        });

        super.enabled();
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
    }

    public void registerCfgHandlers(@NotNull FileRegistry registry){
        registry.registerHandler(PlayerDeath.class, new FilePlayerDeath());
    }
}
