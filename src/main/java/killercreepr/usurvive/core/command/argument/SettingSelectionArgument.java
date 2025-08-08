package killercreepr.usurvive.core.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import killercreepr.crux.core.command.argument.CruxKeyedArgument;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SettingSelectionArgument implements CruxKeyedArgument<PlayerSettingSelection> {
    @NotNull
    @Override
    public PlayerSettingSelection parse(@NotNull Key key) {
        return Objects.requireNonNull(
            USurviveRegistries.PLAYER_SETTING_SELECTION.get(key)
        );
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        try{
            for (PlayerSettingSelection selection : context.getLastChild().getArgument("setting", PlayerSetting.class).validSelections()) {
                builder.suggest(selection.key().asString());
            }
            return builder.buildFuture();
        }catch (Exception e){}
        for (var setting : USurviveRegistries.PLAYER_SETTING_SELECTION) {
            builder.suggest(setting.key().asString());
        }
        return builder.buildFuture();
    }
}
