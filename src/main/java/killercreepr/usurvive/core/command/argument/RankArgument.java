package killercreepr.usurvive.core.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import killercreepr.crux.core.command.argument.CruxKeyedArgument;
import killercreepr.usurvive.api.dialog.KeyedDialogContent;
import killercreepr.usurvive.api.rank.Rank;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class RankArgument implements CruxKeyedArgument<Rank> {
    @NotNull
    @Override
    public Rank parse(@NotNull Key key) {
        return Objects.requireNonNull(
            USurviveRegistries.RANK.get(key)
        );
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (Rank setting : USurviveRegistries.RANK) {
            builder.suggest(setting.key().asString());
        }
        return builder.buildFuture();
    }
}
