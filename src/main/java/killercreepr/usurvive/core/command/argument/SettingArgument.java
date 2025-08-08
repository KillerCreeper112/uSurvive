package killercreepr.usurvive.core.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ticxo.modelengine.api.utils.ticker.PseudoThread;
import killercreepr.crux.core.command.argument.CruxKeyedArgument;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SettingArgument implements CruxKeyedArgument<PlayerSetting> {
    @NotNull
    @Override
    public PlayerSetting parse(@NotNull Key key) {
        return Objects.requireNonNull(
            USurviveRegistries.PLAYER_SETTING.get(key)
        );
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (PlayerSetting setting : USurviveRegistries.PLAYER_SETTING) {
            builder.suggest(setting.key().asString());
        }
        return builder.buildFuture();
    }
}
