package killercreepr.usurvive.config;

import killercreepr.crux.data.communication.*;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.standard.LangConfig;
import killercreepr.usurvive.lang.Lang;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SurviveLangConfig extends LangConfig implements LangProvider {
    public final CreateLang lang;
    public SurviveLangConfig(@NotNull Plugin plugin, @NotNull String path, CreateLang lang) {
        super(plugin, path);
        this.lang = lang;
    }

    public SurviveLangConfig(@NotNull File file, CreateLang lang) {
        super(file);
        this.lang = lang;
    }

    public SurviveLangConfig(@NotNull CruxConfig cfg, CreateLang lang) {
        super(cfg);
        this.lang = lang;
    }

    @Override
    public SurviveLangConfig setDefaults(@NotNull Class<?> type) {
        super.setDefaults(type);
        return this;
    }

    @Override
    public void reload(@NotNull CruxPlugin plugin) {
        if(!file.exists()){
            setDefaults(Lang.class).save();
        }
        populate(lang);
    }

    @Override
    public @NotNull CreateLang lang() {
        return lang;
    }
}
