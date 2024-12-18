package killercreepr.usurvive.core.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import killercreepr.usurvive.api.death.DeathManager;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.death.PlayerDeath;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TargetPlayerDeathArgument implements CustomArgumentType.Converted<TargetPlayerDeathResolver, Long> {
    @Override
    public @NotNull TargetPlayerDeathResolver convert(@NotNull Long aLong) {
        return (ctx, target) ->{
            DeathManager deathManager = USurvivePlugin.inst().getDeathManager();
            UUID uuid = target.getUniqueId();
            List<PlayerDeath> list = new ArrayList<>(deathManager.getAllDeaths(death ->{
                if(!death.getPlayerUUID().equals(uuid)) return false;
                return death.getTime() == aLong;
            }));
            if(list.isEmpty()) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create(
                "No player death from " + target.getName() + " at " + aLong + " found!"
            );
            return list.getFirst();
        };
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        if(context.getSource() instanceof CommandSourceStack source){
            try {
                Player target = context.getLastChild().getArgument("target", PlayerSelectorArgumentResolver.class)
                    .resolve(source).getFirst();
                DeathManager deathManager = USurvivePlugin.inst().getDeathManager();
                deathManager.getAllDeaths(target).forEach(death -> builder.suggest(death.getTime() + ""));
                return builder.buildFuture();
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
        return Converted.super.listSuggestions(context, builder);
    }

    @Override
    public @NotNull ArgumentType<Long> getNativeType() {
        return LongArgumentType.longArg();
    }
}
